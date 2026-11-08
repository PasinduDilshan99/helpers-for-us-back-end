package com.you.and.me.model.request.agency;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AgencyInsertRequest {
    private String name;
    private String description;
    private String address;
    private String city;
    private String webSite;
    private String contactNumber;
    private Double latitude;
    private Double longitude;
}
