package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AdaoTokenAuth implements Serializable {

    @SerializedName("dataExpiracao")
    public String dataExpiracao;

    @SerializedName("token_acesso")
    public String token_acesso;



}
