package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

public class OrderItem {

    @SerializedName("produtoId")
    public int produtoId;

    @SerializedName("quantidade")
    public int quantidade;

    @SerializedName("taxaEntrega")
    public float taxaEntrega;


    @SerializedName("ideStabelecimento")
    public int ideStabelecimento;
}
