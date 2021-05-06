package ao.co.proitconsulting.xpress.fragmentos.estab;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.Callback.ISlideCallbackListener;
import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.modelos.CategoriaEstabelecimento;
import ao.co.proitconsulting.xpress.modelos.TopSlideImages;

public class CategoryEstabViewModel extends ViewModel implements ISlideCallbackListener {

    private MutableLiveData<List<TopSlideImages>> listMutableLiveData;
    private ISlideCallbackListener slideCallbackListener;

    private MutableLiveData<CategoriaEstabelecimento> mutableLiveDataCatEstab;

    public CategoryEstabViewModel() {
        slideCallbackListener = this;
    }

    public MutableLiveData<List<TopSlideImages>> getListMutableLiveData() {
        if (listMutableLiveData == null){
            listMutableLiveData = new MutableLiveData<>();
            loadTopImagesList();
        }
        return listMutableLiveData;
    }

    private void loadTopImagesList() {

        List<TopSlideImages> catEstabTopSlideList = new ArrayList<>();
        catEstabTopSlideList.add(new TopSlideImages(R.drawable.img_top1));
        catEstabTopSlideList.add(new TopSlideImages(R.drawable.img_top2));
        slideCallbackListener.onSlideLoadSuccess(catEstabTopSlideList);

    }

    @Override
    public void onSlideLoadSuccess(List<TopSlideImages> topSlideImages) {
        listMutableLiveData.setValue(topSlideImages);
    }

    @Override
    public void onSlideLoadFailed(String message) {

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
