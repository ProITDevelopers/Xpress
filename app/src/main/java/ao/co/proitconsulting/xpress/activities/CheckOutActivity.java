package ao.co.proitconsulting.xpress.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.EventBus.StartCarteiraXpressFrag;
import ao.co.proitconsulting.xpress.EventBus.StartEncomendaFrag;
import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.helper.NotificationHelper;
import ao.co.proitconsulting.xpress.helper.Utils;
import ao.co.proitconsulting.xpress.localDB.AppDatabase;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;
import ao.co.proitconsulting.xpress.modelos.EncomendaPedido;
import ao.co.proitconsulting.xpress.modelos.ItensExtras;
import ao.co.proitconsulting.xpress.modelos.ItensFacturados;
import ao.co.proitconsulting.xpress.modelos.LocalEncomenda;
import ao.co.proitconsulting.xpress.modelos.ProdutoExtra;
import ao.co.proitconsulting.xpress.modelos.UsuarioPerfil;
import ao.co.proitconsulting.xpress.modelos.Wallet;
import dmax.dialog.SpotsDialog;
import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOutActivity extends AppCompatActivity {

    private static final String TAG = "TAG_CheckOutActivity";


    private TextView txtGetAddress,txtUserPhone,txtSubtotal,txtEntrega,txtTotal;



    private CardView cardViewMulticaixa,cardViewCarteira;
    private Button btnMulticaixa,btnCarteira;



    private Realm realm;


    private LocalEncomenda localEncomenda;
    private String tipoPagamento;


    private double subTotalPrice = 0.0;
    private String subTotalCart;

    private double taxaEntregaPrice = 0.0;
    private String taxaEntregaCart;

    private int total_Items_Cart=0;
    private double totalDeTudoPrice = 0.0;
    private String totalDeTudoCart;

    private Button btn_enviar_pedido;

    private List<ItensFacturados> itensFacturadosList;
    private EncomendaPedido encomendaPedido;
    private AlertDialog waitingDialog;
    private NotificationHelper notificationHelper;

    //DIALOG_LAYOUT_PEDIDO_MESSAGE
    private Dialog dialogLayoutPedidoMessage;
    private ImageView imgPedidoStatus;
    private TextView txtPedidoStatusMsg;
    private Button dialog_btn_pedido_ok;

    //DIALOG_LAYOUT_CONFIRMAR_PROCESSO
    private Dialog dialogLayoutConfirmarProcesso;
    private ImageView imgConfirm;
    private TextView txtConfirmTitle,txtConfirmMsg;
    private Button dialog_btn_deny_processo,dialog_btn_accept_processo;

    //DIALOG_LAYOUT_ENVIAR_CODIG_CONFIRM
    private Dialog dialogLayoutEnviarCodigConfirm;
    private ImageView imgBtnFechar;
    private ShowHidePasswordEditText dialog_pinCodigoConfirmacao;
    private Button dialog_btn_continuar;

    private Wallet wallet = new Wallet();
    private UsuarioPerfil usuarioPerfil;

    @SerializedName("codigoconfirmacao")
    public String codigoConfirmacao;

    @SerializedName("codoperacao")
    public String codigoOperacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(getString(R.string.checkout));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initViews();
        if (getIntent()!=null){

            localEncomenda = (LocalEncomenda) getIntent().getSerializableExtra("localEncomenda");
            tipoPagamento = getIntent().getStringExtra("tipoPagamento");
            total_Items_Cart = getIntent().getIntExtra("totalItems",0);
            subTotalPrice = getIntent().getDoubleExtra("subTotalCart",0.0);
            taxaEntregaPrice = getIntent().getDoubleExtra("taxaEntregaCart",0.0);
            totalDeTudoPrice = getIntent().getDoubleExtra("totalDeTudoCart",0.0);

            Log.d(TAG, "onCreate: LocalEncomenda - "+localEncomenda.nTelefone+", "+localEncomenda.pontodeReferencia);
            Log.d(TAG, "onCreate: tipoPagamento - "+tipoPagamento);
            Log.d(TAG, "onCreate: totalItems - "+total_Items_Cart);
            Log.d(TAG, "onCreate: subTotalCart - "+subTotalPrice);
            Log.d(TAG, "onCreate: taxaEntregaCart - "+taxaEntregaPrice);
            Log.d(TAG, "onCreate: totalDeTudoCart - "+totalDeTudoPrice);

            subTotalCart = String.valueOf(subTotalPrice);
            taxaEntregaCart = String.valueOf(taxaEntregaPrice);
            totalDeTudoCart = String.valueOf(totalDeTudoPrice);

            if (tipoPagamento.equals(getString(R.string.multicaixa)))
                btnMulticaixa.setVisibility(View.VISIBLE);
            else
                btnCarteira.setVisibility(View.VISIBLE);

        }

        prepareOrders();



        txtGetAddress.setText(localEncomenda.pontodeReferencia);
        txtUserPhone.setText(localEncomenda.nTelefone);

        txtSubtotal.setText(new StringBuilder("")
                .append(Utils.formatPrice(subTotalPrice)).append(" AKZ").toString());

        txtEntrega.setText(new StringBuilder("")
                .append(Utils.formatPrice(taxaEntregaPrice)).append(" AKZ").toString());

        txtTotal.setText(new StringBuilder("")
                .append(Utils.formatPrice(totalDeTudoPrice)).append(" AKZ").toString());

        cardViewMulticaixa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnMulticaixa.setVisibility(View.VISIBLE);
                btnCarteira.setVisibility(View.INVISIBLE);
                tipoPagamento = getString(R.string.multicaixa);
            }
        });

        cardViewCarteira.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnMulticaixa.setVisibility(View.INVISIBLE);
                btnCarteira.setVisibility(View.VISIBLE);
                tipoPagamento = getString(R.string.carteira_xpress);

            }
        });


        btn_enviar_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifConecxao();
            }
        });
    }


    private void initViews() {
        usuarioPerfil = AppPrefsSettings.getInstance().getUser();
        notificationHelper = new NotificationHelper(this);

        waitingDialog = new SpotsDialog.Builder().setContext(this).setTheme(R.style.CustomSpotsDialog).build();
        waitingDialog.setMessage("Por favor aguarde...");
        waitingDialog.setCancelable(false);

        realm = Realm.getDefaultInstance();
        realm.where(CartItemProdutos.class).findAllAsync()
                .addChangeListener(cartItems -> {

                });


        txtGetAddress = findViewById(R.id.txtGetAddress);
        txtUserPhone = findViewById(R.id.txtUserPhone);

        cardViewMulticaixa = findViewById(R.id.cardViewMulticaixa);
        btnMulticaixa = findViewById(R.id.btnMulticaixa);

        cardViewCarteira = findViewById(R.id.cardViewCarteira);
        btnCarteira = findViewById(R.id.btnCarteira);

        txtSubtotal = findViewById(R.id.txtSubtotal);
        txtEntrega = findViewById(R.id.txtEntrega);
        txtTotal = findViewById(R.id.txtTotal);


        btn_enviar_pedido = findViewById(R.id.btn_enviar_pedido);

        itensFacturadosList = new ArrayList<>();
        encomendaPedido = new EncomendaPedido();

        //-------------------------------------------------------------//
        //-------------------------------------------------------------//
        //DIALOG_LAYOUT_PEDIDO_MESSAGE
        dialogLayoutPedidoMessage = new Dialog(this);
        dialogLayoutPedidoMessage.setContentView(R.layout.layout_pedido_message);
        dialogLayoutPedidoMessage.setCancelable(false);
        if (dialogLayoutPedidoMessage.getWindow()!=null)
            dialogLayoutPedidoMessage.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        imgPedidoStatus = dialogLayoutPedidoMessage.findViewById(R.id.imgPedidoStatus);
        txtPedidoStatusMsg = dialogLayoutPedidoMessage.findViewById(R.id.txtPedidoStatusMsg);
        dialog_btn_pedido_ok = dialogLayoutPedidoMessage.findViewById(R.id.dialog_btn_pedido_ok);


        //-------------------------------------------------------------//
        //-------------------------------------------------------------//
        //DIALOG_LAYOUT_CONFIRMAR_PROCESSO
        dialogLayoutConfirmarProcesso = new Dialog(this);
        dialogLayoutConfirmarProcesso.setContentView(R.layout.layout_confirmar_processo);
        dialogLayoutConfirmarProcesso.setCancelable(false);
        if (dialogLayoutConfirmarProcesso.getWindow()!=null)
            dialogLayoutConfirmarProcesso.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        imgConfirm = dialogLayoutConfirmarProcesso.findViewById(R.id.imgConfirm);
        txtConfirmTitle = dialogLayoutConfirmarProcesso.findViewById(R.id.txtConfirmTitle);
        txtConfirmMsg = dialogLayoutConfirmarProcesso.findViewById(R.id.txtConfirmMsg);
        dialog_btn_deny_processo = dialogLayoutConfirmarProcesso.findViewById(R.id.dialog_btn_deny_processo);
        dialog_btn_accept_processo = dialogLayoutConfirmarProcesso.findViewById(R.id.dialog_btn_accept_processo);


        //DIALOG_LAYOUT_ENVIAR_CODIG_CONFIRM
        dialogLayoutEnviarCodigConfirm = new Dialog(this);
        dialogLayoutEnviarCodigConfirm.setContentView(R.layout.layout_enviar_codigo_pagamento);
        dialogLayoutEnviarCodigConfirm.setCancelable(false);
        if (dialogLayoutEnviarCodigConfirm.getWindow()!=null)
            dialogLayoutEnviarCodigConfirm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        imgBtnFechar = dialogLayoutEnviarCodigConfirm.findViewById(R.id.imgBtnFechar);
        dialog_pinCodigoConfirmacao = dialogLayoutEnviarCodigConfirm.findViewById(R.id.dialog_pinCodigoConfirmacao);
        dialog_btn_continuar = dialogLayoutEnviarCodigConfirm.findViewById(R.id.dialog_btn_continuar);




    }

    private void prepareOrders() {
        List<CartItemProdutos> cartItems = realm.where(CartItemProdutos.class).sort("estabProduto").findAll();


        if (cartItems != null){
            for (int i = 0; i < cartItems.size(); i++) {
                CartItemProdutos cartItem = cartItems.get(i);

                ItensFacturados orderItem = new ItensFacturados();
                orderItem.itensExtrasList = new ArrayList<>();

                Gson gson= new Gson();

                if (cartItem != null){

                    orderItem.produtoId = cartItem.idProduto;
                    orderItem.quantidade = cartItem.quantity;
                    orderItem.taxaEntrega = 0;
                    orderItem.ideStabelecimento = cartItem.idEstabelecimento;


                    if (Common.getTaxaModelList != null){

                        for (int j = 0; j <Common.getTaxaModelList.size() ; j++) {

                            int taxaIdEstabelecimento = Integer.parseInt(Common.getTaxaModelList.get(j).idEstabelecimento);

                            if (cartItem.idEstabelecimento == taxaIdEstabelecimento){
                                orderItem.taxaEntrega = Float.parseFloat(Common.getTaxaModelList.get(j).valorTaxa);
                            }

                        }

                    }



                    if (cartItem.produtoExtras!=null){
                        List<ProdutoExtra> produtoExtraList = gson.fromJson(cartItem.produtoExtras,
                                new TypeToken<List<ProdutoExtra>>(){}.getType());
                        if (produtoExtraList!=null && produtoExtraList.size()>0){

                            for (ProdutoExtra produtoextra: produtoExtraList) {
                                ItensExtras itensExtras = new ItensExtras();
                                itensExtras.iDProdudoExtra = produtoextra.getiDProdudoExtra();
                                itensExtras.produdoExtraQtd = cartItem.quantity;
                                orderItem.itensExtrasList.add(itensExtras);
                            }
                        }

                    }

                    itensFacturadosList.add(orderItem);

                }








            }
        }



    }


    private void verifConecxao() {
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null) {
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null){
                MetodosUsados.mostrarMensagem(this,getString(R.string.msg_erro_internet));
            } else {
                enviarPedido();
            }
        }
    }

    private void enviarPedido() {

        waitingDialog.show();

        encomendaPedido.localEncomenda = localEncomenda;
        encomendaPedido.itensFacturadosList = itensFacturadosList;

        if (tipoPagamento.equals(getString(R.string.multicaixa))){
            facturacaoMulticaixa();
        }

        if (tipoPagamento.equals(getString(R.string.carteira_xpress))){
            
            saldoContaWalletApi();

            
        }
    }

    private void facturacaoMulticaixa() {

        waitingDialog.setMessage("Enviando o pedido...");
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> facturaTPA = apiInterface.facturaTPA(encomendaPedido);
        facturaTPA.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    waitingDialog.dismiss();

                    mensagemSucesso("O seu pagamento foi concluído."+"\n"+"Aguarde pela entrega", tipoPagamento,true);

                } else {

                    waitingDialog.dismiss();
                    mensagemSucesso(getString(R.string.pedido_nao_foi_enviado), tipoPagamento,false);
                    if (response.code()==401){
                        mensagemTokenExpirado();
                    }


                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                waitingDialog.dismiss();
                if (!MetodosUsados.conexaoInternetTrafego(CheckOutActivity.this,TAG)){
                    MetodosUsados.mostrarMensagem(CheckOutActivity.this,R.string.msg_erro_internet);
                } else if (t.getMessage().contains("timeout")) {
                    MetodosUsados.mostrarMensagem(CheckOutActivity.this,R.string.msg_erro_internet_timeout);
                }else {
                    MetodosUsados.mostrarMensagem(CheckOutActivity.this,R.string.msg_erro);
                }


            }
        });
    }

    private void saldoContaWalletApi() {


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Wallet>> call = apiInterface.getSaldoWallet();
        call.enqueue(new Callback<List<Wallet>>() {
            @Override
            public void onResponse(@NonNull Call<List<Wallet>> call, @NonNull Response<List<Wallet>> response) {

                //response.body()==null
                if (response.isSuccessful()) {

                    if (response.body()!=null){

                        wallet = response.body().get(0);
                        usuarioPerfil.carteiraXpress = wallet;


                        AppPrefsSettings.getInstance().saveUser(usuarioPerfil);

                        facturacaoCarteiraXpress();


                    }




                } else {
                    waitingDialog.dismiss();

                    if (response.code()==401){

                        mensagemTokenExpirado();

                    }else{
                        MetodosUsados.mostrarMensagem(CheckOutActivity.this,"Não possui uma Carteira Xpress. Crie uma para continuar.");
                        mostrarTelaCriarWallet();
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Wallet>>call, @NonNull Throwable t) {
                waitingDialog.dismiss();
                Log.d(TAG, "failed: "+t.getMessage());
                if (!MetodosUsados.conexaoInternetTrafego(CheckOutActivity.this,TAG)){
                    MetodosUsados.mostrarMensagem(CheckOutActivity.this,R.string.msg_erro_internet);
                }else  if (t.getMessage().contains("timeout")) {
                    MetodosUsados.mostrarMensagem(CheckOutActivity.this,R.string.msg_erro_internet_timeout);
                }else {
                    MetodosUsados.mostrarMensagem(CheckOutActivity.this,R.string.msg_erro);
                }
            }
        });
    }

    private void mostrarTelaCriarWallet() {
        //-------------------------------------------------------------//
        //-------------------------------------------------------------//
        //DIALOG_LAYOUT_CONFIRMAR_PROCESSO

        txtConfirmTitle.setText(getString(R.string.carteira_xpress));
        txtConfirmMsg.setText("Não possui uma Carteira Xpress. Criar uma para continuar?");


        dialog_btn_deny_processo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLayoutConfirmarProcesso.cancel();
            }
        });

        dialog_btn_accept_processo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLayoutConfirmarProcesso.cancel();
                EventBus.getDefault().postSticky(new StartCarteiraXpressFrag(true));

                finish();
            }
        });


        dialogLayoutConfirmarProcesso.show();
    }

    private void facturacaoCarteiraXpress() {
        waitingDialog.setMessage("Enviando o pedido...");
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> facturaWallet = apiInterface.facturaWallet(encomendaPedido);
        facturaWallet.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                if (response.isSuccessful()) {

                    waitingDialog.dismiss();

                    if (response.body()!=null){


                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            codigoOperacao = jsonObject.getString("numeroOperacao");
                            Log.d(TAG, "onResponseNumeroOperacao: "+codigoOperacao);
                            mostrarTelaInserirCodigo();
                        } catch (JSONException | IOException jsonException) {
                            jsonException.printStackTrace();
                        }




                    }



                } else {
                    waitingDialog.dismiss();

                    if (response.code()==401){
                        mensagemTokenExpirado();
                    }else if (response.code()==500){

                        String responseErrorMsg ="";
                        JSONObject jsonObject;
                        try {
                            responseErrorMsg = response.errorBody().string();

                            if (responseErrorMsg.startsWith("[")){
                                responseErrorMsg = responseErrorMsg.replaceAll("\\[", "").replaceAll("\\]","");
                                jsonObject = new JSONObject(responseErrorMsg);
                                responseErrorMsg = jsonObject.getString("description");
                            } else if (responseErrorMsg.startsWith("{")){
                                jsonObject = new JSONObject(responseErrorMsg);
                                responseErrorMsg = jsonObject.getString("description");
                            }




//                            JSONObject jsonObject = new JSONObject(responseErrorMsg);
//                            responseErrorMsg = jsonObject.getString("description");

                            Log.d(TAG,"Error code: "+response.code()+", ErrorBody msg: "+responseErrorMsg);

                            mensagemSucesso(responseErrorMsg, tipoPagamento,false);
                            MetodosUsados.mostrarMensagem(CheckOutActivity.this,responseErrorMsg);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }catch (JSONException err){
                            Log.d(TAG, err.toString());
                        }

                    }




                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                waitingDialog.dismiss();
                if (!MetodosUsados.conexaoInternetTrafego(CheckOutActivity.this,TAG)){
                    MetodosUsados.mostrarMensagem(CheckOutActivity.this,R.string.msg_erro_internet);
                } else if (t.getMessage().contains("timeout")) {
                    MetodosUsados.mostrarMensagem(CheckOutActivity.this,R.string.msg_erro_internet_timeout);
                }else {
                    MetodosUsados.mostrarMensagem(CheckOutActivity.this,R.string.msg_erro);
                }


            }
        });
    }

    private void mostrarTelaInserirCodigo() {
        imgBtnFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodosUsados.esconderTeclado(CheckOutActivity.this);
                dialog_pinCodigoConfirmacao.setText(null);
                dialog_pinCodigoConfirmacao.setError(null);
                dialogLayoutEnviarCodigConfirm.cancel();
                mensagemSucesso(getString(R.string.pedido_nao_foi_enviado), tipoPagamento,false);
            }
        });

        dialog_btn_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificarCampoCodigo()){
                    enviarCodConrfirmacaoPagamento();
                }
            }
        });

        dialogLayoutEnviarCodigConfirm.show();
    }

    private boolean verificarCampoCodigo() {
        codigoConfirmacao = dialog_pinCodigoConfirmacao.getText().toString();


        if (codigoConfirmacao.isEmpty()) {
            MetodosUsados.mostrarMensagem(this, R.string.msg_confirm_code);
            dialog_pinCodigoConfirmacao.requestFocus();
            dialog_pinCodigoConfirmacao.setError(getString(R.string.msg_erro_campo_vazio));
            return false;
        }
        if (codigoConfirmacao.length() <=2) {
            dialog_pinCodigoConfirmacao.requestFocus();
            dialog_pinCodigoConfirmacao.setError(getString(R.string.msg_confirm_code_min_three));
            MetodosUsados.mostrarMensagem(this, R.string.msg_confirm_code_short);
            return false;
        }



        return true;

    }

    private void enviarCodConrfirmacaoPagamento() {

        AlertDialog waitingDialog = new SpotsDialog.Builder().setContext(this).setTheme(R.style.CustomSpotsDialog).build();
        waitingDialog.setMessage("Quase pronto...");
        waitingDialog.setCancelable(false);
        waitingDialog.show();

        RequestBody codigoconfirmacao = RequestBody.create(MediaType.parse("multipart/form-data"),codigoConfirmacao);
        RequestBody codoperacao = RequestBody.create(MediaType.parse("multipart/form-data"), codigoOperacao);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> enviarCod = apiInterface.enviarConfirCodigoPagamento(codigoconfirmacao,codoperacao);
        enviarCod.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    dialogLayoutEnviarCodigConfirm.cancel();
                    dialog_pinCodigoConfirmacao.setText(null);
                    waitingDialog.dismiss();
                    mensagemSucesso("O seu pagamento foi concluído."+"\n"+"Aguarde pela entrega", tipoPagamento,true);



                } else {
                    waitingDialog.dismiss();
                    String responseErrorMsg = "";
                    try {

                        responseErrorMsg = response.errorBody().string();

                        Log.d(TAG,"Error code: "+response.code()+", ErrorBody msg: "+responseErrorMsg+", CodigoOperacao:"+codigoOperacao+", ConfirmCode:"+codigoConfirmacao);

                        JSONObject jsonObject = new JSONObject(responseErrorMsg);
                        responseErrorMsg = jsonObject.getString("description");

                        dialog_pinCodigoConfirmacao.setError("Código errado.");
                        MetodosUsados.mostrarMensagem(CheckOutActivity.this,responseErrorMsg);

                        Log.d(TAG,"Error code: "+response.code()+", ErrorBody msg: "+responseErrorMsg+", CodigoOperacao:"+codigoOperacao+", ConfirmCode:"+codigoConfirmacao);


                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

                waitingDialog.dismiss();
                if (!MetodosUsados.conexaoInternetTrafego(CheckOutActivity.this,TAG)){
                    MetodosUsados.mostrarMensagem(CheckOutActivity.this,R.string.msg_erro_internet);
                } else if (t.getMessage().contains("timeout")) {
                    MetodosUsados.mostrarMensagem(CheckOutActivity.this,R.string.msg_erro_internet_timeout);
                }else {
                    MetodosUsados.mostrarMensagem(CheckOutActivity.this,R.string.msg_erro);
                }

            }
        });



    }

    private void mensagemSucesso(String message, String tipoPagamento,boolean status) {

        String mNotificationTitle = "Pedido por ".concat(tipoPagamento);

        if (status){
            // as the order placed successfully, clear the cart
            AppDatabase.clearAllCart();
            imgPedidoStatus.setImageResource(R.drawable.ic_baseline_check_24);
            dialog_btn_pedido_ok.setText("Voltar ao menu");

        }else {
            dialog_btn_pedido_ok.setText(getString(R.string.cancelar));
            imgPedidoStatus.setImageResource(R.drawable.ic_baseline_clear_24);
        }



        notificationHelper.createNotification(mNotificationTitle,message,false);
        txtPedidoStatusMsg.setText(message);



        dialog_btn_pedido_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (status){
                    dialogLayoutPedidoMessage.cancel();
                    // as the order placed successfully, clear the cart
                    AppDatabase.clearAllCart();

                    imgPedidoStatus.setImageResource(R.drawable.ic_baseline_check_24);

                    EventBus.getDefault().postSticky(new StartEncomendaFrag(true));

                    finish();

                }else {
                    dialogLayoutPedidoMessage.cancel();
                    finish();
                }



            }
        });
        dialogLayoutPedidoMessage.show();
    }






    private void mensagemTokenExpirado() {
        //-------------------------------------------------------------//
        //-------------------------------------------------------------//
        //DIALOG_LAYOUT_CONFIRMAR_PROCESSO


        txtConfirmTitle.setText(getString(R.string.a_sessao_expirou));
        txtConfirmMsg.setText(getString(R.string.inicie_outra_vez_a_sessao));
        dialog_btn_deny_processo.setVisibility(View.GONE);
        dialog_btn_accept_processo.setText(getString(R.string.ok));

        dialog_btn_accept_processo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLayoutConfirmarProcesso.cancel();
            }
        });


        dialogLayoutConfirmarProcesso.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().removeAllStickyEvents();
        super.onDestroy();

        if (realm != null) {
            realm.removeAllChangeListeners();
            realm.close();
        }
        dialogLayoutPedidoMessage.cancel();
        dialogLayoutConfirmarProcesso.cancel();

    }

}