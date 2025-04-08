package com.dharmaapp.hellojavafx;

import java.util.ResourceBundle;
import java.util.Locale;

public enum AppLanguage {
    ENGLISH("en", "English"),
    HINDI("hi", "हिन्दी"),
    SANSKRIT("sa", "संस्कृत"),
    MARATHI("mr", "मराठी");

    private final String code;
    private final String displayName;

    AppLanguage(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle("messages", new Locale(code));
    }

    @Override
    public String toString() {
        return displayName;
    }
}

class LanguageManager {
    private static AppLanguage currentLanguage = AppLanguage.ENGLISH;

    public static void setLanguage(AppLanguage language) {
        currentLanguage = language;
        // Here you would update UI components, reload resources, etc.
    }

    public static AppLanguage getCurrentLanguage() {
        return currentLanguage;
    }

    public static ResourceBundle getCurrentResourceBundle() {
        return currentLanguage.getResourceBundle();
    }
}