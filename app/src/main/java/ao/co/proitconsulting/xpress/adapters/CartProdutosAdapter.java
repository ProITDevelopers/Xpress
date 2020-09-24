package ao.co.proitconsulting.xpress.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;
import ao.co.proitconsulting.xpress.modelos.Produtos;
import io.realm.RealmResults;

public class CartProdutosAdapter extends RecyclerView.Adapter<CartProdutosAdapter.ItemViewHolder> {

    private Context context;
    private List<CartItemProdutos> cartItems = Collections.emptyList();
    private CartProductsAdapterListener listener;



    public CartProdutosAdapter(Context context, CartProductsAdapterListener listener) {
        this.context = context;
        this.listener = listener;

    }

    public void setData(List<CartItemProdutos> cartItems) {
        if (cartItems == null) {
            this.cartItems = Collections.emptyList();
        }

        this.cartItems = cartItems;

        notifyDataSetChanged();
    }

    public void updateItem(int position, RealmResults<CartItemProdutos> cartItems) {
        this.cartItems = cartItems;
        notifyItemChanged(position);
    }




    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart_list, parent, false);
        return new ItemViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        CartItemProdutos cartItem = cartItems.get(position);
        Produtos product = cartItem.produtos;

        Picasso.with(context).load(product.getImagemProduto()).placeholder(R.drawable.store_placeholder).into(holder.thumbnail);



        holder.name.setText(product.getDescricaoProdutoC());
        holder.price.setText(holder.name.getContext().getString(R.string.lbl_item_price_quantity, context.getString(R.string.price_with_currency, product.getPrecoUnid())+ " AKZ", cartItem.quantity));
        holder.estabelecimento.setText(product.getEstabelecimento());


        if (listener != null)
            holder.btn_remove.setOnClickListener(view ->

                    listener.onCartItemRemoved(position, cartItem)
            );


        holder.ic_add.setOnClickListener(view -> {

            listener.onProductAddedCart(position, product);
        });

        holder.ic_remove.setOnClickListener(view -> {
            listener.onProductRemovedFromCart(position, product);
        });

        if (cartItems != null) {
            if (cartItem != null) {
                holder.product_count.setText(String.valueOf(cartItem.quantity));
            } else {
                holder.product_count.setText(String.valueOf(0));

            }
        }

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnail;
        private TextView name;
        private TextView price;
        private TextView estabelecimento;
        private ImageView btn_remove;

        private ImageView ic_add,ic_remove;
        private TextView product_count;


        public ItemViewHolder(View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            name =  itemView.findViewById(R.id.name);
            price =  itemView.findViewById(R.id.price);
            estabelecimento = itemView.findViewById(R.id.estabelecimento);
            btn_remove =  itemView.findViewById(R.id.btn_remove);

            ic_add =  itemView.findViewById(R.id.ic_add);
            ic_remove =  itemView.findViewById(R.id.ic_remove);
            product_count = itemView.findViewById(R.id.product_count);

        }


    }

    public interface CartProductsAdapterListener {
        void onCartItemRemoved(int index, CartItemProdutos cartItem);

        void onProductAddedCart(int index, Produtos product);

        void onProductRemovedFromCart(int index, Produtos product);
    }
}
