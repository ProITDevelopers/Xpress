package ao.co.proitconsulting.xpress.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.FaceBookLoginRequest;
import ao.co.proitconsulting.xpress.modelos.LoginRequest;
import ao.co.proitconsulting.xpress.modelos.UsuarioAuth;
import ao.co.proitconsulting.xpress.modelos.UsuarioPerfil;
import ao.co.proitconsulting.xpress.mySignalR.MySignalRService;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "TAG_LoginActivity";
    private LinearLayout login_root;
    private FloatingActionButton fabPrevious;
    private AppCompatEditText editEmail;
//    private ShowHidePasswordEditText editPassword;
    private TextInputLayout text_input_password;
    private TextView txtRemember;
    private SwitchCompat switchRemember;
    private TextView txtForgotPassword,txtRegister;
    private Button btnLogin,btnLoginFacebook;
    private String emailTelefone,password;
    private LoginRequest loginRequest = new LoginRequest();
    private AlertDialog waitingDialog;

    private UsuarioPerfil usuarioPerfil;


    //DIALOG_LAYOUT_CONFIRMAR_PROCESSO
    private Dialog dialogLayoutConfirmarProcesso;
    private ImageView imgConfirm;
    private TextView txtConfirmTitle,txtConfirmMsg;
    private Button dialog_btn_deny_processo,dialog_btn_accept_processo;

    //LOGIN_FACEBOOK
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private AccessToken accessToken;
    private String facebookToken;
    private FaceBookLoginRequest faceBookLoginRequest = new FaceBookLoginRequest();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //showBackground in status bar
//        MetodosUsados.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.white));
        setContentView(R.layout.activity_login);
//        printKeyHash();


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

        waitingDialog = new SpotsDialog.Builder().setContext(this).setTheme(R.style.CustomSpotsDialog).build();

        loginRequest.rememberMe = false;

        login_root = findViewById(R.id.login_root);
        fabPrevious = findViewById(R.id.fabPrevious);
        editEmail = findViewById(R.id.editEmail);
