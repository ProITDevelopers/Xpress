package ao.co.proitconsulting.xpress.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.concurrent.TimeUnit;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.api.ErrorResponce;
import ao.co.proitconsulting.xpress.api.ErrorUtils;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.modelos.RegisterRequest;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "RegisterActivity";

    private RelativeLayout register_root;
    private CircleImageView imgUserPhoto;
    private AppCompatEditText editPrimeiroNome,editUltimoNome,editNomeUtilizador;
    private AppCompatEditText editTelefone,editEmail;
    private ShowHidePasswordEditText editPass,editConfirmPass;
    private AppCompatEditText editMunicipio,editBairro,editRua,editNCasa;
    private Spinner editCidadeSpiner;
    private RadioGroup radioGroup;
    private RadioButton radioBtnFem,radioBtnMasc;
    private Button btnRegistro;
    private TextView txtCancelar;

    private String nomeUtilizador,primeiroNome,sobreNome,email,telefone,senha,senhaConf;
    private String valorGeneroItem,valorCidadeItem;
    private String municipio,bairro,rua,nCasa;
    private String textlistenerNome="";
    private String textlistenerSobreNome="";
    private String textFullUserName="";
    private Uri selectedImage;
    private String postPath="";


    //DIALOG_LAYOUT_CONFIRMAR_PROCESSO
    private Dialog dialogLayoutConfirmarProcesso;
    private ImageView imgConfirm;
    private TextView txtConfirmTitle,txtConfirmMsg;
    private Button dialog_btn_deny_processo,dialog_btn_accept_processo;

    //DIALOG_LAYOUT_SUCESSO
    private Dialog dialogLayoutSuccess;
    private ImageView dialog_img_status;
    private TextView dialog_txtConfirmSucesso;
    private Button dialog_btn_sucesso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
    }

    private void initViews() {

        MetodosUsados.spotsDialog(this);

        register_root = findViewById(R.id.register_root);
        imgUserPhoto = findViewById(R.id.imgUserPhoto);

        editPrimeiroNome = findViewById(R.id.editPrimeiroNome);
        editUltimoNome = findViewById(R.id.editUltimoNome);
        editNomeUtilizador = findViewById(R.id.editNomeUtilizador);
        editEmail = findViewById(R.id.editEmail);

        editPass = findViewById(R.id.editPass);
        editConfirmPass = findViewById(R.id.editConfirmPass);

        editTelefone = findViewById(R.id.editTelefone);

        editMunicipio = findViewById(R.id.editMunicipio);
        editBairro = findViewById(R.id.editBairro);
        editRua = findViewById(R.id.editRua);
        editNCasa = findViewById(R.id.editNCasa);
        editCidadeSpiner = findViewById(R.id.editCidadeSpiner);

        radioGroup = findViewById(R.id.radioGroup);
        radioBtnFem = findViewById(R.id.radioBtnFem);
        radioBtnMasc = findViewById(R.id.radioBtnMasc);

        btnRegistro = findViewById(R.id.btnRegistro);
        txtCancelar = findViewById(R.id.txtCancelar);


        ArrayAdapter<CharSequence> adapterCidade = ArrayAdapter.createFromResource(this,
                R.array.cidade, android.R.layout.simple_spinner_item);
        adapterCidade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editCidadeSpiner.setAdapter(adapterCidade);
        editCidadeSpiner.setOnItemSelectedListener(this);

        editPrimeiroNome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textlistenerNome = String.valueOf(s);
                textlistenerNome = MetodosUsados.removeAcentos(textlistenerNome);
                textlistenerNome = textlistenerNome.replaceAll("\\s+","").toLowerCase();
                if (textlistenerSobreNome==null)
                    textlistenerSobreNome="";
                textFullUserName = textlistenerNome.concat(textlistenerSobreNome);
                editNomeUtilizador.setText(textFullUserName);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editUltimoNome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textlistenerSobreNome = String.valueOf(s);
                textlistenerSobreNome = MetodosUsados.removeAcentos(textlistenerSobreNome);
                textlistenerSobreNome = textlistenerSobreNome.replaceAll("\\s+","").toLowerCase();
                if (textlistenerNome==null)
                    textlistenerNome="";
                textFullUserName = textlistenerNome.concat(textlistenerSobreNome);
                editNomeUtilizador.setText(textFullUserName);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        radioBtnFem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    radioBtnFem.setError(null);
                    radioBtnMasc.setError(null);
                }
            }
        });

        radioBtnMasc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    radioBtnFem.setError(null);
                    radioBtnMasc.setError(null);
                }
            }
        });


        Picasso.with(this).load(R.drawable.photo_placeholder).into(imgUserPhoto);
        imgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodosUsados.mostrarMensagem(RegisterActivity.this,"verificarPermissaoFotoCameraGaleria();");

            }
        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificarCampos()){
                    verificarConecxaoInternet();
                }
            }
        });
        txtCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

        dialog_btn_sucesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MetodosUsados.esconderTeclado(RegisterActivity.this);
                dialogLayoutSuccess.cancel();
                launchHomeScreen();
            }
        });



    }

    private void launchHomeScreen() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private boolean verificarCampos() {

        nomeUtilizador = editNomeUtilizador.getText().toString().trim();
        primeiroNome = editPrimeiroNome.getText().toString().trim();
        sobreNome = editUltimoNome.getText().toString().trim();
        email = editEmail.getText().toString().trim();
        telefone = editTelefone.getText().toString().trim();
        senha = editPass.getText().toString().trim();
        senhaConf = editConfirmPass.getText().toString().trim();

        municipio = editMunicipio.getText().toString().trim();
        bairro = editBairro.getText().toString().trim();
        rua = editRua.getText().toString().trim();
        nCasa = editNCasa.getText().toString().trim();


        if (primeiroNome.isEmpty()){
            editPrimeiroNome.setError(getString(R.string.msg_erro_campo_vazio));
            return false;
        }

//        if (!primeiroNome.matches("^[a-zA-Z\\s]+$")){
        if (primeiroNome.matches(".*\\d.*")){
            editPrimeiroNome.setError(getString(R.string.msg_erro_campo_com_letras));
            return false;
        }

        if (primeiroNome.length()<3){
            editPrimeiroNome.setError(getString(R.string.msg_erro_min_tres_letras));
            return false;
        }

        if (sobreNome.isEmpty()){
            editUltimoNome.setError(getString(R.string.msg_erro_campo_vazio));
            return false;
        }

        if (sobreNome.matches(".*\\d.*")){
            editUltimoNome.setError(getString(R.string.msg_erro_campo_com_letras));
            return false;
        }

        if (sobreNome.length()<3){
            editUltimoNome.setError(getString(R.string.msg_erro_min_tres_letras));
            return false;
        }



        if (nomeUtilizador.isEmpty()){
            editNomeUtilizador.setError(getString(R.string.msg_erro_campo_vazio));
            return false;
        }

        if (nomeUtilizador.contains(" ")){
            editNomeUtilizador.setError(getString(R.string.msg_erro_campo_vazio_sem_espaco));
            return false;
        }

//        if (!nomeUtilizador.matches("^[a-zA-Z\\s]+$")){
//            editNomeUtilizador.setError(getString(R.string.msg_erro_campo_com_letras));
//            return false;
//        }

        if (nomeUtilizador.length()<3){
            editNomeUtilizador.setError(getString(R.string.msg_erro_min_tres_letras));
            return false;
        }

        try {
            nomeUtilizador =  MetodosUsados.removeAcentos(nomeUtilizador);
        }catch (Exception e){
            e.printStackTrace();
        }


        if (email.isEmpty()){
            editEmail.setError(getString(R.string.msg_erro_campo_vazio));
            return false;
        }

        if (!MetodosUsados.validarEmail(email)){
            editEmail.setError(getString(R.string.msg_erro_email_invalido));
            return false;
        }


        if (telefone.isEmpty()){
            editTelefone.setError(getString(R.string.msg_erro_campo_vazio));
            return false;
        }

        if (!telefone.matches("9[1-9][0-9]\\d{6}")){
            editTelefone.setError(getString(R.string.msg_erro_num_telefone_invalido));
            return false;
        }



        if (senha.isEmpty()){
            editPass.setError(getString(R.string.msg_erro_campo_vazio));
            return false;
        }

        if (senha.length()<=5){
            editPass.setError(getString(R.string.msg_erro_password_fraca));
            return false;
        }

        if (senhaConf.length()<=5){
            editConfirmPass.setError(getString(R.string.msg_erro_password_fraca));
            return false;
        }

        if (!senha.equals(senhaConf)){
            editConfirmPass.setError(getString(R.string.msg_erro_password_diferente));
            return false;
        }


        if (municipio.isEmpty()){
            editMunicipio.setError(getString(R.string.msg_erro_campo_vazio));
            return false;
        }

        if (municipio.length()<3){
            editMunicipio.setError(getString(R.string.msg_erro_min_tres_letras));
            return false;
        }


        if (bairro.isEmpty()){
            editBairro.setError(getString(R.string.msg_erro_campo_vazio));
            return false;
        }

        if (bairro.length()<3){
            editBairro.setError(getString(R.string.msg_erro_min_tres_letras));
            return false;
        }


        if (rua.isEmpty()){
            editRua.setError(getString(R.string.msg_erro_campo_vazio));
            return false;
        }


        if (nCasa.isEmpty()){
            editNCasa.setError(getString(R.string.msg_erro_campo_vazio));
            return false;
        }


        if (!radioBtnMasc.isChecked() && !radioBtnFem.isChecked()){
            radioBtnMasc.setError(getString(R.string.msg_selecione_genero));
            radioBtnFem.setError(getString(R.string.msg_selecione_genero));
            return false;
        }else{
            radioBtnMasc.setError(null);
            radioBtnFem.setError(null);
            switch (radioGroup.getCheckedRadioButtonId()) {
                case R.id.radioBtnFem:
                    valorGeneroItem = radioBtnFem.getText().toString().trim();
                    break;
                case R.id.radioBtnMasc:
                    valorGeneroItem= radioBtnMasc.getText().toString().trim();
                    break;

            }

        }




        return true;
    }

    private void verificarConecxaoInternet() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null){
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null) {
                MetodosUsados.mostrarMensagem(this,R.string.msg_erro_internet);
            } else {
                vericarImgUsuario();
            }
        }
    }

    private void vericarImgUsuario() {
        if (selectedImage == null) {
            mensagemRegistoSemFoto();
        }else{
            registrandoUsuario(true);
        }
    }

    private void mensagemRegistoSemFoto() {

        imgConfirm.setImageResource(R.drawable.photo_placeholder);
        txtConfirmTitle.setText(getString(R.string.registo_sem_foto));
        txtConfirmMsg.setText(getString(R.string.msg_deseja_continuar));

        dialog_btn_accept_processo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLayoutConfirmarProcesso.cancel();
                registrandoUsuario(false);
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

    private void registrandoUsuario(boolean hasPhoto){

        MetodosUsados.showLoadingDialog(getString(R.string.msg_register_quase_pronto));

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.usuario = nomeUtilizador;
        registerRequest.primeiroNome = primeiroNome;
        registerRequest.ultimoNome = sobreNome;
        registerRequest.email = email;
        registerRequest.contactoMovel = telefone;
        registerRequest.password = senha;
        registerRequest.provincia = valorCidadeItem;
        registerRequest.municipio = municipio;
        registerRequest.bairro = bairro;
        registerRequest.rua = rua;
        registerRequest.nCasa = nCasa;
        registerRequest.sexo = valorGeneroItem;

        RequestBody primeiroNome = RequestBody.create(MultipartBody.FORM,registerRequest.primeiroNome);
        RequestBody ultimoNome = RequestBody.create(MultipartBody.FORM, registerRequest.ultimoNome);
        RequestBody usuario = RequestBody.create(MultipartBody.FORM,registerRequest.usuario);
        RequestBody email = RequestBody.create(MultipartBody.FORM, registerRequest.email);
        RequestBody password = RequestBody.create(MultipartBody.FORM, registerRequest.password);
        RequestBody contactoMovel = RequestBody.create(MultipartBody.FORM, registerRequest.contactoMovel);
        RequestBody sexo = RequestBody.create(MultipartBody.FORM, registerRequest.sexo);
        RequestBody provincia = RequestBody.create(MultipartBody.FORM, registerRequest.provincia);
        RequestBody municipio = RequestBody.create(MultipartBody.FORM, registerRequest.municipio);
        RequestBody bairro = RequestBody.create(MultipartBody.FORM, registerRequest.bairro);
        RequestBody rua = RequestBody.create(MultipartBody.FORM, registerRequest.rua);
        RequestBody nCasa = RequestBody.create(MultipartBody.FORM, registerRequest.nCasa);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<Void> call = null;
        File file = null;
        RequestBody filepart = null;
        MultipartBody.Part file1 = null;

        if (hasPhoto){
            file = new File(postPath);
//            filepart = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImage)),file);
            filepart = RequestBody.create(MediaType.parse("image/*"),file);
            file1 = MultipartBody.Part.createFormData("imagem",file.getName(),filepart);

            call = apiInterface.registrarUsuarioComImg(primeiroNome,ultimoNome,
                    usuario,email,password,contactoMovel,sexo,
                    provincia,municipio,bairro,rua,nCasa,file1);
        } else {
            call = apiInterface.registrarUsuarioSemImg(primeiroNome,ultimoNome,
                    usuario,email,password,contactoMovel,sexo,
                    provincia,municipio,bairro,rua,nCasa);
        }

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()){

                    Completable.timer(2, TimeUnit.SECONDS,
                            AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() throws Exception {

                                    MetodosUsados.hideLoadingDialog();
                                    imgUserPhoto.setImageResource(R.drawable.photo_placeholder);
                                    editNomeUtilizador.setText("");
                                    editPrimeiroNome.setText("");
                                    editUltimoNome.setText("");
                                    editEmail.setText("");
                                    editTelefone.setText("");
                                    editPass.setText("");
                                    editConfirmPass.setText("");
                                    editMunicipio.setText("");
                                    editBairro.setText("");
                                    editRua.setText("");
                                    editNCasa.setText("");



                                    dialog_txtConfirmSucesso.setText(getString(R.string.msg_conta_criada_sucesso));
                                    dialogLayoutSuccess.show();
                                }
                            });



                } else {
                    ErrorResponce errorResponce = ErrorUtils.parseError(response);
                    MetodosUsados.hideLoadingDialog();

                    try {

//                        Snackbar
//                                .make(raiz, response.errorBody().string(), 4000)
//                                .setActionTextColor(Color.WHITE)
//                                .show();

                        Snackbar.make(register_root, errorResponce.getError(), 4000)
                                .setActionTextColor(Color.WHITE)
                                .show();
                    }catch (Exception e){
                        Log.i(TAG,"autenticacaoVerif snakBar" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                MetodosUsados.hideLoadingDialog();
                if (!MetodosUsados.conexaoInternetTrafego(RegisterActivity.this,TAG)){
                    MetodosUsados.mostrarMensagem(RegisterActivity.this,R.string.msg_erro_internet);
                }else  if ("timeout".equals(t.getMessage())) {
                    MetodosUsados.mostrarMensagem(RegisterActivity.this,R.string.msg_erro_internet_timeout);
                }else {
                    MetodosUsados.mostrarMensagem(RegisterActivity.this,R.string.msg_erro);
                }
                Log.i(TAG,"onFailure" + t.getMessage());

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(this, R.color.login_register_text_color));
        if (parent.getId() == R.id.editCidadeSpiner) {
            valorCidadeItem = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onDestroy() {
        MetodosUsados.hideLoadingDialog();
        dialogLayoutConfirmarProcesso.cancel();
        dialogLayoutSuccess.cancel();
        super.onDestroy();
    }
}