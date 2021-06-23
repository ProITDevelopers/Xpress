package ao.co.proitconsulting.xpress.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import ao.co.proitconsulting.xpress.Callback.IRecyclerClickListener;
import ao.co.proitconsulting.xpress.EventBus.EncomendaClick;
import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.helper.Utils;
import ao.co.proitconsulting.xpress.modelos.Factura;

public class EncomendaFacturaAdapter extends RecyclerView.Adapter<EncomendaFacturaAdapter.FacturaViewHolder> {

    private Context context;
    private List<Factura> facturaList;


    public EncomendaFacturaAdapter(Context context, List<Factura> facturaList) {
        this.facturaList = facturaList;
        this.context = context;
    }

    @NonNull
    @Override
    public FacturaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_encomenda_list, parent, false);
        return new FacturaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacturaViewHolder holder, int position) {

        Factura factura = facturaList.get(position);
        if (factura != null){

            holder.txtFacturaId.setText(context.getString(R.string.order_id, String.valueOf(factura.idFactura)));
            holder.txtDataPedido.setText(Utils.getOrderTimestamp(factura.dataPedido));

            if (factura.estado.equals("Pedido Fechado")){
                holder.txtEstado.setTextColor(ContextCompat.getColor(context, R.color.encomenda_estado_color));
            }

            if (factura.estado.equals("À Ser Confencionado")){
                holder.txtEstado.setTextColor(ContextCompat.getColor(context, R.color.yellow));
            }

            if (factura.estado.equals("Pronto Para Entrega")){
                holder.txtEstado.setTextColor(ContextCompat.getColor(context, R.color.yellow));
            }

            if (factura.estado.equals("À Caminho")){
                holder.txtEstado.setTextColor(ContextCompat.getColor(context, R.color.yellow));
            }


            if (factura.estado.equals("Entregue")){
                holder.txtEstado.setTextColor(ContextCompat.getColor(context, R.color.xpress_green));
            }

            holder.txtEstado.setText(factura.estado);

            double displayPrice = Double.parseDouble(String.valueOf(factura.total));
            holder.txtTotal.setText(new StringBuilder("")
                    .append(Utils.formatPrice(displayPrice))
                    .append(" AKZ").toString());

//            String total = String.valueOf(factura.total);
//            holder.txtTotal.setText(context.getString(R.string.price_with_currency, Float.parseFloat(total)).concat(" AKZ"));



            holder.setListener(new IRecyclerClickListener() {
                @Override
                public void onItemClickListener(View view, int position) {
                    Common.selectedFactura = facturaList.get(position);
                    EventBus.getDefault().postSticky(new EncomendaClick(true, facturaList.get(position)));

                }
            });



        }



    }

    @Override
    public int getItemCount() {
        return facturaList.size();
    }

    class FacturaViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        private CardView cardViewFactura;
        private TextView txtFacturaId, txtTotal, txtEstado, txtDataPedido;
        private ImageView imgNext;
        private IRecyclerClickListener listener;


        public FacturaViewHolder(@NonNull final View itemView) {
            super(itemView);


            cardViewFactura = itemView.findViewById(R.id.cardViewFactura);
            txtFacturaId = itemView.findViewById(R.id.txtFacturaId);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            txtEstado = itemView.findViewById(R.id.txtEstado);
            txtDataPedido = itemView.findViewById(R.id.txtDataPedido);
            imgNext = itemView.findViewById(R.id.imgNext);

            cardViewFactura.setOnClickListener(this);
            imgNext.setOnClickListener(this);


        }

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            listener.onItemClickListener(view,getAdapterPosition());
        }
    }


}
