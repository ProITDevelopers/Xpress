package ao.co.proitconsulting.xpress.fragmentos.encomenda_history;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.adapters.EncomendaFacturaAdapter;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.modelos.Factura;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EncomendasHistoricoFragment extends Fragment {

    private static String TAG = "TAG_EncomendasHistoricoFrag";
    private View view;

    private AlertDialog waitingDialog;
    private NestedScrollView nestedScrollView;
    private ProgressBar progress_bar;
    private RecyclerView recyclerView;
    private List<Factura> facturaList;

    private int page = 1;
    private int limit = 10;

    private String errorMessage;

    private ConstraintLayout coordinatorLayout;
    private RelativeLayout errorLayout;
    private ImageView imgErro;
    private TextView txtMsgErro;
    private TextView btnTentarDeNovo;

    //DIALOG_LAYOUT_CONFIRMAR_PROCESSO
    private Dialog dialogLayoutConfirmarProcesso;
    private ImageView imgConfirm;
    private TextView txtConfirmTitle,txtConfirmMsg;
    private Button dialog_btn_deny_processo,dialog_btn_accept_processo;

    public EncomendasHistoricoFragment() {}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_encomendas_historico, container, false);

        initViews();

        return view;
    }

    private void initViews() {
        waitingDialog = new SpotsDialog.Builder().setContext(getContext()).setTheme(R.style.CustomSpotsDialog).build();
        waitingDialog.setMessage("Por favor aguarde...");
        waitingDialog.setCancelable(false);



        facturaList = new ArrayList<>();

        nestedScrollView = view.findViewById(R.id.scroll_View);
        recyclerView = view.findViewById(R.id.recyclerView);
        progress_bar = view.findViewById(R.id.progress_bar);

        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        errorLayout = view.findViewById(R.id.erroLayout);
        imgErro = view.findViewById(R.id.imgErro);
        txtMsgErro = view.findViewById(R.id.txtMsgErro);
        btnTentarDeNovo = view.findViewById(R.id.btn);
        btnTentarDeNovo.setVisibility(View.INVISIBLE);


        //-------------------------------------------------------------//
        //-------------------------------------------------------------//
        //DIALOG_LAYOUT_CONFIRMAR_PROCESSO
        if (getContext()!=null)
            dialogLayoutConfirmarProcesso = new Dialog(getContext());

        dialogLayoutConfirmarProcesso.setContentView(R.layout.layout_confirmar_processo);
        dialogLayoutConfirmarProcesso.setCancelable(false);
        if (dialogLayoutConfirmarProcesso.getWindow()!=null)
            dialogLayoutConfirmarProcesso.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        imgConfirm = dialogLayoutConfirmarProcesso.findViewById(R.id.imgConfirm);
        txtConfirmTitle = dialogLayoutConfirmarProcesso.findViewById(R.id.txtConfirmTitle);
        txtConfirmMsg = dialogLayoutConfirmarProcesso.findViewById(R.id.txtConfirmMsg);
        dialog_btn_deny_processo = dialogLayoutConfirmarProcesso.findViewById(R.id.dialog_btn_deny_processo);
        dialog_btn_accept_processo = dialogLayoutConfirmarProcesso.findViewById(R.id.dialog_btn_accept_processo);

        verifConecxaoEncomendas();

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                    //When reach last item position
                    //Increase page size
                    page++;
                    //Show progress bar
                    progress_bar.setVisibility(View.VISIBLE);
                    //Call method
                    carregarListaFacturas(page,limit);

                }
            }
        });
    }

    private void verifConecxaoEncomendas() {
        if (getContext()!=null){
            ConnectivityManager conMgr =  (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
                    page = 1;
                    limit = 10;
                    facturaList = new ArrayList<>();
                    btnTentarDeNovo.setVisibility(View.VISIBLE);
                    imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24);
                    txtMsgErro.setText(getString(R.string.msg_erro_internet));
                    mostarMsnErro();
                } else {
                    waitingDialog.show();
                    carregarListaFacturas(page, limit);
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

    private void carregarListaFacturas(int page, int limit) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        Call<List<Factura>> rv = apiInterface.getTodasFacturas();
        Call<List<Factura>> rv = apiInterface.getFacturas_Historico(page,limit);
        rv.enqueue(new Callback<List<Factura>>() {
            @Override
            public void onResponse(@NonNull Call<List<Factura>> call, @NonNull Response<List<Factura>> response) {



                if (response.isSuccessful()) {
                    waitingDialog.dismiss();
                    if (response.body()!=null && response.body().size()>0){
                        facturaList.addAll(response.body());

                        // Order the list by regist date.
                        Collections.sort(facturaList, new Factura());



                        setAdapters(facturaList);


                    }else{
                        waitingDialog.dismiss();
                        progress_bar.setVisibility(View.GONE);

                    }
                } else {

                    waitingDialog.dismiss();
                    progress_bar.setVisibility(View.GONE);
                    if (response.code()==401){
                        mensagemTokenExpirado();
                    }else{
                        btnTentarDeNovo.setVisibility(View.INVISIBLE);
                        imgErro.setImageResource(R.drawable.ic_baseline_report_problem_24);
                        txtMsgErro.setText(getString(R.string.msg_erro));
                        mostarMsnErro();
                    }

                    try {
                        errorMessage = response.errorBody().string();
                        Log.d(TAG, "onResponseEncomendaError: "+errorMessage+", ErrorCode:"+response.code());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }




            }

            @Override
            public void onFailure(@NonNull Call<List<Factura>> call, @NonNull Throwable t) {
                waitingDialog.dismiss();
                progress_bar.setVisibility(View.GONE);
                if (getContext()!=null){
                    if (!MetodosUsados.conexaoInternetTrafego(getContext(),TAG)){
                        btnTentarDeNovo.setVisibility(View.VISIBLE);
                        imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24);
                        txtMsgErro.setText(getString(R.string.msg_erro_internet));
                        mostarMsnErro();
                    }else  if (t.getMessage().contains("timeout")) {
                        btnTentarDeNovo.setVisibility(View.VISIBLE);
                        imgErro.setImageResource(R.drawable.ic_baseline_wifi_alert_24);
                        txtMsgErro.setText(getString(R.string.msg_erro_internet_timeout));
                        mostarMsnErro();
                    }else {
                        btnTentarDeNovo.setVisibility(View.INVISIBLE);
                        imgErro.setImageResource(R.drawable.ic_baseline_report_problem_24);
                        txtMsgErro.setText(getString(R.string.msg_erro));
                        mostarMsnErro();
                    }
                }
            }
        });
    }

    private void setAdapters(List<Factura> facturaList) {

        progress_bar.setVisibility(View.GONE);

        if (facturaList.size()>0){

            Collections.reverse(facturaList);

            EncomendaFacturaAdapter encomendaFacturaAdapter = new EncomendaFacturaAdapter(getContext(),facturaList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(encomendaFacturaAdapter);

        }else {
            MetodosUsados.mostrarMensagem(getContext(),"Não fez nenhum pedido!");
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
    public void onDestroyView() {
        EventBus.getDefault().removeAllStickyEvents();
        super.onDestroyView();
    }
}