//        editPassword = findViewById(R.id.editPassword);
        text_input_password = findViewById(R.id.text_input_password);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        txtRegister = findViewById(R.id.txtRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnLoginFacebook = findViewById(R.id.btnLoginFacebook);

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
        
        //---------------------------------
        //---------------------------------
        loginButton = findViewById(R.id.btnFBLogin);
        LoginManager.getInstance().logOut();
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList("email","public_profile"));

        btnLoginFacebook.setEnabled(true);
        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
                btnLoginFacebook.setEnabled(false);
            }
        });


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();

                btnLoginFacebook.setEnabled(false);

                ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                if (conMgr!=null){
                    NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                    if (netInfo == null) {
                        LoginManager.getInstance().logOut();
                        MetodosUsados.mostrarMensagem(LoginActivity.this,getString(R.string.msg_erro_internet));
                        btnLoginFacebook.setEnabled(true);
                    } else {
                        loaduserProfile(accessToken);
                    }
                }


            }

            @Override
            public void onCancel() {
                btnLoginFacebook.setEnabled(true);

            }

            @Override
            public void onError(FacebookException error) {
                btnLoginFacebook.setEnabled(true);
                Toast.makeText(LoginActivity.this, "FacebookException: "+error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private boolean verificarCamposEmailTelefone() {

        emailTelefone = editEmail.getText().toString().trim();
        password = text_input_password.getEditText().getText().toString().trim();

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
            text_input_password.requestFocus();
            text_input_password.setError(getString(R.string.msg_erro_campo_vazio));
            return false;
        }else{
            text_input_password.setError(null);

        }



        editEmail.onEditorAction(EditorInfo.IME_ACTION_DONE);
        text_input_password.getEditText().onEditorAction(EditorInfo.IME_ACTION_DONE);



        return true;
    }

    private void verificarConecxaoInternet() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr!=null){
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null) {
                MetodosUsados.mostrarMensagem(LoginActivity.this,R.string.msg_erro_internet);
            } else {

                if (switchRemember.isChecked()){
                    autenticacaoLogin();
                }else{
                    mensagemLembrarSessao();
                }


            }
        }
    }

    private void mensagemLembrarSessao() {



//        Glide.with(this)
//                .asGif()
//                .load(R.drawable.better_toogle)
//                .apply(new RequestOptions().override(150, 150))
//                .into(imgConfirm);

        txtConfirmTitle.setText("Lembrar sessão");
        txtConfirmMsg.setText(new StringBuilder("")
                .append("Não ativou a opcão ")
                .append("\"Lembrar-me\"").append(". ")
                .append("Os seus dados não serão guardados.").append("\n")
                .append(getString(R.string.msg_deseja_continuar)).toString()
        );

        dialog_btn_accept_processo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLayoutConfirmarProcesso.cancel();
                autenticacaoLogin();
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


    private void autenticacaoLogin() {

        loginRequest.password = password;

        waitingDialog.setMessage(getString(R.string.msg_login_auth_verification));
        waitingDialog.setCancelable(false);
        waitingDialog.show();



        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<UsuarioAuth> call = apiInterface.autenticarCliente(loginRequest);
        call.enqueue(new Callback<UsuarioAuth>() {
            @Override
            public void onResponse(@NonNull Call<UsuarioAuth> call, @NonNull Response<UsuarioAuth> response) {


                waitingDialog.setMessage(getString(R.string.msg_login_auth_validando));
                if (response.isSuccessful()) {
                    if (response.body() != null){
                        UsuarioAuth userToken = response.body();

                        AppPrefsSettings.getInstance().saveAuthToken(userToken.tokenuser);
                        AppPrefsSettings.getInstance().saveTokenTime(userToken.expiracao);

                        if (loginRequest.rememberMe){
                            AppPrefsSettings.getInstance().setLoggedIn(true);
                        }


                        carregarMeuPerfil(userToken.tokenuser);


                    }
                } else {

                    waitingDialog.dismiss();

                    String message ="";

                    try {
                        message = response.errorBody().string();
                        Log.d(TAG, "onLoginResponseError: "+message+", Error code: "+response.code());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (message.contains("Errados")){
                        MetodosUsados.mostrarMensagem(LoginActivity.this,getString(R.string.msg_email_palavra_passe_errada));
                    }

                    if (message.contains("Não Existe")){
                        Snackbar.make(login_root, getString(R.string.msg_erro_user_not_found), Snackbar.LENGTH_LONG)
                                .setActionTextColor(ContextCompat.getColor(LoginActivity.this, R.color.xpress_green))
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
                waitingDialog.dismiss();
                if (!MetodosUsados.conexaoInternetTrafego(LoginActivity.this,TAG)){
                    MetodosUsados.mostrarMensagem(LoginActivity.this,R.string.msg_erro_internet);
                }else  if (t.getMessage().contains("timeout")) {
                    MetodosUsados.mostrarMensagem(LoginActivity.this,R.string.msg_erro_internet_timeout);
                }else {
                    MetodosUsados.mostrarMensagem(LoginActivity.this,R.string.msg_erro);
                }
                Log.d(TAG,"onFailure" + t.getMessage());


            }
        });

    }

    private void carregarMeuPerfil(String token) {
        waitingDialog.setMessage(getString(R.string.msg_login_auth_carregando_dados));
        String bearerToken = Common.bearerApi.concat(token);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<UsuarioPerfil>>  usuarioCall = apiInterface.getPerfilLogin(bearerToken);

        usuarioCall.enqueue(new Callback<List<UsuarioPerfil>>() {
            @Override
            public void onResponse(@NonNull Call<List<UsuarioPerfil>> call, @NonNull Response<List<UsuarioPerfil>> response) {

                if (response.isSuccessful()) {


                    if (response.body()!=null){
                        usuarioPerfil = response.body().get(0);
                        AppPrefsSettings.getInstance().saveUser(usuarioPerfil);

                        waitingDialog.dismiss();
                        launchHomeScreen();
                    }

                } else {
                    waitingDialog.dismiss();
                    AppPrefsSettings.getInstance().clearAppPrefs();
                    LoginManager.getInstance().logOut();
                    btnLoginFacebook.setEnabled(true);

                }




            }

            @Override
            public void onFailure(@NonNull Call<List<UsuarioPerfil>> call, @NonNull Throwable t) {
                AppPrefsSettings.getInstance().clearAppPrefs();
                waitingDialog.dismiss();
                if (!MetodosUsados.conexaoInternetTrafego(LoginActivity.this,TAG)){
                    MetodosUsados.mostrarMensagem(LoginActivity.this,R.string.msg_erro_internet);
                }else  if (t.getMessage().contains("timeout")) {
                    MetodosUsados.mostrarMensagem(LoginActivity.this,R.string.msg_erro_internet_timeout);
                }else {
                    MetodosUsados.mostrarMensagem(LoginActivity.this,R.string.msg_erro);
                }
                Log.d(TAG, "onLoginPerfilFailed: "+t.getMessage());

            }
        });
    }

    private void launchHomeScreen() {




        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        Intent serviceIntent = new Intent(this, MySignalRService.class);
        startService(serviceIntent);


//        String title = "Olá, ".concat(usuarioPerfil.primeiroNome+" "+usuarioPerfil.ultimoNome);
//        String message = "Seja bem-vindo(a) ao Xpress Lengueno!";
//        notificationHelper.createNotification(title,message,false);


        finish();
    }


    private void loaduserProfile(AccessToken newAccessToken){


        facebookToken =newAccessToken.getToken();
        Log.d(TAG, "facebookToken: "+facebookToken);

        faceBookLoginRequest.accessToken = facebookToken;
        autenticacaoFaceBook();

//        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
//            @Override
//            public void onCompleted(JSONObject object, GraphResponse response) {
//
//                try {
//                    String first_name = object.getString("first_name");
//                    String last_name = object.getString("last_name");
//                    String email = object.getString("email");
//                    String id = object.getString("id");
//                    String name = first_name + " "+last_name;
//
//                    String image_url = "https://graph.facebook.com/"+id+ "/picture?type=large";
//
//
//                    Log.d(TAG, "facebookToken: "+facebookToken);
//
//
//
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//
//        Bundle parameters = new Bundle();
//        parameters.putString("fields","first_name,last_name,email,id");
//        request.setParameters(parameters);
//        request.executeAsync();

    }

    private void autenticacaoFaceBook() {
        waitingDialog.setMessage(getString(R.string.msg_login_auth_verification));
        waitingDialog.setCancelable(false);
        waitingDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<UsuarioAuth> call = apiInterface.autenticarFaceBook(faceBookLoginRequest);
        call.enqueue(new Callback<UsuarioAuth>() {
            @Override
            public void onResponse(@NonNull Call<UsuarioAuth> call, @NonNull Response<UsuarioAuth> response) {


                waitingDialog.setMessage(getString(R.string.msg_login_auth_validando));
                if (response.isSuccessful()) {
                    if (response.body() != null){

                        UsuarioAuth userToken = response.body();

                        AppPrefsSettings.getInstance().saveAuthToken(userToken.tokenuser);
                        AppPrefsSettings.getInstance().saveTokenTime(userToken.expiracao);

                        AppPrefsSettings.getInstance().setLoggedIn(true);


                        carregarMeuPerfil(userToken.tokenuser);


                    }
                } else {

                    waitingDialog.dismiss();
                    LoginManager.getInstance().logOut();
                    btnLoginFacebook.setEnabled(true);

                    String message ="";

                    try {
                        message = response.errorBody().string();
                        Log.d(TAG, "onLoginResponseError: "+message+", Error code: "+response.code());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    MetodosUsados.mostrarMensagem(LoginActivity.this,message+", ErroCode: "+response.code());



                }
            }

            @Override
            public void onFailure(@NonNull Call<UsuarioAuth> call, @NonNull Throwable t) {
                waitingDialog.dismiss();
                LoginManager.getInstance().logOut();
                btnLoginFacebook.setEnabled(true);
                if (!MetodosUsados.conexaoInternetTrafego(LoginActivity.this,TAG)){
                    MetodosUsados.mostrarMensagem(LoginActivity.this,R.string.msg_erro_internet);
                }else  if (t.getMessage().contains("timeout")) {
                    MetodosUsados.mostrarMensagem(LoginActivity.this,R.string.msg_erro_internet_timeout);
                }else {
                    MetodosUsados.mostrarMensagem(LoginActivity.this,R.string.msg_erro);
                }
                Log.d(TAG,"onFailure" + t.getMessage());


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {


        super.onResume();
    }

    @Override
    protected void onDestroy() {

        dialogLayoutConfirmarProcesso.cancel();
        super.onDestroy();
    }

    @Override
    protected void onPause() {

        super.onPause();

    }



}