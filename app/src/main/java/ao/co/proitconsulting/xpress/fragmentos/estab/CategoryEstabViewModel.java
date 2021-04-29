package ao.co.proitconsulting.xpress.fragmentos.estab;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.modelos.CategoriaEstabelecimento;

public class CategoryEstabViewModel extends ViewModel {

    private MutableLiveData<CategoriaEstabelecimento> mutableLiveDataCatEstab;

    public CategoryEstabViewModel() {

    }

    public MutableLiveData<CategoriaEstabelecimento> getMutableLiveDataCatEstab() {
        if (mutableLiveDataCatEstab == null)
            mutableLiveDataCatEstab = new MutableLiveData<>();
        mutableLiveDataCatEstab.setValue(Common.selectedCategoryEstab);

        return mutableLiveDataCatEstab;
    }

    public void setCatEstabModel(CategoriaEstabelecimento revistaModel){
        if (mutableLiveDataCatEstab != null)
            mutableLiveDataCatEstab.setValue(revistaModel);
    }
}
