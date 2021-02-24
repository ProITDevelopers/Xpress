package ao.co.proitconsulting.xpress.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.adapters.ProductDetailsListener;
import ao.co.proitconsulting.xpress.localDB.AppDatabase;
import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;
import ao.co.proitconsulting.xpress.modelos.Produtos;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ProdutoDetailsActivity extends AppCompatActivity implements ProductDetailsListener {




    private String toolbarTitle ="";
    private Menu menu;

    private ImageView productImg;
    private TextView txtProdutoNome,txtProdutoPrice,txtDescricao;
    private ImageView ic_remove,ic_add;
    private TextView product_count;
    private Button btn_add,btn_comprar;

    private FrameLayout frameLayoutFab;
    private FloatingActionButton fabCart;


    private Realm realm;
    private RealmResults<CartItemProdutos> cartItems;
    private RealmChangeListener<RealmResults<CartItemProdutos>> cartRealmChangeListener;
    private int cart_count = 0;
    private Produtos produtos;
    private CartItemProdutos cartItem;
    private ProductDetailsListener listener;
    private int produtoId,position;
    private Drawable btn_addCart_green_bk,btn_addCart_grey_bk;
    private ConstraintLayout product_detail_root;
    private NotificationBadge badge,fabBadge;
    private ImageView cart_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showCustomUI();
        if (getIntent()!=null){
            toolbarTitle = getIntent().getStringExtra("toolbarTitle");
            produtoId = getIntent().getIntExtra("produtoId",1);
            position = getIntent().getIntExtra("position",0);
        }
        setContentView(R.layout.activity_produto_details);

        collapseToobar();

        realm = Realm.getDefaultInstance();
        cartItems = realm.where(CartItemProdutos.class).findAllAsync();
        produtos = realm.where(Produtos.class).equalTo("idProduto", produtoId).findFirst();
        cartItem = realm.where(CartItemProdutos.class).equalTo("produtos.idProduto", produtoId).findFirst();

        btn_addCart_green_bk = getResources().getDrawable(R.drawable.button_background) ;
        btn_addCart_grey_bk = getResources().getDrawable( R.drawable.button_background_grey );


        listener = this;


        initViews();

        cartRealmChangeListener = cartItems -> {

            if (cartItems != null && cartItems.size() > 0) {
                setCartInfoBar(cartItems);
            } else {
                cart_count = 0;
                invalidateOptionsMenu();

                updateFabCartCount();
            }

            invalidateOptionsMenu();
        };



        





    }

    private void showCustomUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

    }

    private void collapseToobar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(toolbarTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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

        productImg = findViewById(R.id.productImg);
        Picasso.with(this).load(produtos.imagemProduto).fit().centerCrop().placeholder(R.drawable.store_placeholder).into(productImg);



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



        frameLayoutFab = findViewById(R.id.frameLayoutFab);
        fabCart = findViewById(R.id.fabCart);
        fabBadge = findViewById(R.id.fabBadge);

        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProdutoDetailsActivity.this,ShoppingCartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                Intent intent = new Intent(ProdutoDetailsActivity.this, ShopCartActivity.class);
                startActivity(intent);
            }
        });

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

                if (cartItem != null) {
                    Intent intent = new Intent(ProdutoDetailsActivity.this,ShoppingCartActivity.class);
//                    Intent intent = new Intent(ProdutoDetailsActivity.this,ShopCartActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    listener.onProductAddedCart(position,produtos);
                    Intent intent = new Intent(ProdutoDetailsActivity.this,ShoppingCartActivity.class);
//                    Intent intent = new Intent(ProdutoDetailsActivity.this,ShopCartActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }



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

        updateFabCartCount();


    }

    private void updateCartCount() {
        if (badge == null) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (cart_count == 0)
                    badge.setVisibility(View.INVISIBLE);
                else{
                    badge.setVisibility(View.VISIBLE);
                    badge.setText(String.valueOf(cart_count));
                }
            }
        });
    }

    private void updateFabCartCount() {
        if (fabBadge == null) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (cart_count == 0)
                    fabBadge.setVisibility(View.INVISIBLE);
                else{
                    fabBadge.setVisibility(View.VISIBLE);
                    fabBadge.setText(String.valueOf(cart_count));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_options; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_produto_details, menu);
        hideOption(R.id.action_cart);

        View view = menu.findItem(R.id.action_cart).getActionView();
        badge = (NotificationBadge)view.findViewById(R.id.badge);
        cart_icon = (ImageView) view.findViewById(R.id.cart_icon);
        cart_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProdutoDetailsActivity.this,ShoppingCartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            Intent intent = new Intent(this, ShopCartActivity.class);
                startActivity(intent);
            }
        });
        updateCartCount();



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
//            Intent intent = new Intent(this,ShopCartActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,ConfiguracoesActivity.class);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);


    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
        frameLayoutFab.setVisibility(View.VISIBLE);

    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
        frameLayoutFab.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        updateCartCount();
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