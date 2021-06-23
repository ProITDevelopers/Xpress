package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Wallet implements Serializable {

    @SerializedName("id")
    public String id;

    @SerializedName("number")
    public String number;

    @SerializedName("amount")
    public String amount;
}
