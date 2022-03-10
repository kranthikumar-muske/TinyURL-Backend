package com.tiny.url.lambdas;

public class Stats {
    private int URLAccessedCountPast24Hrs;
    private int URLAccessedCountPastWeek;
    private int URLAccessedCountAllTime;

    public Stats(int URLAccessedCountPast24Hrs, int URLAccessedCountPastWeek, int URLAccessedCountAllTime) {
        this.URLAccessedCountPast24Hrs = URLAccessedCountPast24Hrs;
        this.URLAccessedCountPastWeek = URLAccessedCountPastWeek;
        this.URLAccessedCountAllTime = URLAccessedCountAllTime;
    }

    public int getURLAccessedCountPast24Hrs() {
        return URLAccessedCountPast24Hrs;
    }

    public void setURLAccessedCountPast24Hrs(int URLAccessedCountPast24Hrs) {
        this.URLAccessedCountPast24Hrs = URLAccessedCountPast24Hrs;
    }

    public int getURLAccessedCountPastWeek() {
        return URLAccessedCountPastWeek;
    }

    public void setURLAccessedCountPastWeek(int URLAccessedCountPastWeek) {
        this.URLAccessedCountPastWeek = URLAccessedCountPastWeek;
    }

    public int getURLAccessedCountAllTime() {
        return URLAccessedCountAllTime;
    }

    public void setURLAccessedCountAllTime(int URLAccessedCountAllTime) {
        this.URLAccessedCountAllTime = URLAccessedCountAllTime;
    }
}
