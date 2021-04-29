package ao.co.proitconsulting.xpress.adapters.homeEstab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.modelos.CategoriaEstabelecimento;
import ao.co.proitconsulting.xpress.modelos.Estabelecimento;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder>{

    private Context context;
    private List<CategoriaEstabelecimento> sectionList;

    public MainRecyclerAdapter(Context context, List<CategoriaEstabelecimento> sectionList) {
        this.context = context;
        this.sectionList = sectionList;
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
        holder.sectionNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, ""+sectionName, Toast.LENGTH_SHORT).show();
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

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView sectionNameTextView;
        RecyclerView childRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sectionNameTextView = itemView.findViewById(R.id.sectionNameTextView);
            childRecyclerView = itemView.findViewById(R.id.childRecyclerView);
        }
    }
}
