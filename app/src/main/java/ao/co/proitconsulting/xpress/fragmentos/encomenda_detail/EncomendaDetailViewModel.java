package ao.co.proitconsulting.xpress.fragmentos.encomenda_detail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.modelos.Factura;
import ao.co.proitconsulting.xpress.modelos.Produtos;

public class EncomendaDetailViewModel extends ViewModel {

    private MutableLiveData<Factura> mutableLiveDataEncomenda;

    public EncomendaDetailViewModel() {
    }


    public MutableLiveData<Factura> getEncomendaMutableLiveData() {
        if (mutableLiveDataEncomenda == null)
            mutableLiveDataEncomenda = new MutableLiveData<>();
        mutableLiveDataEncomenda.setValue(Common.selectedFactura);

        return mutableLiveDataEncomenda;
    }
}
