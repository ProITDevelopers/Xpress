package ao.co.proitconsulting.xpress.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.adapters.CartProdutosAdapter;
import ao.co.proitconsulting.xpress.api.ADAO.ApiClientADAO;
import ao.co.proitconsulting.xpress.api.ADAO.ApiInterfaceADAO;
import ao.co.proitconsulting.xpress.api.ADAO.GetTaxaModel;
import ao.co.proitconsulting.xpress.api.ADAO.ListTaxaModel;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.helper.Utils;
import ao.co.proitconsulting.xpress.localDB.AppDatabase;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;
import ao.co.proitconsulting.xpress.modelos.LocalEncomenda;
import ao.co.proitconsulting.xpress.modelos.Produtos;
import ao.co.proitconsulting.xpress.modelos.UsuarioPerfil;
import dmax.dialog.SpotsDialog;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingCartActivity extends AppCompatActivity implements CartProdutosAdapter.CartProductsAdapterListener{

    private static final String TAG = "TAG_CartActivity";
    private static final int PLACE_PICKER_REQ_CODE = 12;

    private RecyclerView recyclerView;
    private CartProdutosAdapter mAdapter;

    private ConstraintLayout constraintLayoutViews;
    private ImageView imgEditAddress;
    private TextView txtGetAddress,txtSubtotal,textViewEntrega,txtEntrega,txtTotal;

    private Button btnCheckout;

    private Realm realm;
    private RealmResults<CartItemProdutos> cartItems;
    private RealmChangeListener<RealmResults<CartItemProdutos>> cartItemRealmChangeListener;



    private double subTotalPrice = 0.0;
    private String subTotalCart;

    private double taxaEntregaPrice = 0.0;
    private String taxaEntregaCart;

    private int total_Items_Cart=0;
    private double totalDeTudoPrice = 0.0;
    private String totalDeTudoCart;



    private UsuarioPerfil usuarioPerfil;
    private String latitude,longitude,endereco,telefone,tipoPagamento;






    //DIALOG_LAYOUT_NUM_TELEFONE_ADDRESS
    private Dialog dialogLayoutEnviarNumTelefoneAdress;
    private AppCompatEditText dialog_editTelefone,dialog_editEndereco;
    private TextView dialog_btn_meu_telefone;
    private Button dialog_btn_cancelar,dialog_btn_continuar;

    //DIALOG_LAYOUT_CONFIRMAR_TIPO_DE_PAGAMENTO
    private Dialog dialogLayoutTipoPagamento;

    private Button dialog_btn_multicaixa,dialog_btn_carteira_xpress;

    //DIALOG_LAYOUT_CONFIRMAR_PROCESSO
    private Dialog dialogLayoutConfirmarProcesso;
    private ImageView imgConfirm;
    private TextView txtConfirmTitle,txtConfirmMsg;
    private Button dialog_btn_deny_processo,dialog_btn_accept_processo;

    private AlertDialog waitingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //showBackground in status bar
//        MetodosUsados.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.white));
        setContentView(R.layout.activity_shopping_cart);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(getString(R.string.carrinho));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();



        cartItemRealmChangeListener = cartItems -> {

            mAdapter.setData(cartItems);

            if (cartItems != null && cartItems.size() > 0) {
                setCartSubTotal(cartItems);

                toggleCartInformation(true);

            } else {
                toggleCartInformation(false);
            }
        };

        cartItems.addChangeListener(cartItemRealmChangeListener);
        init();



    }

    private void initViews() {
        usuarioPerfil = AppPrefsSettings.getInstance().getUser();

        waitingDialog = new SpotsDialog.Builder().setContext(this).setTheme(R.style.CustomSpotsDialog).build();
        waitingDialog.setMessage("Por favor aguarde...");
        waitingDialog.setCancelable(false);

        realm = Realm.getDefaultInstance();
        cartItems = realm.where(CartItemProdutos.class).sort("estabProduto").findAllAsync();
//        cartItems = realm.where(CartItemProdutos.class).findAllAsync();

        recyclerView = findViewById(R.id.recycler_view);

        constraintLayoutViews = findViewById(R.id.constraintLayoutViews);
        imgEditAddress = findViewById(R.id.imgEditAddress);
        txtGetAddress = findViewById(R.id.txtGetAddress);
        txtSubtotal = findViewById(R.id.txtSubtotal);
        textViewEntrega = findViewById(R.id.textViewEntrega);
        txtEntrega = findViewById(R.id.txtEntrega);
        txtTotal = findViewById(R.id.txtTotal);


        btnCheckout = findViewById(R.id.btn_checkout);

        imgEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                abrirMapaActivity();
            }
        });


        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (verificarEnderecoEntrega()){
                    abrirCheckOutActivity();
                }


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


        dialog_btn_multicaixa = dialogLayoutTipoPagamento.findViewById(R.id.dialog_btn_multicaixa);
        dialog_btn_carteira_xpress = dialogLayoutTipoPagamento.findViewById(R.id.dialog_btn_carteira_xpress);


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


    private void init() {
        mAdapter = new CartProdutosAdapter(this, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }




    private void setCartSubTotal(RealmResults<CartItemProdutos> cartItems) {
        int itemCount = 0;
        for (CartItemProdutos cartItem : cartItems) {
            itemCount += cartItem.quantity;
        }
        total_Items_Cart = itemCount;
        subTotalCart = String.valueOf(Utils.getCartPrice(cartItems));

        subTotalPrice = Double.parseDouble(subTotalCart);

        txtSubtotal.setText(new StringBuilder("")
                .append(Utils.formatPrice(subTotalPrice)).append(" AKZ").toString());
    }

    private void toggleCartInformation(boolean show) {
        if (show)
            constraintLayoutViews.setVisibility(View.VISIBLE);
        else
            constraintLayoutViews.setVisibility(View.GONE);
    }



    @Override
    public void onCartItemRemoved(int index, CartItemProdutos cartItem) {
        textViewEntrega.setVisibility(View.GONE);
        txtEntrega.setVisibility(View.GONE);
        txtTotal.setVisibility(View.GONE);
        txtEntrega.setText("");
        txtTotal.setText("");
        AppDatabase.removeCartItem(cartItem);
    }

    @Override
    public void onProductAddedCart(int index, Produtos product) {

        AppDatabase.addItemToCart(this,product);
        if (cartItems != null) {
            mAdapter.updateItem(index, cartItems);

            textViewEntrega.setVisibility(View.GONE);
            txtEntrega.setVisibility(View.GONE);
            txtTotal.setVisibility(View.GONE);
            txtEntrega.setText("");
            txtTotal.setText("");

        }
    }

    @Override
    public void onProductRemovedFromCart(int index, Produtos product) {

        AppDatabase.removeCartItem(product);

        if (cartItems != null) {
            mAdapter.updateItem(index, cartItems);

            textViewEntrega.setVisibility(View.GONE);
            txtEntrega.setVisibility(View.GONE);
            txtTotal.setVisibility(View.GONE);
            txtEntrega.setText("");
            txtTotal.setText("");

        }
    }

    private void abrirMapaActivity() {
        Intent intent = new Intent(ShoppingCartActivity.this,MapaActivity.class);
        intent.putExtra("toolbarTitle",getString(R.string.endereco_de_entrega));
        startActivityForResult(intent, PLACE_PICKER_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_PICKER_REQ_CODE) {
            if (resultCode == RESULT_OK) {

                latitude = data.getStringExtra("latitude");
                longitude = data.getStringExtra("longitude");
                endereco = data.getStringExtra("endereco");

//                mostrarTelaInserirNumeroEndereco();

                if (usuarioPerfil.contactoMovel != null){



                    tipoPagamento = getString(R.string.multicaixa);
                    telefone = usuarioPerfil.contactoMovel;
                    txtGetAddress.setText(endereco);

                    criarLista();
                }else{
                    MetodosUsados.mostrarMensagem(this,
                            "É necessário o seu "+getString(R.string.numero_telefone)+" para continuar.");
                }



            }
        }

    }

    private void mostrarTelaInserirNumeroEndereco() {

        //DIALOG_LAYOUT_NUM_TELEFONE_ADDRESS
        dialog_editEndereco.setText(endereco);
        dialog_btn_meu_telefone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telefone = usuarioPerfil.contactoMovel;
                dialog_editTelefone.setText(null);
                dialog_editTelefone.setError(null);
                if (telefone != null)
                    dialog_editTelefone.setText(telefone);
                else
                    MetodosUsados.mostrarMensagem(ShoppingCartActivity.this, "Não possui um "+getString(R.string.numero_telefone));
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
                    txtGetAddress.setText(dialog_editEndereco.getText().toString());
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



        dialog_btn_multicaixa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipoPagamento = dialog_btn_multicaixa.getText().toString();
                dialogLayoutTipoPagamento.cancel();
//                abrirCheckOutActivity();
                criarLista();

            }
        });

        dialog_btn_carteira_xpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tipoPagamento = dialog_btn_carteira_xpress.getText().toString();
                dialogLayoutTipoPagamento.cancel();
