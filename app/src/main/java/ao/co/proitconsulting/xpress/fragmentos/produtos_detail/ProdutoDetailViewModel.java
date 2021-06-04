package ao.co.proitconsulting.xpress.fragmentos.produtos_detail;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.List;

import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.localDB.AppDatabase;
import ao.co.proitconsulting.xpress.modelos.ProdutoListExtras;
import ao.co.proitconsulting.xpress.modelos.Produtos;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProdutoDetailViewModel extends ViewModel {

    private MutableLiveData<Produtos> mutableLiveDataProduto;
    private MutableLiveData<String> messageError;

    public ProdutoDetailViewModel() {
    }


    public MutableLiveData<Produtos> getProdutoMutableLiveData() {
        if (mutableLiveDataProduto == null){
            mutableLiveDataProduto = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            if (Common.selectedProduto.produtoListExtras == null){
                carregarProductsListExtras();
            }else{

                mutableLiveDataProduto.setValue(Common.selectedProduto);
            }
        }

        return mutableLiveDataProduto;
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    public void setMutableLiveDataProduto(MutableLiveData<Produtos> mutableLiveDataProduto) {
        this.mutableLiveDataProduto = mutableLiveDataProduto;
    }

    private void carregarProductsListExtras() {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<ProdutoListExtras>> rv = apiInterface.getProdutosExtras(Common.selectedProduto.idProduto);
        rv.enqueue(new Callback<List<ProdutoListExtras>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProdutoListExtras>> call, @NonNull Response<List<ProdutoListExtras>> response) {


                if (response.isSuccessful()) {

                    if (response.body()!=null && response.body().size()>0){

                        Common.selectedProduto.produtoListExtras = new RealmList<>();
                        Common.selectedProduto.produtoListExtras.addAll(response.body());
                        AppDatabase.saveProductsExtras(response.body());

                        AppDatabase.updateSingleProduct(Common.selectedProduto);

                        mutableLiveDataProduto.setValue(Common.selectedProduto);



                    }else {

                        mutableLiveDataProduto.setValue(Common.selectedProduto);
                        messageError.setValue("Produto sem extras.");

                    }

                } else {

                    try {
                        String errorMessage = response.errorBody().string();
                        messageError.setValue(errorMessage.concat(", ResponseCode: "+response.code()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProdutoListExtras>> call, @NonNull Throwable t) {

                messageError.setValue(t.getMessage());


            }
        });
    }
}
