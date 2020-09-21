package ao.co.proitconsulting.xpress.localDB;

import android.content.Context;
import android.widget.Toast;

import java.util.List;

import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;
import ao.co.proitconsulting.xpress.modelos.FavoritosItem;
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

//    public static RealmResults<Produtos> getProducts() {
//        return Realm.getDefaultInstance().where(Produtos.class).findAll();
//    }

    public static RealmResults<Produtos> getProducts(String estabelecimento) {
        return Realm.getDefaultInstance().where(Produtos.class).equalTo("estabelecimento", estabelecimento).findAll();
    }


    /**
     * Adding product to cart
     * Will create a new cart entry if there is no cart created yet
     * Will increase the product quantity count if the item exists already
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



    /**
     * Adding product to favourites
     * Will create a new favourite entry if there is no favourite created yet
     */
    public static void addItemToFavourite(Produtos product) {
        initNewFavourite(product);
    }

    private static void initNewFavourite(Produtos product) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            FavoritosItem favoritosItem = realm.where(FavoritosItem.class).equalTo("produtos.idProduto", product.idProduto).findFirst();
            if (favoritosItem == null) {
                FavoritosItem ci = new FavoritosItem();
                ci.produtos = product;
                ci.quantity = 1;
                realm.copyToRealmOrUpdate(ci);
            }
        });
    }

    public static void removeFavouriteItem(Produtos product) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            FavoritosItem favoritosItem = realm.where(FavoritosItem.class).equalTo("produtos.idProduto", product.idProduto).findFirst();
            if (favoritosItem != null) {
                if (favoritosItem.quantity == 1) {
                    favoritosItem.deleteFromRealm();
                }
            }
        });
    }

    public static void removeFavouriteItem(FavoritosItem favoritosItem) {
        Realm.getDefaultInstance().executeTransaction(realm -> realm.where(FavoritosItem.class).equalTo("produtos.idProduto", favoritosItem.produtos.idProduto).findAll().deleteAllFromRealm());
    }

    public static void clearAllFavouriteItem() {
        Realm.getDefaultInstance().executeTransactionAsync(realm -> realm.delete(FavoritosItem.class));
    }

    public static void clearData() {
        Realm.getDefaultInstance().executeTransaction(realm -> realm.deleteAll());
    }
}
