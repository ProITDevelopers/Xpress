package ao.co.proitconsulting.xpress.api.ADAO;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetTaxaModel implements Serializable{

    @SerializedName("raio")
    public String raio;

    @SerializedName("taxaFixa")
    public String taxaFixa;

    @SerializedName("idEstabelecimento")
    public String idEstabelecimento;

    @SerializedName("taxaVariavel")
    public String taxaVariavel;

    @SerializedName("distanciaKm")
    public String distanciaKm;

    @SerializedName("valorTaxa")
    public String valorTaxa;


}
