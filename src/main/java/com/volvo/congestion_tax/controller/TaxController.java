package com.volvo.congestion_tax.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.volvo.congestion_tax.model.TaxRequest;
import com.volvo.congestion_tax.model.TaxResponse;
import com.volvo.congestion_tax.service.TaxService;

@RestController
@RequestMapping("/api/tax")
public class TaxController {

    @Autowired
    private TaxService taxService;

    @PostMapping("/calculate")
    public TaxResponse calculateTax(@RequestBody TaxRequest taxRequest) {

    	// Pass the city name to load the corresponding tax rules
        return taxService.calculateTotalTax(taxRequest.getTimestamps(), taxRequest.getCity(), taxRequest.getVehicleType());
        
    }
}
