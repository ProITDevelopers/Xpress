package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FaceBookLoginRequest implements Serializable {
    @SerializedName("accessToken")
    public String accessToken;
}
