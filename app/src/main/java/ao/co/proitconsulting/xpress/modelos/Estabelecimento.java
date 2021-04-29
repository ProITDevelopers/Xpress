package ao.co.proitconsulting.xpress.modelos;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Estabelecimento implements Serializable {


    @SerializedName("estabelecimentoID")
    public int estabelecimentoID;

    @SerializedName("nomeEstabelecimento")
    public String nomeEstabelecimento;

    @SerializedName("descricao")
    public String descricao;

    @SerializedName("contacto1")
    public String contacto1;

    @SerializedName("contacto2")
    public String contacto2;

    @SerializedName("logotipo")
    public String logotipo;

    @SerializedName("imagemCapa")
    public String imagemCapa;

    @SerializedName("latitude")
    public String latitude;

    @SerializedName("longitude")
    public String longitude;

    @SerializedName("tipoDeEstabelecimento")
    public TipoDeEstabelecimento tipoDeEstabelecimento;


    @SerializedName("endereco")
    public String endereco;

    @SerializedName("estadoEstabelecimento")
    public String estadoEstabelecimento;


    @SerializedName("estadoAtendimento")
    public String estadoAtendimento;

    public Estabelecimento() {}



}
