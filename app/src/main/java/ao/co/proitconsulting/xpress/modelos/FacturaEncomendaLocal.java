package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FacturaEncomendaLocal implements Serializable {

    @SerializedName("idlEncomenda")
    public String idlEncomenda;

    @SerializedName("nTelefone")
    public String nTelefone;

    @SerializedName("pontodeReferencia")
    public String pontodeReferencia;

    @SerializedName("latitude")
    public Double latitude;

    @SerializedName("longitude")
    public Double longitude;

    public FacturaEncomendaLocal() {}
}
