package ao.co.proitconsulting.xpress.activities;

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
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.adapters.menuBanner.MenuBannerAdapter;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.helper.NotificationHelper;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.LoginRequest;
import ao.co.proitconsulting.xpress.modelos.TopSlideImages;
import ao.co.proitconsulting.xpress.modelos.UsuarioAuth;
import ao.co.proitconsulting.xpress.modelos.UsuarioPerfil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.util.Base64;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "TAG_LoginActivity";
    private LinearLayout login_root;
    private FloatingActionButton fabPrevious;
    private AppCompatEditText editEmail;
    private ShowHidePasswordEditText editPassword;
    private TextView txtRemember;
    private SwitchCompat switchRemember;
    private TextView txtForgotPassword,txtRegister;
    private Button btnLogin;
    private String emailTelefone,password;
    private LoginRequest loginRequest = new LoginRequest();
    private NotificationHelper notificationHelper;
    private UsuarioPerfil usuarioPerfil;
    //DIALOG_LAYOUT_COVID_19
    private Dialog dialogLayoutCOVID;
    private LoopingViewPager loopingViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //showBackground in status bar
        MetodosUsados.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.white));
        setContentView(R.layout.activity_login);
//        printKeyHash();

        notificationHelper = new NotificationHelper(this);

        //InitViews
        initViews();
    }

    private void printKeyHash() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo("ao.co.proitconsulting.xpress", PackageManager.GET_SIGNATURES);
            for (Signature signature:info.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.e("KEYHASH", Base64.encodeToString(messageDigest.digest(),Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }



    private void initViews(){

        loginRequest.rememberMe = false;

        login_root = findViewById(R.id.login_root);
        fabPrevious = findViewById(R.id.fabPrevious);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        txtRegister = findViewById(R.id.txtRegister);
        btnLogin = findViewById(R.id.btnLogin);

        txtRemember = findViewById(R.id.txtRemember);
        switchRemember = findViewById(R.id.switchRemember);

        SpannableString spannableString = new SpannableString(getString(R.string.splash_sign_up_hint));
        ForegroundColorSpan fcsGreen = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.btn_login_start_color));
//        spannableString.setSpan(fcsGreen,19,29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(fcsGreen,22,29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new UnderlineSpan(),22,29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtRegister.setText(spannableString);


        fabPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
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

        txtRemember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!switchRemember.isChecked()){
                    switchRemember.setChecked(true);
                    loginRequest.rememberMe = true;
                }else{
                    switchRemember.setChecked(false);
                    loginRequest.rememberMe = false;
                }
            }
        });



         //-------------------------------------------------------------//
        //-------------------------------------------------------------//
        //DIALOG_LAYOUT_COVID_19
        dialogLayoutCOVID = new Dialog(this);
        dialogLayoutCOVID.setContentView(R.layout.layout_covid);
        dialogLayoutCOVID.setCancelable(false);
        if (dialogLayoutCOVID.getWindow()!=null)
            dialogLayoutCOVID.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        loopingViewPager = dialogLayoutCOVID.findViewById(R.id.loopingViewPager);
        List<TopSlideImages> topSlideImages = new ArrayList<>();
        topSlideImages.add(new TopSlideImages(R.drawable.img_lavar_maos2));
        topSlideImages.add(new TopSlideImages(R.drawable.img_lavar_maos));
        MenuBannerAdapter menuBannerAdapter = new MenuBannerAdapter(this,topSlideImages,true);
        loopingViewPager.setAdapter(menuBannerAdapter);


        ImageView imgBtnFecharTelef = dialogLayoutCOVID.findViewById(R.id.imgBtnFecharTelef);
        imgBtnFecharTelef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogLayoutCOVID.dismiss();
                dialogLayoutCOVID.cancel();
            }
        });

        dialogLayoutCOVID.show();

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

                    if (loginRequest.rememberMe){
                        AppPrefsSettings.getInstance().setLoggedIn(true);
                    }




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

        String title = "Olá, ".concat(usuarioPerfil.primeiroNome+" "+usuarioPerfil.ultimoNome);
        String message = "Seja bem-vindo(a) ao Xpress Lengueno!";
        notificationHelper.createNotification(title,message,false);

        finish();
    }

    @Override
    protected void onResume() {
        loopingViewPager.resumeAutoScroll();
        MetodosUsados.spotsDialog(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        MetodosUsados.hideLoadingDialog();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        loopingViewPager.pauseAutoScroll();
        super.onPause();

    }



}