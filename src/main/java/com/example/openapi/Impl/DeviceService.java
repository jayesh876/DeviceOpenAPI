package com.example.openapi.Impl;

import com.example.openapi.api.DeviceApiDelegate;
import com.example.openapi.dto.DeviceDetails;
import com.example.openapi.dto.DeviceDto;
import com.example.openapi.mapper.DeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
public class DeviceService implements DeviceApiDelegate {

    private static final String url = "http://localhost:9090/device";
    @Autowired
    private WebClient.Builder builder;
    @Autowired
    private DeviceMapper mapper;

    public DeviceService(WebClient.Builder builder) {
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
        String idUrl = url + "/{id}";
        String response = builder.build()
                .delete()
                .uri(idUrl, id)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return new ResponseEntity<>(response, HttpStatus.OK);
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
        String idUrl = url + "/{id}";
        DeviceDetails details = builder.build()
                .get()
                .uri(idUrl, id)
                .retrieve()
                .bodyToMono(DeviceDetails.class)
                .block();
        return new ResponseEntity<>(details, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DeviceDetails> updateDevice(Integer id, DeviceDetails deviceDetails) {
        String idUrl = url + "/{id}";
        DeviceDetails details =  builder.build()
                .put()
                .uri(idUrl, id)
                .body(Mono.just(deviceDetails), DeviceDetails.class)
                .retrieve()
                .bodyToMono(DeviceDetails.class)
                .block();

        return new ResponseEntity<>(details, HttpStatus.CREATED);
    }

}
