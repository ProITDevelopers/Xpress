package ao.co.proitconsulting.xpress.api.ADAO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClientADAO {


//    private static final String BASE_URL_XPRESS = "http://ec2-18-188-197-193.us-east-2.compute.amazonaws.com:8083/";
    private static final String BASE_URL_XPRESS_ADAO = "https://apitaxas.lengueno.com/";
    private static Retrofit retrofit = null;
    private static int REQUEST_TIMEOUT = 90;
    private static OkHttpClient okHttpClient;



    public static Retrofit getClient() {

        if (okHttpClient == null)
            initOkHttp();

        if (retrofit == null) {

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();


            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL_XPRESS_ADAO)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }

    private static void initOkHttp() {

        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);

        httpClient.interceptors().add(new AddKEYADAOInterceptor());


        okHttpClient = httpClient.build();


    }




}