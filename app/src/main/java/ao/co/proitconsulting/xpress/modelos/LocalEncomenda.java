package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LocalEncomenda implements Serializable {

    @SerializedName("nTelefone")
    public String nTelefone;

    @SerializedName("pontodeReferencia")
    public String pontodeReferencia;

    @SerializedName("latitude")
    public String latitude;

    @SerializedName("longitude")
    public String longitude;

    public LocalEncomenda() {}
}
