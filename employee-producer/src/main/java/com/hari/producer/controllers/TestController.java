package com.hari.producer.controllers;

import com.hari.producer.model.Employee;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.jboss.logging.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    Logger logger = Logger.getLogger(TestController.class);

    @RequestMapping(value = "/employee", method = RequestMethod.GET)
    public Employee firstPage() {

        Employee emp = new Employee();
        emp.setName("emp1");
        emp.setDesignation("manager");
        emp.setEmpId("1");
        emp.setSalary(3000);

        return emp;
    }

    @HystrixCommand(commandProperties = {@HystrixProperty(
            name = "execution.isolation.thread.timeoutInMilliseconds",
            value = "500")}, fallbackMethod = "getDataFallBack")
    @RequestMapping(value = "/employee/{name}", method = RequestMethod.GET)
    public Employee dynamicEmployee(@PathVariable(name = "name") String name) {

        logger.warn("Harry path variable emp =" + name);

        if (name.equals("hari")) {
            throw new RuntimeException();
        }
        if (name.equals("babu")) {
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Employee emp = new Employee();
        emp.setName(name);
        emp.setDesignation("manager");
        emp.setEmpId("1");
        emp.setSalary(3000);

        return emp;
    }

    public Employee getDataFallBack(String errorMessage) {

        Employee emp = new Employee();
        emp.setName("ERROR ::::::::::::::::::: " + errorMessage);
        emp.setDesignation("fallback-manager");
        emp.setEmpId("fallback-1");
        emp.setSalary(3000);

        return emp;

    }

}
