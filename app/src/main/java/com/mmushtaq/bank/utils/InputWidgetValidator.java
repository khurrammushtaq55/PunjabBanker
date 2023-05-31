package com.mmushtaq.bank.utils;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.abs;

public class InputWidgetValidator {

    public static boolean isValidLength(String minLength, String maxLength, String inputText) {
        boolean isValid = true;

        if ( !BaseMethods.Companion.isNullOrEmptyString(inputText) ) {
            if ( !BaseMethods.Companion.isNullOrEmptyString(minLength) ) {
                int min = Integer.parseInt(minLength);
                if ( inputText.length() < min ) {
                    isValid = false;
                }
            }

            if ( !BaseMethods.Companion.isNullOrEmptyString(maxLength) ) {
                int max = Integer.parseInt(maxLength);
                if ( inputText.length() > max ) {
                    isValid = false;
                }
            }
        }

        return isValid;
    }

    public static boolean isValidRegex(String regex, String inputText) {
        if ( BaseMethods.Companion.isNullOrEmptyString(regex) || BaseMethods.Companion.isNullOrEmptyString(inputText) ) {
            return true;
        }
        try {
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(inputText);
            if ( !matcher.matches() ) {
                return inputText.matches(regex);
            }
            return matcher.matches();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidValue(String minValue, String maxValue, String enteredText) {
        boolean isValid = true;
        if ( !BaseMethods.Companion.isNullOrEmptyString(enteredText) ) {
            try {
                double inputText = Double.parseDouble(enteredText);
                    if ( !BaseMethods.Companion.isNullOrEmptyString(minValue) ) {
                        double min = Double.parseDouble(minValue);
                        if ( inputText < min ) {
                            isValid = false;
                        }
                    }

                    if ( !BaseMethods.Companion.isNullOrEmptyString(maxValue) ) {
                        double max = abs(Double.parseDouble(maxValue));
                        if ( inputText > max ) {
                            isValid = false;
                        }
                    }


            } catch (NumberFormatException e) {
                Log.e("NumberFormatException", e.getLocalizedMessage());
                return false;
            }
        }
        return isValid;
    }
}