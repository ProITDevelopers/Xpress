package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UsuarioAuth implements Serializable {

    @SerializedName("expiracao")
    public String expiracao;

    @SerializedName("tokenuser")
    public String tokenuser;

    @SerializedName("role")
    public String role;


}
