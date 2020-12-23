package com.solvevolve.examplesonly.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.solvevolve.examplesonly.model.User;
public class UserDataProvider {

    private static final String USER_DATA_PREF = "user_data";
    public static final String TOKEN_KEY = "token";

    private static final String USER_UUID = "user_uuid";
    private static final String USER_FIRST_NAME_KEY = "user_first_name";
    private static final String USER_LAST_NAME_KEY = "user_last_name";
    private static final String USER_EMAIL_KEY = "user_email";
    private static final String USER_GENDER_KEY = "user_gender";
    private static final String USER_DOB_KEY = "user_dob";
    private static final String USER_BIO_KEY = "user_bio";
    private static final String USER_VERIFICATION_KEY = "user_verified";
    private static final String USER_PROFILE_PHOTO = "user_profile_photo";
    private static final String USER_COVER_PHOTO = "user_cover_photo";

    private final SharedPreferences sharedPreferences;

    private User user;
    private static UserDataProvider userDataProvider;

    private UserDataProvider(Context context) {
        final Context appContext = context.getApplicationContext();
        this.sharedPreferences = appContext.getSharedPreferences(USER_DATA_PREF, Context.MODE_PRIVATE);
    }

    public static UserDataProvider getInstance(Context context) {
        if (userDataProvider == null) {
            userDataProvider = new UserDataProvider(context);
            return userDataProvider;
        }
        return userDataProvider;
    }

    public String getToken() {
        return sharedPreferences.getString(TOKEN_KEY, null);
    }

    public String getUserUuid() {
        return sharedPreferences.getString(USER_UUID, null);
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

    public String getUserBio() {
        return sharedPreferences.getString(USER_BIO_KEY, null);
    }

    public String getUserDob() {
        return sharedPreferences.getString(USER_DOB_KEY, null);
    }

    public String getUserProfileImage() {
        return sharedPreferences.getString(USER_PROFILE_PHOTO, null);
    }

    public String getUserCoverImage() {
        return sharedPreferences.getString(USER_COVER_PHOTO, null);
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
                .putString(USER_UUID, user.getUuid())
                .putString(USER_FIRST_NAME_KEY, user.getFirstName())
                .putString(USER_LAST_NAME_KEY, user.getLastName())
                .putString(USER_EMAIL_KEY, user.getEmail())
                .putString(USER_GENDER_KEY, user.getGender())
                .putString(USER_DOB_KEY, user.getDob())
                .putString(USER_BIO_KEY, user.getBio())
                .putString(USER_PROFILE_PHOTO, user.getProfilePhoto())
                .putString(USER_COVER_PHOTO, user.getCoverPhoto())
                .putBoolean(USER_VERIFICATION_KEY, user.isEmailVerified()).apply();
        updateCurrentUser();
    }

    public void updateCurrentUser() {
        user = new User()
                .setUuid(getUserUuid())
                .setFirstName(getUserFirstName())
                .setLastName(getUserLastName())
                .setEmail(getUserEmail())
                .setGender(getUserGender())
                .setBio(getUserBio())
                .setEmailVerified(getVerificationStatus())
                .setProfilePhoto(getUserProfileImage())
                .setCoverPhoto(getUserCoverImage());
    }

    public User getCurrentUser() {
        if (user == null) {
            updateCurrentUser();
        }
        return user;
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
