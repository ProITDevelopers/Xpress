package ao.co.proitconsulting.xpress.fragmentos.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.Callback.ISlideCallbackListener;
import ao.co.proitconsulting.xpress.modelos.TopSlideImages;

public class HomeViewModel extends ViewModel implements ISlideCallbackListener {

    private MutableLiveData<List<TopSlideImages>> listMutableLiveData;
    private ISlideCallbackListener slideCallbackListener;

    public HomeViewModel() {
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

        List<TopSlideImages> homeTopSlideList = new ArrayList<>();
        homeTopSlideList.add(new TopSlideImages("https://s3.us-east-2.amazonaws.com/xpress-entrega/estafeta/estiloXpress1622828336903Hello_Hanna.png"));
        homeTopSlideList.add(new TopSlideImages("https://s3.us-east-2.amazonaws.com/xpress-entrega/estafeta/laranja1622828574377Mask_Group.png"));
        slideCallbackListener.onSlideLoadSuccess(homeTopSlideList);

    }

    @Override
    public void onSlideLoadSuccess(List<TopSlideImages> topSlideImages) {
        listMutableLiveData.setValue(topSlideImages);
    }

    @Override
    public void onSlideLoadFailed(String message) {

    }
}
