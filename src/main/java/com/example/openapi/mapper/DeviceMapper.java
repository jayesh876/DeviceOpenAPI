package com.example.openapi.mapper;


import com.example.openapi.dto.DeviceDetails;
import com.example.openapi.dto.DeviceDto;
import org.springframework.stereotype.Service;

@Service
public class DeviceMapper{
    public void getDeviceDetails(DeviceDetails deviceDetails , DeviceDto deviceDto){
        deviceDetails.setId(deviceDto.getId());
        deviceDetails.setName(deviceDto.getName());
        deviceDetails.setCapacity(deviceDto.getCapacity());
    }
}
