package com.example.openapi.exception;

public class DeviceNotFoundException extends RuntimeException{

    public DeviceNotFoundException(String message){
        super(message);
    }
}
