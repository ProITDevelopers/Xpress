package ao.co.proitconsulting.xpress.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.modelos.CheckoutOrder;
import ao.co.proitconsulting.xpress.utilityClasses.CheckOutItemsListView;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ItemViewHolder> {

    private Context context;
    private List<CheckoutOrder> checkoutOrderList;




    public CheckoutAdapter(Context context, List<CheckoutOrder> checkoutOrderList) {
        this.context = context;
        this.checkoutOrderList = checkoutOrderList;

    }



    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkout_list, parent, false);
        return new ItemViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        CheckoutOrder checkoutOrder = checkoutOrderList.get(position);

        if (checkoutOrder != null){

            holder.checkOutItemsListView.setOrderItems(checkoutOrder.itens);

            holder.txt_cart_itens.setText(context.getString(R.string.cart_info_bar_items, checkoutOrder.itens_cart));
            holder.txt_cart_total.setText(context.getString(R.string.cart_info_bar_total, Float.parseFloat(checkoutOrder.total_cart)).concat(" AKZ"));
            holder.txtMetododPagamento.setText(checkoutOrder.metododPagamento);
            holder.txtTelefone.setText(checkoutOrder.contacto);
            holder.txtEndereco.setText(checkoutOrder.endereco);
        }



    }

    @Override
    public int getItemCount() {
        return checkoutOrderList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private CheckOutItemsListView checkOutItemsListView;

        private TextView txt_cart_itens;
        private TextView txt_cart_total;
        private TextView txtMetododPagamento;
        private TextView txtTelefone;
        private TextView txtEndereco;


        public ItemViewHolder(View itemView) {
            super(itemView);

            checkOutItemsListView = itemView.findViewById(R.id.order_items);

            txt_cart_itens = itemView.findViewById(R.id.txt_cart_itens);
            txt_cart_total = itemView.findViewById(R.id.txt_cart_total);
            txtMetododPagamento = itemView.findViewById(R.id.txtMetododPagamento);
            txtTelefone = itemView.findViewById(R.id.txtTelefone);
            txtEndereco = itemView.findViewById(R.id.txtEndereco);

        }


    }
}
