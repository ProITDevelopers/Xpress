package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ReferenciaRequest implements Serializable {

    @SerializedName("id")
    public String id;

    @SerializedName("codigo")
    public String codigo;

    @SerializedName("entidade")
    public String entidade;

    @SerializedName("valor")
    public String valor;

    @SerializedName("dataCriacao")
    public String dataCriacao;

    @SerializedName("dateExpiracao")
    public String dateExpiracao;

    @SerializedName("estado")
    public String estado;

    @SerializedName("nome")
    public String nome;

    @SerializedName("telefone")
    public String telefone;

    @SerializedName("email")
    public String email;

    @SerializedName("descricao")
    public String descricao;

}
