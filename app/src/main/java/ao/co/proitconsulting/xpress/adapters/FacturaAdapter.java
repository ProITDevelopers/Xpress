package ao.co.proitconsulting.xpress.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.helper.Utils;
import ao.co.proitconsulting.xpress.modelos.Factura;
import ao.co.proitconsulting.xpress.utilityClasses.OrderItemsListView;

public class FacturaAdapter extends RecyclerView.Adapter<FacturaAdapter.FacturaViewHolder> {

    private Context context;
    private List<Factura> facturaList;
    private RecyclerViewOnItemClickListener itemClickListener;



    public FacturaAdapter(Context context, List<Factura> facturaList) {
        this.facturaList = facturaList;
        this.context = context;
    }

    @NonNull
    @Override
    public FacturaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedidos_list, parent, false);
        return new FacturaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacturaViewHolder holder, int position) {

        Factura factura = facturaList.get(position);
        if (factura != null){

            holder.txtFacturaId.setText(context.getString(R.string.order_id, String.valueOf(factura.idFactura)));
            holder.txtDataPedido.setText(Utils.getOrderTimestamp(factura.dataPedido));
            holder.orderItems.setOrderItems(factura.itens);
            holder.txtEstado.setText(factura.estado);

            if (factura.estadoPagamento.equals("Pagamento Efectuado")){
                holder.txtEstadoPagamento.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.txtEstadoPagamento.setBackgroundColor(ContextCompat.getColor(context, R.color.login_register_text_color));
            }else{
                holder.txtEstadoPagamento.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.txtEstadoPagamento.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
            }
            holder.txtEstadoPagamento.setText(factura.estadoPagamento);



            holder.txtMetododPagamento.setText(factura.metododPagamento);

            String total = String.valueOf(factura.total);
            holder.txtTotal.setText(context.getString(R.string.price_with_currency, Float.parseFloat(total)).concat(" AKZ"));

            holder.txtDataPagamento.setText(Utils.getOrderTimestamp(factura.dataPagamento));



            boolean isExpanded = facturaList.get(position).isExpanded();
            holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);


        }



    }

    @Override
    public int getItemCount() {
        return facturaList.size();
    }

    class FacturaViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {


        ConstraintLayout expandableLayout;
        TextView txtFacturaId, txtDataPedido;
        OrderItemsListView orderItems;

        TextView txtEstado, txtEstadoPagamento, txtMetododPagamento, txtTotal, txtDataPagamento;

        public FacturaViewHolder(@NonNull final View itemView) {
            super(itemView);

            expandableLayout = itemView.findViewById(R.id.expandableLayout);

            txtFacturaId = itemView.findViewById(R.id.txtFacturaId);
            txtDataPedido = itemView.findViewById(R.id.txtDataPedido);

            orderItems = itemView.findViewById(R.id.order_items);

            txtEstado = itemView.findViewById(R.id.txtEstado);
            txtEstadoPagamento = itemView.findViewById(R.id.txtEstadoPagamento);
            txtMetododPagamento = itemView.findViewById(R.id.txtMetododPagamento);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            txtDataPagamento = itemView.findViewById(R.id.txtDataPagamento);



            txtFacturaId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Factura factura = facturaList.get(getAdapterPosition());
                    factura.setExpanded(!factura.isExpanded());
                    notifyItemChanged(getAdapterPosition());

                }
            });


            expandableLayout.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) itemClickListener.onItemClickListener(getAdapterPosition());
        }
    }

    public void setItemClickListener(RecyclerViewOnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
