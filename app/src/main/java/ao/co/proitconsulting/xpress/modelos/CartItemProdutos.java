package ao.co.proitconsulting.xpress.modelos;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CartItemProdutos extends RealmObject {
    @PrimaryKey
    public String id = UUID.randomUUID().toString();
    public Produtos produtos;
    public int quantity = 0;

    public CartItemProdutos() {}


}
