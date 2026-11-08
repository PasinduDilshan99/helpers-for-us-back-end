package com.you.and.me.repository;

import com.you.and.me.model.dto.location.AgencyBasicDetailsDTO;
import com.you.and.me.model.request.agency.AgencyInsertRequest;
import com.you.and.me.model.response.agency.AgencyBasicDetailsResponse;

import java.util.List;

public interface LocationRepository {
    List<AgencyBasicDetailsDTO> getAgenciesBasicDetailsByIdList(List<Long> locations);

    AgencyBasicDetailsDTO getLocationById(Long startLocation);

    Long addAgency(AgencyInsertRequest agencyInsertRequest, Long userId);

    List<AgencyBasicDetailsResponse> getAllAgencies();
}
