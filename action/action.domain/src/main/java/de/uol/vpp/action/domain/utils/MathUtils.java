package de.uol.vpp.action.domain.utils;

public class MathUtils {

    public static double round(double value) {
        return Math.round(1000. * value) / 1000.;
    }

}
