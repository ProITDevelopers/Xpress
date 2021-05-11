package ao.co.proitconsulting.xpress.fragmentos.produtos_detail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.modelos.Produtos;

public class ProdutoDetailViewModel extends ViewModel {

    private MutableLiveData<Produtos> mutableLiveDataProduto;

    public ProdutoDetailViewModel() {
    }


    public MutableLiveData<Produtos> getProdutoMutableLiveData() {
        if (mutableLiveDataProduto == null)
            mutableLiveDataProduto = new MutableLiveData<>();
        mutableLiveDataProduto.setValue(Common.selectedProduto);

        return mutableLiveDataProduto;
    }
}
