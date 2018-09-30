package com.hari.consumer.clients.impl;

import com.hari.consumer.clients.EmployeeFeignClient;
import org.springframework.stereotype.Service;

@Service
public class EmployeeFeignClientImpl implements EmployeeFeignClient {
    @Override
    public Object getEmpFromProducer() {
        return "Error for feign fallback";
    }

    @Override
    public Object getEmpFromProducerWithLoadBalance() {
        return "Error for feign fallback and loadbalancer";
    }
}