//                abrirCheckOutActivity();
                criarLista();
            }
        });

        dialogLayoutTipoPagamento.show();
    }

    private boolean verificarEnderecoEntrega() {

        String address = txtGetAddress.getText().toString().trim();
        String precoEntrega = txtEntrega.getText().toString().trim();

        if (address.isEmpty()){
            MetodosUsados.mostrarMensagem(ShoppingCartActivity.this,"Adicione o "+getString(R.string.endereco_de_entrega));
            return false;
        }

        if (precoEntrega.isEmpty()) {
//            escolherTipodePagamento();
            criarLista();
            return false;
        }


        return true;
    }





    private void criarLista(){
        waitingDialog.show();


        if (cartItems != null && cartItems.size()>0){

            List<ListTaxaModel> listTaxaList = new ArrayList<>();

            for (int i = 0; i < cartItems.size(); i++) {

                ListTaxaModel listTaxaModel = new ListTaxaModel();

                CartItemProdutos cartItemProdutos = cartItems.get(i);

                if (cartItemProdutos!=null){
//                    Produtos produtos = cartItemProdutos.produtos;
                    listTaxaModel.idEstabelecimento = String.valueOf(cartItemProdutos.idEstabelecimento);
                    listTaxaModel.latitudeOrigem = Double.parseDouble(cartItemProdutos.latitude);
                    listTaxaModel.longitudeOrigem = Double.parseDouble(cartItemProdutos.longitude);
                    listTaxaModel.latitudeDestino = Double.parseDouble(latitude);
                    listTaxaModel.longitudeDestino = Double.parseDouble(longitude);
                    listTaxaList.add(listTaxaModel);
                }

            }

            List<ListTaxaModel> noRepeatTaxaList = new ArrayList<>();

            for (ListTaxaModel event : listTaxaList) {
                boolean isFound = false;
                // check if the event name exists in noRepeat
                for (ListTaxaModel e : noRepeatTaxaList) {
                    if (e.idEstabelecimento.equals(event.idEstabelecimento) || (e.equals(event))) {
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) noRepeatTaxaList.add(event);
            }


            listTaxaList.clear();

            enviarListaCalcularTaxa(noRepeatTaxaList);

        }



    }

    private void enviarListaCalcularTaxa(List<ListTaxaModel> listTaxaModelList) {

        waitingDialog.setMessage("Calculando a taxa...");

        ApiInterfaceADAO apiInterface = ApiClientADAO.getClient(Common.BASE_URL_XPRESS_ADAO_TAXA).create(ApiInterfaceADAO.class);
        Call<List<GetTaxaModel>> getTaxaList = apiInterface.sendListTOGETTaxa(listTaxaModelList);
        getTaxaList.enqueue(new Callback<List<GetTaxaModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<GetTaxaModel>> call, @NonNull Response<List<GetTaxaModel>> response) {



                if (response.isSuccessful()) {

                    if (response.body()!=null){

                        if (Common.getTaxaModelList != null)
                            Common.getTaxaModelList.clear();

                        Common.getTaxaModelList = response.body();

                        taxaEntregaPrice = 0.0;
                        totalDeTudoPrice = 0.0;
                        for (int i = 0; i <response.body().size() ; i++) {
                            Log.d(TAG, "onResponseGetTaxa: i="+i+" - Estab: "+response.body().get(i).idEstabelecimento+", taxa: "+response.body().get(i).valorTaxa);
                            taxaEntregaPrice += Double.parseDouble(response.body().get(i).valorTaxa);
                        }
                        Log.d(TAG, "onResponseGetTaxa: "+"Entrega: "+taxaEntregaPrice);
                        taxaEntregaCart = String.valueOf(taxaEntregaPrice);
                        textViewEntrega.setVisibility(View.VISIBLE);
                        txtEntrega.setVisibility(View.VISIBLE);
                        txtTotal.setVisibility(View.VISIBLE);



                        txtEntrega.setText(new StringBuilder("")
                                .append(Utils.formatPrice(taxaEntregaPrice)).append(" AKZ").toString());


                        totalDeTudoPrice = subTotalPrice + taxaEntregaPrice;

                        totalDeTudoCart = String.valueOf(totalDeTudoPrice);

                        txtTotal.setText(new StringBuilder("")
                                .append(Utils.formatPrice(totalDeTudoPrice)).append(" AKZ").toString());

                        waitingDialog.cancel();
                    }

                } else {

                    waitingDialog.cancel();
                    try {
                        Log.d(TAG, "onResponseGetTaxaError: "+response.errorBody().string()+", responseCode: "+response.code());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<GetTaxaModel>> call, @NonNull Throwable t) {
                waitingDialog.cancel();
                if (!MetodosUsados.conexaoInternetTrafego(ShoppingCartActivity.this,TAG)){
                    MetodosUsados.mostrarMensagem(ShoppingCartActivity.this,R.string.msg_erro_internet);
                } else if (t.getMessage().contains("timeout")) {
                    MetodosUsados.mostrarMensagem(ShoppingCartActivity.this,R.string.msg_erro_internet_timeout);
                }else {
                    MetodosUsados.mostrarMensagem(ShoppingCartActivity.this,R.string.msg_erro);
                }


            }
        });

    }

    private void abrirCheckOutActivity() {
        if (telefone.equals("") || telefone.equals("?")){
            MetodosUsados.mostrarMensagem(ShoppingCartActivity.this,"Adicione um "+getString(R.string.numero_telefone)+" ao seu Perfil para continuar.");
            return;
        }
        if (!telefone.matches("9[1-9][0-9]\\d{6}")){
            MetodosUsados.mostrarMensagem(ShoppingCartActivity.this,"O seu "+getString(R.string.numero_telefone)+" é inválido. Por favor edite o seu Perfil.");
            return;
        }

        LocalEncomenda localEncomenda = new LocalEncomenda();
        localEncomenda.latitude = Double.valueOf(latitude);
        localEncomenda.longitude = Double.valueOf(longitude);
        localEncomenda.pontodeReferencia = endereco;
        localEncomenda.nTelefone = telefone;

        Intent intent = new Intent(this,CheckOutActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("localEncomenda",localEncomenda);
        intent.putExtra("tipoPagamento",tipoPagamento);
        intent.putExtra("totalItems",total_Items_Cart);
        intent.putExtra("subTotalCart",subTotalPrice);
        intent.putExtra("taxaEntregaCart",taxaEntregaPrice);
        intent.putExtra("totalDeTudoCart",totalDeTudoPrice);
        startActivity(intent);
        finish();
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

                    deleteAllItemsFromCart(getString(R.string.msg_remover_um_item));

                }else {

                    deleteAllItemsFromCart(getString(R.string.msg_remover_todos_items));
                }

            } else {
                Toast.makeText(this, "O carrinho está vazio!", Toast.LENGTH_SHORT).show();
            }

        }

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,ConfiguracoesActivity.class);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void deleteAllItemsFromCart(String message) {
        //-------------------------------------------------------------//
        //-------------------------------------------------------------//
        //DIALOG_LAYOUT_CONFIRMAR_PROCESSO

        imgConfirm.setImageResource(R.drawable.xpress_logo);
        txtConfirmTitle.setText(getString(R.string.msg_tem_certeza));
        txtConfirmMsg.setText(message);
        dialog_btn_deny_processo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLayoutConfirmarProcesso.cancel();
            }
        });


        dialog_btn_accept_processo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDatabase.clearAllCart();

                dialogLayoutConfirmarProcesso.cancel();
            }
        });


        dialogLayoutConfirmarProcesso.show();
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
        dialogLayoutConfirmarProcesso.cancel();


    }










}