package ao.co.proitconsulting.xpress.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.UsuarioPerfil;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilActivity extends AppCompatActivity {

    private static final String TAG = "TAG_PerfilActivity";
    private static final int PROFILE_REQUEST_CODE = 13;

    private UsuarioPerfil usuarioPerfil;
    private CircleImageView imageView;
    private TextView txtUserNameInitial,txtNomeCompleto,txtTelefone,txtTelefoneAlternativo;
    private TextView txtEmail,txtSexo,txtUserProv,txtProvincia,txtUserAddress,txtEndereco;

    //DIALOG_LAYOUT_CONFIRMAR_PROCESSO
    private Dialog dialogLayoutConfirmarProcesso;
    private ImageView imgConfirm;
    private TextView txtConfirmTitle,txtConfirmMsg;
    private Button dialog_btn_deny_processo,dialog_btn_accept_processo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(getString(R.string.nav_meu_perfil_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();

        ///carregar dados do Usuario
        usuarioPerfil = AppPrefsSettings.getInstance().getUser();
        carregarMeuPerfilOffline(usuarioPerfil);

    }

    private void initViews() {
        imageView = findViewById(R.id.imgUserPhoto);

        txtUserNameInitial = findViewById(R.id.txtUserNameInitial);
        txtNomeCompleto = findViewById(R.id.txtNomeCompleto);
        txtTelefone = findViewById(R.id.txtTelefone);
        txtTelefoneAlternativo = findViewById(R.id.txtTelefoneAlternativo);

        txtEmail = findViewById(R.id.txtEmail);
        txtSexo = findViewById(R.id.txtSexo);
        txtUserProv = findViewById(R.id.txtUserProv);
        txtProvincia = findViewById(R.id.txtProvincia);

        txtUserAddress = findViewById(R.id.txtUserAddress);
        txtEndereco = findViewById(R.id.txtEndereco);

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

    private void carregarMeuPerfilOffline(UsuarioPerfil usuarioPerfil) {

        String userNameInitial;
        if (usuarioPerfil!=null){

            if (usuarioPerfil.imagem.equals(getString(R.string.sem_imagem))){
                if (!usuarioPerfil.primeiroNome.isEmpty()){
                    userNameInitial = String.valueOf(usuarioPerfil.primeiroNome.charAt(0));
                    txtUserNameInitial.setText(userNameInitial.toUpperCase());
                    txtUserNameInitial.setVisibility(View.VISIBLE);

                }else {
                    userNameInitial = String.valueOf(usuarioPerfil.email.charAt(0));
                    txtUserNameInitial.setText(userNameInitial.toUpperCase());
                }

            }else {
                txtUserNameInitial.setVisibility(View.GONE);
                Picasso.with(this)
                        .load(usuarioPerfil.imagem)
//                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .fit().centerCrop()
                        .placeholder(R.drawable.photo_placeholder)
                        .into(imageView);
            }

            txtNomeCompleto.setText(usuarioPerfil.nomeCompleto);
            txtTelefone.setText(usuarioPerfil.contactoMovel);

            if (usuarioPerfil.contactoAlternativo==null || usuarioPerfil.contactoAlternativo.isEmpty()){
                txtTelefoneAlternativo.setVisibility(View.GONE);
                txtTelefoneAlternativo.setText("");
            } else{
                txtTelefoneAlternativo.setText(usuarioPerfil.contactoAlternativo);
            }

            txtEmail.setText(usuarioPerfil.email);

            if (usuarioPerfil.sexo.equals("F"))
                txtSexo.setText("Feminino");
            else
                txtSexo.setText("Masculino");

            if (usuarioPerfil.provincia==null){
                txtUserProv.setVisibility(View.GONE);
                txtProvincia.setVisibility(View.GONE);
                txtProvincia.setText("");
            }
            else{
                txtProvincia.setText(usuarioPerfil.provincia);
            }




            if (usuarioPerfil.municipio==null || usuarioPerfil.bairro==null ||
                    usuarioPerfil.rua==null||usuarioPerfil.nCasa==null){

                txtUserAddress.setVisibility(View.GONE);
                txtEndereco.setVisibility(View.GONE);
                txtEndereco.setText("");

            }else{
                txtEndereco.setText("Município "+
                        usuarioPerfil.municipio +", "+
                        "Bairro "+usuarioPerfil.bairro+", "+
                        "Rua "+usuarioPerfil.rua+", "+
                        "Casa nº"+usuarioPerfil.nCasa);
            }




        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_navigation_bottom; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_perfil, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        if (id == R.id.menu_perfil_edit) {

            Intent intent = new Intent(this, EditarPerfilActivity.class);
            intent.putExtra("toolbarTitle","Editar perfil");
            startActivityForResult(intent, PROFILE_REQUEST_CODE);
        }

        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this,ConfiguracoesActivity.class);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                String perfilEditado = data.getStringExtra("perfilEdited");
                verificaoPerfil();

            }
        }

    }

    private void verificaoPerfil() {
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null) {
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo != null){
                carregarMeuPerfil();
            }
        }
    }

    private void carregarMeuPerfil() {
        AlertDialog waitingDialog = new SpotsDialog.Builder().setContext(this).build();
        waitingDialog.setMessage(getString(R.string.msg_login_auth_carregando_dados));
        waitingDialog.setCancelable(false);
        waitingDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<UsuarioPerfil>> usuarioCall = apiInterface.getMeuPerfil();
        usuarioCall.enqueue(new Callback<List<UsuarioPerfil>>() {
            @Override
            public void onResponse(@NonNull Call<List<UsuarioPerfil>> call, @NonNull Response<List<UsuarioPerfil>> response) {

                if (response.isSuccessful()) {
                    waitingDialog.cancel();
                    if (response.body()!=null){
                        usuarioPerfil = response.body().get(0);

                        AppPrefsSettings.getInstance().saveUser(usuarioPerfil);

                        carregarMeuPerfilOffline(usuarioPerfil);

                    }

                } else {

                    waitingDialog.cancel();
                    if (response.code()==401){
                        mensagemTokenExpirado();
                    }

                }





            }

            @Override
            public void onFailure(@NonNull Call<List<UsuarioPerfil>> call, @NonNull Throwable t) {
                waitingDialog.cancel();

            }
        });
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