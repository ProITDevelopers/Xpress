package ao.co.proitconsulting.xpress.api.ADAO;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddKEYADAOInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        builder.addHeader("Content-type", "application/json");
        builder.addHeader("Accept", "application/json");
        builder.header("key", "PIq12oaO9opUyE482pgrY");

        return chain.proceed(builder.build());
    }
}
