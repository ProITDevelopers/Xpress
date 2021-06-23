package ao.co.proitconsulting.xpress.helper;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ao.co.proitconsulting.xpress.api.ADAO.GetTaxaModel;
import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;
import ao.co.proitconsulting.xpress.modelos.FacturaItensExtras;
import ao.co.proitconsulting.xpress.modelos.ProdutoExtra;
import io.realm.RealmResults;

public class Utils {

    public static float getCartPrice(RealmResults<CartItemProdutos> cartItems) {
        float price = 0f;
        for (CartItemProdutos item : cartItems) {
            price += (item.precoProduto+item.produtoPrecoExtra) * item.quantity;


        }
        return price;
    }

    public static Double calculateExtraPrice(List<ProdutoExtra> userSelectedAddon) {
        Double result = 0.0;
        if(userSelectedAddon == null)
            return 0.0;
        else {
            //If userSelectedAddon != null , we need sum price
            for (ProdutoExtra addonModel : userSelectedAddon)
                result+=addonModel.getPrecoProdudoExtra();
            return result;
        }


    }

    public static String formatPrice(double price) {
        if (price != 0){
            DecimalFormat df = new DecimalFormat("#,##0.00");
            df.setRoundingMode(RoundingMode.UP);
            String finalPrice = new StringBuilder(df.format(price)).toString();
            return finalPrice.replace(".",",");
        }
        else
            return "0.00";
    }

    public static float getCartTaxa(List<GetTaxaModel> taxaModelList, float totalPrice) {
        float taxa = 0f;
        for (GetTaxaModel item : taxaModelList) {
            float valorTaxa = Float.parseFloat(item.valorTaxa);
            taxa += valorTaxa + totalPrice;

        }
        return taxa;
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

    public static String getListAddon(List<ProdutoExtra> produtoExtraList) {
        StringBuilder result = new StringBuilder();
        for (ProdutoExtra produtoExtra: produtoExtraList) {
            result.append(produtoExtra.getNomeProdudoExtra()).append(", ");
        }
        result.substring(1,result.length()-1);
        return result.substring(0,result.length()-1);
    }

    public static String getListAddonFactura(List<FacturaItensExtras> facturaItensExtrasList) {
        StringBuilder result = new StringBuilder();
        for (FacturaItensExtras itensExtras: facturaItensExtrasList) {
            result.append(itensExtras.nomeExtra).append(", ");
        }
        result.substring(1,result.length()-1);
        return result.substring(0,result.length()-1);
    }
}
