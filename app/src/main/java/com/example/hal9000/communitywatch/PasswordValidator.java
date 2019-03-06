package com.example.hal9000.communitywatch;

public class PasswordValidator
{
    private int PASSWORD_LENGTH = 8;

    public PasswordValidator(){};

    public  boolean is_Valid_Password(String password) {

        if (password.length() < PASSWORD_LENGTH) return false;

        int lowerCharCount = 0;
        int upperCharCount = 0;
        int numCount = 0;

        for (int i = 0; i < password.length(); i++) {

            char ch = password.charAt(i);

            if (is_Numeric(ch)) numCount++;
            else if (is_LowerLetter(ch)) lowerCharCount++;
            else if(is_UpperLetter(ch)) upperCharCount++;
            else return false;
        }


        return (lowerCharCount >= 1 && numCount >= 1 && upperCharCount >= 1);
    }

    private static boolean is_LowerLetter(char ch) {
        return (ch >= 'a' && ch <= 'z');
    }

    private static boolean is_UpperLetter(char ch) {
        return (ch >= 'A' && ch <= 'Z');
    }


    private static boolean is_Numeric(char ch) {

        return (ch >= '0' && ch <= '9');
    }

}
