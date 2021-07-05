package ao.co.proitconsulting.xpress.fragmentos.telasallestab;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.adapters.CategoryEstabAdapter;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.Estabelecimento;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerquisarTodosEsbalecimentosFragment extends Fragment {

    private static final String TAG = "TAG_PerquisarFragment";
//    private PerquisarTodosEsbalecimentosViewModel perquisarTodosEsbalecimentosViewModel;
    private View view;

    private List<Estabelecimento> estabelecimentoList = new ArrayList<>();
    private CategoryEstabAdapter categoryEstabAdapter;

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private AlertDialog waitingDialog;
    private String errorMessage;

    private ConstraintLayout coordinatorLayout;
    private RelativeLayout errorLayout;
    private ImageView imgErro;
    private TextView txtMsgErro;
    private TextView btnTentarDeNovo;
    private String escolherFiltro="";

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL = 5000; // 5 secs
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    private boolean mLocationPermissionGranted = false;

    //DIALOG_LAYOUT_CONFIRMAR_PROCESSO
    private Dialog dialogLayoutConfirmarProcesso;
    private ImageView imgConfirm;
    private TextView txtConfirmTitle,txtConfirmMsg;
    private Button dialog_btn_deny_processo,dialog_btn_accept_processo;

    public PerquisarTodosEsbalecimentosFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        perquisarTodosEsbalecimentosViewModel =
//                new ViewModelProvider(this).get(PerquisarTodosEsbalecimentosViewModel.class);
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_pesquisar_todos_estabs, container, false);

        if (getActivity()!=null){
            if (((AppCompatActivity)getActivity())
                    .getSupportActionBar()!=null){
                if (getContext()!=null){
                    final Drawable upArrow = ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_arrow_back_24);;
                    assert upArrow != null;
                    upArrow.setColorFilter(getResources().getColor(R.color.ic_menu_burguer_color), PorterDuff.Mode.SRC_ATOP);
                    ((AppCompatActivity)getActivity())
                            .getSupportActionBar().setHomeAsUpIndicator(upArrow);

                }

            }
        }


        initViews();




