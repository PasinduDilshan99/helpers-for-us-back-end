package com.you.and.me.service;

import com.you.and.me.model.request.agency.AgencyInsertRequest;
import com.you.and.me.model.request.location.LocationRequest;
import com.you.and.me.model.response.agency.AgencyBasicDetailsResponse;
import com.you.and.me.model.response.common.CommonResponse;
import com.you.and.me.model.response.common.InsertResponse;
import com.you.and.me.model.response.location.OptimalDestinationResponse;

import java.util.List;

public interface LocationService {
    CommonResponse<List<OptimalDestinationResponse>> computeOptimalDestinations(LocationRequest locationRequest);

    CommonResponse<InsertResponse> addAgency(AgencyInsertRequest agencyInsertRequest);

    CommonResponse<List<AgencyBasicDetailsResponse>> getAllAgencies();
}
