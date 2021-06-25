package ao.co.proitconsulting.xpress.fragmentos.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.Callback.ISlideCallbackListener;
import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.api.ADAO.ApiClientADAO;
import ao.co.proitconsulting.xpress.api.ADAO.ApiInterfaceADAO;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
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
            if (MetodosUsados.isConnected(10000)){
                loadTopImagesList();
            }else{
                loadLocalTopImagesList();
            }

        }
        return listMutableLiveData;
    }

    private void loadLocalTopImagesList() {
        List<TopSlideImages> homeTopSlideList = new ArrayList<>();
        homeTopSlideList.add(new TopSlideImages(R.drawable.img_top1));
        homeTopSlideList.add(new TopSlideImages(R.drawable.img_top2));
        slideCallbackListener.onSlideLoadSuccess(homeTopSlideList);
    }

    private void loadTopImagesList() {

        List<TopSlideImages>temp = new ArrayList<>();
        ApiInterfaceADAO apiInterface = ApiClientADAO.getClient(Common.BASE_URL_XPRESS_ADAO_TAXA).create(ApiInterfaceADAO.class);
        Call<List<TopSlideImages>> getTopImageList = apiInterface.getTopSlideImagesList();
        getTopImageList.enqueue(new Callback<List<TopSlideImages>>() {
            @Override
            public void onResponse(@NonNull Call<List<TopSlideImages>> call, @NonNull Response<List<TopSlideImages>> response) {


                if (response.isSuccessful()) {

                    if (response.body()!=null && response.body().size()>0){


                        for (TopSlideImages topSlideImages :response.body()) {
                            temp.add(topSlideImages);
                        }

                        slideCallbackListener.onSlideLoadSuccess(temp);



                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TopSlideImages>> call, @NonNull Throwable t) {

                if (!MetodosUsados.isConnected(10000)){
                    slideCallbackListener.onSlideLoadFailed("O dispositivo não está conectado a nenhuma rede 3G ou WI-FI.");
                }else  if (t.getMessage().contains("timeout")) {
                    slideCallbackListener.onSlideLoadFailed("O tempo de comunicação excedeu. Possivelmente a internet está lenta.");
                }else {
                    slideCallbackListener.onSlideLoadFailed("Algum problema ocorreu. Relate o problema.");

                }



            }
        });

    }

    @Override
    public void onSlideLoadSuccess(List<TopSlideImages> topSlideImages) {
        listMutableLiveData.setValue(topSlideImages);
    }

    @Override
    public void onSlideLoadFailed(String message) {

    }
}
