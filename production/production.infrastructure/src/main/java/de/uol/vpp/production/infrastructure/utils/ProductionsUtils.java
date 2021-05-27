package de.uol.vpp.production.infrastructure.utils;

/**
 * Berechnungsformel für die Erstellung der Erzeugungswerte
 */
public class ProductionsUtils {

    /**
     * Berechnet die Leistung einer Windkraftanlage
     *
     * @param radius     Radius des Windrads in Meter
     * @param windSpeed  Windgeschwindigkeit in m/s
     * @param efficiency Wirkungsgrad in Prozent
     * @return Leistung der Windkraftanlage in kW
     */
    public static Double calculateWind(Double radius, Double windSpeed, Double efficiency) {
        double AIR_DENSITY = 1.225;
        return ((Math.PI / 2) * Math.pow(radius, 2) * Math.pow(windSpeed, 3) * AIR_DENSITY * (efficiency / 100)) / 1000;
    }

    /**
     * Berechnung der Leistung eines Wasserkraftwerks
     *
     * @param fallHeight effektive Fallhöhe in m
     * @param gravity    Fallgeschwindigkeit in m/s
     * @param waterTight Wasserdichte in kg/m^3as
     * @param efficiency Wirkungsgrad in %
     * @param volumeFlow Volumenstrom m^3/s
     * @return Leistung der Wasserkraftwerks in kW
     */
    public static Double calculateWater(Double fallHeight, Double gravity, Double waterTight, Double efficiency, Double volumeFlow) {
        return (efficiency / 100 * waterTight * gravity * fallHeight * volumeFlow) / 1000;
    }
}
