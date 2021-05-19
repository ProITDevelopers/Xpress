package ao.co.proitconsulting.xpress.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.Callback.IRecyclerClickListener;
import ao.co.proitconsulting.xpress.EventBus.EstabelecimentoClick;
import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.modelos.Estabelecimento;

import static ao.co.proitconsulting.xpress.helper.Common.SPAN_COUNT_ONE;
import static ao.co.proitconsulting.xpress.helper.Common.VIEW_TYPE_GRID;
import static ao.co.proitconsulting.xpress.helper.Common.VIEW_TYPE_LIST;


public class CategoryEstabAdapter extends RecyclerView.Adapter<CategoryEstabAdapter.ViewHolder>
        implements Filterable {

    private Context context;
    private List<Estabelecimento> estabelecimentoList;
    private List<Estabelecimento> estabelecimentoListFull;
    private GridLayoutManager mLayoutManager;

    public CategoryEstabAdapter(Context context, List<Estabelecimento> estabelecimentoList, GridLayoutManager layoutManager) {
        this.context = context;
        this.estabelecimentoList = estabelecimentoList;
        this.mLayoutManager = layoutManager;

        estabelecimentoListFull = new ArrayList<>(estabelecimentoList);
    }

    @Override
    public int getItemViewType(int position) {
        int spanCount = mLayoutManager.getSpanCount();
        if (spanCount == SPAN_COUNT_ONE) {
            return VIEW_TYPE_LIST;
        } else {
            return VIEW_TYPE_GRID;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_LIST) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_estabelecimento_list, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_estabelecimento_grid, parent, false);
        }
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        Estabelecimento estabelecimento = estabelecimentoList.get(position);


        holder.title.setText(estabelecimento.nomeEstabelecimento);
        Picasso.with(context).load(estabelecimento.logotipo).fit().centerCrop().placeholder(R.drawable.store_placeholder).into(holder.iv);

        if (getItemViewType(position) == VIEW_TYPE_LIST) {
            holder.info.setText(estabelecimento.descricao);
        }


        if (estabelecimento.estadoEstabelecimento.equals("Aberto")){
            holder.estadoInfoList.setTextColor(ContextCompat.getColor(context, R.color.xpress_green));
        }else{
            holder.cardViewItem.setAlpha(0.6f);
            holder.estadoInfoList.setTextColor(ContextCompat.getColor(context, R.color.xpress_purple));
        }
        holder.estadoInfoList.setText(estabelecimento.estadoEstabelecimento);

        holder.setListener(new IRecyclerClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                Common.selectedEstab = estabelecimentoList.get(position);
                Common.selectedEstabPosition = position;
                if(estabelecimentoList.get(position).estadoEstabelecimento.equals("Aberto")){
                    EventBus.getDefault().postSticky(new EstabelecimentoClick(true, estabelecimentoList.get(position)));
                }else{
                    Toast.makeText(context, ""+estabelecimentoList.get(position).nomeEstabelecimento+" encontra-se fechado.", Toast.LENGTH_SHORT).show();
                }
            }
        });





    }

    @Override
    public int getItemCount() {
        return estabelecimentoList.size();
    }

    @Override
    public Filter getFilter() {
        return estabelecimentoFilter;
    }

    private Filter estabelecimentoFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Estabelecimento> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0){
                filteredList.addAll(estabelecimentoListFull);
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (Estabelecimento estab: estabelecimentoListFull) {
                    if (estab.nomeEstabelecimento.toLowerCase().contains(filterPattern)){
                        filteredList.add(estab);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            estabelecimentoList.clear();
            estabelecimentoList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView iv;
        TextView title,info,estadoInfoList;
        CardView cardViewItem;
        private IRecyclerClickListener listener;

        ViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEW_TYPE_LIST) {
                iv = itemView.findViewById(R.id.imgList);
                title =  itemView.findViewById(R.id.titleList);
                info = itemView.findViewById(R.id.descInfoList);
                estadoInfoList = itemView.findViewById(R.id.estadoInfoList);
                cardViewItem = itemView.findViewById(R.id.cardViewItem);
            } else {
                iv =  itemView.findViewById(R.id.imgGrid);
                title =  itemView.findViewById(R.id.titleGrid);
                estadoInfoList =  itemView.findViewById(R.id.estadoInfoList);
                cardViewItem =  itemView.findViewById(R.id.cardViewItem);
            }

            cardViewItem.setOnClickListener(this);


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
