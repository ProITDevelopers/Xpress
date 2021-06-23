package ao.co.proitconsulting.xpress.modelos;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CartItemProdutos extends RealmObject {
    @PrimaryKey
    public String id = UUID.randomUUID().toString();

    public int idProduto;
    public String nomeProduto;
    public String descricaoProduto;
    public float precoProduto;
    public String imagemProduto;

    public int quantity = 0;
    public int idEstabelecimento;

    public String estabProduto;

    public String latitude;

    public String longitude;

    public String produtoExtras;

    public Double produtoPrecoExtra;

    public int emStockProduto;
    public int idCategoria;
    public String categoriaProduto;


    public CartItemProdutos() {}

}
