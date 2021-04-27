package de.uol.vpp.production.infrastructure.utils;

public class ProductionsUtils {

    public static Double calculateWind(Double radius, Double windSpeed, Double airDensity, Double efficiency) {
        return ((Math.PI / 2) * Math.pow(radius, 2) * Math.pow(windSpeed, 3) * airDensity * (efficiency / 100)) / 1000;
    }

    public static Double calculateWater(Double fallHeight, Double gravity, Double waterTight, Double efficiency, Double volumeFlow) {
        return efficiency / 100 * waterTight / 1000 * gravity * fallHeight * volumeFlow;
    }

    public static Double calculateAirDensity(Double airPressure, Double airHumidity, Double temperatureCelsius) {
        double relativeHumidity = 287.058 / (1 - (airHumidity / 100) * 12.74 / airPressure * (1 - 287.058 / 461.523));
        return (airPressure / (relativeHumidity * (temperatureCelsius + 273.15)) * 100);
    }
}
