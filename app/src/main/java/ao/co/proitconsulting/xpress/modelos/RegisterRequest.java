package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RegisterRequest implements Serializable {

    @SerializedName("primeiroNome")
    public String primeiroNome;

    @SerializedName("ultimoNome")
    public String ultimoNome;

    @SerializedName("email")
    public String email;

    @SerializedName("password")
    public String password;

    @SerializedName("contactoMovel")
    public String contactoMovel;

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

    @SerializedName("usuario")
    public String usuario;
}
