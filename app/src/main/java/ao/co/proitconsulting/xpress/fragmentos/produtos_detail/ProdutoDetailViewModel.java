package ao.co.proitconsulting.xpress.fragmentos.produtos_detail;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.Callback.IProdutoCallbackListener;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.modelos.ProdutoListExtras;
import ao.co.proitconsulting.xpress.modelos.Produtos;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProdutoDetailViewModel extends ViewModel implements IProdutoCallbackListener{

    private MutableLiveData<Produtos> mutableLiveDataProduto;
    private MutableLiveData<String> messageError;
    private IProdutoCallbackListener produtoCallbackListener;

    public ProdutoDetailViewModel() {
        produtoCallbackListener = this;
    }


    public MutableLiveData<Produtos> getProdutoMutableLiveData() {
        if (mutableLiveDataProduto == null){
            mutableLiveDataProduto = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            if (Common.selectedProduto.getProdutoExtrasList() == null){
                if (MetodosUsados.isConnected(10000)){
                    carregarProductsListExtras();
                }else{
                    produtoCallbackListener.onProdutoLoadSuccess(Common.selectedProduto);
                    produtoCallbackListener.onProdutoLoadFailed("O dispositivo não está conectado a nenhuma rede 3G ou WI-FI.");
                }
            }else{

                produtoCallbackListener.onProdutoLoadSuccess(Common.selectedProduto);
//                mutableLiveDataProduto.setValue(Common.selectedProduto);
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
        Call<List<ProdutoListExtras>> rv = apiInterface.getProdutosExtras(Common.selectedProduto.getIdProduto());
        rv.enqueue(new Callback<List<ProdutoListExtras>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProdutoListExtras>> call, @NonNull Response<List<ProdutoListExtras>> response) {


                if (response.isSuccessful()) {

                    if (response.body()!=null && response.body().size()>0){

                        Common.selectedProduto.setProdutoExtrasList(new ArrayList<>());

                        for (ProdutoListExtras produtoListExtra:response.body()) {
                            Common.selectedProduto.getProdutoExtrasList().add(produtoListExtra.produtoextras);
                        }


                        produtoCallbackListener.onProdutoLoadSuccess(Common.selectedProduto);

//                        mutableLiveDataProduto.setValue(Common.selectedProduto);



                    }else {
                        produtoCallbackListener.onProdutoLoadSuccess(Common.selectedProduto);
//                        mutableLiveDataProduto.setValue(Common.selectedProduto);


                    }

                } else {

                    try {
                        String errorMessage = response.errorBody().string();
//                        messageError.setValue(errorMessage.concat(", ResponseCode: "+response.code()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (response.code() != 401){
//                        messageError.setValue("Algum problema ocorreu. Relate o problema.");
                        produtoCallbackListener.onProdutoLoadFailed("Algum problema ocorreu. Relate o problema.");
                    }


                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProdutoListExtras>> call, @NonNull Throwable t) {

                if (!MetodosUsados.isConnected(10000)){
                    produtoCallbackListener.onProdutoLoadFailed("O dispositivo não está conectado a nenhuma rede 3G ou WI-FI.");
                }else  if (t.getMessage().contains("timeout")) {
                    produtoCallbackListener.onProdutoLoadFailed("O tempo de comunicação excedeu. Possivelmente a internet está lenta.");
                }else {
                    produtoCallbackListener.onProdutoLoadFailed("Algum problema ocorreu. Relate o problema.");

                }



            }
        });
    }

    @Override
    public void onProdutoLoadSuccess(Produtos produto) {
        mutableLiveDataProduto.setValue(produto);
    }

    @Override
    public void onProdutoLoadFailed(String message) {
        messageError.setValue(message);
    }
}
