package ao.co.proitconsulting.xpress.fragmentos.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.Callback.ISlideCallbackListener;
import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.modelos.TopSlideImages;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        homeTopSlideList.add(new TopSlideImages(R.drawable.img_top1));
        homeTopSlideList.add(new TopSlideImages(R.drawable.img_top2));
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
