package com.nft.cloud.Utils;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;


public class ValidationChecks
{
    public static boolean validateAnyName(EditText editText, String message) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.setError(message);
            return false;
        } else {
            return true;
        }

    }
    public static boolean validateAnyTextName(TextView textView, String message) {
        if (textView.getText().toString().trim().isEmpty()) {
            textView.setError(message);
            return false;
        } else {
            return true;
        }

    }
    public static boolean validateEmail(EditText editText, String message) {
        String email = editText.getText().toString().trim();

        if (!isValidEmail(email)) {
            editText.setError("please enter valid email");
            return false;
        } else {
            return true;
        }

    }
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean validatePasswordLenght(EditText editText, String message)
    {
        String password = editText.getText().toString().trim();
        if (password.length()<6) {
            editText.setError("Password Lenght Must Me Be More Thank 6 Charachter");
            return false;
        } else {
            return true;
        }
    }


}
