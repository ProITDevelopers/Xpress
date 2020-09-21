package ao.co.proitconsulting.xpress.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.api.ErrorResponce;
import ao.co.proitconsulting.xpress.api.ErrorUtils;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.modelos.ReporSenha;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlterarPalavraPasseActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AlterarSenhaActivity";


    //DIALOG_LAYOUT_ALTERAR_PASS_NUM_TELEFONE
    private Dialog dialogLayoutEnviarNumTelefone;
    private AppCompatEditText dialog_editTelefone;
    private Button dialog_btn_cancelar,dialog_btn_enviar;

    //DIALOG_LAYOUT_ALTERAR_PASS_CONFIRM_CODE_PASSWORD
    private Dialog dialogLayoutCodeResetPass;
    private ImageView imgBtnFecharTelef;
    private TextView txtTenteOutraVez,txt_telefone;
    private ShowHidePasswordEditText dialog_pinCodigoConfirmacao,dialog_editNovaPass;
    private Button dialog_btn_continuar;
    private String telefoneReceberDeNovo;
    private String telefoneRedif_senha;
    private String codigoConfTelef,novaSenha;

    //DIALOG_LAYOUT_SUCESSO
    private Dialog dialogLayoutSuccess;
    private TextView dialog_txtConfirmSucesso;
    private Button dialog_btn_sucesso;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showCustomUI();
        setContentView(R.layout.activity_alterar_palavra_passe);

        initViews();


    }

    private void showCustomUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void initViews() {

        //-------------------------------------------------------------//
        //-------------------------------------------------------------//
        //DIALOG_LAYOUT_ALTERAR_PASS_NUM_TELEFONE
        dialogLayoutEnviarNumTelefone = new Dialog(this);
        dialogLayoutEnviarNumTelefone.setContentView(R.layout.layout_alterarpass_num_telefone);
        dialogLayoutEnviarNumTelefone.setCancelable(false);
        if (dialogLayoutEnviarNumTelefone.getWindow()!=null)
            dialogLayoutEnviarNumTelefone.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog_editTelefone = dialogLayoutEnviarNumTelefone.findViewById(R.id.dialog_editTelefone);
        dialog_btn_cancelar = dialogLayoutEnviarNumTelefone.findViewById(R.id.dialog_btn_cancelar);
        dialog_btn_enviar = dialogLayoutEnviarNumTelefone.findViewById(R.id.dialog_btn_enviar);

        dialog_btn_cancelar.setOnClickListener(this);
        dialog_btn_enviar.setOnClickListener(this);

        dialogLayoutEnviarNumTelefone.show();

        //-------------------------------------------------------------//
        //-------------------------------------------------------------//
        //DIALOG_LAYOUT_ALTERAR_PASS_CONFIRM_CODE_PASSWORD
        dialogLayoutCodeResetPass = new Dialog(this);
        dialogLayoutCodeResetPass.setContentView(R.layout.layout_alterarpass_confirmcod_password);
        dialogLayoutCodeResetPass.setCancelable(false);
        if (dialogLayoutCodeResetPass.getWindow()!=null)
            dialogLayoutCodeResetPass.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//        linearBtnFecharTelef = dialogLayoutCodeResetPass.findViewById(R.id.linearBtnFecharTelef);
        imgBtnFecharTelef = dialogLayoutCodeResetPass.findViewById(R.id.imgBtnFecharTelef);
        txtTenteOutraVez = dialogLayoutCodeResetPass.findViewById(R.id.txtTenteOutraVez);
        txt_telefone = dialogLayoutCodeResetPass.findViewById(R.id.txt_telefone);
        dialog_pinCodigoConfirmacao = dialogLayoutCodeResetPass.findViewById(R.id.dialog_pinCodigoConfirmacao);
        dialog_editNovaPass = dialogLayoutCodeResetPass.findViewById(R.id.dialog_editNovaPass);
        dialog_btn_continuar = dialogLayoutCodeResetPass.findViewById(R.id.dialog_btn_continuar);

//        linearBtnFecharTelef.setOnClickListener(this);
        imgBtnFecharTelef.setOnClickListener(this);
        txtTenteOutraVez.setOnClickListener(this);
        dialog_btn_continuar.setOnClickListener(this);

        //-------------------------------------------------------------//
        //-------------------------------------------------------------//
        //DIALOG_LAYOUT_SUCESSO
        dialogLayoutSuccess = new Dialog(this);
        dialogLayoutSuccess.setContentView(R.layout.layout_sucesso);
        dialogLayoutSuccess.setCancelable(false);
        if (dialogLayoutSuccess.getWindow()!=null)
            dialogLayoutSuccess.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog_txtConfirmSucesso = dialogLayoutSuccess.findViewById(R.id.dialog_txtConfirmSucesso);
        dialog_btn_sucesso = dialogLayoutSuccess.findViewById(R.id.dialog_btn_sucesso);

        dialog_btn_sucesso.setOnClickListener(this);
        //-------------------------------------------------------------//
        //-------------------------------------------------------------//

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //DIALOG_LAYOUT_ALTERAR_PASS_NUM_TELEFONE
            case R.id.dialog_btn_cancelar:
                MetodosUsados.esconderTeclado(this);
                cancelarEnvioTelefone();
                break;

            case R.id.dialog_btn_enviar:
                MetodosUsados.esconderTeclado(this);
                enviarTelefonelRedif();
                break;
            //-------------------------------------------------------------//
            //DIALOG_LAYOUT_ALTERAR_PASS_CONFIRM_CODE_PASSWORD
//            case R.id.linearBtnFecharTelef:
            case R.id.imgBtnFecharTelef:
                MetodosUsados.esconderTeclado(this);
                dialogLayoutEnviarNumTelefone.show();
                limparPinView(dialog_pinCodigoConfirmacao,dialog_editNovaPass);
                dialogLayoutCodeResetPass.cancel();
                break;

            case R.id.txtTenteOutraVez:
                if (!TextUtils.isEmpty(telefoneReceberDeNovo)) {
                    verificarConecxaoInternetReenviarNumber();
                } else {
                    MetodosUsados.esconderTeclado(this);
                    MetodosUsados.mostrarMensagem(this, R.string.txtTentarmaistarde);
                }
                break;

            case R.id.dialog_btn_continuar:
                if (verificarCampoTelef()){
                    MetodosUsados.esconderTeclado(this);
                    verificarConecxaoInternetReporSenha();
                }
                break;

            //-------------------------------------------------------------//
            //DIALOG_LAYOUT_SUCESSO
            case R.id.dialog_btn_sucesso:
                MetodosUsados.esconderTeclado(this);
                dialogLayoutSuccess.cancel();
                finish();
                break;
        }
    }

    private void cancelarEnvioTelefone() {
        dialog_editTelefone.setText(null);
        dialog_editTelefone.setError(null);
        finish();
    }

    private void enviarTelefonelRedif() {
        if (verificarTelefone()) {
            verificarConecxaoInternetSenDNumber();
        }
    }

    private boolean verificarTelefone() {

        if (dialog_editTelefone.getText() != null) {
            telefoneRedif_senha = dialog_editTelefone.getText().toString().trim();
            if (!telefoneRedif_senha.matches("9[1-9][0-9]\\d{6}")) {
                dialog_editTelefone.setError(getString(R.string.msg_erro_num_telefone_invalido));
                return false;
            }else {
                return true;
            }
        }else {
            dialog_editTelefone.setError(getString(R.string.msg_erro_num_telefone));
            return false;
        }
    }

    private void mandarTelefoneResetSenha(String telefone) {

        MetodosUsados.showLoadingDialog(getString(R.string.enviando_num_telefone));
        dialog_editTelefone.setError(null);
        telefoneReceberDeNovo = telefone;

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Void> enviarTelefoneReset = apiInterface.enviarTelefone(Integer.parseInt(telefone));
        enviarTelefoneReset.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {

                    MetodosUsados.hideLoadingDialog();

                    dialog_editTelefone.setText(null);
                    txt_telefone.setText(getString(R.string.msg_support_telefone).concat(" "+telefone));

                    dialogLayoutCodeResetPass.show();
                    dialogLayoutEnviarNumTelefone.cancel();

                } else {
                    MetodosUsados.hideLoadingDialog();
                    ErrorResponce errorResponce = ErrorUtils.parseError(response);
                    dialog_editTelefone.setError(errorResponce.getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                MetodosUsados.hideLoadingDialog();
                if (!MetodosUsados.conexaoInternetTrafego(AlterarPalavraPasseActivity.this,TAG)){
                    MetodosUsados.mostrarMensagem(AlterarPalavraPasseActivity.this,R.string.msg_erro_internet);
                }else  if ("timeout".equals(t.getMessage())) {
                    MetodosUsados.mostrarMensagem(AlterarPalavraPasseActivity.this,R.string.msg_erro_internet_timeout);
                }else {
                    MetodosUsados.mostrarMensagem(AlterarPalavraPasseActivity.this,R.string.msg_erro);
                }
                Log.i(TAG,"onFailure" + t.getMessage());
            }
        });
    }

    private void enviarTelefoneRedifDeNovo() {

        MetodosUsados.showLoadingDialog(getString(R.string.reenviando_num_telefone));

        dialog_editTelefone.setError(null);
        String telefone = telefoneReceberDeNovo.trim();


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Void> enviarTelefoneReset = apiInterface.enviarTelefone(Integer.parseInt(telefone));
        enviarTelefoneReset.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    MetodosUsados.hideLoadingDialog();
                    MetodosUsados.mostrarMensagem(AlterarPalavraPasseActivity.this, R.string.msg_corfim_code_reenviado);
                } else {
                    MetodosUsados.hideLoadingDialog();
                    ErrorResponce errorResponce = ErrorUtils.parseError(response);
                    MetodosUsados.mostrarMensagem(AlterarPalavraPasseActivity.this, errorResponce.getError());

                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                MetodosUsados.hideLoadingDialog();
                if (!MetodosUsados.conexaoInternetTrafego(AlterarPalavraPasseActivity.this,TAG)) {
                    MetodosUsados.mostrarMensagem(AlterarPalavraPasseActivity.this, R.string.msg_erro_internet);
                } else if ("timeout".equals(t.getMessage())) {
                    MetodosUsados.mostrarMensagem(AlterarPalavraPasseActivity.this, R.string.msg_erro_internet_timeout);
                } else {
                    MetodosUsados.mostrarMensagem(AlterarPalavraPasseActivity.this, R.string.msg_erro);
                }
                Log.i(TAG, "onFailure" + t.getMessage());
            }
        });

    }

    private boolean verificarCampoTelef() {
        codigoConfTelef = dialog_pinCodigoConfirmacao.getText().toString();
        novaSenha = dialog_editNovaPass.getText().toString();

        if (codigoConfTelef.isEmpty()) {
            MetodosUsados.mostrarMensagem(this, R.string.msg_confirm_code);
            dialog_pinCodigoConfirmacao.requestFocus();
            dialog_pinCodigoConfirmacao.setError(getString(R.string.msg_erro_campo_vazio));
            return false;
        }
        if (codigoConfTelef.length() <=2) {
            dialog_pinCodigoConfirmacao.requestFocus();
            dialog_pinCodigoConfirmacao.setError(getString(R.string.msg_confirm_code_min_three));
            MetodosUsados.mostrarMensagem(this, R.string.msg_confirm_code_short);
            return false;
        }

        if (novaSenha.isEmpty()) {
            dialog_editNovaPass.requestFocus();
            dialog_editNovaPass.setError(getString(R.string.msg_erro_campo_vazio));
            return false;
        }

        if (novaSenha.length()<=5){
            dialog_editNovaPass.requestFocus();
            dialog_editNovaPass.setError(getString(R.string.msg_erro_password_fraca));
            return false;
        }


        return true;

    }

    private void enviarCodRedifinicaoTelef() {

        MetodosUsados.showLoadingDialog(getString(R.string.enviando_confirm_code));

        String telefone = telefoneReceberDeNovo.trim();
        ReporSenha reporSenha = new ReporSenha();
        reporSenha.codigoRecuperacao = codigoConfTelef;
        reporSenha.novaPassword = novaSenha;


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<String> enviarCod = apiInterface.enviarConfirCodigo(telefone,reporSenha);
        enviarCod.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {


                    String message = response.body();

                    limparPinView(dialog_pinCodigoConfirmacao,dialog_editNovaPass);
                    MetodosUsados.hideLoadingDialog();

                    dialog_txtConfirmSucesso.setText(getString(R.string.msg_sucesso_senha_alterada));

                    dialogLayoutSuccess.show();
                    dialogLayoutCodeResetPass.cancel();


                } else {
                    MetodosUsados.hideLoadingDialog();
                    ErrorResponce errorResponce = ErrorUtils.parseError(response);
                    MetodosUsados.mostrarMensagem(AlterarPalavraPasseActivity.this, errorResponce.getError());
                    dialog_pinCodigoConfirmacao.setError(getString(R.string.msg_confirm_code_incorrecto));

                }
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                MetodosUsados.hideLoadingDialog();
                if (!MetodosUsados.conexaoInternetTrafego(AlterarPalavraPasseActivity.this,TAG)) {
                    MetodosUsados.mostrarMensagem(AlterarPalavraPasseActivity.this, R.string.msg_erro_internet);
                } else if ("timeout".equals(t.getMessage())) {
                    MetodosUsados.mostrarMensagem(AlterarPalavraPasseActivity.this, R.string.msg_erro_internet_timeout);
                } else {
                    MetodosUsados.mostrarMensagem(AlterarPalavraPasseActivity.this, R.string.msg_erro);
                }
                Log.i(TAG, "onFailure" + t.getMessage());
            }
        });



    }

    private void limparPinView(ShowHidePasswordEditText pinCodigoConfirmacaoTelef, ShowHidePasswordEditText editNovaSenha) {
        pinCodigoConfirmacaoTelef.setText(null);
        editNovaSenha.setText(null);
    }


    private void verificarConecxaoInternetSenDNumber() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null){
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null) {
                MetodosUsados.mostrarMensagem(this,R.string.msg_erro_internet);
            } else {
                mandarTelefoneResetSenha(telefoneRedif_senha);
            }
        }
    }

    private void verificarConecxaoInternetReenviarNumber() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null){
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null) {
                MetodosUsados.mostrarMensagem(this,R.string.msg_erro_internet);
            } else {
                enviarTelefoneRedifDeNovo();
            }
        }
    }


    private void verificarConecxaoInternetReporSenha() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null){
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null) {
                MetodosUsados.mostrarMensagem(this,R.string.msg_erro_internet);
            } else {
                enviarCodRedifinicaoTelef();
            }
        }
    }

    @Override
    protected void onResume() {
        MetodosUsados.spotsDialog(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        dialogLayoutEnviarNumTelefone.cancel();
        dialogLayoutCodeResetPass.cancel();
        dialogLayoutSuccess.cancel();
        MetodosUsados.hideLoadingDialog();
        super.onDestroy();
    }
}