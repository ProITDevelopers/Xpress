package ao.co.proitconsulting.xpress.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.modelos.SobreNos;

public class DefinicoesSobreNosAdapter extends RecyclerView.Adapter<DefinicoesSobreNosAdapter.ItemViewHolder> {

    private Context context;
    private List<SobreNos> sobreNosList;
    private RecyclerViewOnItemClickListener itemClickListener;

    public DefinicoesSobreNosAdapter(Context context, List<SobreNos> sobreNosList) {
        this.context = context;
        this.sobreNosList = sobreNosList;
    }



    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sobrenos_layout, parent, false);

        return new ItemViewHolder(view);

    }



    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        SobreNos sobreNos = sobreNosList.get(position);

        holder.txtTitle.setText(sobreNos.getTitle());
        holder.txtDescription.setText(sobreNos.getDescription());

    }

    @Override
    public int getItemCount() {
        return sobreNosList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtTitle, txtDescription;

        public ItemViewHolder(View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDescription =  itemView.findViewById(R.id.txtDescription);

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
