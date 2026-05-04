package com.ror.gameutil;


public enum AiDifficulty {
    EASY,
    MEDIUM;


    public static AiDifficulty from(String raw) {
        if (raw == null) return MEDIUM;
        switch (raw.trim().toUpperCase()) {
            case "EASY":   return EASY;
            case "MEDIUM": return MEDIUM;
            default:       return MEDIUM;
        }
    }
}