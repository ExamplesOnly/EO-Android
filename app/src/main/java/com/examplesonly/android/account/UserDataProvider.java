package com.examplesonly.android.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.examplesonly.android.model.User;
public class UserDataProvider {

    private static final String USER_DATA_PREF = "user_data";
    public static final String TOKEN_KEY = "token";

    private static final String USER_FIRST_NAME_KEY = "user_first_name";
    private static final String USER_LAST_NAME_KEY = "user_last_name";
    private static final String USER_EMAIL_KEY = "user_email";
    private static final String USER_GENDER_KEY = "user_gender";
    private static final String USER_VERIFICATION_KEY = "user_verified";

    private final SharedPreferences sharedPreferences;

    public UserDataProvider(Context context) {
        this.sharedPreferences = context.getSharedPreferences(USER_DATA_PREF, Context.MODE_PRIVATE);
    }

    public String getToken() {
        return sharedPreferences.getString(TOKEN_KEY, null);
    }

    public String getUserFirstName() {
        return sharedPreferences.getString(USER_FIRST_NAME_KEY, null);
    }

    public String getUserLastName() {
        return sharedPreferences.getString(USER_LAST_NAME_KEY, null);
    }

    public String getUserFullName() {
        return sharedPreferences.getString(USER_FIRST_NAME_KEY, null) + " " + sharedPreferences
                .getString(USER_LAST_NAME_KEY, null);
    }

    public String getUserEmail() {
        return sharedPreferences.getString(USER_EMAIL_KEY, null);
    }

    public String getUserGender() {
        return sharedPreferences.getString(USER_GENDER_KEY, null);
    }

    public boolean getVerificationStatus() {
        return sharedPreferences.getBoolean(USER_VERIFICATION_KEY, false);
    }

    public boolean isAuthorized() {
        return !TextUtils.isEmpty(getToken());
    }

    public boolean isVerified() {
        return getVerificationStatus();
    }

    public void saveToken(String token) {
        sharedPreferences.edit()
                .putString(TOKEN_KEY, token).apply();
    }

    public void saveUserData(User user) {
        sharedPreferences.edit()
                .putString(USER_FIRST_NAME_KEY, user.getFirstName())
                .putString(USER_LAST_NAME_KEY, user.getLastName())
                .putString(USER_EMAIL_KEY, user.getEmail())
                .putString(USER_GENDER_KEY, user.getGender())
                .putBoolean(USER_VERIFICATION_KEY, user.getVerified()).apply();
    }

    public User getCurrentUser() {
        return new User().setFirstName(getUserFirstName())
                .setLastName(getUserLastName())
                .setEmail(getUserEmail())
                .setGender(getUserGender())
                .setVerified(getVerificationStatus());
    }

    public String toString() {
        return "Name: " + getUserFirstName() + " " + getUserLastName() + "\n" +
                "Email: " + getUserEmail() + "\n" +
                "Verified: " + getVerificationStatus() + "\n" +
                "Token: " + getToken();
    }

    public void logout() {
        sharedPreferences.edit()
                .putString(TOKEN_KEY, null)
                .putString(USER_FIRST_NAME_KEY, null)
                .putString(USER_LAST_NAME_KEY, null)
                .putString(USER_EMAIL_KEY, null)
                .putString(USER_GENDER_KEY, null)
                .putBoolean(USER_VERIFICATION_KEY, false).apply();
    }

}
