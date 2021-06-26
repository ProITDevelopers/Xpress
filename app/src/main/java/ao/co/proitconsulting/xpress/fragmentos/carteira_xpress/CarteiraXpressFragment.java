package ao.co.proitconsulting.xpress.fragmentos.carteira_xpress;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.UsuarioPerfil;
import ao.co.proitconsulting.xpress.modelos.Wallet;
import ao.co.proitconsulting.xpress.modelos.WalletRequest;
import dmax.dialog.SpotsDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarteiraXpressFragment extends Fragment {

    private static final String TAG = "TAG_CarteiraXpress";
    private View view;
    private ConstraintLayout constraintLayout;

    //DIALOG_LAYOUT_CONFIRMAR_PROCESSO
    private Dialog dialogLayoutConfirmarProcesso;
    private ImageView imgConfirm;
    private TextView txtConfirmTitle,txtConfirmMsg;
    private Button dialog_btn_deny_processo,dialog_btn_accept_processo;

    //COORDINATORLAYOUT_XPRESS_SALDO
    private CoordinatorLayout coordinatorLayout_XpressSaldo;
    private RoundedImageView imgUserPhoto;
    private TextView txtUserNameInitial,txtPrimeiroNome,txtSobrenome;
    private TextView txtTelefone,txtTelefoneAlternativo,txtEmail;
    private TextView txtNumeroConta,txtCreditValue;


    //RELATIVELAYOUT_XPRESS_CONTA
    private RelativeLayout relative_XpressConta;
    private AppCompatEditText editTextBI,editTextDataNasc;
    private Button btnCriar_carteira_xpress;

    private String bi, dataNasc, data_toShow, date;
    long minDateMilliseconds,maxDateMilliseconds;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Wallet wallet = new Wallet();
    private UsuarioPerfil usuarioPerfil;



    //RELATIVELAYOUT_ERRORLAYOUT
    private RelativeLayout errorLayout;
    private ImageView imgErro;
    private TextView txtMsgErro;
    private TextView btnTentarDeNovo;
    private String errorMessage;

    private AlertDialog waitingDialog;

    public CarteiraXpressFragment() {}


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_carteira_xpress, container, false);
        if (getActivity()!=null){
            if (((AppCompatActivity)getActivity())
                    .getSupportActionBar()!=null){
                if (getContext()!=null){
                    final Drawable upArrow = ContextCompat.getDrawable(getContext(), R.drawable.ic_menu_burguer);;
                    assert upArrow != null;
                    upArrow.setColorFilter(getResources().getColor(R.color.ic_menu_burguer_color), PorterDuff.Mode.SRC_ATOP);
                    ((AppCompatActivity)getActivity())
                            .getSupportActionBar().setHomeAsUpIndicator(upArrow);

                }

            }
        }


        initViews();



        verifConecxaoSaldoWallet();

        return view;
    }

    private void initViews() {
        usuarioPerfil = AppPrefsSettings.getInstance().getUser();
        waitingDialog = new SpotsDialog.Builder().setContext(getContext()).setTheme(R.style.CustomSpotsDialog).build();
        waitingDialog.setMessage("Por favor aguarde...");
        waitingDialog.setCancelable(false);

        constraintLayout = view.findViewById(R.id.constraintLayout);
        errorLayout = view.findViewById(R.id.erroLayout);
        imgErro = view.findViewById(R.id.imgErro);
        txtMsgErro = view.findViewById(R.id.txtMsgErro);
        btnTentarDeNovo = view.findViewById(R.id.btn);

        //-------------------------------------------------------------//
        //-------------------------------------------------------------//
        //DIALOG_LAYOUT_CONFIRMAR_PROCESSO
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

        //COORDINATORLAYOUT_XPRESS_SALDO
        coordinatorLayout_XpressSaldo = view.findViewById(R.id.coordinatorLayout_XpressSaldo);
        imgUserPhoto = view.findViewById(R.id.imgUserPhoto);
        txtUserNameInitial = view.findViewById(R.id.txtUserNameInitial);
        txtPrimeiroNome = view.findViewById(R.id.txtPrimeiroNome);
        txtSobrenome = view.findViewById(R.id.txtSobrenome);
        txtTelefone = view.findViewById(R.id.txtTelefone);
        txtTelefoneAlternativo = view.findViewById(R.id.txtTelefoneAlternativo);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtNumeroConta = view.findViewById(R.id.txtNumeroConta);
        txtCreditValue = view.findViewById(R.id.txtCreditValue);

        //RELATIVELAYOUT_XPRESS_CONTA
        relative_XpressConta = view.findViewById(R.id.relative_XpressConta);
        editTextBI = view.findViewById(R.id.editTextBI);
        editTextDataNasc = view.findViewById(R.id.editTextDataNasc);
        btnCriar_carteira_xpress = view.findViewById(R.id.btnCriar_carteira_xpress);

        //RELATIVELAYOUT_ERRORLAYOUT
//        errorLayout = view.findViewById(R.id.errorLayout);
//        btnTentarDeNovo = view.findViewById(R.id.btnTentarDeNovo);

        String minDate = "1914-09-01";
        String maxDate = "2000-12-31";

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date daymin = f.parse(minDate);
//            Date daymax = f.parse(maxDate);
//            minDateMilliseconds = daymin.getTime();
//            maxDateMilliseconds = daymax.getTime();



        } catch (ParseException e) {
            e.printStackTrace();
        }


        Calendar calendarMin = Calendar.getInstance();
        calendarMin.add(Calendar.YEAR, -90);
        calendarMin.set(Calendar.MONTH,0);
        calendarMin.set(Calendar.DAY_OF_MONTH,1);
        minDateMilliseconds = calendarMin.getTimeInMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -19);
        calendar.set(Calendar.MONTH,11);
        calendar.set(Calendar.DAY_OF_MONTH,31);
        maxDateMilliseconds = calendar.getTimeInMillis();

        editTextDataNasc.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            int ano = cal.get(Calendar.YEAR);
            int mes = cal.get(Calendar.MONTH);
            int dia = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    getContext(),
                    R.style.DialogTheme,
                    mDateSetListener,
                    ano, mes, dia);

            dialog.getDatePicker().setMinDate(minDateMilliseconds);
            dialog.getDatePicker().setMaxDate(maxDateMilliseconds);
            dialog.show();
        });

        mDateSetListener = (datePicker, ano, mes, dia) -> {
            mes = mes + 1;

            String monthString = String.valueOf(mes);
            String dayString = String.valueOf(dia);
            if (monthString.length() == 1) {
                monthString = "0" + monthString;
            }

            if (dayString.length() == 1) {
                dayString = "0" + dayString;
            }


//            date = ano + "-" + mes + "-" + dia;
            data_toShow = dayString + "/" + monthString + "/" + ano;
            date = ano + "-" + monthString + "-" + dayString;
            editTextDataNasc.setText(data_toShow);
        };


    }

    private void mostarMsnErro(){

        if (errorLayout.getVisibility() == View.GONE){
            errorLayout.setVisibility(View.VISIBLE);

            constraintLayout.setVisibility(View.GONE);


        }

        btnTentarDeNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                constraintLayout.setVisibility(View.VISIBLE);

                errorLayout.setVisibility(View.GONE);
                verifConecxaoSaldoWallet();
            }
        });
    }



    private void verifConecxaoSaldoWallet() {
        if (getActivity()!=null) {
            ConnectivityManager conMgr =  (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
                    imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24);
                    txtMsgErro.setText(getString(R.string.msg_erro_internet));
                    mostarMsnErro();
                } else {
                    saldoContaWalletApi();
                }
            }
        }



    }

    private void saldoContaWalletApi() {

        waitingDialog.setMessage("Consultar a conta...");
        waitingDialog.show();


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

                        carregarMeuPerfilOffline(usuarioPerfil);


                    }




                } else {
                    waitingDialog.dismiss();

                    if (response.code()==401){

                        mensagemTokenExpirado();

                    }else{
                        mostrarTelaCriarWallet();
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Wallet>>call, @NonNull Throwable t) {
                waitingDialog.dismiss();

                Log.d(TAG, "CarteiraXpressFragment: "+t.getMessage());
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

    private void verifConecxaoCriarWallet() {

        if (getActivity()!=null) {
            ConnectivityManager conMgr =  (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
                    imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24);
                    txtMsgErro.setText(getString(R.string.msg_erro_internet));
                    mostarMsnErro();
                } else {
                    criarContaWalletApi();
                }
            }
        }
    }

    private void criarContaWalletApi() {

        if (usuarioPerfil.contactoMovel == null){
            MetodosUsados.mostrarMensagem(getContext(),"É necessário o seu "+getString(R.string.numero_telefone)+" para continuar.");
            return;
        }

        if (usuarioPerfil.contactoMovel.equals("") || usuarioPerfil.contactoMovel.equals("?")){
            MetodosUsados.mostrarMensagem(getContext(),"Adicione um "+getString(R.string.numero_telefone)+" ao seu Perfil para continuar.");
            return;
        }
        if (!usuarioPerfil.contactoMovel.matches("9[1-9][0-9]\\d{6}")){
            MetodosUsados.mostrarMensagem(getContext(),"O seu "+getString(R.string.numero_telefone)+" é inválido. Por favor edite o seu Perfil.");
            return;
        }

        waitingDialog.setMessage("Criar conta...");
        waitingDialog.show();

        WalletRequest walletRequest = new WalletRequest();
        walletRequest.numeroBi = bi;
        walletRequest.dataNasc = dataNasc;
//        walletRequest.identityType = "BI";

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.criarContaWallet(walletRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                //response.body()==null
                if (response.isSuccessful()) {

                    waitingDialog.dismiss();
                    relative_XpressConta.setVisibility(View.GONE);
                    verifConecxaoSaldoWallet();


                } else {
                    waitingDialog.dismiss();
                    try {
                        errorMessage = response.errorBody().string();
                        Log.d(TAG, "relative_XpressContaError: "+errorMessage+", ResponseCode: "+response.code());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (response.code() != 401){
                        imgErro.setImageResource(R.drawable.ic_baseline_report_problem_24);
                        txtMsgErro.setText(getString(R.string.msg_erro));
                        mostarMsnErro();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                waitingDialog.dismiss();
                Log.d(TAG, "relative_XpressContaFailed: "+t.getMessage());
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

    private void carregarMeuPerfilOffline(UsuarioPerfil usuarioPerfil) {
        waitingDialog.dismiss();
        if (coordinatorLayout_XpressSaldo.getVisibility() == View.GONE){
            coordinatorLayout_XpressSaldo.setVisibility(View.VISIBLE);
            relative_XpressConta.setVisibility(View.GONE);

        }

        String userNameInitial, userLastNameInitial;
        if (usuarioPerfil!=null){

            if (usuarioPerfil.imagem.equals(getString(R.string.sem_imagem)) || usuarioPerfil.imagem == null){
                if (usuarioPerfil.primeiroNome != null && usuarioPerfil.ultimoNome != null){
                    userNameInitial = String.valueOf(usuarioPerfil.primeiroNome.charAt(0));
                    userLastNameInitial = String.valueOf(usuarioPerfil.ultimoNome.charAt(0));
                    txtUserNameInitial.setText(userNameInitial.toUpperCase().concat(userLastNameInitial.toUpperCase()));
                    txtUserNameInitial.setVisibility(View.VISIBLE);

                }

            }else {
                txtUserNameInitial.setVisibility(View.GONE);
                Picasso.with(getContext())
                        .load(usuarioPerfil.imagem)
//                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .fit().centerCrop()
                        .placeholder(R.drawable.photo_placeholder)
                        .into(imgUserPhoto);
            }

            txtPrimeiroNome.setText(usuarioPerfil.primeiroNome);
            txtSobrenome.setText(usuarioPerfil.ultimoNome);
            txtTelefone.setText(usuarioPerfil.contactoMovel);

            if (usuarioPerfil.contactoAlternativo==null || usuarioPerfil.contactoAlternativo.isEmpty()){
                txtTelefoneAlternativo.setVisibility(View.GONE);
                txtTelefoneAlternativo.setText("");
            } else{
                txtTelefoneAlternativo.setText(usuarioPerfil.contactoAlternativo);
            }

            txtEmail.setText(usuarioPerfil.email);

            txtNumeroConta.setText(wallet.number);
            txtCreditValue.setText(wallet.amount.concat(" AKZ"));


        }


    }



    private void mostrarTelaCriarWallet(){

        if (relative_XpressConta.getVisibility() == View.GONE){
            relative_XpressConta.setVisibility(View.VISIBLE);

        }

        btnCriar_carteira_xpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificarCampos()){
                    verifConecxaoCriarWallet();
                }
            }
        });
    }


    private boolean verificarCampos() {

        bi = editTextBI.getText().toString().trim();
        dataNasc = date;


        if (bi.isEmpty()){
            editTextBI.requestFocus();
            editTextBI.setError("Preencha o campo");


            return false;
        }

        if (!bi.matches("(\\d){9}[a-zA-Z][a-zA-Z](\\d){3}")){
            editTextBI.requestFocus();
            editTextBI.setError("Verifica o campo.");

            return false;
        }

        if (dataNasc.isEmpty()){
            editTextDataNasc.requestFocus();
            editTextDataNasc.setError("Preencha o campo");

            return false;
        }


        return true;

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
    
    
}

    

    















    