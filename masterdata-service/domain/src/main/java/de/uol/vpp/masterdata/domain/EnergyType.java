package de.uol.vpp.masterdata.domain;

public enum EnergyType {
    ELECTRICITY,
    HEAT;

    public static boolean isValid(String value) {
        try {
            Enum.valueOf(EnergyType.class, value);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }
}
