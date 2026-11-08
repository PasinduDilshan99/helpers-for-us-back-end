package com.you.and.me.model.request.location;

import lombok.Data;

import java.util.List;

@Data
public class LocationRequest {

    private Long startLocation;
    private Long endLocation;
    private List<Long> locations;
    private Boolean isSeeSister;
    private Boolean isBarista;

}