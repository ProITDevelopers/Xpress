package ao.co.proitconsulting.xpress.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlterarPalavraPasseActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AlterarSenhaActivity";

    private ImageView imgBackground;

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
    private ImageView dialog_img_status;
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
        imgBackground = findViewById(R.id.imgBackground);

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
        dialog_img_status = dialogLayoutSuccess.findViewById(R.id.dialog_img_status);
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
                    enviarTelefoneRedifDeNovo();
                } else {
                    MetodosUsados.esconderTeclado(this);
                    MetodosUsados.mostrarMensagem(this, R.string.txtTentarmaistarde);
                }
                break;

            case R.id.dialog_btn_continuar:
                if (verificarCampoTelef()){
                    MetodosUsados.esconderTeclado(this);
                    enviarCodRedifinicaoTelef();
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
            mandarTelefoneResetSenha(telefoneRedif_senha);
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

        dialog_editTelefone.setError(null);
        telefoneReceberDeNovo = telefone;

        dialog_editTelefone.setText(null);

        txt_telefone.setText(getString(R.string.msg_support_telefone).concat(" "+telefone));

        dialogLayoutCodeResetPass.show();
        dialogLayoutEnviarNumTelefone.cancel();


//        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        Call<Void> enviarTelefoneReset = apiInterface.enviarTelefone(Integer.parseInt(telefone));
//        progressDialog.setMessage(msgAEnviarTelefone);
//        progressDialog.show();
//        enviarTelefoneReset.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
//                if (response.isSuccessful()) {
//
//                    progressDialog.cancel();
//                    dialog_editTelefone.setText(null);
//
//                    txt_telefone.setText(msgSupporte.concat(telefone));
//                    dialogLayoutCodeResetPass.show();
//                } else {
//                    ErrorResponce errorResponce = ErrorUtils.parseError(response);
//                    progressDialog.dismiss();
//                    dialog_editTelefone_telefone.setError(errorResponce.getError());
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
//                progressDialog.dismiss();
//                if (!conexaoInternetTrafego(AlterarSenhaActivity.this,TAG)){
//                    mostrarMensagem(AlterarSenhaActivity.this,R.string.txtMsg);
//                }else  if ("timeout".equals(t.getMessage())) {
//                    mostrarMensagem(AlterarSenhaActivity.this,R.string.txtTimeout);
//                }else {
//                    mostrarMensagem(AlterarSenhaActivity.this,R.string.txtProblemaMsg);
//                }
//                Log.i(TAG,"onFailure" + t.getMessage());
//            }
//        });
    }

    private void enviarTelefoneRedifDeNovo() {

        dialog_editTelefone.setError(null);
        String telefone = telefoneReceberDeNovo.trim();
        MetodosUsados.mostrarMensagem(this, R.string.msg_corfim_code_reenviado);

//        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        Call<Void> enviarYelefoneReset = apiInterface.enviarTelefone(Integer.parseInt(telefone));
//        progressDialog.setMessage(msgReenviarNumTelef);
//        progressDialog.show();
//        enviarYelefoneReset.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
//                if (response.isSuccessful()) {
//                    progressDialog.dismiss();
//                    mostrarMensagem(AlterarSenhaActivity.this, R.string.txtCodigoenviado);
//                } else {
//                    ErrorResponce errorResponce = ErrorUtils.parseError(response);
//                    Toast.makeText(AlterarSenhaActivity.this, errorResponce.getError(), Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
//                progressDialog.dismiss();
//                if (!conexaoInternetTrafego(AlterarSenhaActivity.this,TAG)) {
//                    mostrarMensagem(AlterarSenhaActivity.this, R.string.txtMsg);
//                } else if ("timeout".equals(t.getMessage())) {
//                    mostrarMensagem(AlterarSenhaActivity.this, R.string.txtTimeout);
//                } else {
//                    mostrarMensagem(AlterarSenhaActivity.this, R.string.txtProblemaMsg);
//                }
//                Log.i(TAG, "onFailure" + t.getMessage());
//            }
//        });

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
        if (codigoConfTelef.length() <2) {
            MetodosUsados.mostrarMensagem(this, R.string.msg_confirm_code_short);
            return false;
        }

        if (novaSenha.isEmpty()) {
            dialog_editNovaPass.requestFocus();
            dialog_editNovaPass.setError(getString(R.string.msg_erro_campo_vazio));
            return false;
        }


        return true;

    }

    private void enviarCodRedifinicaoTelef() {
//        imgBackground.setImageResource(R.color.login_register_text_color);

        String telefone = telefoneReceberDeNovo.trim();
        limparPinView(dialog_pinCodigoConfirmacao,dialog_editNovaPass);

        dialog_txtConfirmSucesso.setText(getString(R.string.msg_sucesso_senha_alterada));

        dialogLayoutSuccess.show();
        dialogLayoutCodeResetPass.cancel();


//        progressDialog.setMessage(msgEnviandoCodigo);
//        progressDialog.show();
//
//        String telefone = telefoneReceberDeNovo.trim();
//
//        ReporSenha reporSenha = new ReporSenha();
//        reporSenha.codigoRecuperacao = codigoConfTelef;
//        reporSenha.novaPassword = novaSenha;
//
////        ApiInterface apiInterface = ApiClient.apiClient().create(ApiInterface.class);
//        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
////        Call<String> enviarCod = apiInterface.enviarConfirCodigo(Integer.parseInt(telefone), codigoConfTelef,novaSenha);
//        Call<String> enviarCod = apiInterface.enviarConfirCodigo(telefone,reporSenha);
//        enviarCod.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                if (!response.isSuccessful()) {
//                    ErrorResponce errorResponce = ErrorUtils.parseError(response);
//                    mostrarMensagem(AlterarSenhaActivity.this, errorResponce.getError());
//                    progressDialog.cancel();
//                } else {
//
//                    String message = response.body();
//
//                    limparPinView(pinCodigoConfirmacaoTelef,editNovaSenha);
//                    dialogSenhaEnviarTelefoneCodReset.cancel();
//                    progressDialog.dismiss();
//                    txtConfirmSucesso.setText(getString(R.string.msg_sucesso_senha_alterada));
//                    dialogConfirmTelefoneSuccesso.show();
////                    mostrarDialogoOK(message);
//                }
//            }
//            @Override
//            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                progressDialog.dismiss();
//                if (!conexaoInternetTrafego(AlterarSenhaActivity.this,TAG)) {
//                    mostrarMensagem(AlterarSenhaActivity.this, R.string.txtMsg);
//                } else if ("timeout".equals(t.getMessage())) {
//                    mostrarMensagem(AlterarSenhaActivity.this, R.string.txtTimeout);
//                } else {
//                    mostrarMensagem(AlterarSenhaActivity.this, R.string.txtProblemaMsg);
//                }
//                Log.i(TAG, "onFailure" + t.getMessage());
//            }
//        });



    }

    private void limparPinView(ShowHidePasswordEditText pinCodigoConfirmacaoTelef, ShowHidePasswordEditText editNovaSenha) {
        pinCodigoConfirmacaoTelef.setText(null);
        editNovaSenha.setText(null);
    }

    @Override
    protected void onResume() {

//        if (dialogLayoutSuccess.isShowing()){
//            imgBackground.setImageResource(R.color.login_register_text_color);
//        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        dialogLayoutEnviarNumTelefone.cancel();
        dialogLayoutCodeResetPass.cancel();
        dialogLayoutSuccess.cancel();
        super.onDestroy();
    }
}