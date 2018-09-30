package com.hari.consumer.clients;

import com.hari.consumer.clients.impl.EmployeeFeignClientImpl;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "employee-producer", fallback = EmployeeFeignClientImpl.class)
public interface EmployeeFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/employee")
    Object getEmpFromProducer();

    @RequestMapping(value = "/employee/{name}", method = RequestMethod.GET)
    Object getEmpFromProducerWithLoadBalance();
}
