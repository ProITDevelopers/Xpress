package ao.co.proitconsulting.xpress.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.adapters.ProdutosAdapter;
import ao.co.proitconsulting.xpress.adapters.RecyclerViewOnItemClickListener;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.localDB.AppDatabase;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;
import ao.co.proitconsulting.xpress.modelos.Estabelecimento;
import ao.co.proitconsulting.xpress.modelos.Produtos;
import ao.co.proitconsulting.xpress.utilityClasses.AddBadgeCartConverter;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProdutosActivity extends AppCompatActivity implements ProdutosAdapter.ProductsAdapterListener{

    private static final String TAG = "TAG_ProdutosActivity";

    @SerializedName("ideStabelecimento")
    public int ideStabelecimento;

    List<Produtos> produtosList = new ArrayList<>();



    private RecyclerView recyclerView;
    private ProdutosAdapter itemAdapter;
    private GridLayoutManager gridLayoutManager;
    private ProgressBar progressBar;


    ImageView header;

    int cart_count = 0;
    int imgCart = R.drawable.ic_baseline_shopping_cart_24;

    private Realm realm;
    private RealmResults<CartItemProdutos> cartItems;
    private RealmChangeListener<RealmResults<CartItemProdutos>> cartRealmChangeListener;

    private ConstraintLayout coordinatorLayout;
    private RelativeLayout errorLayout;
    private TextView btnTentarDeNovo;

    private TextView txtEstabNome,txtEstabAddress;
    private Estabelecimento estabelecimento;
    private ImageView imgShopCart;

    //DIALOG_LAYOUT_CONFIRMAR_PROCESSO
    private Dialog dialogLayoutConfirmarProcesso;
    private ImageView imgConfirm;
    private TextView txtConfirmTitle,txtConfirmMsg;
    private Button dialog_btn_deny_processo,dialog_btn_accept_processo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showCustomUI();
        if (getIntent()!=null){
            estabelecimento = (Estabelecimento) getIntent().getSerializableExtra("estabelecimento");

            if (estabelecimento!=null){
                if (estabelecimento.estabelecimentoID!=0){
                    ideStabelecimento = estabelecimento.estabelecimentoID;
                }
            }
        }
        setContentView(R.layout.activity_produtos);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        errorLayout = findViewById(R.id.erroLayout);
        btnTentarDeNovo = findViewById(R.id.btn);


        realm = Realm.getDefaultInstance();
        cartItems = realm.where(CartItemProdutos.class).findAllAsync();


        header = findViewById(R.id.imgHeader);
        txtEstabNome = findViewById(R.id.txtEstabNome);
        txtEstabAddress = findViewById(R.id.txtEstabAddress);
        imgShopCart = findViewById(R.id.imgShopCart);

        imgShopCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProdutosActivity.this,ShoppingCartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        Picasso.with(this).load(estabelecimento.imagemCapa).placeholder(R.drawable.store_placeholder).into(header);
        txtEstabNome.setText(estabelecimento.nomeEstabelecimento);
        txtEstabAddress.setText(estabelecimento.endereco);



        recyclerView =  findViewById(R.id.recyclewProdutos);
        gridLayoutManager = new GridLayoutManager(this, 1);
        progressBar = findViewById(R.id.progressBar);



//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        verifConecxaoProdutos();

        cartRealmChangeListener = cartItems -> {
//            Timber.d("Cart items changed! " + this.cartItems.size());
            if (cartItems != null && cartItems.size() > 0) {
                setCartInfoBar(cartItems);
//                toggleCartBar(true);
            } else {
//                toggleCartBar(false);
                cart_count = 0;
                imgShopCart.setImageResource(R.drawable.ic_baseline_shopping_cart_24);

            }

            if (itemAdapter!=null)
                itemAdapter.setCartItems(cartItems);

        };


        //-------------------------------------------------------------//
        //-------------------------------------------------------------//
        //DIALOG_LAYOUT_CONFIRMAR_PROCESSO
        dialogLayoutConfirmarProcesso = new Dialog(this);
        dialogLayoutConfirmarProcesso.setContentView(R.layout.layout_confirmar_processo);
        dialogLayoutConfirmarProcesso.setCancelable(true);
        if (dialogLayoutConfirmarProcesso.getWindow()!=null)
            dialogLayoutConfirmarProcesso.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        imgConfirm = dialogLayoutConfirmarProcesso.findViewById(R.id.imgConfirm);
        txtConfirmTitle = dialogLayoutConfirmarProcesso.findViewById(R.id.txtConfirmTitle);
        txtConfirmMsg = dialogLayoutConfirmarProcesso.findViewById(R.id.txtConfirmMsg);
        dialog_btn_deny_processo = dialogLayoutConfirmarProcesso.findViewById(R.id.dialog_btn_deny_processo);
        dialog_btn_accept_processo = dialogLayoutConfirmarProcesso.findViewById(R.id.dialog_btn_accept_processo);


    }
    private void showCustomUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


    private void verifConecxaoProdutos() {
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null) {
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null){
                mostarMsnErro();
            } else {
                carregarProductsList();
//                renderProducts();
            }
        }
    }

    private void mostarMsnErro(){

        if (errorLayout.getVisibility() == View.GONE){
            errorLayout.setVisibility(View.VISIBLE);

            coordinatorLayout.setVisibility(View.GONE);

        }

        btnTentarDeNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coordinatorLayout.setVisibility(View.VISIBLE);

                errorLayout.setVisibility(View.GONE);
                verifConecxaoProdutos();
            }
        });
    }

    private void setCartInfoBar(RealmResults<CartItemProdutos> cartItems) {
        int itemCount = 0;
        for (CartItemProdutos cartItem : cartItems) {
            itemCount += cartItem.quantity;
        }
//        cartInfoBar.setData(itemCount, String.valueOf(Utils.getCartPrice(cartItems)));
//        cart_count = itemCount;
        cart_count = cartItems.size();



        imgShopCart.setImageDrawable(AddBadgeCartConverter.convertLayoutToImage(this,cart_count,imgCart));

    }

    @Override
    public void onProductAddedCart(int index, Produtos product) {
        AppDatabase.addItemToCart(this,product);
        if (cartItems != null) {
            itemAdapter.updateItem(index, cartItems);

        }
    }

    @Override
    public void onProductRemovedFromCart(int index, Produtos product) {
        AppDatabase.removeCartItem(product);
        if (cartItems != null) {
            itemAdapter.updateItem(index, cartItems);

        }

    }

    private void carregarProductsList() {
        progressBar.setVisibility(View.VISIBLE);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Produtos>> rv = apiInterface.getAllProdutosDoEstabelecimento(estabelecimento.estabelecimentoID);
        rv.enqueue(new Callback<List<Produtos>>() {
            @Override
            public void onResponse(@NonNull Call<List<Produtos>> call, @NonNull Response<List<Produtos>> response) {

                if (response.isSuccessful()) {

                    if (response.body()!=null){

                        produtosList = response.body();

                        setProdutosAdapters(produtosList);
                        AppDatabase.saveProducts(produtosList);

                    } else {
                        Toast.makeText(ProdutosActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                } else {

                    progressBar.setVisibility(View.GONE);
                    if (response.code()==401){
                        mensagemTokenExpirado();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Produtos>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                if (!MetodosUsados.conexaoInternetTrafego(ProdutosActivity.this,TAG)){
                    MetodosUsados.mostrarMensagem(ProdutosActivity.this,R.string.msg_erro_internet);
                }else  if ("timeout".equals(t.getMessage())) {
                    MetodosUsados.mostrarMensagem(ProdutosActivity.this,R.string.msg_erro_internet_timeout);
                }else {
                    MetodosUsados.mostrarMensagem(ProdutosActivity.this,R.string.msg_erro);
                }
            }
        });
    }

    private void mensagemTokenExpirado() {
        imgConfirm.setImageResource(R.drawable.xpress_logo);
        txtConfirmTitle.setText(getString(R.string.a_sessao_expirou));
        txtConfirmMsg.setText(getString(R.string.inicie_outra_vez_a_sessao));

        dialog_btn_accept_processo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLayoutConfirmarProcesso.cancel();
                logOutTemp();
            }
        });

        dialog_btn_deny_processo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogLayoutConfirmarProcesso.cancel();

            }
        });

        dialogLayoutConfirmarProcesso.show();
    }

    private void setProdutosAdapters(List<Produtos> produtosList) {

        if (produtosList.size()>0){

            itemAdapter = new ProdutosAdapter(this, produtosList,this, gridLayoutManager,ideStabelecimento);
            recyclerView.setAdapter(itemAdapter);
            recyclerView.setLayoutManager(gridLayoutManager);

            itemAdapter.setItemClickListener(new RecyclerViewOnItemClickListener() {
                @Override
                public void onItemClickListener(int position) {
                    Produtos produto = produtosList.get(position);

                }
            });


        }


    }

    private void logOutTemp() {

        AppPrefsSettings.getInstance().deleteToken();
        Intent intent = new Intent(this, SplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }




    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (cartItems != null) {
            cartItems.addChangeListener(cartRealmChangeListener);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cartItems != null) {
            cartItems.addChangeListener(cartRealmChangeListener);
        }
        if (realm != null) {
            realm.close();
        }
        progressBar.setVisibility(View.GONE);
        dialogLayoutConfirmarProcesso.cancel();
    }
}