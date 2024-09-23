package com.volvo.congestion_tax.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class DateUtils {

    public static boolean isExemptDay(LocalDateTime dateTime, List<String> publicHolidays, List<String> daysBeforeHoliday, boolean julyExempt) {
        LocalDate date = dateTime.toLocalDate();

        // Exempt entire month of July if applicable
        if (julyExempt && date.getMonthValue() == 7) {
            return true;
        }

        // Check if the date is a public holiday
        for (String holiday : publicHolidays) {
            LocalDate holidayDate = LocalDate.parse(holiday);
            if (date.equals(holidayDate)) {
                return true;
            }
        }

        // Check if the date is the day before a public holiday
        for (String dayBeforeHoliday : daysBeforeHoliday) {
            LocalDate dayBeforeHolidayDate = LocalDate.parse(dayBeforeHoliday);
            if (date.equals(dayBeforeHolidayDate)) {
                return true;
            }
        }

        // Check if the date is a weekend
        if (date.getDayOfWeek().getValue() >= 6) {
            return true;
        }

        return false;
    }

    public static boolean within60Minutes(LocalDateTime last, LocalDateTime current) {
        return last.plusMinutes(60).isAfter(current);
    }

    // Other helper methods for time range comparison
    public static boolean isWithinTimeRange(LocalDateTime dateTime, String start, String end) {
        LocalTime startTime = LocalTime.parse(start);
        LocalTime endTime = LocalTime.parse(end);
        LocalTime time = dateTime.toLocalTime();

        return !time.isBefore(startTime) && !time.isAfter(endTime);
    }
}

