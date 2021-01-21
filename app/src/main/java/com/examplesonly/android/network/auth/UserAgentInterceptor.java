package com.examplesonly.android.network.auth;

import android.os.Build;

import com.examplesonly.android.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class UserAgentInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        JSONObject eoAgent = new JSONObject();
        try {
            eoAgent.put("platform", "Android");
            eoAgent.put("model", Build.MODEL);
            eoAgent.put("manufacture", Build.MANUFACTURER);
            eoAgent.put("eo_version_code", BuildConfig.VERSION_CODE);
            eoAgent.put("eo_version_name", BuildConfig.VERSION_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Timber.e("EO-Agent %s", eoAgent.toString());

        Request authenticatedRequest = chain.request()
                .newBuilder()
                .addHeader("User-Agent", System.getProperty("http.agent"))
                .addHeader("EO-Agent", eoAgent.toString())
                .build();
        return chain.proceed(authenticatedRequest);
    }
}
