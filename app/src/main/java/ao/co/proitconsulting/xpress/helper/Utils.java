package ao.co.proitconsulting.xpress.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ao.co.proitconsulting.xpress.api.ADAO.GetTaxaModel;
import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;
import ao.co.proitconsulting.xpress.modelos.FavoritosItem;
import io.realm.RealmResults;

public class Utils {

    public static float getCartPrice(RealmResults<CartItemProdutos> cartItems) {
        float price = 0f;
        for (CartItemProdutos item : cartItems) {
            price += item.produtos.getPrecoUnid() * item.quantity;
        }
        return price;
    }

    public static float getCartTaxa(List<GetTaxaModel> taxaModelList, float totalPrice) {
        float taxa = 0f;
        for (GetTaxaModel item : taxaModelList) {
            float valorTaxa = Float.parseFloat(item.valorTaxa);
            taxa += valorTaxa + totalPrice;

        }
        return taxa;
    }

    public static int getFavoriteQuantity(RealmResults<FavoritosItem> favoritosItems) {
        int price = 0;
        for (FavoritosItem item : favoritosItems) {
            price += item.quantity;
        }
        return price;
    }

    public static String getOrderTimestamp(String timestamp) {
//        SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat outputFormatter = new SimpleDateFormat("d MMM, yyyy");
        try {
            Date date = inputFormatter.parse(timestamp);
            return outputFormatter.format(date);
        } catch (ParseException e) {
//            Timber.e("Exception: %s", e);
        }

        return "";

    }
}
