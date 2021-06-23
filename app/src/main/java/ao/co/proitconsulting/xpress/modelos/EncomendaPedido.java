package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EncomendaPedido {

    @SerializedName("localEncomenda")
    public LocalEncomenda localEncomenda;

    @SerializedName("itensFacturaos")
    public List<ItensFacturados> itensFacturadosList;

    public EncomendaPedido() {
    }
}
