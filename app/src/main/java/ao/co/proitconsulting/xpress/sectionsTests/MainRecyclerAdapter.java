package ao.co.proitconsulting.xpress.sectionsTests;

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

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<CartItemProdutosSections> sectionList;

    public MainRecyclerAdapter(Context context,List<CartItemProdutosSections> sectionList) {
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

        CartItemProdutosSections section = sectionList.get(position);
        String sectionName = section.sectionName;
        List<CartItemProdutos> items = section.sectionItems;

        holder.sectionNameTextView.setText(sectionName);

        ChildRecyclerAdapter childRecyclerAdapter = new ChildRecyclerAdapter(items);
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


    static class ChildRecyclerAdapter extends RecyclerView.Adapter<ChildRecyclerAdapter.ViewHolder> {

        private Context context;
        private List<CartItemProdutos> cartItems = Collections.emptyList();
        private CartProductsSectionAdapterListener listener;

        public ChildRecyclerAdapter(List<CartItemProdutos> cartItems) {
            this.cartItems = cartItems;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.item_cart_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CartItemProdutos cartItem = cartItems.get(position);
            Produtos product = cartItem.produtos;

            Picasso.with(context).load(product.getImagemProduto()).fit().centerCrop().placeholder(R.drawable.store_placeholder).into(holder.thumbnail);



            holder.name.setText(product.getDescricaoProdutoC());
            holder.price.setText(holder.name.getContext().getString(R.string.lbl_item_price_quantity, context.getString(R.string.price_with_currency, product.getPrecoUnid())+ " AKZ", cartItem.quantity));
            holder.estabelecimento.setText(product.getEstabelecimento());


//            if (listener != null)
//                holder.btn_remove.setOnClickListener(view ->
//
//                        listener.onCartItemRemoved(position, cartItem)
//                );
//
//
//            holder.ic_add.setOnClickListener(view -> {
//
//                listener.onProductAddedCart(position, product);
//            });
//
//            holder.ic_remove.setOnClickListener(view -> {
//                listener.onProductRemovedFromCart(position, product);
//            });

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

        class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView thumbnail;
            private TextView name;
            private TextView price;
            private TextView estabelecimento;
            private ImageView btn_remove;

            private ImageView ic_add,ic_remove;
            private TextView product_count;

            public ViewHolder(@NonNull View itemView) {
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
    }


    public interface CartProductsSectionAdapterListener {
        void onCartItemRemoved(int index, CartItemProdutos cartItem);

        void onProductAddedCart(int index, Produtos product);

        void onProductRemovedFromCart(int index, Produtos product);
    }
}
