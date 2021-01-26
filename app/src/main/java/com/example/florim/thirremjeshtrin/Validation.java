package com.example.florim.thirremjeshtrin;

import java.util.regex.Pattern;

/**
 * Created by Gresa on 06-Dec-16.
 */

public class Validation {

    public static final String EMAIL_REGEX="[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}";
    public static final String USERNAME_REGEX="^[a-zA-Z0-9]+[a-zA-Z0-9_-]*$";
    public static final String PASSWORD_REGEX="^[A-Za-z\\d]{8,}$";
    public static final String NUMBER_REGEX="^[0-9]+$";


    public static boolean validateData(String Data,String Regex){
        return Pattern.matches(Regex, Data);
    }
}
