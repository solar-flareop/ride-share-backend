package com.solarflare.locationservice.controller;

import com.solarflare.locationservice.dto.DriverLocationRequest;
import com.solarflare.locationservice.dto.NearByDriverResponse;
import com.solarflare.locationservice.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/v1/locations")
public class LocationController {

    private final LocationService locationService;


    //Driver sends the updated location every 3 secs
    @PostMapping("/drivers/update")
    public ResponseEntity<String> updateDriverLocation(@RequestBody DriverLocationRequest driverLocationRequest){
        locationService.updateDriverLocation(driverLocationRequest);
        return ResponseEntity.ok("Driver location updated successfully " + driverLocationRequest.getDriverId());
    }

    //Matching service call this api when a ride is requested for nearby drivers
    @GetMapping("/drivers/nearby")
    public ResponseEntity<List<NearByDriverResponse>> updateDriverLocation(@RequestParam double latitude,
                                                                           @RequestParam double longitude,
                                                                           @RequestParam(defaultValue = "5.0") double radius){
        List<NearByDriverResponse> listOfNearbyDrivers = locationService.getNearByDrivers(latitude,longitude,radius);
        return ResponseEntity.ok(listOfNearbyDrivers);

    }

    //When a rider goes offline
    @DeleteMapping("/drivers/{driverId}")
    public ResponseEntity<String> updateDriverLocation(@PathVariable String driverId){
        locationService.removeDriver(driverId);
        return ResponseEntity.ok("Driver removed");
    }
}
