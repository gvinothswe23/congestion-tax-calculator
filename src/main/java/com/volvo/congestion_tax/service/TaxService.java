package com.volvo.congestion_tax.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.volvo.congestion_tax.model.TaxResponse;
import com.volvo.congestion_tax.utils.DateUtils;

import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaxService {

    // Method to load city-specific rules from a JSON file
    public JsonNode loadCityRules(String city) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String fileName = city.toLowerCase() + "_rules.json";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        return mapper.readTree(inputStream);
    }

    public TaxResponse calculateTotalTax(List<String> timestamps, String city, String vehicleType) {
    	TaxResponse response = new TaxResponse();
    	try {
    		double totalTax = 0.0;
            double highestTollInWindow = 0.0;
            LocalDateTime lastTollTime = null;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Load city-specific rules from the JSON file
            JsonNode cityRules = loadCityRules(city);

            // Get the tax-exempt vehicles
            List<String> exemptVehicles = new ArrayList<>();
            cityRules.get("taxExemptVehicles").forEach(v -> exemptVehicles.add(v.asText()));

            // Check if the vehicle is exempt
            if (exemptVehicles.contains(vehicleType.toLowerCase())) {
            	
            	response.setTaxAmount((double) 0);
            	return response;
            }

            // Get the year scope
            int yearScope = cityRules.get("yearScope").asInt();

            // Get the maximum daily tax
            double maxDailyTax = cityRules.get("maxDailyTax").asDouble();

            // Get public holidays and days before public holidays
            List<String> publicHolidays = new ArrayList<>();
            cityRules.get("publicHolidays").forEach(v -> publicHolidays.add(v.asText()));

            List<String> daysBeforePublicHoliday = new ArrayList<>();
            cityRules.get("daysBeforePublicHoliday").forEach(v -> daysBeforePublicHoliday.add(v.asText()));

            boolean julyExempt = cityRules.get("julyExempt").asBoolean();

            // Parse timestamps and calculate total tax
            for (String ts : timestamps) {
                LocalDateTime dateTime = LocalDateTime.parse(ts, formatter);

                // Scope to the specified year
                if (dateTime.getYear() != yearScope) {
                    continue;
                }

                // Skip if it's a public holiday, a day before a public holiday, or July (if exempt)
                if (DateUtils.isExemptDay(dateTime, publicHolidays, daysBeforePublicHoliday, julyExempt)) {
                    continue;
                }

                double currentTax = getTaxForTime(dateTime, cityRules.get("timeIntervals"));

                // Apply the single-charge rule (60 minutes)
                if (lastTollTime != null && DateUtils.within60Minutes(lastTollTime, dateTime)) {
                    highestTollInWindow = Math.max(highestTollInWindow, currentTax);
                } else {
                    totalTax += highestTollInWindow;
                    highestTollInWindow = currentTax;
                }

                lastTollTime = dateTime;
            }

            totalTax = Math.min(totalTax + highestTollInWindow, maxDailyTax);
            response.setTaxAmount(totalTax);
            return response;
            
    	}catch(Exception e) {
    		
    		response.setMessage(e.getMessage());
    		return response;
			
    	}
    }

    // Helper method to get the tax for a given time based on the city's time intervals
    private double getTaxForTime(LocalDateTime dateTime, JsonNode timeIntervals) {
        for (JsonNode interval : timeIntervals) {
            if (DateUtils.isWithinTimeRange(dateTime, interval.get("start").asText(), interval.get("end").asText())) {
                return interval.get("amount").asDouble();
            }
        }
        return 0;
    }
}
