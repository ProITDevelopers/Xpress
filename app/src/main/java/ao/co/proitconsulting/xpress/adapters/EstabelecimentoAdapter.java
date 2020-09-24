package ao.co.proitconsulting.xpress.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.modelos.Estabelecimento;

import static ao.co.proitconsulting.xpress.helper.Common.SPAN_COUNT_ONE;
import static ao.co.proitconsulting.xpress.helper.Common.VIEW_TYPE_GRID;
import static ao.co.proitconsulting.xpress.helper.Common.VIEW_TYPE_LIST;

public class EstabelecimentoAdapter extends RecyclerView.Adapter<EstabelecimentoAdapter.ItemViewHolder> {

    private List<Estabelecimento> estabelecimentoList;
    private GridLayoutManager mLayoutManager;
    private Context context;
    private RecyclerViewOnItemClickListener itemClickListener;


    public EstabelecimentoAdapter(Context context, List<Estabelecimento> estabelecimentoList, GridLayoutManager layoutManager) {
        this.context = context;
        this.estabelecimentoList = estabelecimentoList;
        this.mLayoutManager = layoutManager;
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
            holder.estadoInfoList.setTextColor(ContextCompat.getColor(context, R.color.login_register_text_color));
            holder.linearLayout.setEnabled(true);
        }else{
            holder.linearLayout.setEnabled(false);
            holder.linearLayout.setAlpha(0.5f);
            holder.estadoInfoList.setTextColor(ContextCompat.getColor(context, R.color.login_icon_text_color));
        }
        holder.estadoInfoList.setText(estabelecimento.estadoEstabelecimento);



//        if (getItemViewType(position) == VIEW_TYPE_BIG) {
//            holder.info.setText(item.descricao);
//        }

//        if (item.estadoEstabelecimento != null){
//
//            if (item.estadoEstabelecimento.equals("Aberto")){
//                holder.image_estado.setImageResource(image_aberto);
//            }else{
//                holder.image_estado.setImageResource(image_fechado);
//            }
//        }


    }

    @Override
    public int getItemCount() {
        return estabelecimentoList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView iv;
        TextView title,info,estadoInfoList;
        LinearLayout linearLayout;

        ItemViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEW_TYPE_LIST) {
                iv = itemView.findViewById(R.id.imgList);
                title =  itemView.findViewById(R.id.titleList);
                info = itemView.findViewById(R.id.descInfoList);
                estadoInfoList = itemView.findViewById(R.id.estadoInfoList);
                linearLayout = itemView.findViewById(R.id.linearLayout);
            } else {
                iv =  itemView.findViewById(R.id.imgGrid);
                title =  itemView.findViewById(R.id.titleGrid);
                estadoInfoList =  itemView.findViewById(R.id.estadoInfoList);
                linearLayout =  itemView.findViewById(R.id.linearLayout);
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
