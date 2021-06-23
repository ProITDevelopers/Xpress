package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.List;

public class Factura implements Comparator<Factura> {

    @SerializedName("idFactura")
    public int idFactura;

    @SerializedName("metododPagamento")
    public String metododPagamento;

    @SerializedName("estado")
    public String estado;

    @SerializedName("estadoPagamento")
    public String estadoPagamento;

    @SerializedName("dataPagamento")
    public String dataPagamento;

    @SerializedName("dataPedido")
    public String dataPedido;

    @SerializedName("taxaboy")
    public float taxaEntrega;

    @SerializedName("total")
    public float total;

    @SerializedName("horaEntregueMotoboy")
    public String horaEntregueMotoboy;

    @SerializedName("horaRecebidoCliente")
    public String horaRecebidoCliente;

    @SerializedName("clienteID")
    public int clienteID;

    @SerializedName("identificacaoPagamento")
    public String identificacaoPagamento;

    @SerializedName("entidade")
    public String entidade;

    @SerializedName("itens")
    public List<FacturaItens> itens;

    @SerializedName("localEncomenda")
    public FacturaEncomendaLocal localEncomenda;



    @Override
    public int compare(Factura o1, Factura o2) {
        // Order ascending.
//        int ret = String.valueOf(o1.idFactura).compareTo(String.valueOf(o2.idFactura));
        int ret = o1.idFactura - o2.idFactura;

        return ret;
    }
}
