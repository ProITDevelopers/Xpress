package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ItensFacturados {

    @SerializedName("produtoId")
    public int produtoId;

    @SerializedName("quantidade")
    public int quantidade;

    @SerializedName("taxaEntrega")
    public float taxaEntrega;

    @SerializedName("ideStabelecimento")
    public int ideStabelecimento;

    @SerializedName("itensExtrasFacturas")
    public List<ItensExtras> itensExtrasList;

}
