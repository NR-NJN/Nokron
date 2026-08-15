package com.portfolio.tracker.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class MathUtils {
    public static double calculateXIRR(double[] amounts, LocalDate[] dates) {
        double rate = 0.10; 
        double tolerance = 0.0000001; 
        int maxIterations = 100;

        for (int i = 0; i < maxIterations; i++) {
            double npv = 0.0;
            double derivative = 0.0;

            for (int j = 0; j < amounts.length; j++) {
                double days = ChronoUnit.DAYS.between(dates[0], dates[j]);
                double years = days / 365.0;
                
                npv += amounts[j] / Math.pow(1.0 + rate, years);
 
                if (years > 0) {
                    derivative -= (years * amounts[j]) / Math.pow(1.0 + rate, years + 1.0);
                }
            }

            double newRate = rate - (npv / derivative);
            if (Math.abs(newRate - rate) < tolerance) {
                return newRate;
            }
            rate = newRate;
        }
        
        throw new RuntimeException("IRR calculation did not converge.");
    }
    
}
