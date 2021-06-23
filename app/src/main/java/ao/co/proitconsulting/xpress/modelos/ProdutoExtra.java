package ao.co.proitconsulting.xpress.modelos;

import com.google.gson.annotations.SerializedName;

public class ProdutoExtra {

    @SerializedName("iDextras")
    private int iDProdudoExtra;

    @SerializedName("descricao")
    private String nomeProdudoExtra;

    @SerializedName("preco")
    private float precoProdudoExtra;

    private boolean selectedProdudoExtra;

    public int getiDProdudoExtra() {
        return iDProdudoExtra;
    }

    public void setiDProdudoExtra(int iDProdudoExtra) {
        this.iDProdudoExtra = iDProdudoExtra;
    }

    public String getNomeProdudoExtra() {
        return nomeProdudoExtra;
    }

    public void setNomeProdudoExtra(String nomeProdudoExtra) {
        this.nomeProdudoExtra = nomeProdudoExtra;
    }

    public float getPrecoProdudoExtra() {
        return precoProdudoExtra;
    }

    public void setPrecoProdudoExtra(float precoProdudoExtra) {
        this.precoProdudoExtra = precoProdudoExtra;
    }

    public boolean isSelectedProdudoExtra() {
        return selectedProdudoExtra;
    }

    public void setSelectedProdudoExtra(boolean selectedProdudoExtra) {
        this.selectedProdudoExtra = selectedProdudoExtra;
    }
}
