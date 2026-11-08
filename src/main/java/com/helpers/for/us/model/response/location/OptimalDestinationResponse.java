package com.you.and.me.model.response.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OptimalDestinationResponse {
    private int index;
    private String locationName;
    private String city;
    private String webSiteUrl;
    private double longitude;
    private double latitude;
}
