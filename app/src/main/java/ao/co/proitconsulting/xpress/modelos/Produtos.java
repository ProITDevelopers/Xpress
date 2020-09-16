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

    @SerializedName("imagemProduto")
    public String imagemProduto;

    public float precoUnid;

    @SerializedName("emStock")
    public int emStock;

    public int tempo_de_preparacao;


    public Produtos() {}


    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public String getCategoriaProduto() {
        return categoriaProduto;
    }

    public void setCategoriaProduto(String categoriaProduto) {
        this.categoriaProduto = categoriaProduto;
    }

    public String getDescricaoProdutoC() {
        return descricaoProdutoC;
    }

    public void setDescricaoProdutoC(String descricaoProdutoC) {
        this.descricaoProdutoC = descricaoProdutoC;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public String getEstabelecimento() {
        return estabelecimento;
    }

    public void setEstabelecimento(String estabelecimento) {
        this.estabelecimento = estabelecimento;
    }

    public String getImagemProduto() {
        return imagemProduto;
    }

    public void setImagemProduto(String imagemProduto) {
        this.imagemProduto = imagemProduto;
    }

    public float getPrecoUnid() {
        return precoUnid;
    }

    public void setPrecoUnid(float precoUnid) {
        this.precoUnid = precoUnid;
    }

    public int getEmStock() {
        return emStock;
    }

    public void setEmStock(int emStock) {
        this.emStock = emStock;
    }

    public int getTempo_de_preparacao() {
        return tempo_de_preparacao;
    }

    public void setTempo_de_preparacao(int tempo_de_preparacao) {
        this.tempo_de_preparacao = tempo_de_preparacao;
    }

}
