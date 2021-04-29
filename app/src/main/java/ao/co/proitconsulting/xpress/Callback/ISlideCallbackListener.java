package ao.co.proitconsulting.xpress.Callback;

import java.util.List;

import ao.co.proitconsulting.xpress.modelos.TopSlideImages;

public interface ISlideCallbackListener {
    void onSlideLoadSuccess(List<TopSlideImages> topSlideImages);
    void onSlideLoadFailed(String message);
}
