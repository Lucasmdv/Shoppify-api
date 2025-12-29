package org.stockify.util;

import java.text.Normalizer;

public class StringNormalizer {
    /**
     * Normaliza un string: lo pasa a minúsculas y elimina acentos/diacríticos.
     */
    public static String normalize(String input) {
        if (input == null) return null;
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized.toLowerCase();
    }
}

