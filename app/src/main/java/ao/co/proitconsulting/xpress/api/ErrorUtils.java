package ao.co.proitconsulting.xpress.api;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class ErrorUtils {

    public static ErrorResponce parseError (Response<?> response){
//        Converter<ResponseBody, ErrorResponce> conversorDeErro = ApiClient.apiClient()
//                .responseBodyConverter(ErrorResponce.class , new Annotation[0]);

        Converter<ResponseBody, ErrorResponce> conversorDeErro = ApiClient.getClient()
                .responseBodyConverter(ErrorResponce.class , new Annotation[0]);


        ErrorResponce errorResponce = null;
        try{
            if (response.errorBody() != null) {
                errorResponce = conversorDeErro.convert(response.errorBody());
            }
        }catch (IOException e){
            return new ErrorResponce();
        }finally {
            return errorResponce;
        }
    }
}