//        perquisarTodosEsbalecimentosViewModel.getMutableLiveData_ALLEstab().observe(this, new Observer<List<Estabelecimento>>() {
//            @Override
//            public void onChanged(List<Estabelecimento> estabelecimentoList) {
//
//                if (estabelecimentoList==null){
//                    imgErro.setImageResource(R.drawable.ic_baseline_store_off_24);
//                    txtMsgErro.setText("Sem estabelecimentos disponíveis!");
//                    mostarMsnErro();
//                }else {
//                    if (estabelecimentoList.size()>0){
//                        setAdapters(estabelecimentoList);
//                    }else{
//                        imgErro.setImageResource(R.drawable.ic_baseline_store_off_24);
//                        txtMsgErro.setText("Sem estabelecimentos disponíveis!");
//                        mostarMsnErro();
//                    }
//                }
//
//
//
//            }
//        });




        return view;
    }



    private void initViews() {
        waitingDialog = new SpotsDialog.Builder().setContext(getContext()).setTheme(R.style.CustomSpotsDialog).build();
        waitingDialog.setMessage("Por favor aguarde...");
        waitingDialog.setCancelable(false);


        recyclerView = view.findViewById(R.id.recyclerViewEstab);

        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        errorLayout = view.findViewById(R.id.erroLayout);
        imgErro = view.findViewById(R.id.imgErro);
        txtMsgErro = view.findViewById(R.id.txtMsgErro);
        btnTentarDeNovo = view.findViewById(R.id.btn);
        btnTentarDeNovo.setVisibility(View.INVISIBLE);

        if (getContext()!=null)
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

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
        txtConfirmTitle.setVisibility(View.INVISIBLE);
        txtConfirmMsg = dialogLayoutConfirmarProcesso.findViewById(R.id.txtConfirmMsg);
        dialog_btn_deny_processo = dialogLayoutConfirmarProcesso.findViewById(R.id.dialog_btn_deny_processo);
        dialog_btn_deny_processo.setText(getString(R.string.no_thanks));
        dialog_btn_accept_processo = dialogLayoutConfirmarProcesso.findViewById(R.id.dialog_btn_accept_processo);
        dialog_btn_accept_processo.setText(getString(R.string.ok));
    }

    //--------------LISTAR_ESTABELECIMENTOS_TODOS-----------------------------///
    //-----------------------------------------------------------------------///
    private void verifConecxaoEstabelecimento_TODOS() {

        if (getActivity()!=null) {
            ConnectivityManager conMgr =  (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
                    imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24);
                    txtMsgErro.setText(getString(R.string.msg_erro_internet));
                    mostarMsnErro();
                } else {
//                    carregarListaMenuCategory();
                    carregarListaEstabelicimentos_TODOS();
                }
            }
        }

    }

    private void carregarListaEstabelicimentos_TODOS() {

        waitingDialog.show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Estabelecimento>> rv = apiInterface.getEstabelecimentos_TODOS();
        rv.enqueue(new Callback<List<Estabelecimento>>() {
            @Override
            public void onResponse(@NonNull Call<List<Estabelecimento>> call, @NonNull Response<List<Estabelecimento>> response) {

                if (response.isSuccessful()) {
                    waitingDialog.setMessage("Carregando...");
                    if (estabelecimentoList!=null)
                        estabelecimentoList.clear();
                    else
                        estabelecimentoList = new ArrayList<>();

                    Common.todosEstabelecimentoList=new ArrayList<>();

                    if (response.body()!=null && response.body().size()>0){


                        for (Estabelecimento estab: response.body()) {
                            if (estab!=null){
                                if (estab.estadoEstabelecimento!=null){
                                    estabelecimentoList.add(estab);

                                }
                            }
                        }
                        Common.todosEstabelecimentoList.addAll(estabelecimentoList);

                        setAdapters(Common.todosEstabelecimentoList);



                    }else{
                        waitingDialog.dismiss();
                        MetodosUsados.mostrarMensagem(getContext(),"Sem estabelecimentos disponíveis!");
                        imgErro.setImageResource(R.drawable.ic_baseline_store_off_24);
                        txtMsgErro.setText("Sem estabelecimentos disponíveis!");
                        mostarMsnErro();
                        Common.todosEstabelecimentoList = null;
                    }

                } else {

//                    progressBar.setVisibility(View.GONE);
                    waitingDialog.dismiss();
                    try {
                        errorMessage = response.errorBody().string();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (response.code() != 401){
                        imgErro.setImageResource(R.drawable.ic_baseline_report_problem_24);
                        txtMsgErro.setText(getString(R.string.msg_erro));
                        mostarMsnErro();
                    }

                    Common.todosEstabelecimentoList = null;

                }


            }

            @Override
            public void onFailure(@NonNull Call<List<Estabelecimento>> call, @NonNull Throwable t) {
                waitingDialog.dismiss();
                Log.d(TAG, "onResponseEstabFailed: "+t.getMessage());
                if (getContext()!=null){
                    if (!MetodosUsados.conexaoInternetTrafego(getContext(),TAG)){
                        imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24);
                        txtMsgErro.setText(getString(R.string.msg_erro_internet));
                        mostarMsnErro();
                    }else  if (t.getMessage().contains("timeout")) {
                        imgErro.setImageResource(R.drawable.ic_baseline_wifi_alert_24);
                        txtMsgErro.setText(getString(R.string.msg_erro_internet_timeout));
                        mostarMsnErro();
                    }else {
                        imgErro.setImageResource(R.drawable.ic_baseline_report_problem_24);
                        txtMsgErro.setText(getString(R.string.msg_erro));
                        mostarMsnErro();
                    }
                }

            }
        });
    }
    //-------------LISTAR_ESTABELECIMENTOS_TODOS-----------------------------///
    //----------------------------------------------------------------------///


    //-------------LISTAR_ESTABELECIMENTOS_PERTO_DE_MIM-----------------------------///
    //----------------------------------------------------------------------///
    private void verifConecxaoEstabelecimento_PERTO_DE_MIM(double latitude,double longitude) {

        if (getActivity()!=null) {
            ConnectivityManager conMgr =  (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
                    imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24);
                    txtMsgErro.setText(getString(R.string.msg_erro_internet));
                    mostarMsnErro();
                } else {
//                    carregarListaMenuCategory();
                    carregarListaEstabelicimentos_PERTO_DE_MIM(latitude,longitude);
                }
            }
        }

    }

    private void carregarListaEstabelicimentos_PERTO_DE_MIM(double latitude,double longitude) {
        waitingDialog.show();


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Estabelecimento>> rv = apiInterface.getEstabelecimentos_PERTO_DE_MIM(latitude,longitude);
        rv.enqueue(new Callback<List<Estabelecimento>>() {
            @Override
            public void onResponse(@NonNull Call<List<Estabelecimento>> call, @NonNull Response<List<Estabelecimento>> response) {

                if (response.isSuccessful()) {
                    waitingDialog.setMessage("Carregando...");
                    if (estabelecimentoList!=null)
                        estabelecimentoList.clear();
                    else
                        estabelecimentoList = new ArrayList<>();

                    Common.todosEstabelecimentoList=new ArrayList<>();

                    if (response.body()!=null && response.body().size()>0){


                        for (Estabelecimento estab: response.body()) {
                            if (estab!=null){
                                if (estab.estadoEstabelecimento!=null){
                                    estabelecimentoList.add(estab);

                                    Log.d(TAG, "onResponseEstab: "+estab.nomeEstabelecimento+" - "+estab.tipoDeEstabelecimento.descricao);
                                }
                            }
                        }
                        Common.todosEstabelecimentoList.addAll(estabelecimentoList);

                        setAdapters(Common.todosEstabelecimentoList);



                    }else{
                        waitingDialog.dismiss();
                        MetodosUsados.mostrarMensagem(getContext(),"Sem estabelecimentos disponíveis!");
                        imgErro.setImageResource(R.drawable.ic_baseline_store_off_24);
                        txtMsgErro.setText("Sem estabelecimentos disponíveis!");
                        mostarMsnErro();
                        Common.todosEstabelecimentoList = null;
                    }

                } else {

//                    progressBar.setVisibility(View.GONE);
                    waitingDialog.dismiss();
                    try {
                        errorMessage = response.errorBody().string();
                        Log.d(TAG, "onResponseEstabError: "+errorMessage+", ResponseCode: "+response.code());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (response.code() != 401){
                        imgErro.setImageResource(R.drawable.ic_baseline_report_problem_24);
                        txtMsgErro.setText(getString(R.string.msg_erro));
                        mostarMsnErro();
                    }

                    Common.todosEstabelecimentoList = null;

                }


            }

            @Override
            public void onFailure(@NonNull Call<List<Estabelecimento>> call, @NonNull Throwable t) {
                waitingDialog.dismiss();
                Log.d(TAG, "onResponseEstabFailed: "+t.getMessage());
                if (getContext()!=null){
                    if (!MetodosUsados.conexaoInternetTrafego(getContext(),TAG)){
                        imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24);
                        txtMsgErro.setText(getString(R.string.msg_erro_internet));
                        mostarMsnErro();
                    }else  if (t.getMessage().contains("timeout")) {
                        imgErro.setImageResource(R.drawable.ic_baseline_wifi_alert_24);
                        txtMsgErro.setText(getString(R.string.msg_erro_internet_timeout));
                        mostarMsnErro();
                    }else {
                        imgErro.setImageResource(R.drawable.ic_baseline_report_problem_24);
                        txtMsgErro.setText(getString(R.string.msg_erro));
                        mostarMsnErro();
                    }
                }
            }
        });
    }
    //-------------LISTAR_ESTABELECIMENTOS_PERTO_DE_MIM-----------------------------///
    //----------------------------------------------------------------------///


    //-------------LISTAR_ESTABELECIMENTOS_MAIS_POPULARES-----------------------------///
    //----------------------------------------------------------------------///
    private void verifConecxaoEstabelecimento_MAIS_POPULARES() {

        if (getActivity()!=null) {
            ConnectivityManager conMgr =  (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
                    imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24);
                    txtMsgErro.setText(getString(R.string.msg_erro_internet));
                    mostarMsnErro();
                } else {
//                    carregarListaMenuCategory();
                    carregarListaEstabelicimentos_MAIS_POPULARES();
                }
            }
        }

    }

    private void carregarListaEstabelicimentos_MAIS_POPULARES() {
        waitingDialog.show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Estabelecimento>> rv = apiInterface.getEstabelecimentos_MAISPOPULARES();
        rv.enqueue(new Callback<List<Estabelecimento>>() {
            @Override
            public void onResponse(@NonNull Call<List<Estabelecimento>> call, @NonNull Response<List<Estabelecimento>> response) {

                if (response.isSuccessful()) {
                    waitingDialog.setMessage("Carregando...");
                    if (estabelecimentoList!=null)
                        estabelecimentoList.clear();
                    else
                        estabelecimentoList = new ArrayList<>();

                    Common.todosEstabelecimentoList=new ArrayList<>();

                    if (response.body()!=null && response.body().size()>0){


                        for (Estabelecimento estab: response.body()) {
                            if (estab!=null){
                                if (estab.estadoEstabelecimento!=null){
                                    estabelecimentoList.add(estab);

                                    Log.d(TAG, "onResponseEstab: "+estab.nomeEstabelecimento+" - "+estab.tipoDeEstabelecimento.descricao);
                                }
                            }
                        }
                        Common.todosEstabelecimentoList.addAll(estabelecimentoList);

                        setAdapters(Common.todosEstabelecimentoList);



                    }else{
                        waitingDialog.dismiss();
                        MetodosUsados.mostrarMensagem(getContext(),"Sem estabelecimentos disponíveis!");
                        imgErro.setImageResource(R.drawable.ic_baseline_store_off_24);
                        txtMsgErro.setText("Sem estabelecimentos disponíveis!");
                        mostarMsnErro();
                        Common.todosEstabelecimentoList=null;
                    }

                } else {

//                    progressBar.setVisibility(View.GONE);
                    waitingDialog.dismiss();
                    try {
                        errorMessage = response.errorBody().string();
                        Log.d(TAG, "onResponseEstabError: "+errorMessage+", ResponseCode: "+response.code());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (response.code() != 401){
                        imgErro.setImageResource(R.drawable.ic_baseline_report_problem_24);
                        txtMsgErro.setText(getString(R.string.msg_erro));
                        mostarMsnErro();
                    }

                    Common.todosEstabelecimentoList=null;

                }


            }

            @Override
            public void onFailure(@NonNull Call<List<Estabelecimento>> call, @NonNull Throwable t) {
                waitingDialog.dismiss();
                Log.d(TAG, "onResponseEstabFailed: "+t.getMessage());
                if (getContext()!=null){
                    if (!MetodosUsados.conexaoInternetTrafego(getContext(),TAG)){
                        imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24);
                        txtMsgErro.setText(getString(R.string.msg_erro_internet));
                        mostarMsnErro();
                    }else  if (t.getMessage().contains("timeout")) {
                        imgErro.setImageResource(R.drawable.ic_baseline_wifi_alert_24);
                        txtMsgErro.setText(getString(R.string.msg_erro_internet_timeout));
                        mostarMsnErro();
                    }else {
                        imgErro.setImageResource(R.drawable.ic_baseline_report_problem_24);
                        txtMsgErro.setText(getString(R.string.msg_erro));
                        mostarMsnErro();
                    }
                }
            }
        });
    }
    //-------------LISTAR_ESTABELECIMENTOS_MAIS_POPULARES-----------------------------///
    //----------------------------------------------------------------------///


    //-------------LISTAR_ESTABELECIMENTOS_ALTAS_HORAS-----------------------------///
    //----------------------------------------------------------------------///
    private void verifConecxaoEstabelecimento_ALTASHORAS() {

        if (getActivity()!=null) {
            ConnectivityManager conMgr =  (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
                    imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24);
                    txtMsgErro.setText(getString(R.string.msg_erro_internet));
                    mostarMsnErro();
                } else {
//                    carregarListaMenuCategory();
                    carregarListaEstabelicimentos_ALTASHORAS();
                }
            }
        }

    }

    private void carregarListaEstabelicimentos_ALTASHORAS() {
        waitingDialog.show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Estabelecimento>> rv = apiInterface.getEstabelecimentos_ALTASHORAS();
        rv.enqueue(new Callback<List<Estabelecimento>>() {
            @Override
            public void onResponse(@NonNull Call<List<Estabelecimento>> call, @NonNull Response<List<Estabelecimento>> response) {

                if (response.isSuccessful()) {
                    waitingDialog.setMessage("Carregando...");
                    if (estabelecimentoList!=null)
                        estabelecimentoList.clear();
                    else
                        estabelecimentoList = new ArrayList<>();

                    Common.todosEstabelecimentoList=new ArrayList<>();

                    if (response.body()!=null && response.body().size()>0){


                        for (Estabelecimento estab: response.body()) {
                            if (estab!=null){
                                if (estab.estadoEstabelecimento!=null){
                                    estabelecimentoList.add(estab);

                                    Log.d(TAG, "onResponseEstab: "+estab.nomeEstabelecimento+" - "+estab.tipoDeEstabelecimento.descricao);
                                }
                            }
                        }
                        Common.todosEstabelecimentoList.addAll(estabelecimentoList);

                        setAdapters(Common.todosEstabelecimentoList);



                    }else{
                        waitingDialog.dismiss();
                        MetodosUsados.mostrarMensagem(getContext(),"Sem estabelecimentos disponíveis!");
                        imgErro.setImageResource(R.drawable.ic_baseline_store_off_24);
                        txtMsgErro.setText("Sem estabelecimentos disponíveis!");
                        mostarMsnErro();
                        Common.todosEstabelecimentoList=null;
                    }

                } else {

//                    progressBar.setVisibility(View.GONE);
                    waitingDialog.dismiss();
                    try {
                        errorMessage = response.errorBody().string();
                        Log.d(TAG, "onResponseEstabError: "+errorMessage+", ResponseCode: "+response.code());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (response.code() != 401){
                        imgErro.setImageResource(R.drawable.ic_baseline_report_problem_24);
                        txtMsgErro.setText(getString(R.string.msg_erro));
                        mostarMsnErro();
                    }
                    Common.todosEstabelecimentoList=null;

                }


            }

            @Override
            public void onFailure(@NonNull Call<List<Estabelecimento>> call, @NonNull Throwable t) {
                waitingDialog.dismiss();
                Log.d(TAG, "onResponseEstabFailed: "+t.getMessage());
                if (getContext()!=null){
                    if (!MetodosUsados.conexaoInternetTrafego(getContext(),TAG)){
                        imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24);
                        txtMsgErro.setText(getString(R.string.msg_erro_internet));
                        mostarMsnErro();
                    }else  if (t.getMessage().contains("timeout")) {
                        imgErro.setImageResource(R.drawable.ic_baseline_wifi_alert_24);
                        txtMsgErro.setText(getString(R.string.msg_erro_internet_timeout));
                        mostarMsnErro();
                    }else {
                        imgErro.setImageResource(R.drawable.ic_baseline_report_problem_24);
                        txtMsgErro.setText(getString(R.string.msg_erro));
                        mostarMsnErro();
                    }
                }
            }
        });
    }
    //-------------LISTAR_ESTABELECIMENTOS_ALTAS_HORAS-----------------------------///
    //----------------------------------------------------------------------///

    private void setAdapters(List<Estabelecimento> estabelecimentoList) {
        waitingDialog.dismiss();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), AppPrefsSettings.getInstance().getListGridViewMode());
        categoryEstabAdapter = new CategoryEstabAdapter(getContext(), estabelecimentoList, gridLayoutManager);
        recyclerView.setAdapter(categoryEstabAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
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

            }
        });
    }

    @Override
    public void onResume() {


        if (Common.todosEstabelecimentoList == null)
            Common.todosEstabelecimentoList = new ArrayList<>();
        else
            Common.todosEstabelecimentoList.clear();

        if (errorLayout.getVisibility() == View.VISIBLE){
            errorLayout.setVisibility(View.GONE);
            coordinatorLayout.setVisibility(View.VISIBLE);
        }

        switch (AppPrefsSettings.getInstance().getEstabFilterView()){
//----------------------------------------------------------------------///
            //LISTAR_ESTABELECIMENTOS_TODOS
            case 0:

                verifConecxaoEstabelecimento_TODOS();
                break;
//----------------------------------------------------------------------///
            //LISTAR_ESTABELECIMENTOS_PERTO_DE_MIM
            case 1:

                if(checkMapServices()){
                    if(mLocationPermissionGranted){
                        //PERTO DE MIM
                        getMyLoCation();

                    }else{
                        getLocationPermission();
                    }

                }
                break;
//----------------------------------------------------------------------///
            //LISTAR_ESTABELECIMENTOS_MAIS_POPULARES
            case 2:

                verifConecxaoEstabelecimento_MAIS_POPULARES();
                break;
//----------------------------------------------------------------------///
            //LISTAR_ESTABELECIMENTOS_ALTAS_HORAS
            case 3:

                verifConecxaoEstabelecimento_ALTASHORAS();
                break;
//----------------------------------------------------------------------///
            //LISTAR_ESTABELECIMENTOS_TODOS
            default:

                verifConecxaoEstabelecimento_TODOS();
                break;
        }


        super.onResume();



    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Common.mLastLocation = locationResult.getLocations().get(locationResult.getLocations().size() - 1); //Get last location
                displayLocation();
            }
        };
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void displayLocation() {

        if (getContext()!=null){
            if (ActivityCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }


        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Common.mLastLocation = location;

                if (Common.mLastLocation != null) {


                    latitude = Common.mLastLocation.getLatitude();
                    longitude = Common.mLastLocation.getLongitude();


                    verifConecxaoEstabelecimento_PERTO_DE_MIM(latitude,longitude);


                    Log.d(TAG, String.format("Your location was changed : %f / %f",
                            latitude, longitude));

                } else {
                    Log.d(TAG, "Can not get your location");
                }
            }
        });

    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {


        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Lojas");
//        searchView.setQueryHint(getString(R.string.pesquisar));

        SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete)searchView.findViewById(R.id.search_src_text);

        if (getContext()!=null)
            theTextArea.setTextColor(ContextCompat.getColor(getContext(), R.color.search_text_color));
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

