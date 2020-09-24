package ao.co.proitconsulting.xpress.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.adapters.FacturaAdapter;
import ao.co.proitconsulting.xpress.adapters.RecyclerViewOnItemClickListener;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.modelos.Factura;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeusPedidosActivity extends AppCompatActivity {

    private static String TAG = "TAG_MeusPedidosActivity";
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private List<Factura> facturaList;

    private ConstraintLayout coordinatorLayout;
    private RelativeLayout errorLayout;
    private TextView btnTentarDeNovo;

    //DIALOG_LAYOUT_CONFIRMAR_PROCESSO
    private Dialog dialogLayoutConfirmarProcesso;
    private ImageView imgConfirm;
    private TextView txtConfirmTitle,txtConfirmMsg;
    private Button dialog_btn_deny_processo,dialog_btn_accept_processo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_pedidos);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(getString(R.string.nav_encomendas_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        errorLayout = findViewById(R.id.erroLayout);
        btnTentarDeNovo = findViewById(R.id.btn);


        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

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

        verifConecxaoPedidos();
    }

    private void verifConecxaoPedidos() {
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null) {
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null){
                MetodosUsados.mostrarMensagem(this,getString(R.string.msg_erro_internet));
                mostarMsnErro();
            } else {
                carregarListaFacturas();
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
                verifConecxaoPedidos();
            }
        });
    }

    private void carregarListaFacturas() {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Factura>> rv = apiInterface.getTodasFacturas();
        rv.enqueue(new Callback<List<Factura>>() {
            @Override
            public void onResponse(@NonNull Call<List<Factura>> call, @NonNull Response<List<Factura>> response) {

                facturaList = new ArrayList<>();

                if (response.isSuccessful()) {
                    if (response.body()!=null){
                        facturaList = response.body();

                        // Order the list by regist date.
                        Collections.sort(facturaList, new Factura());


                        progressBar.setVisibility(View.GONE);
                        setAdapters(facturaList);


                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    if (response.code()==401){
                        mensagemTokenExpirado();
                    }
                }




            }

            @Override
            public void onFailure(@NonNull Call<List<Factura>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                if (!MetodosUsados.conexaoInternetTrafego(MeusPedidosActivity.this,TAG)){
                    MetodosUsados.mostrarMensagem(MeusPedidosActivity.this,R.string.msg_erro_internet);
                }else  if ("timeout".equals(t.getMessage())) {
                    MetodosUsados.mostrarMensagem(MeusPedidosActivity.this,R.string.msg_erro_internet_timeout);
                }else {
                    MetodosUsados.mostrarMensagem(MeusPedidosActivity.this,R.string.msg_erro);
                }
            }
        });
    }

    private void setAdapters(List<Factura> facturaList) {

        if (facturaList.size()>0){

            Collections.reverse(facturaList);

            FacturaAdapter facturaAdapter = new FacturaAdapter(this,facturaList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerView.setHasFixedSize(true);
            facturaAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(facturaAdapter);
            facturaAdapter.setItemClickListener(new RecyclerViewOnItemClickListener() {
                @Override
                public void onItemClickListener(int position) {

                }
            });

        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pedidos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        if (id == R.id.menu_refresh) {
            if (errorLayout.getVisibility() == View.VISIBLE){
                errorLayout.setVisibility(View.GONE);
                coordinatorLayout.setVisibility(View.VISIBLE);
            }
            verifConecxaoPedidos();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        dialogLayoutConfirmarProcesso.cancel();
        super.onDestroy();
    }
}