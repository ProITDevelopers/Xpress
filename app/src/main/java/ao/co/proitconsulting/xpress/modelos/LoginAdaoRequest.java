package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginAdaoRequest implements Serializable {

    @SerializedName("telefone")
    public String telefone;

    @SerializedName("palavraPasse")
    public String password;



}
