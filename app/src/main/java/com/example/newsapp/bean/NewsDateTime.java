package com.example.newsapp.bean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;

public class NewsDateTime {
//    private LocalDateTime localDateTime;
    private LocalDate date;
    private  LocalTime time;

    public NewsDateTime() { this(LocalDate.now()); }
    public NewsDateTime(@NonNull LocalDate date) { this(date, null); }
    public NewsDateTime(@NonNull LocalDate date, @Nullable LocalTime time) {
        this.date = date;
        this.time = time;
    }

    public NewsDateTime(int year, int month, int day, int hours, int minutes, int seconds) {
        date = LocalDate.of(year, month, day);
        time = LocalTime.of(hours, minutes, seconds);
    }
    public NewsDateTime(int year, int month, int day, int hours, int minutes) { this(year, month, day, hours, minutes, 0); }
    public NewsDateTime(int year, int month, int day, int hours) { this(year, month, day, hours, 0); }

    public NewsDateTime(int year, int month, int day) { this(LocalDate.of(year, month, day)); }
    public NewsDateTime(int year, int month) { this(year, month, 1); }

    public static NewsDateTime parse(String s) {
        String[] datetime = s.split(" ");
        return new NewsDateTime(LocalDate.parse(datetime[0]), LocalTime.parse(datetime[1]));
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder(date.toString());
        if (time != null) {
            ret.append(" ").append(String.format(Locale.SIMPLIFIED_CHINESE, "%02d", time.getHour())).append(":")
                    .append(String.format(Locale.SIMPLIFIED_CHINESE, "%02d", time.getMinute())).append(":")
                    .append(String.format(Locale.SIMPLIFIED_CHINESE, "%02d", time.getSecond()));
        }
        return ret.toString();
    }
}
