package com.example.openapi.Impl;

import com.example.openapi.api.DeviceApiDelegate;
import com.example.openapi.dto.DeviceDetails;
import com.example.openapi.dto.DeviceDto;
import com.example.openapi.exception.DeviceNotFoundException;
import com.example.openapi.mapper.DeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class DeviceApiDelegateImpl implements DeviceApiDelegate {

    private static final String url = "http://localhost:9090/device";
    private static final String idUrl = url + "/{id}";
    @Autowired
    private WebClient.Builder builder;
    @Autowired
    private DeviceMapper mapper;

    public DeviceApiDelegateImpl(WebClient.Builder builder) {
        this.builder = builder;
    }
    @Override
    public ResponseEntity<DeviceDetails> createDevice(String color, Integer price, DeviceDto deviceDto) {

        // convert deviceDto to deviceDetails since we need to have headers and query params to be included in response.
        DeviceDetails deviceDetails = new DeviceDetails();
        mapper = new DeviceMapper();
        mapper.getDeviceDetails(deviceDetails, deviceDto);
        deviceDetails.setColor(color);
        deviceDetails.setPrice(price);

        DeviceDetails details =  builder.build()
                .post()
                .uri(uriBuilder -> uriBuilder.scheme("http")
                        .host("localhost")
                        .port(9090)
                        .path("/device")
                        .queryParam("color", color)
                        .build()
                )
                .header("price", Integer.toString(price))
                .body(Mono.just(deviceDetails), DeviceDetails.class)
                .retrieve()
                .bodyToMono(DeviceDetails.class)
                .block();
        return new ResponseEntity<>(details, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<String> deleteDevice(Integer id) {
        try {
            String response = builder.build()
                    .delete()
                    .uri(idUrl, id)
                    .retrieve()
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                        return Mono.error(new DeviceNotFoundException("Device with Id not found"));
                    })
                    .bodyToMono(String.class)
                    .block();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch(DeviceNotFoundException exception){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<List<DeviceDetails>> getAllDeviceDetails() {
        List<DeviceDetails> deviceDetails= builder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(DeviceDetails.class)
                .collectList()
                .block();
        return new ResponseEntity<>(deviceDetails, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DeviceDetails> getDeviceDetailsById(Integer id) {
        try {
            DeviceDetails details = builder.build()
                    .get()
                    .uri(idUrl, id)
                    .retrieve()
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                        return Mono.error(new DeviceNotFoundException("Device with Id not found"));
                    })
                    .bodyToMono(DeviceDetails.class)
                    .block();
            return new ResponseEntity<>(details, HttpStatus.OK);
        }catch (DeviceNotFoundException exception){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<DeviceDetails> updateDevice(Integer id, DeviceDetails deviceDetails) {
        try {
             DeviceDetails details = builder.build()
                    .put()
                    .uri(idUrl, id)
                    .body(Mono.just(deviceDetails), DeviceDetails.class)
                    .retrieve()
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                        return Mono.error(new DeviceNotFoundException("Device with Id not found"));
                    })
                    .bodyToMono(DeviceDetails.class)
                    .block();
            return new ResponseEntity<>(details, HttpStatus.CREATED);
        }catch(DeviceNotFoundException exception){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }


    }

}
