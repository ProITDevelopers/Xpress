package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

public class FacturaItensExtras {

    @SerializedName("iDextrafactura")
    public int iDextrafactura;

    @SerializedName("nomeExtra")
    public String nomeExtra;

    @SerializedName("preco")
    public Double preco;

    @SerializedName("quantidade")
    public int quantidade;

}
