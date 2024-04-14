package me.knighthat.api.utils;

import me.knighthat.api.v2.Env;

import java.util.Locale;

public class SystemInfo {

    public static String countryCode() {
        String defCountry = Env.VariableNames.DEFAULT_COUNTRY.get();
        if ( defCountry != null && !defCountry.isBlank() )
            return defCountry;

        defCountry = Locale.getDefault().getCountry();
        if ( defCountry.isBlank() )
            defCountry = "US";

        return defCountry;
    }
}
