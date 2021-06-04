package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ProdutoListExtras extends RealmObject{

    @PrimaryKey
    public int itensProdutoextraid;

    public int idProduto;

    @SerializedName("produtoextras")
    public ProdutoExtra produtoextras;

    public int produtoextrasid;

}
