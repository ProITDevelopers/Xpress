package ao.co.proitconsulting.xpress.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.adapters.CheckoutAdapter;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;
import ao.co.proitconsulting.xpress.modelos.CheckoutOrder;
import ao.co.proitconsulting.xpress.modelos.LocalEncomenda;
import io.realm.Realm;
import io.realm.RealmResults;

public class CheckOutActivity extends AppCompatActivity {

    private LocalEncomenda localEncomenda;
    private String tipoPagamento,totalCart;
    private int totalItems;

    private RecyclerView recyclerView;
    private Button btn_enviar_pedido;

    private List<CheckoutOrder> checkOutList = new ArrayList<>();

    private CheckoutOrder checkoutOrder = new CheckoutOrder();

    private Realm realm;
    private CheckoutAdapter mAdapter;
    private RealmResults<CartItemProdutos> cartItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(getString(R.string.checkout));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (getIntent()!=null){
            localEncomenda = (LocalEncomenda) getIntent().getSerializableExtra("localEncomenda");
            tipoPagamento = getIntent().getStringExtra("tipoPagamento");
            totalItems = getIntent().getIntExtra("totalItems",0);
            totalCart = getIntent().getStringExtra("totalCart");
        }

        recyclerView = findViewById(R.id.recyclerView);
        btn_enviar_pedido = findViewById(R.id.btn_enviar_pedido);

        realm = Realm.getDefaultInstance();
        cartItems = realm.where(CartItemProdutos.class).findAllAsync();

        checkoutOrder.itens = cartItems;
        checkoutOrder.itens_cart = totalItems;
        checkoutOrder.total_cart = totalCart;
        checkoutOrder.metododPagamento = tipoPagamento;
        checkoutOrder.contacto = localEncomenda.nTelefone;
        checkoutOrder.endereco = localEncomenda.pontodeReferencia;


        checkOutList.add(checkoutOrder);


        CheckoutAdapter mAdapter = new CheckoutAdapter(this, checkOutList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


        btn_enviar_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifConecxao();
            }
        });
    }

    private void verifConecxao() {
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null) {
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null){
                MetodosUsados.mostrarMensagem(this,getString(R.string.msg_erro_internet));
            } else {
                abrirActividadePagamento();
            }
        }
    }

    private void abrirActividadePagamento() {
        Intent intent = new Intent(CheckOutActivity.this,EnviarPedidoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("tipoPagamento",tipoPagamento);
        intent.putExtra("localEncomenda",localEncomenda);
        startActivity(intent);
        finish();
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
        super.onDestroy();

        if (realm != null) {
            realm.close();
        }


    }

}