package com.hari.consumer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@RestController
public class ConsumerControllerClient {

    @Autowired
    private DiscoveryClient discoveryClient;

    @RequestMapping(value = "/emp/using-rest-template")
    public Object getEmpFromProducer() {
        return getEmployee();
    }

    public Object getEmployee() {

        List<ServiceInstance> instances = discoveryClient.getInstances("employee-producer");
        ServiceInstance serviceInstance = instances.get(0);

        String baseUrl = serviceInstance.getUri().toString();

        baseUrl = baseUrl + "/employee";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(baseUrl,
                    HttpMethod.GET, getHeaders(), String.class);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        System.out.println(response.getBody());
        return response.getBody();
    }

    private static HttpEntity<?> getHeaders() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        return new HttpEntity<>(headers);
    }
}