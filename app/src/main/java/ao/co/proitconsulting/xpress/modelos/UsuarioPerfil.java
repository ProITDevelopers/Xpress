package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UsuarioPerfil implements Serializable {

    @SerializedName("nomeCompleto")
    public String nomeCompleto;

    @SerializedName("primeiroNome")
    public String primeiroNome;

    @SerializedName("ultimoNome")
    public String ultimoNome;

    @SerializedName("email")
    public String email;


    @SerializedName("nCasa")
    public String nCasa;

    @SerializedName("sexo")
    public String sexo;


    @SerializedName("rua")
    public String rua;

    @SerializedName("bairro")
    public String bairro;

    @SerializedName("municipio")
    public String municipio;

    @SerializedName("provincia")
    public String provincia;

    @SerializedName("contactoMovel")
    public String contactoMovel;

    @SerializedName("contactoAlternativo")
    public String contactoAlternativo;

    @SerializedName("userName")
    public String userName;

    @SerializedName("imagem")
    public String imagem;


    public Wallet carteiraXpress;



    public UsuarioPerfil() {
    }

    public UsuarioPerfil(String primeiroNome, String ultimoNome,String contactoAlternativo, String provincia, String municipio, String bairro, String rua, String nCasa, String sexo) {
        this.primeiroNome = primeiroNome;
        this.ultimoNome = ultimoNome;
        this.contactoAlternativo = contactoAlternativo;
        this.provincia = provincia;
        this.municipio = municipio;
        this.bairro = bairro;
        this.rua = rua;
        this.nCasa = nCasa;
        this.sexo = sexo;
    }


}
