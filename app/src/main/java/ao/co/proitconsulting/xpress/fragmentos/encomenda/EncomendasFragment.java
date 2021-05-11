package ao.co.proitconsulting.xpress.fragmentos.encomenda;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.activities.MeusPedidosActivity;
import ao.co.proitconsulting.xpress.adapters.FacturaAdapter;
import ao.co.proitconsulting.xpress.adapters.RecyclerViewOnItemClickListener;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.modelos.Factura;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EncomendasFragment extends Fragment {

    private static String TAG = "TAG_EncomendasFragment";
    private View view;

    private AlertDialog waitingDialog;
    private RecyclerView recyclerView;
    private List<Factura> facturaList;


    private ConstraintLayout coordinatorLayout;
    private RelativeLayout errorLayout;
    private TextView btnTentarDeNovo;

    public EncomendasFragment() {}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_encomendas, container, false);

        initViews();

        return view;
    }

    private void initViews() {
        waitingDialog = new SpotsDialog.Builder().setContext(getContext()).build();
        waitingDialog.setMessage("Por favor aguarde...");
        waitingDialog.setCancelable(false);

        recyclerView = view.findViewById(R.id.recyclerView);

        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        errorLayout = view.findViewById(R.id.erroLayout);
        btnTentarDeNovo = view.findViewById(R.id.btn);

        verifConecxaoEncomendas();
    }

    private void verifConecxaoEncomendas() {
        if (getContext()!=null){
            ConnectivityManager conMgr =  (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
                    MetodosUsados.mostrarMensagem(getContext(),getString(R.string.msg_erro_internet));
                    mostarMsnErro();
                } else {
                    carregarListaFacturas();
                }
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
                verifConecxaoEncomendas();
            }
        });
    }

    private void carregarListaFacturas() {

        waitingDialog.show();
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



                        setAdapters(facturaList);


                    }
                } else {

                    waitingDialog.dismiss();
//                    if (response.code()==401){
//                        mensagemTokenExpirado();
//                    }
                }




            }

            @Override
            public void onFailure(@NonNull Call<List<Factura>> call, @NonNull Throwable t) {
                waitingDialog.dismiss();
                if (!MetodosUsados.conexaoInternetTrafego(getContext(),TAG)){
                    MetodosUsados.mostrarMensagem(getContext(),R.string.msg_erro_internet);
                }else  if ("timeout".equals(t.getMessage())) {
                    MetodosUsados.mostrarMensagem(getContext(),R.string.msg_erro_internet_timeout);
                }else {
                    MetodosUsados.mostrarMensagem(getContext(),R.string.msg_erro);
                }
            }
        });
    }

    private void setAdapters(List<Factura> facturaList) {
        waitingDialog.dismiss();

        if (facturaList.size()>0){

            Collections.reverse(facturaList);

            FacturaAdapter facturaAdapter = new FacturaAdapter(getContext(),facturaList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setHasFixedSize(true);
            facturaAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(facturaAdapter);
            facturaAdapter.setItemClickListener(new RecyclerViewOnItemClickListener() {
                @Override
                public void onItemClickListener(int position) {

                }
            });

        }else {
            MetodosUsados.mostrarMensagem(getContext(),"Não fez nenhum pedido!");
        }
    }
}
