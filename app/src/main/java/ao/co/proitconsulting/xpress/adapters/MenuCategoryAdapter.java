package ao.co.proitconsulting.xpress.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.modelos.MenuCategory;

import static ao.co.proitconsulting.xpress.helper.Common.SPAN_COUNT_ONE;
import static ao.co.proitconsulting.xpress.helper.Common.VIEW_TYPE_GRID;
import static ao.co.proitconsulting.xpress.helper.Common.VIEW_TYPE_LIST;

public class MenuCategoryAdapter extends RecyclerView.Adapter<MenuCategoryAdapter.ItemViewHolder>{

    private List<MenuCategory> menuCategoryList;
    private GridLayoutManager mLayoutManager;
    private Context context;
    private RecyclerViewOnItemClickListener itemClickListener;


    public MenuCategoryAdapter(Context context, List<MenuCategory> menuCategoryList, GridLayoutManager layoutManager) {
        this.context = context;
        this.menuCategoryList = menuCategoryList;
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menucategory_list, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menucategory_grid, parent, false);
        }
        return new ItemViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {


        MenuCategory menuCategory = menuCategoryList.get(position);


        holder.txt_descricao.setText(menuCategory.getDescricao());






    }

    @Override
    public int getItemCount() {
        return menuCategoryList.size();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView img_menu;
        TextView txt_descricao;

        ItemViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEW_TYPE_LIST) {
                img_menu = itemView.findViewById(R.id.img_menu_list);
                txt_descricao =  itemView.findViewById(R.id.txt_descricao_list);

            } else {
                img_menu =  itemView.findViewById(R.id.img_menu_grid);
                txt_descricao =  itemView.findViewById(R.id.txt_descricao_grid);
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
