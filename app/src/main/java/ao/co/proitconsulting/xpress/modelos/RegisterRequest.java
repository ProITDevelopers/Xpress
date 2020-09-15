package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

import java.io.File;
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

    @SerializedName("sexo")
    public String sexo;


    public File imagem;
}
