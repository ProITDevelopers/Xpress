package ao.co.proitconsulting.xpress.localDB;

import android.content.Context;
import android.widget.Toast;

import java.util.List;

import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;
import ao.co.proitconsulting.xpress.modelos.ProdutoListExtras;
import ao.co.proitconsulting.xpress.modelos.Produtos;
import io.realm.Realm;
import io.realm.RealmResults;

public class AppDatabase {

    public AppDatabase() {
    }

//    public static void saveUser(UsuarioPerfil user) {
//        Realm.getDefaultInstance().executeTransaction(realm -> {
//            realm.delete(UsuarioPerfil.class); // deleting previous user data
//            realm.copyToRealmOrUpdate(user);
//        });
//    }
//
//    public static UsuarioPerfil getUser() {
//        return Realm.getDefaultInstance().where(UsuarioPerfil.class).findFirst();
//    }


    public static void saveProducts(final List<Produtos> produtosList) {
        Realm.getDefaultInstance().executeTransactionAsync(realm -> {
            for (Produtos produtos : produtosList) {
                realm.copyToRealmOrUpdate(produtos);
            }
        });
    }

    public static void saveProductsExtras(final List<ProdutoListExtras> produtoListExtras) {
        Realm.getDefaultInstance().executeTransactionAsync(realm -> {
            for (ProdutoListExtras produtoExtras : produtoListExtras) {
                realm.copyToRealmOrUpdate(produtoExtras);
            }
        });
    }

    public static void updateSingleProduct(Produtos produto) {
        Realm.getDefaultInstance().executeTransactionAsync(realm -> {


            realm.copyToRealmOrUpdate(produto);
        });
    }

//    public static RealmResults<Produtos> getProducts() {
//        return Realm.getDefaultInstance().where(Produtos.class).findAll();
//    }

    public static RealmResults<Produtos> getProducts(String estabelecimento) {
        return Realm.getDefaultInstance().where(Produtos.class).equalTo("estabelecimento", estabelecimento).findAll();
    }

    /**
     * ADD PRODUTO TO CART, FROM SHOPPING CART ATIVITY
     */
    public static void addItemToCart(Context context, Produtos product) {
        initNewCart(context,product);
    }

    private static void initNewCart(Context context, Produtos product) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            CartItemProdutos cartItem = realm.where(CartItemProdutos.class).equalTo("produtos.idProduto", product.idProduto).findFirst();
            if (cartItem == null) {
                CartItemProdutos ci = new CartItemProdutos();
                ci.produtos = product;
                ci.quantity = 1;


                realm.copyToRealmOrUpdate(ci);
            } else {

                if (cartItem.quantity<=cartItem.produtos.emStock){
                    cartItem.quantity += 1;
                    realm.copyToRealmOrUpdate(cartItem);
                }else {
                    Toast.makeText(context, "Atingiu o limite de compra!", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    /**
     * Adding product to cart
     * Will create a new cart entry if there is no cart created yet
     * Will increase the product quantity count if the item exists already
     */
    public static void addItemToCart(Context context, Produtos product,int quantidadeSelected) {
        initNewCart(context,product,quantidadeSelected);
    }

    private static void initNewCart(Context context, Produtos product,int quantidadeSelected) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            CartItemProdutos cartItem = realm.where(CartItemProdutos.class).equalTo("produtos.idProduto", product.idProduto).findFirst();
            if (cartItem == null) {
                CartItemProdutos ci = new CartItemProdutos();
                ci.produtos = product;
//                ci.quantity = 1;
                ci.quantity = quantidadeSelected;

                realm.copyToRealmOrUpdate(ci);
            } else {

//                if (cartItem.quantity<=cartItem.produtos.emStock){
//                    cartItem.quantity += 1;
//                    realm.copyToRealmOrUpdate(cartItem);
//                }else {
//                    Toast.makeText(context, "Atingiu o limite de compra!", Toast.LENGTH_SHORT).show();
//                }

                cartItem.quantity =  quantidadeSelected;

                if (cartItem.quantity<=cartItem.produtos.emStock){

                    realm.copyToRealmOrUpdate(cartItem);
                }else{
                    Toast.makeText(context, "Atingiu o limite de compra!", Toast.LENGTH_SHORT).show();
                }




            }
        });
    }



    public static void removeCartItem(Produtos product) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            CartItemProdutos cartItem = realm.where(CartItemProdutos.class).equalTo("produtos.idProduto", product.idProduto).findFirst();
            if (cartItem != null) {
                if (cartItem.quantity == 1) {
                    cartItem.deleteFromRealm();
                } else {
                    cartItem.quantity -= 1;
                    realm.copyToRealmOrUpdate(cartItem);
                }
            }
        });
    }

    public static void removeCartItem(CartItemProdutos cartItem) {
        Realm.getDefaultInstance().executeTransaction(realm -> realm.where(CartItemProdutos.class).equalTo("produtos.idProduto", cartItem.produtos.idProduto).findAll().deleteAllFromRealm());
    }

    public static void clearAllCart() {
        Realm.getDefaultInstance().executeTransactionAsync(realm -> realm.delete(CartItemProdutos.class));
    }











    public static void clearData() {
        Realm.getDefaultInstance().executeTransaction(realm -> realm.deleteAll());
    }
}
