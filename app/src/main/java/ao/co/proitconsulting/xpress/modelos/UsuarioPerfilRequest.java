package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UsuarioPerfilRequest implements Serializable {

    @SerializedName("primeiroNome")
    public String primeiroNome;

    @SerializedName("ultimoNome")
    public String ultimoNome;

    @SerializedName("contactoAlternativo")
    public String contactoAlternativo;

    @SerializedName("provincia")
    public String provincia;

    @SerializedName("municipio")
    public String municipio;

    @SerializedName("bairro")
    public String bairro;

    @SerializedName("rua")
    public String rua;

    @SerializedName("nCasa")
    public String nCasa;

    @SerializedName("sexo")
    public String sexo;

}
