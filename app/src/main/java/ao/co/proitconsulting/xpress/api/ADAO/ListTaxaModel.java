package ao.co.proitconsulting.xpress.api.ADAO;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ListTaxaModel implements Serializable{

    @SerializedName("idEstabelecimento")
    public String idEstabelecimento;

    @SerializedName("latitudeOrigem")
    public double latitudeOrigem;

    @SerializedName("longitudeOrgin")
    public double longitudeOrigem;

    @SerializedName("latitudeDestino")
    public double latitudeDestino;

    @SerializedName("longitudeDestino")
    public double longitudeDestino;




}
