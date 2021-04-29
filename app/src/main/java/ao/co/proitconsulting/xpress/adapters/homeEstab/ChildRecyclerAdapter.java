package ao.co.proitconsulting.xpress.adapters.homeEstab;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import ao.co.proitconsulting.xpress.Callback.IRecyclerClickListener;
import ao.co.proitconsulting.xpress.EventBus.EstabelecimentoClick;
import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.activities.ProdutosActivity;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.modelos.Estabelecimento;

public class ChildRecyclerAdapter extends RecyclerView.Adapter<ChildRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Estabelecimento> items;

    public ChildRecyclerAdapter(Context context, List<Estabelecimento> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemTextView.setText(items.get(position).nomeEstabelecimento);
        Picasso.with(context).load(items.get(position).logotipo).fit().centerCrop().placeholder(R.drawable.store_placeholder).into(holder.imgEstab);

        //Event
        holder.setListener(new IRecyclerClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
//                Common.selectedEstab = items.get(position);
//                Common.selectedEstabPosition = position;
//                EventBus.getDefault().postSticky(new EstabelecimentoClick(true, items.get(position)));

                Estabelecimento estabelecimento = items.get(position);

                if (estabelecimento.estadoEstabelecimento!=null){

                    if (estabelecimento.estadoEstabelecimento.equals("Aberto")){
                        Intent intent = new Intent(context, ProdutosActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("estabelecimento",estabelecimento);
                        context.startActivity(intent);
                    }else{
                        MetodosUsados.mostrarMensagem(context,"O estabelecimento encontra-se ".concat(estabelecimento.estadoEstabelecimento));
                    }

                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private IRecyclerClickListener listener;
        private CardView cardViewEstab;
        private ImageView imgEstab;
        private TextView itemTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewEstab = itemView.findViewById(R.id.cardViewEstab);
            imgEstab = itemView.findViewById(R.id.imgEstab);
            itemTextView = itemView.findViewById(R.id.itemTextView);

            cardViewEstab.setOnClickListener(this);
            itemTextView.setOnClickListener(this);
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
