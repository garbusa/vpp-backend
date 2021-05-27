package de.uol.vpp.action.domain.utils;

/**
 * Math. Helferfunktionen
 */
public class MathUtils {

    /**
     * Rundet auf eine Dezimalzahl auf die dritte Kommastelle
     *
     * @param value Dezimalzahl
     * @return gerundete Dezimalzahl
     */
    public static double round(double value) {
        return Math.round(1000. * value) / 1000.;
    }

}
