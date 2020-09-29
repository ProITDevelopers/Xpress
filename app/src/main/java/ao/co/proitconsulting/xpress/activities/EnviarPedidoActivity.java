package ao.co.proitconsulting.xpress.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.helper.NotificationHelper;
import ao.co.proitconsulting.xpress.localDB.AppDatabase;
import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;
import ao.co.proitconsulting.xpress.modelos.LocalEncomenda;
import ao.co.proitconsulting.xpress.modelos.Order;
import ao.co.proitconsulting.xpress.modelos.OrderItem;
import ao.co.proitconsulting.xpress.modelos.ReferenciaRequest;
import dmax.dialog.SpotsDialog;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnviarPedidoActivity extends AppCompatActivity {

    private static String TAG = "TAG_EnviarPedidoActivity";

    private AlertDialog waitingDialog;

    private LocalEncomenda localEncomenda;
    private String tipoPagamento;
    private ReferenciaRequest referenciaRequest;
    private NotificationHelper notificationHelper;

    private Realm realm;

    private List<OrderItem> orderItems;

    //DIALOG_LAYOUT_SUCESSO
    private Dialog dialogLayoutSuccess;
    private ImageView imgStatus;
    private TextView dialog_txtConfirmSucesso;
    private Button dialog_btn_sucesso;

    //DIALOG_LAYOUT_CONFIRMAR_PROCESSO
    private Dialog dialogLayoutConfirmarProcesso;
    private ImageView imgConfirm;
    private TextView txtConfirmTitle,txtConfirmMsg;
    private Button dialog_btn_deny_processo,dialog_btn_accept_processo;


    private RelativeLayout errorLayout;
    private TextView btnTentarDeNovo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_pedido);
        notificationHelper = new NotificationHelper(this);
        
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(getString(R.string.enviar_pedido));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (getIntent()!=null){
            localEncomenda = (LocalEncomenda) getIntent().getSerializableExtra("localEncomenda");
            tipoPagamento = getIntent().getStringExtra("tipoPagamento");

        }

        initViews();
        prepareOrder();
    }

    private void initViews() {

        waitingDialog = new SpotsDialog.Builder().setContext(this).build();
        realm = Realm.getDefaultInstance();
        realm.where(CartItemProdutos.class).findAllAsync()
                .addChangeListener(cartItems -> {

                });

        //-------------------------------------------------------------//
        //DIALOG_LAYOUT_SUCESSO
        dialogLayoutSuccess = new Dialog(this);
        dialogLayoutSuccess.setContentView(R.layout.layout_sucesso);
        dialogLayoutSuccess.setCancelable(false);
        if (dialogLayoutSuccess.getWindow()!=null)
            dialogLayoutSuccess.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        imgStatus = dialogLayoutSuccess.findViewById(R.id.imgStatus);
        dialog_txtConfirmSucesso = dialogLayoutSuccess.findViewById(R.id.dialog_txtConfirmSucesso);
        dialog_btn_sucesso = dialogLayoutSuccess.findViewById(R.id.dialog_btn_sucesso);

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


        errorLayout = findViewById(R.id.erroLayout);
        btnTentarDeNovo = findViewById(R.id.btn);


    }

    private void prepareOrder() {

        List<CartItemProdutos> cartItems = realm.where(CartItemProdutos.class).findAll();

        orderItems = new ArrayList<>();
        for (CartItemProdutos cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.produtoId = cartItem.produtos.getIdProduto();
            orderItem.quantidade = cartItem.quantity;
            orderItem.ideStabelecimento = cartItem.produtos.ideStabelecimento;


            orderItems.add(orderItem);
        }

        verificarConexao();
        
    }

    private void verificarConexao() {
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null) {
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null){
                MetodosUsados.mostrarMensagem(this,getString(R.string.msg_erro_internet));
                mostarMsnErro();
            } else {
                enviarPedidos();
            }
        }
    }

    private void mostarMsnErro(){

        if (errorLayout.getVisibility() == View.GONE){
            errorLayout.setVisibility(View.VISIBLE);
        }

        btnTentarDeNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorLayout.setVisibility(View.GONE);
                verificarConexao();
            }
        });
    }

    private void enviarPedidos() {

        waitingDialog.setMessage("Enviando o pedido...");
        waitingDialog.setCancelable(false);
        waitingDialog.show();

        Order order = new Order();
        order.localEncomenda = localEncomenda;
        order.orderItems = orderItems;

        if (tipoPagamento.equals(getString(R.string.multicaixa))){
            facturacaoMulticaixa(order);
        }

        if (tipoPagamento.equals(getString(R.string.referencia))){

            facturacaoReferencia(order);
        }


    }

    private void facturacaoMulticaixa(Order order) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> facturaTPA = apiInterface.facturaTPA(order);
        facturaTPA.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    waitingDialog.cancel();

                    mensagemSucesso(getString(R.string.pedido_enviado_com_sucesso), tipoPagamento,true);

                } else {

                    waitingDialog.cancel();
                    mensagemSucesso(getString(R.string.pedido_nao_foi_enviado), tipoPagamento,false);
                    if (response.code()==401){
                        mensagemTokenExpirado();
                    }


                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                waitingDialog.cancel();
                if ("timeout".equals(t.getMessage())) {
                    MetodosUsados.mostrarMensagem(EnviarPedidoActivity.this,R.string.msg_erro_internet_timeout);
                }else {
                    MetodosUsados.mostrarMensagem(EnviarPedidoActivity.this,R.string.msg_erro);
                }


            }
        });
    }



    private void facturacaoReferencia(Order order) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<ReferenciaRequest>> facturaWallet = apiInterface.facturaReferencia(order);
        facturaWallet.enqueue(new Callback<List<ReferenciaRequest>>() {
            @Override
            public void onResponse(@NonNull Call<List<ReferenciaRequest>> call, @NonNull Response<List<ReferenciaRequest>> response) {

                if (response.isSuccessful()) {
                    waitingDialog.cancel();
                    if (response.body()!=null){

                        referenciaRequest = response.body().get(0);

                        mensagemSucesso(getString(R.string.pedido_enviado_com_sucesso), tipoPagamento,true);


                    }

                } else {

                    waitingDialog.cancel();
                    mensagemSucesso(getString(R.string.pedido_nao_foi_enviado), tipoPagamento,false);
                    if (response.code()==401){
                        mensagemTokenExpirado();
                    }


                }

            }

            @Override
            public void onFailure(@NonNull Call<List<ReferenciaRequest>> call, @NonNull Throwable t) {
                waitingDialog.cancel();
                if ("timeout".equals(t.getMessage())) {
                    MetodosUsados.mostrarMensagem(EnviarPedidoActivity.this,R.string.msg_erro_internet_timeout);
                }else {
                    MetodosUsados.mostrarMensagem(EnviarPedidoActivity.this,R.string.msg_erro);
                }


            }
        });

    }

    private void mensagemSucesso(String message, String tipoPagamento,boolean status) {

        String mNotificationTitle = "Pedido por ".concat(tipoPagamento);
        String status_message="";
        if (status){
            // as the order placed successfully, clear the cart
            AppDatabase.clearAllCart();
            imgStatus.setImageResource(R.drawable.phone_success);

            if (tipoPagamento.equals(getString(R.string.referencia))){
                status_message = message.concat("\n").concat("\n")
                        .concat(tipoPagamento+": "+referenciaRequest.codigo).concat("\n")
                        .concat("Entidade: "+referenciaRequest.entidade).concat("\n")
                        .concat("Valor: "+referenciaRequest.valor+" AKZ");
            }else{
                status_message = message;
            }

        }else {
            status_message = message;
            imgStatus.setImageResource(R.drawable.phone_failed);
        }



        notificationHelper.createNotification(mNotificationTitle,message,false);
        dialog_txtConfirmSucesso.setText(status_message);



        dialog_btn_sucesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (status){
                    dialogLayoutSuccess.cancel();
                    // as the order placed successfully, clear the cart
                    AppDatabase.clearAllCart();
                    imgStatus.setImageResource(R.drawable.phone_success);

                    Intent intent = new Intent(EnviarPedidoActivity.this,MeusPedidosActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    finish();

                }else {
                    dialogLayoutSuccess.cancel();
                    finish();
                }



            }
        });
        dialogLayoutSuccess.show();
    }

    private void mensagemTokenExpirado() {
        //-------------------------------------------------------------//
        //-------------------------------------------------------------//
        //DIALOG_LAYOUT_CONFIRMAR_PROCESSO

        imgConfirm.setImageResource(R.drawable.xpress_logo);
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
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.removeAllChangeListeners();
            realm.close();
        }
        dialogLayoutSuccess.cancel();
        dialogLayoutConfirmarProcesso.cancel();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}