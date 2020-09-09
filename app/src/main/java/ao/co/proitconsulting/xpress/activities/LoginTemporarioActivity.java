package ao.co.proitconsulting.xpress.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import ao.co.proitconsulting.xpress.MainActivity;
import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.LoginRequest;
import ao.co.proitconsulting.xpress.modelos.UsuarioAuth;
import ao.co.proitconsulting.xpress.modelos.UsuarioPerfil;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginTemporarioActivity extends AppCompatActivity {

    private static final String TAG = "LoginTemporarioActivity";
    private RelativeLayout logintemp_root;
    private CircleImageView imgUserPhoto;
    private TextView txtNomeUser,txtUserNameInitial;
    private ShowHidePasswordEditText editPassword;
    private TextView txtForgotPassword,txtOutraConta;
    private Button btnLogin;

    private UsuarioPerfil usuarioPerfil;
    private String emailTelefone,password;


    //DIALOG_LAYOUT_CONFIRMAR_PROCESSO
    private Dialog dialogLayoutConfirmarProcesso;
    private ImageView imgConfirm;
    private TextView txtConfirmTitle,txtConfirmMsg;
    private Button dialog_btn_deny_processo,dialog_btn_accept_processo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showCustomUI();
        setContentView(R.layout.activity_login_temporario);

        //InitViews
        initViews();
    }

    private void showCustomUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void initViews(){

        usuarioPerfil = AppPrefsSettings.getInstance().getUser();

        logintemp_root = findViewById(R.id.logintemp_root);
        imgUserPhoto = findViewById(R.id.imgUserPhoto);
        txtUserNameInitial = findViewById(R.id.txtUserNameInitial);
        txtNomeUser = findViewById(R.id.txtNomeUser);
        editPassword = findViewById(R.id.editPassword);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        txtOutraConta = findViewById(R.id.txtOutraConta);
        btnLogin = findViewById(R.id.btnLogin);

        if (usuarioPerfil!=null){
            emailTelefone = usuarioPerfil.email;
        }

        carregarDadosOffline(usuarioPerfil);


        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginTemporarioActivity.this, AlterarPalavraPasseActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificarCamposEmailTelefone()) {
                    verificarConecxaoInternet();
                }

            }
        });

        txtOutraConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mensagemLogOut();
            }
        });

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

    }

    private void carregarDadosOffline(UsuarioPerfil usuarioPerfil) {
        if (usuarioPerfil!=null){
            txtNomeUser.setText(usuarioPerfil.primeiroNome.concat(" "+usuarioPerfil.ultimoNome));

            if (usuarioPerfil.imagem.equals(getString(R.string.sem_imagem))){
                if (!usuarioPerfil.primeiroNome.isEmpty()){
                    String userNameInitial = String.valueOf(usuarioPerfil.primeiroNome.charAt(0));
                    txtUserNameInitial.setText(userNameInitial.toUpperCase());
                    txtUserNameInitial.setVisibility(View.VISIBLE);

                }else {
                    String userNameInitial = String.valueOf(usuarioPerfil.email.charAt(0));
                    txtUserNameInitial.setText(userNameInitial.toUpperCase());
                }

            }else {
                txtUserNameInitial.setVisibility(View.GONE);
                Picasso.with(this)
                        .load(usuarioPerfil.imagem)
//                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .fit().centerCrop()
                        .placeholder(R.drawable.photo_placeholder)
                        .into(imgUserPhoto);
            }
        }
    }

    private boolean verificarCamposEmailTelefone() {

        password = editPassword.getText().toString().trim();

        if (password.isEmpty()) {
            editPassword.requestFocus();
            editPassword.setError(getString(R.string.msg_erro_campo_vazio));
            return false;
        }

        editPassword.onEditorAction(EditorInfo.IME_ACTION_DONE);

        return true;
    }

    private void verificarConecxaoInternet() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr!=null){
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null) {
                MetodosUsados.mostrarMensagem(LoginTemporarioActivity.this,R.string.msg_erro_internet);
            } else {

                autenticacaoLogin();
            }
        }
    }

    private void autenticacaoLogin() {

        MetodosUsados.showLoadingDialog(getString(R.string.msg_login_auth_verification));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.email = emailTelefone;
        loginRequest.password = password;
        loginRequest.rememberMe = true;

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<UsuarioAuth> call = apiInterface.autenticarCliente(loginRequest);
        call.enqueue(new Callback<UsuarioAuth>() {
            @Override
            public void onResponse(@NonNull Call<UsuarioAuth> call, @NonNull Response<UsuarioAuth> response) {


                MetodosUsados.changeMessageDialog(getString(R.string.msg_login_auth_validando));
                if (response.isSuccessful() && response.body() != null) {
                    UsuarioAuth userToken = response.body();


                    AppPrefsSettings.getInstance().saveAuthToken(userToken.tokenuser);
                    AppPrefsSettings.getInstance().saveTokenTime(userToken.expiracao);


                    carregarMeuPerfil(userToken.tokenuser);


                } else {

                    MetodosUsados.hideLoadingDialog();
                    String message ="";

                        try {
                            message = response.errorBody().string();
                            Log.v("Error code 400",message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (message.equals("\"Password ou Email Errados\"")){
                            MetodosUsados.mostrarMensagem(LoginTemporarioActivity.this,getString(R.string.msg_palavra_passe_errada));
                        }

                        if (message.equals("\"Usuario Não Existe\"")){
                            Snackbar.make(logintemp_root, getString(R.string.msg_erro_user_not_found), Snackbar.LENGTH_LONG)
                                    .setActionTextColor(ContextCompat.getColor(LoginTemporarioActivity.this, R.color.login_register_text_color))
                                    .setAction(getString(R.string.criar_conta), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(LoginTemporarioActivity.this, RegisterActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                    }).show();
                        }




                }
            }

            @Override
            public void onFailure(@NonNull Call<UsuarioAuth> call, @NonNull Throwable t) {
                MetodosUsados.hideLoadingDialog();
                if (!MetodosUsados.conexaoInternetTrafego(LoginTemporarioActivity.this,TAG)){
                    MetodosUsados.mostrarMensagem(LoginTemporarioActivity.this,R.string.msg_erro_internet);
                }else  if ("timeout".equals(t.getMessage())) {
                    MetodosUsados.mostrarMensagem(LoginTemporarioActivity.this,R.string.msg_erro_internet_timeout);
                }else {
                    MetodosUsados.mostrarMensagem(LoginTemporarioActivity.this,R.string.msg_erro);
                }
                Log.i(TAG,"onFailure" + t.getMessage());

                try {
                    Snackbar
                            .make(logintemp_root, t.getMessage(), 4000)
                            .setActionTextColor(Color.MAGENTA)
                            .show();
                } catch (Exception e) {
                    Log.d(TAG, String.valueOf(e.getMessage()));
                }
            }
        });

    }

    private void carregarMeuPerfil(String token) {
        MetodosUsados.changeMessageDialog(getString(R.string.msg_login_auth_carregando_dados));
        String bearerToken = Common.bearerApi.concat(token);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<UsuarioPerfil>>  usuarioCall = apiInterface.getPerfilLogin(bearerToken);

        usuarioCall.enqueue(new Callback<List<UsuarioPerfil>>() {
            @Override
            public void onResponse(@NonNull Call<List<UsuarioPerfil>> call, @NonNull Response<List<UsuarioPerfil>> response) {

                if (response.isSuccessful()) {


                    if (response.body()!=null){
                        UsuarioPerfil usuarioPerfil = response.body().get(0);


                        AppPrefsSettings.getInstance().saveUser(usuarioPerfil);
                        MetodosUsados.hideLoadingDialog();
                        launchHomeScreen();
                    }

                } else {
                    MetodosUsados.hideLoadingDialog();
                    AppPrefsSettings.getInstance().clearAppPrefs();
                }




            }

            @Override
            public void onFailure(@NonNull Call<List<UsuarioPerfil>> call, @NonNull Throwable t) {
                AppPrefsSettings.getInstance().clearAppPrefs();
                MetodosUsados.hideLoadingDialog();
                if (!MetodosUsados.conexaoInternetTrafego(LoginTemporarioActivity.this,TAG)){
                    MetodosUsados.mostrarMensagem(LoginTemporarioActivity.this,R.string.msg_erro_internet);
                }else  if ("timeout".equals(t.getMessage())) {
                    MetodosUsados.mostrarMensagem(LoginTemporarioActivity.this,R.string.msg_erro_internet_timeout);
                }else {
                    MetodosUsados.mostrarMensagem(LoginTemporarioActivity.this,R.string.msg_erro);
                }

            }
        });
    }

    private void launchHomeScreen() {
        Intent intent = new Intent(LoginTemporarioActivity.this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void mensagemLogOut() {

        imgConfirm.setImageResource(R.drawable.xpress_logo);
        txtConfirmTitle.setText(R.string.entrar_com_outra_conta);
        txtConfirmMsg.setText(getString(R.string.msg_deseja_continuar));

        dialog_btn_accept_processo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLayoutConfirmarProcesso.cancel();
                logOut();
            }
        });

        dialog_btn_deny_processo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogLayoutConfirmarProcesso.cancel();

            }
        });

        dialogLayoutConfirmarProcesso.show();

    }

    private void logOut() {

        AppPrefsSettings.getInstance().clearAppPrefs();

        Intent intent = new Intent(LoginTemporarioActivity.this, SplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        MetodosUsados.spotsDialog(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        MetodosUsados.hideLoadingDialog();
        dialogLayoutConfirmarProcesso.cancel();
        super.onDestroy();
    }
}