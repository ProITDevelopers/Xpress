package ao.co.proitconsulting.xpress.fragmentos.produtos_detail;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.activities.ProdutoDetailsActivity;
import ao.co.proitconsulting.xpress.activities.ShoppingCartActivity;
import ao.co.proitconsulting.xpress.adapters.ProductDetailsListener;
import ao.co.proitconsulting.xpress.fragmentos.produtos_estab.ProdutosEstabViewModel;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.localDB.AppDatabase;
import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;
import ao.co.proitconsulting.xpress.modelos.Estabelecimento;
import ao.co.proitconsulting.xpress.modelos.Produtos;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ProdutoDetailFragment extends Fragment implements ProductDetailsListener {

    private ProdutoDetailViewModel produtoDetailViewModel;
    private View view;

    private ImageView productImg;
    private TextView txtProdutoNome,txtProdutoPrice,txtDescricao;
    private ImageView ic_remove,ic_add;
    private TextView product_count;
    private Button btn_comprar;

    private Realm realm;
    private RealmResults<CartItemProdutos> cartItems;
    private RealmChangeListener<RealmResults<CartItemProdutos>> cartRealmChangeListener;
    private int cart_count = 0;
    private Produtos produtos;
    private CartItemProdutos cartItem;
    private ProductDetailsListener listener;
    private int produtoId,position;

    private ConstraintLayout product_detail_root;


    private TextView textViewTitle;



    public ProdutoDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        produtoDetailViewModel =
                new ViewModelProvider(this).get(ProdutoDetailViewModel.class);

        view = inflater.inflate(R.layout.fragment_produto_detail, container, false);
        initViews();

        produtoDetailViewModel.getProdutoMutableLiveData().observe(this, new Observer<Produtos>() {
            @Override
            public void onChanged(Produtos produto) {
                ((AppCompatActivity)getActivity())
                        .getSupportActionBar()
                        .setTitle(produto.getEstabelecimento());

                produtoId = produto.getIdProduto();
                position = Common.selectedProdutoPosition;

            }
        });




        return view;
    }

    private void initViews() {

        textViewTitle = view.findViewById(R.id.textViewTitle);

        realm = Realm.getDefaultInstance();
        cartItems = realm.where(CartItemProdutos.class).findAllAsync();

        product_detail_root = view.findViewById(R.id.product_detail_root);

        productImg = view.findViewById(R.id.productImg);
        txtProdutoNome = view.findViewById(R.id.txtProdutoNome);
        txtProdutoPrice = view.findViewById(R.id.txtProdutoPrice);
        txtDescricao = view.findViewById(R.id.txtDescricao);

        ic_remove = view.findViewById(R.id.ic_remove);
        product_count = view.findViewById(R.id.product_count);
        ic_add = view.findViewById(R.id.ic_add);

        btn_comprar = view.findViewById(R.id.btn_comprar);



        listener = this;
    }


    @Override
    public void onResume() {
        fillAllViews();
        if (cartItems != null) {
            cartItems.addChangeListener(cartRealmChangeListener);
        }
        super.onResume();
    }

    private void fillAllViews() {
        produtos = realm.where(Produtos.class).equalTo("idProduto", produtoId).findFirst();
        cartItem = realm.where(CartItemProdutos.class).equalTo("produtos.idProduto", produtoId).findFirst();

        cartRealmChangeListener = cartItems -> {

        };

        Picasso.with(getContext()).load(produtos.imagemProduto)
                .fit().centerCrop()
                .placeholder(R.drawable.store_placeholder).into(productImg);

        textViewTitle.setText(produtos.getDescricaoProdutoC());
        txtProdutoNome.setText(produtos.getDescricaoProdutoC());
        txtDescricao.setText(produtos.getDescricaoProduto());
        String preco = String.valueOf(produtos.getPrecoUnid());
        txtProdutoPrice.setText(getString(R.string.price_with_currency, Float.parseFloat(preco)).concat(" AKZ"));

        ic_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onProductRemovedFromCart(position,produtos);
            }
        });

        ic_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onProductAddedCart(position,produtos);
            }
        });



        btn_comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cartItem != null) {
                    Intent intent = new Intent(getContext(), ShoppingCartActivity.class);
//                    Intent intent = new Intent(ProdutoDetailsActivity.this,ShopCartActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    listener.onProductAddedCart(position,produtos);
                    Intent intent = new Intent(getContext(),ShoppingCartActivity.class);
//                    Intent intent = new Intent(ProdutoDetailsActivity.this,ShopCartActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }



            }
        });

        checkIfCartAsItems();

//        toolbarFrag.setTitle(produtos.getDescricaoProdutoC());



    }


    private void checkIfCartAsItems(){

        cartItem = realm.where(CartItemProdutos.class).equalTo("produtos.idProduto", produtoId).findFirst();
        if (cartItem != null) {

            if (cartItem.quantity == 1){
                product_count.setText(String.valueOf(cartItem.quantity).concat(" Item"));
            }

            if (cartItem.quantity > 1){
                product_count.setText(String.valueOf(cartItem.quantity).concat(" Items"));
            }

            product_count.setVisibility(View.VISIBLE);



        } else {
            product_count.setVisibility(View.INVISIBLE);
            product_count.setText(String.valueOf(0));


        }

    }


    @Override
    public void onProductAddedCart(int index, Produtos product) {
        AppDatabase.addItemToCart(getContext(),product);
        cartItem = cartItems.where().equalTo("produtos.idProduto", product.getIdProduto()).findFirst();


        if (cartItem != null) {

            if (cartItem.quantity==1){
                Snackbar.make(product_detail_root, produtos.getDescricaoProdutoC()+" adicionado ao Carrinho!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            if (cartItem.quantity == 1){
                product_count.setText(String.valueOf(cartItem.quantity).concat(" Item"));
            }

            if (cartItem.quantity > 1){
                product_count.setText(String.valueOf(cartItem.quantity).concat(" Items"));
            }

            product_count.setVisibility(View.VISIBLE);



        }

    }

    @Override
    public void onProductRemovedFromCart(int index, Produtos product) {
        AppDatabase.removeCartItem(product);
        cartItem = cartItems.where().equalTo("produtos.idProduto", product.getIdProduto()).findFirst();

        if (cartItem != null) {

            if (cartItem.quantity == 1){
                product_count.setText(String.valueOf(cartItem.quantity).concat(" Item"));
            }

            if (cartItem.quantity > 1){
                product_count.setText(String.valueOf(cartItem.quantity).concat(" Items"));
            }




        }else {

            Snackbar.make(product_detail_root, produtos.getDescricaoProdutoC()+" removido do Carrinho!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            product_count.setVisibility(View.INVISIBLE);
            product_count.setText(String.valueOf(0));




        }

    }



    @Override
    public void onDestroyView() {
        EventBus.getDefault().removeAllStickyEvents();

        if (cartItems != null) {
            cartItems.addChangeListener(cartRealmChangeListener);
        }
        if (realm != null) {
            realm.close();
        }

        super.onDestroyView();
    }


}
