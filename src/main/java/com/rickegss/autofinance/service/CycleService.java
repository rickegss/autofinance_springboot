package com.rickegss.autofinance.service;

import com.rickegss.autofinance.entity.User;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class CycleService {

    public FinancialCycle getCurrentCycle(User user, LocalDate today) {
        int cutoffDay = user.getFinancialMonthDay(); // valor entre 1 e 28
        LocalDate cycleStart;
        LocalDate cycleEnd;

        if (today.getDayOfMonth() >= cutoffDay) {
            cycleStart = today.withDayOfMonth(cutoffDay);
            cycleEnd = cycleStart.plusMonths(1).minusDays(1);
        } else {
            cycleStart = today.minusMonths(1).withDayOfMonth(cutoffDay);
            cycleEnd = cycleStart.plusMonths(1).minusDays(1);
        }

        // Ajuste para meses que não têm o dia cutoffDay (ex: fevereiro, cutoff=30)
        if (cycleStart.getDayOfMonth() != cutoffDay) {
            cycleStart = cycleStart.withDayOfMonth(cycleStart.lengthOfMonth());
        }
        if (cycleEnd.getDayOfMonth() != cutoffDay - 1 && cutoffDay > 1) {
            cycleEnd = cycleEnd.withDayOfMonth(cycleEnd.lengthOfMonth());
        }

        return new FinancialCycle(cycleStart, cycleEnd);
    }

    public FinancialCycle getCycleContainingDate(User user, LocalDate date) {
        int cutoffDay = user.getFinancialMonthDay();
        LocalDate cycleStart;
        if (date.getDayOfMonth() >= cutoffDay) {
            cycleStart = date.withDayOfMonth(cutoffDay);
        } else {
            cycleStart = date.minusMonths(1).withDayOfMonth(cutoffDay);
        }
        if (cycleStart.getDayOfMonth() != cutoffDay) {
            cycleStart = cycleStart.withDayOfMonth(cycleStart.lengthOfMonth());
        }
        LocalDate cycleEnd = cycleStart.plusMonths(1).minusDays(1);
        if (cycleEnd.getDayOfMonth() != cutoffDay - 1 && cutoffDay > 1) {
            cycleEnd = cycleEnd.withDayOfMonth(cycleEnd.lengthOfMonth());
        }
        return new FinancialCycle(cycleStart, cycleEnd);
    }

    public record FinancialCycle(LocalDate start, LocalDate end) {}
}