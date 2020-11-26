package ao.co.proitconsulting.xpress.sectionsTests;

import java.util.List;

import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;
import io.realm.RealmResults;

public class CartItemProdutosSections {

    public String sectionName;
    public RealmResults<CartItemProdutos> sectionItems;

    public CartItemProdutosSections(String sectionName, List<CartItemProdutos> sectionItems) {
        this.sectionName = sectionName;
        this.sectionItems = (RealmResults<CartItemProdutos>) sectionItems;
    }
}
