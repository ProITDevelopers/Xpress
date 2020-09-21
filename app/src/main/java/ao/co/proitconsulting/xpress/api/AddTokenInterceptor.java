package ao.co.proitconsulting.xpress.api;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.IOException;

import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddTokenInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        String token = AppPrefsSettings.getInstance().getAuthToken();
        builder.addHeader("Content-type", "application/json");
        builder.addHeader("Accept", "application/json");

        if (!TextUtils.isEmpty(token)) {
            builder.header("Authorization", "Bearer " + token);
        }
        return chain.proceed(builder.build());
    }
}
