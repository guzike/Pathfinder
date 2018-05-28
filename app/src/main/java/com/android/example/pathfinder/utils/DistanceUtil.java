package com.android.example.pathfinder.utils;

public abstract class DistanceUtil {

    public static String formatDist(double meters) {
        if (meters < 1000) {
            return ((int) meters) + "m";
        } else if (meters < 10000) {
            return formatDec(meters / 1000d, 1) + "km";
        } else {
            return ((int) (meters / 1000d)) + "km";
        }
    }

    private static String formatDec(double val, int dec) {
        int factor = (int) Math.pow(10, dec);

        int front = (int) (val);
        int back = (int) Math.abs(val * (factor)) % factor;

        return front + "." + back;
    }
}
