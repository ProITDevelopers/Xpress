package ao.co.proitconsulting.xpress.utilityClasses;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.api.ADAO.GetTaxaModel;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;

public class CheckOutItemsListView extends ConstraintLayout {

    private LinearLayout layoutContainer;

    private List<CartItemProdutos> items = new ArrayList<>();
    private List<GetTaxaModel> taxaModelList = new ArrayList<>();
    private LayoutInflater inflater;

    public CheckOutItemsListView(Context context) {
        super(context);
        init(context, null);
    }

    public CheckOutItemsListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.layout_order_items_list_view, this);
//        ButterKnife.bind(this);
        if (inflater == null) {
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }
        layoutContainer = findViewById(R.id.container);





    }

    public void setOrderItems(List<CartItemProdutos> items) {
        this.items.clear();
        this.items.addAll(items);

        renderItems();
    }

    private void renderItems() {
        layoutContainer.removeAllViews();


        for (int i = 0; i < items.size(); i++) {
            View view = inflater.inflate(R.layout.item_view_checkout_list_row, null);
            CartItemProdutos item = items.get(i);
            float taxaValor = Float.parseFloat(Common.getTaxaModelList.get(i).valorTaxa);

            if (item != null) {

                Picasso.with(getContext())
                        .load(item.imagemProduto)
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.store_placeholder)
                        .into((ImageView) view.findViewById(R.id.thumbnail));

//                ((CircleImageView) view.findViewById(R.id.thumbnail)).setImageResource(R.drawable.snack_food);

                ((TextView) view.findViewById(R.id.name)).setText(item.nomeProduto);
                ((TextView) view.findViewById(R.id.estabelecimento)).setText(item.estabProduto);

                ((TextView) view.findViewById(R.id.price)).setText(getContext().getString(R.string.lbl_item_price_quantity, getContext().getString(R.string.price_with_currency, item.precoProduto)+ " AKZ", item.quantity));
                ((TextView) view.findViewById(R.id.taxa)).setText(getContext().getString(R.string.price_with_currency, taxaValor).concat(" AKZ"));


            }

            layoutContainer.addView(view);
        }
//        for (CartItemProdutos item : items) {
//            View view = inflater.inflate(R.layout.item_view_checkout_list_row, null);
//            if (item.produtos != null) {
//
//                Picasso.with(getContext())
//                        .load(item.produtos.getImagemProduto())
//                        .fit()
//                        .centerCrop()
//                        .placeholder(R.drawable.store_placeholder)
//                        .into((ImageView) view.findViewById(R.id.thumbnail));
//
////                ((CircleImageView) view.findViewById(R.id.thumbnail)).setImageResource(R.drawable.snack_food);
//
//                ((TextView) view.findViewById(R.id.name)).setText(item.produtos.getDescricaoProdutoC());
//                ((TextView) view.findViewById(R.id.estabelecimento)).setText(item.produtos.getEstabelecimento());
//
//                ((TextView) view.findViewById(R.id.price)).setText(getContext().getString(R.string.lbl_item_price_quantity, getContext().getString(R.string.price_with_currency, item.produtos.getPrecoUnid())+ " AKZ", item.quantity));
//
//
//            }
//            layoutContainer.addView(view);
//        }
    }
}
