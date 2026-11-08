package com.you.and.me.repository.impl;

import com.you.and.me.model.dto.location.AgencyBasicDetailsDTO;
import com.you.and.me.model.request.agency.AgencyInsertRequest;
import com.you.and.me.model.response.agency.AgencyBasicDetailsResponse;
import com.you.and.me.repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

@Repository
public class LocationRepositoryImpl implements LocationRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LocationRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<AgencyBasicDetailsDTO> getAgenciesBasicDetailsByIdList(
            List<Long> locations) {

        if (locations == null || locations.isEmpty()) {
            return Collections.emptyList();
        }

        String placeholders = String.join(
                ",",
                Collections.nCopies(locations.size(), "?")
        );

        String sql = """
                SELECT
                    id AS agencyId,
                    name,
                    city,
                    website AS webSiteUrl,
                    latitude,
                    longitude
                FROM agency
                WHERE id IN (%s)
                AND status_id = (
                    SELECT id FROM common_status WHERE name = 'ACTIVE'
                )
                """.formatted(placeholders);

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> AgencyBasicDetailsDTO.builder()
                        .agencyId(rs.getLong("agencyId"))
                        .name(rs.getString("name"))
                        .city(rs.getString("city"))
                        .webSiteUrl(rs.getString("webSiteUrl"))
                        .latitude(rs.getObject("latitude", Double.class))
                        .longitude(rs.getObject("longitude", Double.class))
                        .build(),
                locations.toArray()
        );
    }

    @Override
    public AgencyBasicDetailsDTO getLocationById(Long startLocation) {

        String sql = """
            SELECT
                id AS agencyId,
                name,
                city,
                website AS webSiteUrl,
                latitude,
                longitude
            FROM agency
            WHERE id = ?
            AND status_id = (
                SELECT id FROM common_status WHERE name = 'ACTIVE'
            )
            """;

        try {
            return jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> AgencyBasicDetailsDTO.builder()
                            .agencyId(rs.getLong("agencyId"))
                            .name(rs.getString("name"))
                            .city(rs.getString("city"))
                            .webSiteUrl(rs.getString("webSiteUrl"))
                            .latitude(rs.getObject("latitude", Double.class))
                            .longitude(rs.getObject("longitude", Double.class))
                            .build(),
                    startLocation
            );

        } catch (EmptyResultDataAccessException e) {
            LOGGER.warn("No active agency found for ID: {}", startLocation);
            return null;
        }
    }

    @Override
    public Long addAgency(AgencyInsertRequest agencyInsertRequest, Long userId) {

        String sql = """
        INSERT INTO agency (
            name,
            description,
            address,
            city,
            website,
            contact_number,
            latitude,
            longitude,
            status_id,
            created_by,
            created_at,
            updated_by,
            updated_at
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, 
            (SELECT id FROM common_status WHERE name = 'ACTIVE'),
            ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP)
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                );

                ps.setString(1, agencyInsertRequest.getName());
                ps.setString(2, agencyInsertRequest.getDescription());
                ps.setString(3, agencyInsertRequest.getAddress());
                ps.setString(4, agencyInsertRequest.getCity());
                ps.setString(5, agencyInsertRequest.getWebSite());
                ps.setString(6, agencyInsertRequest.getContactNumber());
                ps.setObject(7, agencyInsertRequest.getLatitude());
                ps.setObject(8, agencyInsertRequest.getLongitude());
                ps.setLong(9, userId);
                ps.setLong(10, userId);

                return ps;
            }, keyHolder);

            return keyHolder.getKey().longValue();

        } catch (DataAccessException e) {
            LOGGER.error("Error while adding agency: {}", agencyInsertRequest.getName(), e);
            throw e;
        }
    }

    @Override
    public List<AgencyBasicDetailsResponse> getAllAgencies() {

        String sql = """
        SELECT
            id AS agencyId,
            name,
            city,
            website AS webSiteUrl,
            latitude,
            longitude
        FROM agency
        WHERE status_id = (
            SELECT id
            FROM common_status
            WHERE name = 'ACTIVE'
        )
        ORDER BY name ASC
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                AgencyBasicDetailsResponse.builder()
                        .agencyId(rs.getLong("agencyId"))
                        .name(rs.getString("name"))
                        .city(rs.getString("city"))
                        .webSiteUrl(rs.getString("webSiteUrl"))
                        .latitude(rs.getObject("latitude", Double.class))
                        .longitude(rs.getObject("longitude", Double.class))
                        .build()
        );
    }
}
