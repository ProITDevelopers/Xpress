package ao.co.proitconsulting.xpress.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.modelos.Estabelecimento;

import static ao.co.proitconsulting.xpress.helper.Common.SPAN_COUNT_ONE;
import static ao.co.proitconsulting.xpress.helper.Common.VIEW_TYPE_GRID;
import static ao.co.proitconsulting.xpress.helper.Common.VIEW_TYPE_LIST;

public class EstabelecimentoAdapter extends RecyclerView.Adapter<EstabelecimentoAdapter.ItemViewHolder>
        implements Filterable {

    private List<Estabelecimento> estabelecimentoList;
    private List<Estabelecimento> estabelecimentoListFull;
    private GridLayoutManager mLayoutManager;
    private Context context;
    private RecyclerViewOnItemClickListener itemClickListener;


    public EstabelecimentoAdapter(Context context, List<Estabelecimento> estabelecimentoList, GridLayoutManager layoutManager) {
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
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_LIST) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_estabelecimento_list, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_estabelecimento_grid, parent, false);
        }
        return new ItemViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {


        Estabelecimento estabelecimento = estabelecimentoList.get(position);


        holder.title.setText(estabelecimento.nomeEstabelecimento);
        Picasso.with(context).load(estabelecimento.logotipo).fit().centerCrop().placeholder(R.drawable.store_placeholder).into(holder.iv);

        if (getItemViewType(position) == VIEW_TYPE_LIST) {
            holder.info.setText(estabelecimento.descricao);
        }


        if (estabelecimento.estadoEstabelecimento.equals("Aberto")){
            holder.estadoInfoList.setTextColor(ContextCompat.getColor(context, R.color.xpress_green));
            holder.cardViewItem.setEnabled(true);
        }else{
            holder.cardViewItem.setEnabled(false);
            holder.cardViewItem.setAlpha(0.5f);
            holder.estadoInfoList.setTextColor(ContextCompat.getColor(context, R.color.xpress_purple));
        }
        holder.estadoInfoList.setText(estabelecimento.estadoEstabelecimento);





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

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView iv;
        TextView title,info,estadoInfoList;
        CardView cardViewItem;

        ItemViewHolder(View itemView, int viewType) {
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

            itemView.setOnClickListener(this);


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
