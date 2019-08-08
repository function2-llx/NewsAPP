package com.example.newsapp.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class NewsDateTime {
//    private LocalDateTime localDateTime;
    private LocalDate date;
    private  LocalTime time;
//    private int year, month, day, hours, minutes, seconds;

    public NewsDateTime(int year, int month, int day, int hours, int minutes, int seconds) {
//        localDateTime = LocalDateTime.of(year, month, day, hours, minutes, seconds);
        date = LocalDate.of(year, month, day);
        if (hours != -1) time = LocalTime.of(hours, minutes, seconds);
    }
    public NewsDateTime(int year, int month, int day, int hours, int minutes) { this(year, month, day, hours, minutes, 0); }
    public NewsDateTime(int year, int month, int day, int hours) { this(year, month, day, hours, 0); }

    public NewsDateTime(int year, int month, int day) { this(year, month, day, -1); }
    public NewsDateTime(int year, int month) { this(year, month, 1); }
    public NewsDateTime() { date = LocalDate.now(); }
    public NewsDateTime(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }

    public static NewsDateTime parse(String s) {
        String[] datetime = s.split(" ");
        return new NewsDateTime(LocalDate.parse(datetime[0]), LocalTime.parse(datetime[1]));
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append(date.getYear())
                .append("-")
                .append(date.getMonthValue())
                .append(date.getDayOfMonth());

        if (time != null) {
            ret.append(time.getHour())
                    .append(time.getMinute())
                    .append(time.getSecond());
        }
        return ret.toString();
    }
}
