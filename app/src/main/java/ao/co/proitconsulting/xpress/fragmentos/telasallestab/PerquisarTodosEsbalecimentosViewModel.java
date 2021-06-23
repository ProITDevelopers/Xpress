package ao.co.proitconsulting.xpress.fragmentos.telasallestab;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.modelos.Estabelecimento;

public class PerquisarTodosEsbalecimentosViewModel extends ViewModel {

    private MutableLiveData<List<Estabelecimento>> mutableLiveData_ALLEstab;

    public PerquisarTodosEsbalecimentosViewModel() {
    }

    public MutableLiveData<List<Estabelecimento>> getMutableLiveData_ALLEstab() {
        if (mutableLiveData_ALLEstab == null)
            mutableLiveData_ALLEstab = new MutableLiveData<>();
        mutableLiveData_ALLEstab.setValue(Common.todosEstabelecimentoList);

        return mutableLiveData_ALLEstab;
    }
}
