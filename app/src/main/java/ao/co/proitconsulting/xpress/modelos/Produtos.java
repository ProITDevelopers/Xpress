package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Produtos extends RealmObject {

    @PrimaryKey
    public int idProduto;

    public String categoriaProduto;
    public String descricaoProdutoC;
    public String descricaoProduto;

    @SerializedName("estabelecimento")
    public String estabelecimento;


    public int idEstabelecimento;


    @SerializedName("imagemProduto")
    public String imagemProduto;

    public float precoUnid;

    @SerializedName("emStock")
    public int emStock;

    public int tempo_de_preparacao;

    @SerializedName("latitude")
    public String latitude;

    @SerializedName("longitude")
    public String longitude;




    public Produtos() {}


    public int getIdProduto() {
        return idProduto;
    }



    public String getCategoriaProduto() {
        return categoriaProduto;
    }



    public String getDescricaoProdutoC() {
        return descricaoProdutoC;
    }



    public String getDescricaoProduto() {
        return descricaoProduto;
    }



    public String getEstabelecimento() {
        return estabelecimento;
    }



    public String getImagemProduto() {
        return imagemProduto;
    }


    public float getPrecoUnid() {
        return precoUnid;
    }



    public int getEmStock() {
        return emStock;
    }


    public int getTempo_de_preparacao() {
        return tempo_de_preparacao;
    }


}
