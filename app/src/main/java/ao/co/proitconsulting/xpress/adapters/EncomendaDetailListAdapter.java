package ao.co.proitconsulting.xpress.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ao.co.proitconsulting.xpress.Callback.IRecyclerClickListener;
import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.helper.Utils;
import ao.co.proitconsulting.xpress.modelos.FacturaItens;

public class EncomendaDetailListAdapter extends RecyclerView.Adapter<EncomendaDetailListAdapter.FacturaViewHolder> {

    private Context context;
    private List<FacturaItens> facturaItens;




    public EncomendaDetailListAdapter(Context context, List<FacturaItens> facturaItens) {
        this.context = context;
        this.facturaItens = facturaItens;
    }

    @NonNull
    @Override
    public FacturaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_encomenda_detail_list, parent, false);
        return new FacturaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacturaViewHolder holder, int position) {

        FacturaItens facturaItem = facturaItens.get(position);
        if (facturaItem != null){

            Picasso.with(context).load(facturaItem.imagem).fit().centerCrop()
                    .placeholder(R.drawable.store_placeholder).into(holder.imgEncomenda);

            holder.titleEncomenda.setText(facturaItem.produto);

            if (facturaItem.itensExtrasFacturas!=null){
                if (facturaItem.itensExtrasFacturas.size()>0){
                    holder.descEncomenda.setVisibility(View.VISIBLE);
                    holder.descEncomenda.setText(new StringBuilder("").append(Utils.getListAddonFactura(facturaItem.itensExtrasFacturas)));
                }
            }

            holder.txtQuantidade.setText("Qtd: ".concat(String.valueOf(facturaItem.quantidade)));

            holder.setListener(new IRecyclerClickListener() {
                @Override
                public void onItemClickListener(View view, int position) {
                    Toast.makeText(context, ""+facturaItem.produto, Toast.LENGTH_SHORT).show();

                }
            });


        }



    }

    @Override
    public int getItemCount() {
        return facturaItens.size();
    }

    class FacturaViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {


        private CardView cardViewEncomenda;
        private ImageView imgEncomenda;
        private TextView titleEncomenda, descEncomenda, txtQuantidade;
        private IRecyclerClickListener listener;


        public FacturaViewHolder(@NonNull final View itemView) {
            super(itemView);


            cardViewEncomenda = itemView.findViewById(R.id.cardViewEncomenda);
            imgEncomenda = itemView.findViewById(R.id.imgEncomenda);
            titleEncomenda = itemView.findViewById(R.id.titleEncomenda);
            descEncomenda = itemView.findViewById(R.id.descEncomenda);
            txtQuantidade = itemView.findViewById(R.id.txtQuantidade);


            cardViewEncomenda.setOnClickListener(this);
            imgEncomenda.setOnClickListener(this);


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
