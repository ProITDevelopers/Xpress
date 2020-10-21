package ao.co.proitconsulting.xpress.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import java.io.IOException;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.helper.NotificationHelper;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.LoginRequest;
import ao.co.proitconsulting.xpress.modelos.UsuarioAuth;
import ao.co.proitconsulting.xpress.modelos.UsuarioPerfil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "TAG_LoginActivity";
    private RelativeLayout login_root;
    private ImageView imgAppLogo;
    private AppCompatEditText editEmail;
    private ShowHidePasswordEditText editPassword;
    private TextView txtForgotPassword,txtRegister;
    private Button btnLogin;
    private String emailTelefone,password;
    private LoginRequest loginRequest = new LoginRequest();
    private NotificationHelper notificationHelper;
    private UsuarioPerfil usuarioPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //showBackground in status bar
        MetodosUsados.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.white));
        setContentView(R.layout.activity_login);

        notificationHelper = new NotificationHelper(this);

        //InitViews
        initViews();
    }



    private void initViews(){

        login_root = findViewById(R.id.login_root);
        imgAppLogo = findViewById(R.id.imgAppLogo);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        txtRegister = findViewById(R.id.txtRegister);
        btnLogin = findViewById(R.id.btnLogin);

        SpannableString spannableString = new SpannableString(getString(R.string.hint_registe_se));
        ForegroundColorSpan fcsGreen = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.login_register_text_color));
        spannableString.setSpan(fcsGreen,19,29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtRegister.setText(spannableString);



        imgAppLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MetodosUsados.mostrarMensagem(LoginActivity.this,getString(R.string.xpress_co_ao));
            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, AlterarPalavraPasseActivity.class);
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

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }

    private boolean verificarCamposEmailTelefone() {

        emailTelefone = editEmail.getText().toString().trim();
        password = editPassword.getText().toString().trim();

        if (emailTelefone.isEmpty()){
            editEmail.setError(getString(R.string.msg_erro_campo_vazio));
            return false;
        }

        if (MetodosUsados.validarEmail(emailTelefone)) {
            emailTelefone = emailTelefone.toLowerCase();
            loginRequest.email = emailTelefone;

        }else {
            if (emailTelefone.matches("9[1-9][0-9]\\d{6}")){
                loginRequest.telefone = emailTelefone;
                return true;
            } else {
                editEmail.requestFocus();
                editEmail.setError(getString(R.string.msg_erro_campo_com_email_telefone));
                return false;
            }
        }

        if (password.isEmpty()) {
            editPassword.requestFocus();
            editPassword.setError(getString(R.string.msg_erro_campo_vazio));
            return false;
        }



        editEmail.onEditorAction(EditorInfo.IME_ACTION_DONE);
        editPassword.onEditorAction(EditorInfo.IME_ACTION_DONE);



        return true;
    }

    private void verificarConecxaoInternet() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr!=null){
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null) {
                MetodosUsados.mostrarMensagem(LoginActivity.this,R.string.msg_erro_internet);
            } else {
                loginRequest.password = password;
                loginRequest.rememberMe = true;
                autenticacaoLogin(loginRequest);
            }
        }
    }


    private void autenticacaoLogin(LoginRequest loginRequest) {

        MetodosUsados.showLoadingDialog(getString(R.string.msg_login_auth_verification));


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
                        MetodosUsados.mostrarMensagem(LoginActivity.this,getString(R.string.msg_email_palavra_passe_errada));
                    }

                    if (message.equals("\"Usuario Não Existe\"")){
                        Snackbar.make(login_root, getString(R.string.msg_erro_user_not_found), Snackbar.LENGTH_LONG)
                                .setActionTextColor(ContextCompat.getColor(LoginActivity.this, R.color.login_register_text_color))
                                .setAction(getString(R.string.criar_conta), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
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
                if (!MetodosUsados.conexaoInternetTrafego(LoginActivity.this,TAG)){
                    MetodosUsados.mostrarMensagem(LoginActivity.this,R.string.msg_erro_internet);
                }else  if ("timeout".equals(t.getMessage())) {
                    MetodosUsados.mostrarMensagem(LoginActivity.this,R.string.msg_erro_internet_timeout);
                }else {
                    MetodosUsados.mostrarMensagem(LoginActivity.this,R.string.msg_erro);
                }
                Log.i(TAG,"onFailure" + t.getMessage());

                try {
                    Snackbar
                            .make(login_root, t.getMessage(), 4000)
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


                    MetodosUsados.hideLoadingDialog();
                    if (response.body()!=null){
                        usuarioPerfil = response.body().get(0);
                        AppPrefsSettings.getInstance().saveUser(usuarioPerfil);


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
                if (!MetodosUsados.conexaoInternetTrafego(LoginActivity.this,TAG)){
                    MetodosUsados.mostrarMensagem(LoginActivity.this,R.string.msg_erro_internet);
                }else  if ("timeout".equals(t.getMessage())) {
                    MetodosUsados.mostrarMensagem(LoginActivity.this,R.string.msg_erro_internet_timeout);
                }else {
                    MetodosUsados.mostrarMensagem(LoginActivity.this,R.string.msg_erro);
                }

            }
        });
    }

    private void launchHomeScreen() {
        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        String title = "Olá, ".concat(usuarioPerfil.primeiroNome);
        String message = "Seja bem-vindo(a) ao Xpress!";
        notificationHelper.createNotification(title,message,false);

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
        super.onDestroy();
    }
}