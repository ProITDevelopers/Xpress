package ao.co.proitconsulting.xpress.api.ADAO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterfaceADAO {




    @POST("/api/taxas/calculadora")
    Call<List<GetTaxaModel>> sendListTOGETTaxa(@Body List<ListTaxaModel> listTaxaModelList);






}
