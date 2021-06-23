package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FacturaItens {

    @SerializedName("idItensFactura")
    public int idItensFactura;

    @SerializedName("produto")
    public String produto;

    @SerializedName("quantidade")
    public int quantidade;

    @SerializedName("imagem")
    public String imagem;

    @SerializedName("itensExtrasFacturas")
    public List<FacturaItensExtras> itensExtrasFacturas;
}
