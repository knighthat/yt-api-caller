package me.knighthat.api.utils;

import java.util.Locale;

public class SystemInfo {

    public static String countryCode() {
        return Locale.getDefault().getCountry();
    }
}
