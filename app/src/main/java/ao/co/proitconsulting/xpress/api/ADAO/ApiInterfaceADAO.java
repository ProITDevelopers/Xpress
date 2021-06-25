package ao.co.proitconsulting.xpress.api.ADAO;

import java.util.List;

import ao.co.proitconsulting.xpress.modelos.AdaoTokenAuth;
import ao.co.proitconsulting.xpress.modelos.LoginAdaoRequest;
import ao.co.proitconsulting.xpress.modelos.TopSlideImages;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterfaceADAO {

    @GET("/api/publicidades/ativas")
    Call<List<TopSlideImages>> getTopSlideImagesList();

    @POST("/api/taxas/calculadora")
    Call<List<GetTaxaModel>> sendListTOGETTaxa(@Body List<ListTaxaModel> listTaxaModelList);


    @POST("/api/login/dono/encomenda")
    Call<AdaoTokenAuth> autenticarToGetToken(@Body LoginAdaoRequest loginAdaoRequest);
}
