package ao.co.proitconsulting.xpress.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;
import ao.co.proitconsulting.xpress.modelos.Produtos;
import io.realm.RealmResults;

import static ao.co.proitconsulting.xpress.helper.Common.SPAN_COUNT_ONE;
import static ao.co.proitconsulting.xpress.helper.Common.VIEW_TYPE_GRID;
import static ao.co.proitconsulting.xpress.helper.Common.VIEW_TYPE_LIST;

public class ProdutosAdapter extends RecyclerView.Adapter<ProdutosAdapter.ItemViewHolder> {

    private Context context;
    private List<Produtos> produtosList;
    //    private RealmResults<Produtos> produtosList;
    private GridLayoutManager mLayoutManager;
    private RealmResults<CartItemProdutos> cartItems;
    private int ideStabelecimento;

    private ProductsAdapterListener listener;

    private RecyclerViewOnItemClickListener itemClickListener;

    public ProdutosAdapter(Context context, List<Produtos> produtosList, ProductsAdapterListener listener, GridLayoutManager layoutManager) {
        this.context = context;
        this.produtosList = produtosList;
        this.listener = listener;
        this.mLayoutManager = layoutManager;
//        this.ideStabelecimento = ideStabelecimento;

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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produto_list, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produto_grid, parent, false);
        }
        return new ItemViewHolder(view, viewType);
    }



    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        Produtos produto = produtosList.get(position);
        if (produto!=null){
//            produto.ideStabelecimento = ideStabelecimento;

            Picasso.with(context).load(produto.getImagemProduto()).fit().centerCrop().placeholder(R.drawable.store_placeholder).into(holder.thumbnail);
            holder.name.setText(produto.getDescricaoProdutoC());
            String preco = String.valueOf(produto.getPrecoUnid());
            holder.price.setText(context.getString(R.string.price_with_currency, Float.parseFloat(preco)).concat(" AKZ"));



            if (getItemViewType(position) == VIEW_TYPE_LIST) {
                holder.descricao.setText(produto.getDescricaoProduto());
            }

            holder.btn_addCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    listener.onProductAddedCart(position, produto);

                    Snackbar.make(view, produto.getDescricaoProdutoC()+" adicionado ao Carrinho!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
            });

            holder.ic_add.setOnClickListener(view -> {

                listener.onProductAddedCart(position, produto);
            });

            holder.ic_remove.setOnClickListener(view -> {
                listener.onProductRemovedFromCart(position, produto);
            });

            if (cartItems != null) {
                CartItemProdutos cartItem = cartItems.where().equalTo("produtos.idProduto", produto.getIdProduto()).findFirst();
                if (cartItem != null) {
                    holder.product_count.setText(String.valueOf(cartItem.quantity));
                    holder.linearAddRemove.setVisibility(View.VISIBLE);
                    holder.btn_addCart.setEnabled(false);
                    holder.btn_addCart.setVisibility(View.INVISIBLE);



                } else {

                    holder.product_count.setText(String.valueOf(0));
                    holder.linearAddRemove.setVisibility(View.GONE);
                    holder.btn_addCart.setEnabled(true);
                    holder.btn_addCart.setVisibility(View.VISIBLE);
                }
            }

        }


    }

    @Override
    public int getItemCount() {
        return produtosList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //        private ImageView thumbnail;
        private ImageView thumbnail;
        private TextView name;
        private TextView descricao;
        private TextView price;
        private ImageView btn_addCart;

        private ImageView ic_add,ic_remove;
        private TextView product_count;
        private LinearLayout linearAddRemove;

        public ItemViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEW_TYPE_LIST) {
                thumbnail = itemView.findViewById(R.id.imgProdList);
                name =  itemView.findViewById(R.id.titleProdList);
                descricao =  itemView.findViewById(R.id.descProdList);
                price =  itemView.findViewById(R.id.priceProdList);
                btn_addCart =  itemView.findViewById(R.id.btn_addCart);

                ic_remove =  itemView.findViewById(R.id.ic_remove);
                product_count =  itemView.findViewById(R.id.product_count);
                ic_add =  itemView.findViewById(R.id.ic_add);
                linearAddRemove = itemView.findViewById(R.id.linearAddRemove);

            } else {
                thumbnail =  itemView.findViewById(R.id.imgProdGrid);
                name = itemView.findViewById(R.id.titleProdGrid);
                price =  itemView.findViewById(R.id.priceProdGrid);
                btn_addCart = itemView.findViewById(R.id.btn_addCart);

                ic_remove =  itemView.findViewById(R.id.ic_remove);
                product_count =  itemView.findViewById(R.id.product_count);
                ic_add =  itemView.findViewById(R.id.ic_add);

                linearAddRemove = itemView.findViewById(R.id.linearAddRemove);
            }


            itemView.setTag(itemView);
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

    public void setCartItems(RealmResults<CartItemProdutos> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }

    public void updateItem(int position, RealmResults<CartItemProdutos> cartItems) {
        this.cartItems = cartItems;
        notifyItemChanged(position);
    }

    public interface ProductsAdapterListener {
        void onProductAddedCart(int index, Produtos product);

        void onProductRemovedFromCart(int index, Produtos product);
    }

}
