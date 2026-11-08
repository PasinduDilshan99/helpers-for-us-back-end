package com.you.and.me.service.impl;

import com.you.and.me.model.dto.location.AgencyBasicDetailsDTO;
import com.you.and.me.model.request.agency.AgencyInsertRequest;
import com.you.and.me.model.request.location.LocationRequest;
import com.you.and.me.model.response.agency.AgencyBasicDetailsResponse;
import com.you.and.me.model.response.common.CommonResponse;
import com.you.and.me.model.response.common.InsertResponse;
import com.you.and.me.model.response.location.OptimalDestinationResponse;
import com.you.and.me.repository.LocationRepository;
import com.you.and.me.service.CommonService;
import com.you.and.me.service.LocationService;
import com.you.and.me.util.CommonResponseMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LocationServiceImpl implements LocationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationServiceImpl.class);

    /** Up to this many stops we solve exactly (Held-Karp). Above it, we use a heuristic. */
    private static final int EXACT_SOLVER_LIMIT = 15;
    private static final int MAX_STOPS = 50;
    private static final double EARTH_RADIUS_KM = 6371.0;

    private final LocationRepository locationRepository;
    private final CommonService commonService;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository, CommonService commonService) {
        this.locationRepository = locationRepository;
        this.commonService = commonService;
    }

    @Override
    public CommonResponse<List<OptimalDestinationResponse>> computeOptimalDestinations(LocationRequest request) {
        validate(request);

        // Remove duplicates and make sure start/end are not treated as intermediate stops
        Set<Long> stopIds = new LinkedHashSet<>(request.getLocations());
        stopIds.remove(request.getStartLocation());
        stopIds.remove(request.getEndLocation());

        if (stopIds.size() > MAX_STOPS) {
            throw new IllegalArgumentException("Too many locations. Maximum allowed is " + MAX_STOPS);
        }

        AgencyBasicDetailsDTO start = requireLocation(request.getStartLocation());
        AgencyBasicDetailsDTO end = requireLocation(request.getEndLocation());
        List<AgencyBasicDetailsDTO> stops = loadStops(stopIds);

        // Node layout: 0 = start, 1..n = stops, n + 1 = end
        List<AgencyBasicDetailsDTO> nodes = new ArrayList<>(stops.size() + 2);
        nodes.add(start);
        nodes.addAll(stops);
        nodes.add(end);

        double[][] distance = buildDistanceMatrix(nodes);
        int[] order = stops.size() <= EXACT_SOLVER_LIMIT
                ? solveExact(distance, stops.size())
                : solveHeuristic(distance, stops.size());

        List<OptimalDestinationResponse> response = new ArrayList<>(order.length);
        for (int i = 0; i < order.length; i++) {
            response.add(toResponse(i, nodes.get(order[i])));
        }

        LOGGER.debug("Computed optimal route for {} stops", stops.size());
        return CommonResponse.<List<OptimalDestinationResponse>>builder()
                .code(CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE)
                .status(CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS)
                .message(CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE)
                .data(response)
                .build();
    }

    @Override
    public CommonResponse<InsertResponse> addAgency(AgencyInsertRequest agencyInsertRequest) {
        try{
            Long userId = commonService.getUserIdBySecurityContext();
            Long agencyId =  locationRepository.addAgency(agencyInsertRequest, userId);
            CommonResponse<InsertResponse> response =
                    new CommonResponse<>(
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                            new InsertResponse(agencyId,"successfully insert"),
                            Instant.now()
                    );
            return response;
        }catch (Exception e){
            LOGGER.info(e.toString());
            return null;
        }
    }

    @Override
    public CommonResponse<List<AgencyBasicDetailsResponse>> getAllAgencies() {
        try{
            List<AgencyBasicDetailsResponse> agencyBasicDetailsResponseList =  locationRepository.getAllAgencies();
            CommonResponse<List<AgencyBasicDetailsResponse>> response =
                    new CommonResponse<>(
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                            agencyBasicDetailsResponseList,
                            Instant.now()
                    );
            return response;
        }catch (Exception e){
            LOGGER.info(e.toString());
            return null;
        }
    }

    // ------------------------------------------------------------------ loading / validation

    private void validate(LocationRequest request) {
        if (request == null
                || request.getStartLocation() == null
                || request.getEndLocation() == null) {
            throw new IllegalArgumentException("Start and end locations are required");
        }
        if (request.getLocations() == null) {
            throw new IllegalArgumentException("Locations list is required");
        }
    }

    private AgencyBasicDetailsDTO requireLocation(Long id) {
        AgencyBasicDetailsDTO location = locationRepository.getLocationById(id);
        if (location == null) {
            throw new IllegalArgumentException("Location not found: " + id);
        }
        requireCoordinates(location);
        return location;
    }

    private List<AgencyBasicDetailsDTO> loadStops(Set<Long> stopIds) {
        if (stopIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<AgencyBasicDetailsDTO> found = locationRepository.getAgenciesBasicDetailsByIdList(new ArrayList<>(stopIds));
        Map<Long, AgencyBasicDetailsDTO> byId = found.stream()
                .collect(Collectors.toMap(AgencyBasicDetailsDTO::getAgencyId, Function.identity(), (a, b) -> a));

        List<Long> missing = stopIds.stream().filter(id -> !byId.containsKey(id)).collect(Collectors.toList());
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Locations not found: " + missing);
        }

        List<AgencyBasicDetailsDTO> ordered = new ArrayList<>(stopIds.size());
        for (Long id : stopIds) {
            AgencyBasicDetailsDTO stop = byId.get(id);
            requireCoordinates(stop);
            ordered.add(stop);
        }
        return ordered;
    }

    private void requireCoordinates(AgencyBasicDetailsDTO location) {
        if (location.getLatitude() == null || location.getLongitude() == null) {
            throw new IllegalArgumentException("Location has no coordinates: " + location.getAgencyId());
        }
    }

    // ------------------------------------------------------------------ distance

    private double[][] buildDistanceMatrix(List<AgencyBasicDetailsDTO> nodes) {
        int size = nodes.size();
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                double d = haversineKm(
                        nodes.get(i).getLatitude(), nodes.get(i).getLongitude(),
                        nodes.get(j).getLatitude(), nodes.get(j).getLongitude());
                matrix[i][j] = d;
                matrix[j][i] = d;
            }
        }
        return matrix;
    }

    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    // ------------------------------------------------------------------ exact solver (Held-Karp)

    /**
     * Returns node indices in visiting order: [0, ...stops..., n + 1].
     * dp[mask][j] = shortest distance from start, visiting exactly the stops in mask, ending at stop j.
     */
    private int[] solveExact(double[][] d, int n) {
        int endNode = n + 1;
        if (n == 0) {
            return new int[]{0, endNode};
        }

        int full = (1 << n) - 1;
        double[][] dp = new double[1 << n][n];
        int[][] parent = new int[1 << n][n];
        for (double[] row : dp) {
            Arrays.fill(row, Double.POSITIVE_INFINITY);
        }
        for (int[] row : parent) {
            Arrays.fill(row, -1);
        }

        for (int j = 0; j < n; j++) {
            dp[1 << j][j] = d[0][j + 1];
        }

        for (int mask = 1; mask <= full; mask++) {
            for (int j = 0; j < n; j++) {
                if ((mask & (1 << j)) == 0 || dp[mask][j] == Double.POSITIVE_INFINITY) {
                    continue;
                }
                for (int k = 0; k < n; k++) {
                    if ((mask & (1 << k)) != 0) {
                        continue;
                    }
                    int next = mask | (1 << k);
                    double candidate = dp[mask][j] + d[j + 1][k + 1];
                    if (candidate < dp[next][k]) {
                        dp[next][k] = candidate;
                        parent[next][k] = j;
                    }
                }
            }
        }

        // Pick the best last stop, including the leg to the end location
        int last = -1;
        double best = Double.POSITIVE_INFINITY;
        for (int j = 0; j < n; j++) {
            double total = dp[full][j] + d[j + 1][endNode];
            if (total < best) {
                best = total;
                last = j;
            }
        }

        // Rebuild the path backwards
        int[] order = new int[n + 2];
        order[0] = 0;
        order[n + 1] = endNode;
        int mask = full;
        for (int pos = n; pos >= 1; pos--) {
            order[pos] = last + 1;
            int prev = parent[mask][last];
            mask ^= (1 << last);
            last = prev;
        }
        return order;
    }

    // ------------------------------------------------------------------ heuristic solver

    /** Nearest neighbour for an initial route, then 2-opt improvement with start and end fixed. */
    private int[] solveHeuristic(double[][] d, int n) {
        int endNode = n + 1;
        int[] route = new int[n + 2];
        boolean[] visited = new boolean[n + 2];

        route[0] = 0;
        visited[0] = true;
        int current = 0;
        for (int pos = 1; pos <= n; pos++) {
            int nearest = -1;
            double nearestDistance = Double.POSITIVE_INFINITY;
            for (int candidate = 1; candidate <= n; candidate++) {
                if (!visited[candidate] && d[current][candidate] < nearestDistance) {
                    nearestDistance = d[current][candidate];
                    nearest = candidate;
                }
            }
            route[pos] = nearest;
            visited[nearest] = true;
            current = nearest;
        }
        route[endNode] = endNode;

        boolean improved = true;
        while (improved) {
            improved = false;
            for (int i = 1; i < n; i++) {
                for (int j = i + 1; j <= n; j++) {
                    double before = d[route[i - 1]][route[i]] + d[route[j]][route[j + 1]];
                    double after = d[route[i - 1]][route[j]] + d[route[i]][route[j + 1]];
                    if (after < before - 1e-9) {
                        reverse(route, i, j);
                        improved = true;
                    }
                }
            }
        }
        return route;
    }

    private void reverse(int[] route, int from, int to) {
        while (from < to) {
            int tmp = route[from];
            route[from++] = route[to];
            route[to--] = tmp;
        }
    }

    // ------------------------------------------------------------------ mapping

    private OptimalDestinationResponse toResponse(int index, AgencyBasicDetailsDTO location) {
        return OptimalDestinationResponse.builder()
                .index(index)
                .locationName(location.getName())
                .city(location.getCity())
                .webSiteUrl(location.getWebSiteUrl())
                .longitude(location.getLongitude())
                .latitude(location.getLatitude())
                .build();
    }
}