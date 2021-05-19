package ao.co.proitconsulting.xpress.adapters.homeEstab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.Callback.IRecyclerClickListener;
import ao.co.proitconsulting.xpress.EventBus.CategoryClick;
import ao.co.proitconsulting.xpress.EventBus.EstabelecimentoClick;
import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.modelos.CategoriaEstabelecimento;
import ao.co.proitconsulting.xpress.modelos.Estabelecimento;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder>
        implements Filterable {

    private Context context;
    private List<CategoriaEstabelecimento> sectionList;
    private List<CategoriaEstabelecimento> sectionListFull;

    public MainRecyclerAdapter(Context context, List<CategoriaEstabelecimento> sectionList) {
        this.context = context;
        this.sectionList = sectionList;

        sectionListFull = new ArrayList<>(sectionList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.section_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CategoriaEstabelecimento section = sectionList.get(position);
        String sectionName = section.getMenuCategory().getDescricao();
        List<Estabelecimento> items = section.getEstabelecimentoList();

        holder.sectionNameTextView.setText(sectionName);

        //Event
        holder.setListener(new IRecyclerClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                Common.selectedCategoryEstab = sectionList.get(position);
                Common.selectedCategoryEstabPosition = position;
                EventBus.getDefault().postSticky(new CategoryClick(true, sectionList.get(position)));

            }
        });

        ChildRecyclerAdapter childRecyclerAdapter = new ChildRecyclerAdapter(context, items);
        holder.childRecyclerView.setHasFixedSize(true);
        holder.childRecyclerView.setLayoutManager(new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false));
        holder.childRecyclerView.setAdapter(childRecyclerAdapter);

    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }

    @Override
    public Filter getFilter() {
        return sectionFilter;
    }

    private Filter sectionFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<CategoriaEstabelecimento> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0){
                filteredList.addAll(sectionListFull);
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (CategoriaEstabelecimento catEstab: sectionListFull) {
                    if (catEstab.getMenuCategory().getDescricao().toLowerCase().contains(filterPattern)){
                        filteredList.add(catEstab);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            sectionList.clear();
            sectionList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView sectionNameTextView,sectionVerMaisTextView;
        private RecyclerView childRecyclerView;
        private IRecyclerClickListener listener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sectionNameTextView = itemView.findViewById(R.id.sectionNameTextView);
            sectionVerMaisTextView = itemView.findViewById(R.id.sectionVerMaisTextView);
            childRecyclerView = itemView.findViewById(R.id.childRecyclerView);

            sectionVerMaisTextView.setOnClickListener(this);
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
