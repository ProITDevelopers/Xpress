package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TipoDeEstabelecimento implements Serializable {

    @SerializedName("idTipo")
    public int idTipo;

    @SerializedName("descricao")
    public String descricao;

}