// Does help!
        ImageView searchClose = (ImageView) searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchClose.setImageResource(R.drawable.ic_baseline_clear_search_24);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (categoryEstabAdapter!=null)
                    categoryEstabAdapter.getFilter().filter(newText);



                return false;
            }
        });
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_options_search, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.action_filtros:
                mostraTelaDosFiltros();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void mostraTelaDosFiltros() {
        NavHostFragment.findNavController(PerquisarTodosEsbalecimentosFragment.this)
                .navigate(R.id.nav_menu_escolher_filtros);
    }

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {

        txtConfirmMsg.setText(getString(R.string.msg_ligar_gps));

        dialog_btn_deny_processo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogLayoutConfirmarProcesso.cancel();
                mostraTelaDosFiltros();
            }
        });

        dialog_btn_accept_processo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogLayoutConfirmarProcesso.cancel();
                Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(enableGpsIntent, Common.PERMISSIONS_REQUEST_ENABLE_GPS);

            }
        });

        dialogLayoutConfirmarProcesso.show();
    }

    public boolean isMapsEnabled(){
        if (getContext()!=null) {
            final LocationManager manager = (LocationManager) getContext().getSystemService( Context.LOCATION_SERVICE );


            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                buildAlertMessageNoGps();
                return false;
            }
            return true;
        }else{
            return false;
        }

    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (getContext()!=null && getActivity()!=null){
            if (ContextCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
//                AppPrefsSettings.getInstance().setLocationStatus(mLocationPermissionGranted);
                getMyLoCation();


            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        Common.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }

    }

    private void getMyLoCation() {
        buildLocationCallBack();
        createLocationRequest();
        displayLocation();
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            if (getActivity()!=null){
                Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, Common.ERROR_DIALOG_REQUEST);
                dialog.show();
            }

        }else{
            if (getContext()!=null)
                Toast.makeText(getContext(), "Você não pode fazer solicitações de localização.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case Common.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;

                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case Common.PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    getMyLoCation();
                }
                else{
                    getLocationPermission();
                }
            }
        }
    }
}
