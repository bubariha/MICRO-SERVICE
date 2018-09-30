package com.hari.consumer.controllers;

import com.hari.consumer.clients.EmployeeFeignClient;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@RestController
public class ConsumerControllerClient {

    @Autowired
    EmployeeFeignClient employeeFeignClient;

    Logger logger = Logger.getLogger(ConsumerControllerClient.class);

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private DiscoveryClient discoveryClient;

    @RequestMapping(value = "/emp/using-rest-template")
    public Object getEmpFromProducer() {
        return getEmployee();
    }

    @RequestMapping(value = "/emp/using-rest-template/zuul")
    public Object getEmpFromProducerWithZuul() {
        return getEmployeeWithZuul();
    }

    @RequestMapping(value = "/emp/using-rest-template/ribbon-load-balancer")
    public Object getEmpFromProducerWithLoadBalance() {
        return getEmployeeWithLoadBalancer();
    }

    @RequestMapping(value = "/emp/using-feign/ribbon-load-balancer")
    public Object getEmpFromProducerWithFeignClient() {
        return getEmployeeWithFeign();
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
            logger.warn(ex);
        }
        logger.warn(response.getBody());
        return response.getBody();
    }

    public Object getEmployeeWithZuul() {
        logger.warn("Harry");
        List<ServiceInstance> instances = discoveryClient.getInstances("zuul-service");
        ServiceInstance serviceInstance = instances.get(0);

        logger.warn("Harry serviceInstance with zuul "+serviceInstance);
        String baseUrl = serviceInstance.getUri().toString();
        logger.warn("Harry baseUrl with zuul "+baseUrl);
        baseUrl = baseUrl + "/employee-producer/employee";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(baseUrl,
                    HttpMethod.GET, getHeaders(), String.class);
        } catch (Exception ex) {
            logger.warn(ex);
        }
        logger.warn(response.getBody());
        return response.getBody();
    }

    private static HttpEntity<?> getHeaders() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        return new HttpEntity<>(headers);
    }

    public Object getEmployeeWithLoadBalancer() {

        ServiceInstance serviceInstance = loadBalancerClient.choose("employee-producer");

        logger.warn(serviceInstance.getUri());
        if (serviceInstance.getUri().toString().contains("8091")) {
            try {
                Thread.sleep(1000000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String baseUrl = serviceInstance.getUri().toString();

        baseUrl = baseUrl + "/employee";

        logger.warn("harry checking load balancing " + baseUrl);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(baseUrl,
                    HttpMethod.GET, getHeaders(), String.class);
        } catch (Exception ex) {
            logger.warn(ex);
        }
        logger.warn(response.getBody());
        return response.getBody();
    }

    public Object getEmployeeWithFeign() {

        return employeeFeignClient.getEmpFromProducer();

    }

}