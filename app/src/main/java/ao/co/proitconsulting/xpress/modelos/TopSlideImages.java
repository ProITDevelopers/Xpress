package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

public class TopSlideImages {


    public int mImagesRes;

    @SerializedName("imagemUrl")
    public String mImagesLinks;

    public TopSlideImages() {
    }

    public TopSlideImages(String mImagesLinks) {
        this.mImagesLinks = mImagesLinks;
    }

    public TopSlideImages(int mImagesRes) {
        this.mImagesRes = mImagesRes;
    }


}
