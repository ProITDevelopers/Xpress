package ao.co.proitconsulting.xpress.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.Callback.IRecyclerClickListener;
import ao.co.proitconsulting.xpress.EventBus.ProdutoClick;
import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.helper.Utils;
import ao.co.proitconsulting.xpress.modelos.Produtos;

import static ao.co.proitconsulting.xpress.helper.Common.VIEW_TYPE_LIST_LEFT;
import static ao.co.proitconsulting.xpress.helper.Common.VIEW_TYPE_LIST_RIGHT;

public class ProdutosViewAdapter extends RecyclerView.Adapter<ProdutosViewAdapter.ItemViewHolder>
        implements Filterable {

    private Context context;
    private List<Produtos> produtosList;
    private List<Produtos> produtosListFull;
//    private RealmResults<CartItemProdutos> cartItems;

//    private ProductsAdapterListener listener;



//    public ProdutosViewAdapter(Context context, List<Produtos> produtosList, ProductsAdapterListener listener) {
//        this.context = context;
//        this.produtosList = produtosList;
//        this.listener = listener;
//
//    }

    public ProdutosViewAdapter(Context context, List<Produtos> produtosList) {
        this.context = context;
        this.produtosList = produtosList;

        produtosListFull = new ArrayList<>(produtosList);

    }

    @Override
    public int getItemViewType(int position) {
        if (position%2==0) {
            return VIEW_TYPE_LIST_LEFT;
        } else {
            return VIEW_TYPE_LIST_RIGHT;
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_LIST_LEFT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produto_left, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produto_right, parent, false);
        }
        return new ItemViewHolder(view, viewType);
    }



    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        Produtos produto = produtosList.get(position);
        if (produto!=null){
//            produto.ideStabelecimento = ideStabelecimento;

            Picasso.with(context).load(produto.getImagemProduto()).fit().centerCrop().placeholder(R.drawable.store_placeholder).into(holder.thumbnail);
            holder.name.setText(produto.getNomeProduto());

//            String preco = String.valueOf(produto.getPrecoUnid());
//            holder.price.setText(context.getString(R.string.price_with_currency, Float.parseFloat(preco)).concat(" AKZ"));


            double displayPrice = Double.parseDouble(String.valueOf(produto.getPrecoProduto()));
            holder.price.setText(new StringBuilder("").append(Utils.formatPrice(displayPrice)).append(" AKZ").toString());

            holder.descricao.setText(produto.getDescricaoProduto());

            holder.setListener(new IRecyclerClickListener() {
                @Override
                public void onItemClickListener(View view, int position) {
                    Common.selectedProduto = produtosList.get(position);
                    Common.selectedProdutoPosition = position;
                    EventBus.getDefault().postSticky(new ProdutoClick(true, produtosList.get(position)));
                }
            });



        }


    }

    @Override
    public int getItemCount() {
        return produtosList.size();
    }

    @Override
    public Filter getFilter() {
        return produtosFilter;
    }

    private Filter produtosFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Produtos> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0){
                filteredList.addAll(produtosListFull);
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (Produtos produto: produtosListFull) {

                    if (produto.getNomeProduto()!=null){
                        if (produto.getNomeProduto().toLowerCase().contains(filterPattern)){
                            filteredList.add(produto);
                        }
                    }

                    if (produto.getCategoriaProduto()!=null){
                        if (produto.getCategoriaProduto().toLowerCase().contains(filterPattern)){
                            filteredList.add(produto);
                        }
                    }


                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            produtosList.clear();
            produtosList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //        private ImageView thumbnail;
        private ImageView thumbnail;
        private TextView name;
        private TextView descricao;
        private TextView price;

        private Button btnGoToDetails;
        private IRecyclerClickListener listener;

        public ItemViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEW_TYPE_LIST_LEFT) {
                thumbnail = itemView.findViewById(R.id.imgProdLeft);
                name =  itemView.findViewById(R.id.titleProdLeft);
                descricao =  itemView.findViewById(R.id.descProdLeft);
                price =  itemView.findViewById(R.id.priceProdLeft);


                btnGoToDetails =  itemView.findViewById(R.id.btnGoToDetails);

            } else {
                thumbnail =  itemView.findViewById(R.id.imgProdRight);
                name = itemView.findViewById(R.id.titleProdRight);
                price =  itemView.findViewById(R.id.priceProdRight);
                descricao =  itemView.findViewById(R.id.descProdRight);



                btnGoToDetails = itemView.findViewById(R.id.btnGoToDetails);
            }

            thumbnail.setOnClickListener(this);
            btnGoToDetails.setOnClickListener(this);
            itemView.setOnClickListener(this);

        }

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            listener.onItemClickListener(view,getAdapterPosition());
        }
    }



//    public void setCartItems(RealmResults<CartItemProdutos> cartItems) {
//        this.cartItems = cartItems;
//        notifyDataSetChanged();
//    }
//
//    public void updateItem(int position, RealmResults<CartItemProdutos> cartItems) {
//        this.cartItems = cartItems;
//        notifyItemChanged(position);
//    }
//
//    public interface ProductsAdapterListener {
//        void onProductAddedCart(int index, Produtos product);
//
//        void onProductRemovedFromCart(int index, Produtos product);
//    }

}
