package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Produtos {

    @SerializedName("idProduto")
    private int idProduto;

    @SerializedName("descricaoProdutoC")
    private String nomeProduto;

    @SerializedName("descricaoProduto")
    private String descricaoProduto;

    @SerializedName("precoUnid")
    private float precoProduto;

    @SerializedName("imagemProduto")
    private String imagemProduto;

    @SerializedName("emStock")
    private int emStockProduto;

    @SerializedName("idCategoria")
    private int idCategoria;

    @SerializedName("categoriaProduto")
    private String categoriaProduto;

    @SerializedName("idEstabelecimento")
    private int idEstabelecimento;

    @SerializedName("estabelecimento")
    private String estabProduto;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    private List<ProdutoExtra> produtoExtrasList;

    //For Cart
    private List<ProdutoExtra> userSelectedAddon;



    public Produtos() {}

    public int getIdProduto() {
        return idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public float getPrecoProduto() {
        return precoProduto;
    }

    public String getImagemProduto() {
        return imagemProduto;
    }

    public int getEmStockProduto() {
        return emStockProduto;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public String getCategoriaProduto() {
        return categoriaProduto;
    }

    public int getIdEstabelecimento() {
        return idEstabelecimento;
    }

    public String getEstabProduto() {
        return estabProduto;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public List<ProdutoExtra> getProdutoExtrasList() {
        return produtoExtrasList;
    }

    public List<ProdutoExtra> getUserSelectedAddon() {
        return userSelectedAddon;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public void setPrecoProduto(float precoProduto) {
        this.precoProduto = precoProduto;
    }

    public void setImagemProduto(String imagemProduto) {
        this.imagemProduto = imagemProduto;
    }

    public void setEmStockProduto(int emStockProduto) {
        this.emStockProduto = emStockProduto;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public void setCategoriaProduto(String categoriaProduto) {
        this.categoriaProduto = categoriaProduto;
    }

    public void setIdEstabelecimento(int idEstabelecimento) {
        this.idEstabelecimento = idEstabelecimento;
    }

    public void setEstabProduto(String estabProduto) {
        this.estabProduto = estabProduto;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setProdutoExtrasList(List<ProdutoExtra> produtoExtrasList) {
        this.produtoExtrasList = produtoExtrasList;
    }

    public void setUserSelectedAddon(List<ProdutoExtra> userSelectedAddon) {
        this.userSelectedAddon = userSelectedAddon;
    }
}
