package ao.co.proitconsulting.xpress.localDB;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;

import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.helper.Utils;
import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;
import ao.co.proitconsulting.xpress.modelos.Produtos;
import io.realm.Realm;

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


//    public static void saveProducts(final List<Produtos> produtosList) {
//        Realm.getDefaultInstance().executeTransactionAsync(realm -> {
//            for (Produtos produtos : produtosList) {
//                realm.copyToRealmOrUpdate(produtos);
//            }
//        });
//    }
//
//    public static void saveProductsExtras(final List<ProdutoListExtras> produtoListExtras) {
//        Realm.getDefaultInstance().executeTransactionAsync(realm -> {
//            for (ProdutoListExtras produtoExtras : produtoListExtras) {
//                realm.copyToRealmOrUpdate(produtoExtras);
//            }
//        });
//    }
//
//    public static void updateSingleProduct(Produtos produto) {
//        Realm.getDefaultInstance().executeTransactionAsync(realm -> {
//
//
//            realm.copyToRealmOrUpdate(produto);
//        });
//    }

//    public static RealmResults<Produtos> getProducts() {
//        return Realm.getDefaultInstance().where(Produtos.class).findAll();
//    }

//    public static RealmResults<Produtos> getProducts(String estabelecimento) {
//        return Realm.getDefaultInstance().where(Produtos.class).equalTo("estabelecimento", estabelecimento).findAll();
//    }

    /**
     * ADD PRODUTO TO CART, FROM SHOPPING CART ATIVITY
     */
    public static void addItemToCart(Context context, Produtos product) {
        initNewCart(context,product);
    }

    private static void initNewCart(Context context, Produtos product) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            CartItemProdutos cartItem = realm.where(CartItemProdutos.class).equalTo("idProduto", product.getIdProduto()).findFirst();
            if (cartItem == null) {
                CartItemProdutos ci = new CartItemProdutos();
                ci.idProduto = product.getIdProduto();
                ci.nomeProduto = product.getNomeProduto();
                ci.descricaoProduto = product.getDescricaoProduto();
                ci.precoProduto = product.getPrecoProduto();
                ci.imagemProduto = product.getImagemProduto();
                ci.quantity = 1;
                ci.idEstabelecimento = product.getIdEstabelecimento();
                ci.estabProduto = product.getEstabProduto();
                ci.latitude = product.getLatitude();
                ci.longitude = product.getLongitude();
//                ci.produtoExtras = product.;
//                ci.produtoPrecoExtra = product;

                ci.emStockProduto = product.getEmStockProduto();
                ci.idCategoria = product.getIdCategoria();
                ci.categoriaProduto = product.getCategoriaProduto();


                realm.copyToRealmOrUpdate(ci);
            } else {

                if (cartItem.quantity<=product.getEmStockProduto()){
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
            CartItemProdutos cartItem = realm.where(CartItemProdutos.class).equalTo("idProduto", product.getIdProduto()).findFirst();
            if (cartItem == null) {
                CartItemProdutos ci = new CartItemProdutos();
                ci.idProduto = product.getIdProduto();
                ci.nomeProduto = product.getNomeProduto();
                ci.descricaoProduto = product.getDescricaoProduto();
                ci.precoProduto = product.getPrecoProduto();
                ci.imagemProduto = product.getImagemProduto();
                ci.quantity = quantidadeSelected;
                ci.idEstabelecimento = product.getIdEstabelecimento();
                ci.estabProduto = product.getEstabProduto();
                ci.latitude = product.getLatitude();
                ci.longitude = product.getLongitude();

                ci.emStockProduto = product.getEmStockProduto();
                ci.idCategoria = product.getIdCategoria();
                ci.categoriaProduto = product.getCategoriaProduto();

                ci.produtoPrecoExtra = Utils.calculateExtraPrice(Common.selectedProduto.getUserSelectedAddon()); //Because default we not choose size + addon so extra price is 0

                if (Common.selectedProduto.getUserSelectedAddon() != null)
                    ci.produtoExtras = new Gson().toJson(Common.selectedProduto.getUserSelectedAddon());
                else
                    ci.produtoExtras = null;


                realm.copyToRealmOrUpdate(ci);
            } else {

//                if (cartItem.quantity<=cartItem.produtos.emStock){
//                    cartItem.quantity += 1;
//                    realm.copyToRealmOrUpdate(cartItem);
//                }else {
//                    Toast.makeText(context, "Atingiu o limite de compra!", Toast.LENGTH_SHORT).show();
//                }

                cartItem.quantity =  quantidadeSelected;

                if (cartItem.quantity<=product.getEmStockProduto()){

                    cartItem.produtoPrecoExtra = Utils.calculateExtraPrice(Common.selectedProduto.getUserSelectedAddon());

                    if (Common.selectedProduto.getUserSelectedAddon() != null)
                        cartItem.produtoExtras = new Gson().toJson(Common.selectedProduto.getUserSelectedAddon());
                    else
                        cartItem.produtoExtras = null;

                    realm.copyToRealmOrUpdate(cartItem);
                }else{
                    Toast.makeText(context, "Atingiu o limite de compra!", Toast.LENGTH_SHORT).show();
                }




            }
        });
    }



    public static void removeCartItem(Produtos product) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            CartItemProdutos cartItem = realm.where(CartItemProdutos.class).equalTo("idProduto", product.getIdProduto()).findFirst();
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
        Realm.getDefaultInstance().executeTransaction(realm -> realm.where(CartItemProdutos.class).equalTo("idProduto", cartItem.idProduto).findAll().deleteAllFromRealm());
    }

    public static void clearAllCart() {
        Realm.getDefaultInstance().executeTransactionAsync(realm -> realm.delete(CartItemProdutos.class));
    }











    public static void clearData() {
        Realm.getDefaultInstance().executeTransaction(realm -> realm.deleteAll());
    }
}
