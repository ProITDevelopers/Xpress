package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginRequest implements Serializable {

    @SerializedName("userName")
    public String userName;

    @SerializedName("telefone")
    public String telefone;

    @SerializedName("email")
    public String email;

    @SerializedName("password")
    public String password;

    @SerializedName("rememberMe")
    public boolean rememberMe;


}
