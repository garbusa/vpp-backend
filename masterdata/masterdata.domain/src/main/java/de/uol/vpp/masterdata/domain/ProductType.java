package de.uol.vpp.masterdata.domain;

public enum ProductType {
    WIND,
    SOLAR,
    WATER,
    BIO;

    public static boolean isValid(String value) {
        try {
            Enum.valueOf(ProductType.class, value);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }
}