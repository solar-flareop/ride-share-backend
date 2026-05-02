package com.solarflare.locationservice.service;

import com.solarflare.locationservice.dto.DriverLocationRequest;
import com.solarflare.locationservice.dto.NearByDriverResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService {

    private final RedisTemplate<String,String> redisTemplate;
    private static final String DRIVERS_GEO_KEY = "drivers:locations";

    public void updateDriverLocation(DriverLocationRequest driverLocationRequest) {
        log.info("Updating the driver location");

        Point driverPoint = new Point(driverLocationRequest.getLongitude(), driverLocationRequest.getLatitude());
        redisTemplate.opsForGeo().add(DRIVERS_GEO_KEY, driverPoint, driverLocationRequest.getDriverId());

        log.info("Location updated for driver: {}",driverLocationRequest.getDriverId());
    }

    public List<NearByDriverResponse> getNearByDrivers(double latitude, double longitude, double radius) {
        log.info("Finding drivers nearby with lat:{}, long:{}, radius:{}", latitude,longitude,radius);

        Circle searchCircle = new Circle(new Point(longitude, latitude), new Distance(radius, Metrics.KILOMETERS));
        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisTemplate.opsForGeo()
                    .radius(DRIVERS_GEO_KEY,
                            searchCircle,
                            RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                                    .includeCoordinates()
                                    .includeDistance()
                                    .sortAscending()
                                    .limit(10)
                    );

        List<NearByDriverResponse> nearByDriverResponseList = new ArrayList<>();

        if(results!=null){
            results.getContent().forEach(result ->{
                RedisGeoCommands.GeoLocation<String> location = result.getContent();
                nearByDriverResponseList.add(new NearByDriverResponse(
                        location.getName(),
                        location.getPoint().getY(),
                        location.getPoint().getX(),
                        result.getDistance().getValue()
                ));
            });
        }

        log.info("Found {} nearby drivers", nearByDriverResponseList.size());
        return nearByDriverResponseList;
    }

    public void removeDriver(String driverId) {
        log.info("Removing driver with id : {}", driverId);
        redisTemplate.opsForGeo().remove(DRIVERS_GEO_KEY,driverId);
    }
}
