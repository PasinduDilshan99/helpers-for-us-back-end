package com.you.and.me.controller;

import com.you.and.me.model.request.agency.AgencyInsertRequest;
import com.you.and.me.model.request.location.LocationRequest;
import com.you.and.me.model.response.agency.AgencyBasicDetailsResponse;
import com.you.and.me.model.response.common.CommonResponse;
import com.you.and.me.model.response.common.InsertResponse;
import com.you.and.me.model.response.location.OptimalDestinationResponse;
import com.you.and.me.service.LocationService;
import com.you.and.me.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v0/location")
public class LocationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationController.class);

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping(path = "/all-agencies")
    public ResponseEntity<CommonResponse<List<AgencyBasicDetailsResponse>>> getAllAgencies() {
        LOGGER.info("{} Start execute compute optimal destinations {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<List<AgencyBasicDetailsResponse>> response = locationService.getAllAgencies();
        LOGGER.info("{} End execute compute optimal destinations {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/optimal-destinations")
    public ResponseEntity<CommonResponse<List<OptimalDestinationResponse>>> computeOptimalDestinations(
            @RequestBody LocationRequest locationRequest) {
        LOGGER.info("{} Start execute compute optimal destinations {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<List<OptimalDestinationResponse>> response =
                locationService.computeOptimalDestinations(locationRequest);
        LOGGER.info("{} End execute compute optimal destinations {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/add-agency")
    public ResponseEntity<CommonResponse<InsertResponse>> addAgency(@RequestBody AgencyInsertRequest agencyInsertRequest) {
        LOGGER.info("{} Start execute compute optimal destinations {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<InsertResponse> response = locationService.addAgency(agencyInsertRequest);
        LOGGER.info("{} End execute compute optimal destinations {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
