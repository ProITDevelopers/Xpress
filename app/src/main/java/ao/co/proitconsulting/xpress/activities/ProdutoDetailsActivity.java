package ao.co.proitconsulting.xpress.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.adapters.ProdutosAdapter;
import ao.co.proitconsulting.xpress.localDB.AppDatabase;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;
import ao.co.proitconsulting.xpress.modelos.Produtos;
import ao.co.proitconsulting.xpress.utilityClasses.AddBadgeCartConverter;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ProdutoDetailsActivity extends AppCompatActivity implements ProdutosAdapter.ProductsAdapterListener {

    private FloatingActionButton fabCart;
    int imgCart = R.drawable.ic_baseline_shopping_cart_white_24;

    private String toolbarTitle ="",img="";
    private ImageView productImg;
    private Menu menu;

    private Realm realm;
    private RealmResults<CartItemProdutos> cartItems;
    private RealmChangeListener<RealmResults<CartItemProdutos>> cartRealmChangeListener;
    int cart_count = 0;

    private TextView txtProdutoNome,txtProdutoPrice,txtDescricao;
    private ImageView ic_remove,ic_add;
    private TextView product_count;
    private Button btn_add,btn_comprar;

    private Produtos produtos;
    private CartItemProdutos cartItem;
    private ProdutosAdapter.ProductsAdapterListener listener;
    private int produtoId,position;
    private Drawable btn_addCart_green_bk,btn_addCart_grey_bk;
    private ConstraintLayout product_detail_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showCustomUI();
        if (getIntent()!=null){
            toolbarTitle = getIntent().getStringExtra("toolbarTitle");
            img = getIntent().getStringExtra("productImg");
            produtoId = getIntent().getIntExtra("produtoId",1);
            position = getIntent().getIntExtra("position",0);
        }

        setContentView(R.layout.activity_produto_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(toolbarTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        listener = this;

        btn_addCart_green_bk = getResources().getDrawable( R.drawable.button_background );
        btn_addCart_grey_bk = getResources().getDrawable( R.drawable.button_background_grey );

        productImg = findViewById(R.id.productImg);
        Picasso.with(this).load(img).placeholder(R.drawable.store_placeholder).into(productImg);

        fabCart = findViewById(R.id.fabCart);
        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProdutoDetailsActivity.this,ShoppingCartActivity.class);
                startActivity(intent);
            }
        });
        
        collapseToobar();



        realm = Realm.getDefaultInstance();
        cartItems = realm.where(CartItemProdutos.class).findAllAsync();

        produtos = realm.where(Produtos.class).equalTo("idProduto", produtoId).findFirst();
        cartItem = realm.where(CartItemProdutos.class).equalTo("produtos.idProduto", produtoId).findFirst();

        cartRealmChangeListener = cartItems -> {

            if (cartItems != null && cartItems.size() > 0) {
                setCartInfoBar(cartItems);
            } else {
                cart_count = 0;
                invalidateOptionsMenu();
                fabCart.setImageResource(R.drawable.ic_baseline_shopping_cart_white_24);
            }

            invalidateOptionsMenu();
        };
        initViews();
    }

    private void showCustomUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void collapseToobar() {
        AppBarLayout mAppBarLayout = findViewById(R.id.appBarLayout);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    showOption(R.id.action_cart);
                } else if (isShow) {
                    isShow = false;
                    hideOption(R.id.action_cart);
                }
            }
        });
    }

    private void initViews(){

        product_detail_root = findViewById(R.id.product_detail_root);
        txtProdutoNome = findViewById(R.id.txtProdutoNome);
        txtProdutoPrice = findViewById(R.id.txtProdutoPrice);
        txtDescricao = findViewById(R.id.txtDescricao);

        txtProdutoNome.setText(produtos.getDescricaoProdutoC());
        txtDescricao.setText(produtos.getDescricaoProduto());
        String preco = String.valueOf(produtos.getPrecoUnid());
        txtProdutoPrice.setText(getString(R.string.price_with_currency, Float.parseFloat(preco)).concat(" AKZ"));

        ic_remove = findViewById(R.id.ic_remove);
        product_count = findViewById(R.id.product_count);
        ic_add = findViewById(R.id.ic_add);

        btn_add = findViewById(R.id.btn_add);
        btn_comprar = findViewById(R.id.btn_comprar);

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

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onProductAddedCart(position,produtos);
            }
        });

        btn_comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProdutoDetailsActivity.this,ShoppingCartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        checkIfCartAsItems();

    }



    private void setCartInfoBar(RealmResults<CartItemProdutos> cartItems) {

        int itemCount = 0;
        for (CartItemProdutos cartItem : cartItems) {
            itemCount += cartItem.quantity;
        }

        cart_count = cartItems.size();

        fabCart.setImageDrawable(AddBadgeCartConverter.convertLayoutToImage(this,cart_count,imgCart));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_options; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_produto_details, menu);
        hideOption(R.id.action_cart);

        MenuItem menuItem = menu.findItem(R.id.action_cart);
        menuItem.setIcon(AddBadgeCartConverter.convertLayoutToImage(this,cart_count,R.drawable.ic_baseline_shopping_cart_white_24));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        if (id == R.id.action_cart) {
            Intent intent = new Intent(this,ShoppingCartActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);


    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    @Override
    protected void onResume() {
        checkIfCartAsItems();
        if (cartItems != null) {
            cartItems.addChangeListener(cartRealmChangeListener);
        }

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (cartItems != null) {
            cartItems.addChangeListener(cartRealmChangeListener);
        }
        if (realm != null) {
            realm.close();
        }
        super.onDestroy();
    }


    @Override
    public void onProductAddedCart(int index, Produtos product) {
        AppDatabase.addItemToCart(this,product);
        cartItem = cartItems.where().equalTo("produtos.idProduto", product.getIdProduto()).findFirst();


        if (cartItem != null) {

            if (cartItem.quantity==1){
                Snackbar.make(product_detail_root, produtos.getDescricaoProdutoC()+" adicionado ao Carrinho!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            product_count.setText(String.valueOf(cartItem.quantity));
            ic_remove.setVisibility(View.VISIBLE);
            product_count.setVisibility(View.VISIBLE);
            btn_add.setText("Adicionado");
            btn_add.setEnabled(false);
            btn_add.setBackground(btn_addCart_grey_bk);


        }

    }

    @Override
    public void onProductRemovedFromCart(int index, Produtos product) {
        AppDatabase.removeCartItem(product);
        cartItem = cartItems.where().equalTo("produtos.idProduto", product.getIdProduto()).findFirst();

        if (cartItem != null) {

            product_count.setText(String.valueOf(cartItem.quantity));




        }else {

            Snackbar.make(product_detail_root, produtos.getDescricaoProdutoC()+" removido do Carrinho!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            product_count.setText(String.valueOf(0));


            ic_remove.setVisibility(View.GONE);
            product_count.setVisibility(View.GONE);
            btn_add.setText("Adicionar");
            btn_add.setEnabled(true);
            btn_add.setBackground(btn_addCart_green_bk);

        }

    }

    private void checkIfCartAsItems(){

        cartItem = realm.where(CartItemProdutos.class).equalTo("produtos.idProduto", produtoId).findFirst();
        if (cartItem != null) {
            product_count.setText(String.valueOf(cartItem.quantity));
            ic_add.setVisibility(View.VISIBLE);
            ic_remove.setVisibility(View.VISIBLE);
            product_count.setVisibility(View.VISIBLE);
            btn_add.setEnabled(false);
            btn_add.setText("Adicionado");
            btn_add.setBackground(btn_addCart_grey_bk);


        } else {
            product_count.setText(String.valueOf(0));
            ic_add.setVisibility(View.VISIBLE);
            ic_remove.setVisibility(View.GONE);
            product_count.setVisibility(View.GONE);
            btn_add.setText("Adicionar");
            btn_add.setEnabled(true);
            btn_add.setBackground(btn_addCart_green_bk);

        }

    }
}