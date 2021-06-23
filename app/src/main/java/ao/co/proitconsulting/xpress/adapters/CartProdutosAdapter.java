package ao.co.proitconsulting.xpress.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

import ao.co.proitconsulting.xpress.Callback.IRecyclerClickListener;
import ao.co.proitconsulting.xpress.EventBus.CartProdutoClick;
import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.activities.ShoppingCartActivity;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.helper.Utils;
import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;
import ao.co.proitconsulting.xpress.modelos.ProdutoExtra;
import ao.co.proitconsulting.xpress.modelos.Produtos;
import io.realm.RealmResults;

public class CartProdutosAdapter extends RecyclerView.Adapter<CartProdutosAdapter.ItemViewHolder> {

    private Context context;
    private List<CartItemProdutos> cartItems = Collections.emptyList();
    private CartProductsAdapterListener listener;
    private Gson gson;



    public CartProdutosAdapter(Context context, CartProductsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.gson = new Gson();

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
//        Produtos product = cartItem.produtos;

        Picasso.with(context).load(cartItem.imagemProduto)
                .fit().centerCrop().placeholder(R.drawable.store_placeholder).into(holder.thumbnail);



        holder.name.setText(cartItem.nomeProduto);

//        holder.price.setText(holder.name.getContext().getString(R.string.lbl_item_price_quantity, context.getString(R.string.price_with_currency, product.getPrecoUnid())+ " AKZ", cartItem.quantity));

        double displayPrice = Double.parseDouble(String.valueOf(cartItem.precoProduto));
        double total = displayPrice + cartItem.produtoPrecoExtra;
        holder.price.setText(new StringBuilder("")
                .append(Utils.formatPrice(total))
                .append(" AKZ").append(" x ").append(cartItem.quantity).toString());



        holder.estabelecimento.setText(cartItem.estabProduto);


        if (cartItem.produtoExtras!=null){
            List<ProdutoExtra> produtoExtraList = gson.fromJson(cartItem.produtoExtras,
                    new TypeToken<List<ProdutoExtra>>(){}.getType());
            if (produtoExtraList!=null && produtoExtraList.size()>0){
                holder.txtExtras.setVisibility(View.VISIBLE);
                holder.txtExtras.setText(new StringBuilder("").append(Utils.getListAddon(produtoExtraList)));
            }

        }



        holder.setListener(new IRecyclerClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                Produtos selectedProduto = new Produtos();
                selectedProduto.setIdProduto(cartItems.get(position).idProduto);
                selectedProduto.setNomeProduto(cartItems.get(position).nomeProduto);
                selectedProduto.setDescricaoProduto(cartItems.get(position).descricaoProduto);
                selectedProduto.setPrecoProduto(cartItems.get(position).precoProduto);
                selectedProduto.setImagemProduto(cartItems.get(position).imagemProduto);
                selectedProduto.setEmStockProduto(cartItems.get(position).emStockProduto);
                selectedProduto.setIdCategoria(cartItems.get(position).idCategoria);
                selectedProduto.setCategoriaProduto(cartItems.get(position).categoriaProduto);
                selectedProduto.setIdEstabelecimento(cartItems.get(position).idEstabelecimento);
                selectedProduto.setEstabProduto(cartItems.get(position).estabProduto);
                selectedProduto.setLatitude(cartItems.get(position).latitude);
                selectedProduto.setLongitude(cartItems.get(position).longitude);




                Common.selectedProduto = selectedProduto;
                Common.selectedProdutoPosition = position;
                EventBus.getDefault().postSticky(new CartProdutoClick(true, cartItems.get(position)));
                ((ShoppingCartActivity) context).finish();
            }
        });


        if (listener != null)
            holder.btn_remove.setOnClickListener(view ->

                    listener.onCartItemRemoved(position, cartItem)
            );


        holder.ic_add.setOnClickListener(view -> {

            Produtos product = new Produtos();
            product.setIdProduto(cartItems.get(position).idProduto);
            product.setNomeProduto(cartItems.get(position).nomeProduto);
            product.setDescricaoProduto(cartItems.get(position).descricaoProduto);
            product.setPrecoProduto(cartItems.get(position).precoProduto);
            product.setImagemProduto(cartItems.get(position).imagemProduto);
            product.setIdEstabelecimento(cartItems.get(position).idEstabelecimento);
            product.setEstabProduto(cartItems.get(position).estabProduto);
            product.setLatitude(cartItems.get(position).latitude);
            product.setLongitude(cartItems.get(position).longitude);


            product.setEmStockProduto(cartItems.get(position).emStockProduto);
            product.setIdCategoria(cartItems.get(position).idCategoria);
            product.setCategoriaProduto(cartItems.get(position).categoriaProduto);
            listener.onProductAddedCart(position, product);
        });

        holder.ic_remove.setOnClickListener(view -> {
            Produtos product = new Produtos();
            product.setIdProduto(cartItems.get(position).idProduto);
            product.setNomeProduto(cartItems.get(position).nomeProduto);
            product.setDescricaoProduto(cartItems.get(position).descricaoProduto);
            product.setPrecoProduto(cartItems.get(position).precoProduto);
            product.setImagemProduto(cartItems.get(position).imagemProduto);
            product.setIdEstabelecimento(cartItems.get(position).idEstabelecimento);
            product.setEstabProduto(cartItems.get(position).estabProduto);
            product.setLatitude(cartItems.get(position).latitude);
            product.setLongitude(cartItems.get(position).longitude);

            product.setEmStockProduto(cartItems.get(position).emStockProduto);
            product.setIdCategoria(cartItems.get(position).idCategoria);
            product.setCategoriaProduto(cartItems.get(position).categoriaProduto);
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

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout cardViewCart;
        private ImageView thumbnail;
        private TextView name;
        private TextView price;
        private TextView estabelecimento;
        private TextView txtExtras;
        private ImageView btn_remove;

        private ImageView ic_add,ic_remove;
        private TextView product_count;
        private IRecyclerClickListener iRecyclerClickListener;


        public ItemViewHolder(View itemView) {
            super(itemView);

            cardViewCart = itemView.findViewById(R.id.cardViewCart);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            name =  itemView.findViewById(R.id.name);
            price =  itemView.findViewById(R.id.price);
            estabelecimento = itemView.findViewById(R.id.estabelecimento);
            txtExtras = itemView.findViewById(R.id.txtExtras);
            btn_remove =  itemView.findViewById(R.id.btn_remove);

            ic_add =  itemView.findViewById(R.id.ic_add);
            ic_remove =  itemView.findViewById(R.id.ic_remove);
            product_count = itemView.findViewById(R.id.product_count);

            cardViewCart.setOnClickListener(this);
            thumbnail.setOnClickListener(this);

        }

        public void setListener(IRecyclerClickListener listener) {
            this.iRecyclerClickListener = listener;
        }

        @Override
        public void onClick(View view) {
            iRecyclerClickListener.onItemClickListener(view,getAdapterPosition());
        }


    }

    public interface CartProductsAdapterListener {
        void onCartItemRemoved(int index, CartItemProdutos cartItem);

        void onProductAddedCart(int index, Produtos product);

        void onProductRemovedFromCart(int index, Produtos product);
    }


}
