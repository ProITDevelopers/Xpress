package ao.co.proitconsulting.xpress.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.adapters.CartProdutosAdapter;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.helper.Utils;
import ao.co.proitconsulting.xpress.localDB.AppDatabase;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;
import ao.co.proitconsulting.xpress.modelos.LocalEncomenda;
import ao.co.proitconsulting.xpress.modelos.Produtos;
import ao.co.proitconsulting.xpress.modelos.UsuarioPerfil;
import ao.co.proitconsulting.xpress.utilityClasses.CartInfoBar;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ShoppingCartActivity extends AppCompatActivity implements CartProdutosAdapter.CartProductsAdapterListener{

    private static final String TAG = "TAG_ShoppingCartActivity";
    private static final int PLACE_PICKER_REQ_CODE = 12;

    private CartInfoBar cartInfoBar;
    private RecyclerView recyclerView;
    private Button btnCheckout;

    private Realm realm;
    private CartProdutosAdapter mAdapter;
    private RealmResults<CartItemProdutos> cartItems;
    private RealmChangeListener<RealmResults<CartItemProdutos>> cartItemRealmChangeListener;

    float totalPrice;
    int totalItems=0;
    String totalCart;

    String latitude,longitude,endereco,telefone,tipoPagamento;
    private UsuarioPerfil usuarioPerfil;

    //DIALOG_LAYOUT_NUM_TELEFONE_ADDRESS
    private Dialog dialogLayoutEnviarNumTelefoneAdress;
    private AppCompatEditText dialog_editTelefone,dialog_editEndereco;
    private TextView dialog_btn_meu_telefone;
    private Button dialog_btn_cancelar,dialog_btn_continuar;

    //DIALOG_LAYOUT_CONFIRMAR_TIPO_DE_PAGAMENTO
    private Dialog dialogLayoutTipoPagamento;
    private ImageView imgBtnFechar;
    private Button dialog_btn_multicaixa,dialog_btn_referencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(getString(R.string.carrinho));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        usuarioPerfil = AppPrefsSettings.getInstance().getUser();

        recyclerView = findViewById(R.id.recycler_view);
        btnCheckout = findViewById(R.id.btn_checkout);

        cartInfoBar  = findViewById(R.id.cart_info_bar);


        realm = Realm.getDefaultInstance();
        cartItems = realm.where(CartItemProdutos.class).findAllAsync();

        cartItemRealmChangeListener = cartItems -> {
            mAdapter.setData(cartItems);
            setTotalPrice();

            if (cartItems != null && cartItems.size() > 0) {
                setCartInfoBar(cartItems);
                toggleCartBar(true);

            } else {
                toggleCartBar(false);
            }
        };

        cartItems.addChangeListener(cartItemRealmChangeListener);
        init();

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirMapaActivity();
            }
        });

        //DIALOG_LAYOUT_NUM_TELEFONE_ADDRESS
        dialogLayoutEnviarNumTelefoneAdress = new Dialog(this);
        dialogLayoutEnviarNumTelefoneAdress.setContentView(R.layout.layout_confirm_num_telefone_address);
        dialogLayoutEnviarNumTelefoneAdress.setCancelable(false);
        if (dialogLayoutEnviarNumTelefoneAdress.getWindow()!=null)
            dialogLayoutEnviarNumTelefoneAdress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog_editTelefone = dialogLayoutEnviarNumTelefoneAdress.findViewById(R.id.dialog_editTelefone);
        dialog_editEndereco = dialogLayoutEnviarNumTelefoneAdress.findViewById(R.id.dialog_editEndereco);
        dialog_btn_meu_telefone = dialogLayoutEnviarNumTelefoneAdress.findViewById(R.id.dialog_btn_meu_telefone);
        dialog_btn_cancelar = dialogLayoutEnviarNumTelefoneAdress.findViewById(R.id.dialog_btn_cancelar);
        dialog_btn_continuar = dialogLayoutEnviarNumTelefoneAdress.findViewById(R.id.dialog_btn_continuar);


        //DIALOG_LAYOUT_CONFIRMAR_TIPO_DE_PAGAMENTO
        dialogLayoutTipoPagamento = new Dialog(this);
        dialogLayoutTipoPagamento.setContentView(R.layout.layout_tipo_pagamento);
        dialogLayoutTipoPagamento.setCancelable(false);
        if (dialogLayoutTipoPagamento.getWindow()!=null)
            dialogLayoutTipoPagamento.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        imgBtnFechar = dialogLayoutTipoPagamento.findViewById(R.id.imgBtnFechar);
        dialog_btn_multicaixa = dialogLayoutTipoPagamento.findViewById(R.id.dialog_btn_multicaixa);
        dialog_btn_referencia = dialogLayoutTipoPagamento.findViewById(R.id.dialog_btn_referencia);


    }

    private void abrirMapaActivity() {
        Intent intent = new Intent(ShoppingCartActivity.this,MapaActivity.class);
        intent.putExtra("toolbarTitle","Local de entrega");
        startActivityForResult(intent, PLACE_PICKER_REQ_CODE);
    }

    private void init() {
        mAdapter = new CartProdutosAdapter(this, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        setTotalPrice();

    }

    private void setTotalPrice() {
        if (cartItems != null) {
            float price = Utils.getCartPrice(cartItems);
            totalPrice = price;
            if (price > 0) {
                btnCheckout.setText(getString(R.string.checkout));
            } else {
                // if the price is zero, dismiss the dialog
//                dismiss();
                btnCheckout.setVisibility(View.GONE);
                btnCheckout.setText(getString(R.string.checkout));
//                Toast.makeText(this, "O carrinho está vazio!", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void setCartInfoBar(RealmResults<CartItemProdutos> cartItems) {
        int itemCount = 0;
        for (CartItemProdutos cartItem : cartItems) {
            itemCount += cartItem.quantity;
        }
        totalItems = itemCount;
        totalCart = String.valueOf(Utils.getCartPrice(cartItems));
        cartInfoBar.setData(itemCount, String.valueOf(Utils.getCartPrice(cartItems)));
    }

    private void toggleCartBar(boolean show) {
        if (show)
            cartInfoBar.setVisibility(View.VISIBLE);
        else
            cartInfoBar.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cartItems != null) {
            cartItems.removeChangeListener(cartItemRealmChangeListener);
        }

        if (realm != null) {
            realm.close();
        }

        dialogLayoutEnviarNumTelefoneAdress.cancel();
        dialogLayoutTipoPagamento.cancel();


    }

    @Override
    public void onCartItemRemoved(int index, CartItemProdutos cartItem) {
        AppDatabase.removeCartItem(cartItem);
    }

    @Override
    public void onProductAddedCart(int index, Produtos product) {
        AppDatabase.addItemToCart(this,product);
        if (cartItems != null) {
            mAdapter.updateItem(index, cartItems);

        }
    }

    @Override
    public void onProductRemovedFromCart(int index, Produtos product) {
        AppDatabase.removeCartItem(product);
        if (cartItems != null) {
            mAdapter.updateItem(index, cartItems);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_navigation_bottom; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shoppingcart, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        if (id == R.id.delete_cart) {

            if (cartItems != null && cartItems.size() > 0) {

                if (cartItems.size()==1){

                    Toast.makeText(this, "Remover este item!", Toast.LENGTH_SHORT).show();

                }

            } else {
                Toast.makeText(this, "O carrinho está vazio!", Toast.LENGTH_SHORT).show();
            }

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_PICKER_REQ_CODE) {
            if (resultCode == RESULT_OK) {

                latitude = data.getStringExtra("latitude");
                longitude = data.getStringExtra("longitude");
                endereco = data.getStringExtra("endereco");


                mostrarConfirmarNumeroEndereco(endereco);

            }
        }

    }

    private void mostrarConfirmarNumeroEndereco(String endereco) {



        //DIALOG_LAYOUT_NUM_TELEFONE_ADDRESS
        dialog_editEndereco.setText(endereco);
        dialog_btn_meu_telefone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telefone = usuarioPerfil.contactoMovel;
                dialog_editTelefone.setText(null);
                dialog_editTelefone.setError(null);
                dialog_editTelefone.setText(telefone);
            }
        });

        dialog_btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MetodosUsados.esconderTeclado(ShoppingCartActivity.this);
                limparCampos();


            }
        });

        dialog_btn_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (verificarCamposTelefone()){
                    limparCampos();
                    escolherTipodePagamento();
                }
            }
        });

        dialogLayoutEnviarNumTelefoneAdress.show();

    }


    private void limparCampos(){
        dialog_editTelefone.setText(null);
        dialog_editTelefone.setError(null);
        dialog_editEndereco.setText(null);
        dialog_editEndereco.setError(null);
        dialogLayoutEnviarNumTelefoneAdress.cancel();

    }

    private boolean verificarCamposTelefone() {

        telefone = dialog_editTelefone.getText().toString().trim();
        endereco = dialog_editEndereco.getText().toString().trim();

        if (telefone.isEmpty()){
            dialog_editTelefone.setError(getString(R.string.msg_erro_campo_vazio));
            return false;
        }

        if (!telefone.matches("9[1-9][0-9]\\d{6}")){
            dialog_editTelefone.setError(getString(R.string.msg_erro_num_telefone_invalido));
            return false;
        }

        if (endereco.isEmpty()) {
            dialog_editEndereco.requestFocus();
            dialog_editEndereco.setError(getString(R.string.msg_erro_campo_vazio));
            return false;
        }

        dialog_editTelefone.onEditorAction(EditorInfo.IME_ACTION_DONE);
        dialog_editEndereco.onEditorAction(EditorInfo.IME_ACTION_DONE);

        return true;
    }

    private void escolherTipodePagamento() {

        MetodosUsados.esconderTeclado(ShoppingCartActivity.this);

        imgBtnFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogLayoutTipoPagamento.cancel();
            }
        });

        dialog_btn_multicaixa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipoPagamento = dialog_btn_multicaixa.getText().toString();
                dialogLayoutTipoPagamento.cancel();
                abrirCheckOutActivity();

            }
        });

        dialog_btn_referencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tipoPagamento = dialog_btn_referencia.getText().toString();
                dialogLayoutTipoPagamento.cancel();
                abrirCheckOutActivity();
            }
        });

        dialogLayoutTipoPagamento.show();
    }

    private void abrirCheckOutActivity() {

        LocalEncomenda localEncomenda = new LocalEncomenda();
        localEncomenda.latitude = latitude;
        localEncomenda.longitude = longitude;
        localEncomenda.pontodeReferencia = endereco;
        localEncomenda.nTelefone = telefone;

        Intent intent = new Intent(this,CheckOutActivity.class);
        intent.putExtra("localEncomenda",localEncomenda);
        intent.putExtra("tipoPagamento",tipoPagamento);
        intent.putExtra("totalItems",totalItems);
        intent.putExtra("totalCart",totalCart);
        startActivity(intent);
        finish();
    }
}