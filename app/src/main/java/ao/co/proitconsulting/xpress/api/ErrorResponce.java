package ao.co.proitconsulting.xpress.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ErrorResponce {

//    @SerializedName("error")
    @SerializedName("message")
    @Expose
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
