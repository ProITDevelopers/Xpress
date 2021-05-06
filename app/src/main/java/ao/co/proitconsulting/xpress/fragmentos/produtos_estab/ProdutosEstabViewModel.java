package ao.co.proitconsulting.xpress.fragmentos.produtos_estab;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.Callback.ISlideCallbackListener;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.modelos.Estabelecimento;
import ao.co.proitconsulting.xpress.modelos.TopSlideImages;

public class ProdutosEstabViewModel extends ViewModel implements ISlideCallbackListener {

    private MutableLiveData<List<TopSlideImages>> listMutableLiveData;
    private ISlideCallbackListener slideCallbackListener;
    private MutableLiveData<Estabelecimento> mutableLiveDataEstab;

    public ProdutosEstabViewModel() {
        slideCallbackListener = this;
    }

    public MutableLiveData<List<TopSlideImages>> getTopImagesMutableLiveData() {
        if (listMutableLiveData == null){
            listMutableLiveData = new MutableLiveData<>();
            loadTopImagesList();
        }
        return listMutableLiveData;
    }

    private void loadTopImagesList() {

        List<TopSlideImages> topSlideImages = new ArrayList<>();
        topSlideImages.add(new TopSlideImages(Common.selectedEstab.logotipo));
        topSlideImages.add(new TopSlideImages(Common.selectedEstab.imagemCapa));
        slideCallbackListener.onSlideLoadSuccess(topSlideImages);

    }

    @Override
    public void onSlideLoadSuccess(List<TopSlideImages> topSlideImages) {
        listMutableLiveData.setValue(topSlideImages);
    }

    @Override
    public void onSlideLoadFailed(String message) {

    }

    public MutableLiveData<Estabelecimento> getEstabelecimentoMutableLiveData() {
        if (mutableLiveDataEstab == null)
            mutableLiveDataEstab = new MutableLiveData<>();
        mutableLiveDataEstab.setValue(Common.selectedEstab);

        return mutableLiveDataEstab;
    }
}
