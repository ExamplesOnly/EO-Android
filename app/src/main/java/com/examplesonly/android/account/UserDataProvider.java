package com.examplesonly.android.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.examplesonly.android.R;
import com.examplesonly.android.handler.UserAccountHandler;
import com.examplesonly.android.model.User;
import com.examplesonly.android.network.auth.AuthInterface;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class UserDataProvider {

    private static final String USER_DATA_PREF = "user_data";
    public static final String TOKEN_KEY = "token";

    public static final String ACCESS_TOKEN_KEY = "access_token";
    public static final String REFRESH_TOKEN_KEY = "refresh_token";

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
    private final Context context;
    private GoogleSignInClient googleSignInClient;
    GoogleSignInOptions signInOptions;

    private User user;
    private static UserDataProvider userDataProvider;

    private UserDataProvider(Context context) {
        this.context = context.getApplicationContext();
        this.sharedPreferences = context.getSharedPreferences(USER_DATA_PREF, Context.MODE_PRIVATE);
        setupGoogleAuth();
    }

    public static UserDataProvider getInstance(Context context) {
        if (userDataProvider == null) {
            userDataProvider = new UserDataProvider(context);
        }
        return userDataProvider;
    }

    private void setupGoogleAuth() {
        Scope genderScope = new Scope("https://www.googleapis.com/auth/user.gender.read");
        Scope birthdayScope = new Scope("https://www.googleapis.com/auth/user.birthday.read");

        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(genderScope, birthdayScope)
                .requestIdToken(context.getString(R.string.google_server_client_id))
                .requestServerAuthCode(context.getString(R.string.google_server_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        googleSignInClient = GoogleSignIn.getClient(context, signInOptions);
    }


    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    public GoogleSignInOptions getSignInOptions() {
        return signInOptions;
    }

    public String getAccessToken() {
        return sharedPreferences.getString(ACCESS_TOKEN_KEY, null);
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(REFRESH_TOKEN_KEY, null);
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
        return (!TextUtils.isEmpty(getAccessToken()) && !TextUtils.isEmpty(getRefreshToken()));
    }

    public boolean isVerified() {
        return getVerificationStatus();
    }

    public void saveToken(String authToken, String sessionToken) {
        sharedPreferences.edit()
                .putString(ACCESS_TOKEN_KEY, authToken).apply();
        sharedPreferences.edit()
                .putString(REFRESH_TOKEN_KEY, sessionToken).apply();
    }

    public void setAccessToken(String authToken) {
        sharedPreferences.edit()
                .putString(ACCESS_TOKEN_KEY, authToken).apply();
    }

    public void setRefreshToken(String refreshToken) {
        sharedPreferences.edit()
                .putString(REFRESH_TOKEN_KEY, refreshToken).apply();
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
                "AccessToken: " + getAccessToken() + "\n" +
                "RefreshToken: " + getRefreshToken() + "\n";
    }

    public void clearUserData() {
        Timber.e("UserDataProvider: clearUserData");
        sharedPreferences.edit()
                .putString(TOKEN_KEY, null)
                .putString(ACCESS_TOKEN_KEY, null)
                .putString(REFRESH_TOKEN_KEY, null)
                .putString(USER_FIRST_NAME_KEY, null)
                .putString(USER_LAST_NAME_KEY, null)
                .putString(USER_EMAIL_KEY, null)
                .putString(USER_GENDER_KEY, null)
                .putBoolean(USER_VERIFICATION_KEY, false).apply();
    }

    private boolean validateToken() {
        Timber.e("UserDataProvider: validateToken");
        String token = userDataProvider.getAccessToken();
        try {
            DecodedJWT jwt = JWT.decode(token);
            if (jwt.getExpiresAt().before(new Date())) {
                return false;
            } else {
                return true;
            }
        } catch (Exception exception) {
            return false;
        }
    }

    public void logout(AuthInterface authInterface, UserAccountHandler accountHandler) {
        String sessionToken = getRefreshToken();
        this.clearUserData();
        authInterface.logout(sessionToken).enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(@NotNull Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                if (accountHandler != null)
                    accountHandler.onLogout(true);
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                if (accountHandler != null)
                    accountHandler.onLogout(false);
            }
        });
    }

}
