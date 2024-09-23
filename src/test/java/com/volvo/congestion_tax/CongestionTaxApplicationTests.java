package com.volvo.congestion_tax;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.volvo.congestion_tax.model.TaxResponse;
import com.volvo.congestion_tax.service.TaxService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class CongestionTaxApplicationTests {

    @InjectMocks
    private TaxService taxService; // Tax service we are testing

    @MockBean
    private ObjectMapper objectMapper; // Mock for reading JSON rules

    private JsonNode cityRules;

    @BeforeEach
    public void setup() throws Exception {
        // Setup mocks and data
        String json = """
        {
            "taxExemptVehicles": ["emergency", "bus", "diplomat", "motorcycle", "military", "foreign"],
            "yearScope": 2013,
            "maxDailyTax": 60,
            "publicHolidays": ["2013-01-01", "2013-12-25"],
            "daysBeforePublicHoliday": ["2013-12-24"],
            "julyExempt": true,
            "timeIntervals": [
                { "start": "06:00", "end": "06:29", "amount": 8 },
                { "start": "06:30", "end": "06:59", "amount": 13 },
                { "start": "07:00", "end": "07:59", "amount": 18 },
                { "start": "08:00", "end": "08:29", "amount": 13 },
                { "start": "08:30", "end": "14:59", "amount": 8 },
                { "start": "15:00", "end": "15:29", "amount": 13 },
                { "start": "15:30", "end": "16:59", "amount": 18 },
                { "start": "17:00", "end": "17:59", "amount": 13 },
                { "start": "18:00", "end": "18:29", "amount": 8 },
                { "start": "18:30", "end": "05:59", "amount": 0 }
            ]
        }
        """;

        ObjectMapper mapper = new ObjectMapper();
        cityRules = mapper.readTree(json);

        // Mock the method to return the mocked JSON
        when(objectMapper.readTree(anyString())).thenReturn(cityRules);
    }

    @Test
    void testCalculateTotalTax_forExemptVehicle() {
        List<String> timestamps = Arrays.asList(
            "2013-02-08 06:27:00", "2013-02-08 16:48:00"
        );

        // Calculate tax for an exempt vehicle
        TaxResponse result = taxService.calculateTotalTax(timestamps, "gothenburg", "bus");

        // Assert no tax is charged for exempt vehicle
        assertEquals(0.0, result.getTaxAmount());
    }

    @Test
    void testCalculateTotalTax_forNonExemptVehicle() {
        List<String> timestamps = Arrays.asList(
            "2013-02-08 06:27:00", "2013-02-08 16:48:00"
        );

        // Calculate tax for a non-exempt vehicle (e.g., car)
        TaxResponse result = taxService.calculateTotalTax(timestamps, "gothenburg", "car");

        // Assert the correct amount is charged
        assertEquals(26.0, result.getTaxAmount()); // From two intervals: 8 SEK and 18 SEK
    }

    @Test
    void testCalculateTotalTax_withMaxDailyTaxCap() {
        List<String> timestamps = Arrays.asList(
            "2013-02-08 06:27:00", "2013-02-08 07:27:00", "2013-02-08 12:48:00", "2013-02-08 15:48:00", "2013-02-08 17:48:00"
        );

        // Calculate tax and ensure it does not exceed the daily max cap of 60 SEK
        TaxResponse result = taxService.calculateTotalTax(timestamps, "gothenburg", "car");

        // Assert max daily tax is applied
        assertEquals(60.0, result.getTaxAmount());
    }

    @Test
    void testCalculateTotalTax_onPublicHoliday() {
        List<String> timestamps = Arrays.asList(
            "2013-12-25 06:27:00" // Public holiday (Christmas day)
        );

        // Calculate tax for a non-exempt vehicle
        TaxResponse result = taxService.calculateTotalTax(timestamps, "gothenburg", "car");

        // Assert no tax is charged on public holidays
        assertEquals(0.0, result.getTaxAmount());
    }

    @Test
    void testCalculateTotalTax_inJulyExemptMonth() {
        List<String> timestamps = Arrays.asList(
            "2013-07-15 06:27:00" // Date in July (tax exempt month)
        );

        // Calculate tax for a non-exempt vehicle
        TaxResponse result = taxService.calculateTotalTax(timestamps, "gothenburg", "car");

        // Assert no tax is charged in July
        assertEquals(0.0, result.getTaxAmount());
    }

    @Test
    void testCalculateTotalTax_withSingleChargeRule() {
        List<String> timestamps = Arrays.asList(
            "2013-02-08 06:27:00", "2013-02-08 06:55:00" // Both within 60 minutes
        );

        // Calculate tax for a non-exempt vehicle
        TaxResponse result = taxService.calculateTotalTax(timestamps, "gothenburg", "car");

        // Assert the highest amount is charged (single charge rule)
        assertEquals(13.0, result.getTaxAmount());
    }
    
}

