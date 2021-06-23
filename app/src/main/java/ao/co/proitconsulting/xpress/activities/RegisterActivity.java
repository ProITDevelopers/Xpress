package ao.co.proitconsulting.xpress.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.activities.imagePicker.ImagePickerActivity;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.Common;
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

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "TAG_RegisterActivity";
    public static final int REQUEST_IMAGE = 100;
    private LinearLayout register_root;
    private FloatingActionButton fabPrevious;
    private CircleImageView imgUserPhoto;
    private AppCompatEditText editPrimeiroNome,editUltimoNome;
    private AppCompatEditText editTelefone,editEmail;
    private ShowHidePasswordEditText editPass,editConfirmPass;

    private AppCompatCheckBox checkboxAcceptTerms;
    private TextView txtTermsCondicoes;
    private Button btnRegistro;


    private String primeiroNome,sobreNome,email,telefone,senha,confirmSenha;
    private String valorGeneroItem;

    private Uri selectedImage;
    private String postPath="";


    //DIALOG_LAYOUT_CONFIRMAR_PROCESSO
    private Dialog dialogLayoutConfirmarProcesso;
    private ImageView imgConfirm;
    private TextView txtConfirmTitle,txtConfirmMsg;
    private Button dialog_btn_deny_processo,dialog_btn_accept_processo;

    //DIALOG_LAYOUT_SUCESSO
    private Dialog dialogLayoutSuccess;
    private TextView dialog_txtConfirmSucesso;
    private Button dialog_btn_sucesso;
    private String textlistenerNome, textlistenerSobreNome;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //showBackground in status bar
//        MetodosUsados.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.white));
        setContentView(R.layout.activity_register);



        initViews();
    }

    private void initViews() {


        register_root = findViewById(R.id.register_root);
        fabPrevious = findViewById(R.id.fabPrevious);
        imgUserPhoto = findViewById(R.id.imgUserPhoto);

        editPrimeiroNome = findViewById(R.id.editPrimeiroNome);
        editUltimoNome = findViewById(R.id.editUltimoNome);

        editEmail = findViewById(R.id.editEmail);

        editPass = findViewById(R.id.editPass);
        editConfirmPass = findViewById(R.id.editConfirmPass);


        editTelefone = findViewById(R.id.editTelefone);



        btnRegistro = findViewById(R.id.btnRegistro);

        checkboxAcceptTerms = findViewById(R.id.checkboxAcceptTerms);
        txtTermsCondicoes = findViewById(R.id.txtTermsCondicoes);


        fabPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });

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
//                textFullUserName = textlistenerNome.concat(textlistenerSobreNome);
//                editNomeUtilizador.setText(textFullUserName);
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
//                textFullUserName = textlistenerNome.concat(textlistenerSobreNome);
//                editNomeUtilizador.setText(textFullUserName);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




        imgUserPhoto.setImageResource(R.drawable.ic_baseline_outline_person_add_24);
        imgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarPermissaoFotoCameraGaleria();

            }
        });

        checkboxAcceptTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    checkboxAcceptTerms.setError(null);
                }


            }
        });

        txtTermsCondicoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.getLink = Common.GOOGLE_DRIVE_READ_PDF+Common.TERMS_CONDITIONS_XPRESS_LINK;
                Intent intent = new Intent(RegisterActivity.this, WebViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
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

    private void verificarPermissaoFotoCameraGaleria() {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });

    }

    private void launchCameraIntent() {
        Intent intent = new Intent(RegisterActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(RegisterActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);


                    selectedImage = uri;
                    postPath = selectedImage.getPath();

                    Log.d("TAG", "Image Get Uri path: " + uri.getPath());
                    Log.d("TAG", "Image Get Uri toString(): " + uri.toString());

                    // loading profile image from local cache
                    loadProfile(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


    private boolean verificarCampos() {

        primeiroNome = editPrimeiroNome.getText().toString().trim();
        sobreNome = editUltimoNome.getText().toString().trim();
        telefone = editTelefone.getText().toString().trim();
        email = editEmail.getText().toString().trim();
        senha = editPass.getText().toString().trim();
        confirmSenha = editConfirmPass.getText().toString().trim();
        valorGeneroItem = "M";



        if (primeiroNome.isEmpty()){
            editPrimeiroNome.setError(getString(R.string.msg_erro_campo_vazio));
            MetodosUsados.mostrarMensagemSnackBar(register_root,"Preencha o campo: 'Primeiro nome'");
            return false;
        }

//        if (!primeiroNome.matches("^[a-zA-Z\\s]+$")){
        if (primeiroNome.matches(".*\\d.*")){
            editPrimeiroNome.setError(getString(R.string.msg_erro_campo_com_letras));
            MetodosUsados.mostrarMensagemSnackBar(register_root,"Verifique o campo: 'Primeiro nome'");
            return false;
        }

        if (primeiroNome.length()<3){
            editPrimeiroNome.setError(getString(R.string.msg_erro_min_tres_letras));
            MetodosUsados.mostrarMensagemSnackBar(register_root,"'Primeiro nome', três letras no mínimo");
            return false;
        }

        if (sobreNome.isEmpty()){
            editUltimoNome.setError(getString(R.string.msg_erro_campo_vazio));
            MetodosUsados.mostrarMensagemSnackBar(register_root,"Preencha o campo: 'Sobrenome'");
            return false;
        }

        if (sobreNome.matches(".*\\d.*")){
            editUltimoNome.setError(getString(R.string.msg_erro_campo_com_letras));
            MetodosUsados.mostrarMensagemSnackBar(register_root,"Verifique o campo: 'Sobrenome'");
            return false;
        }

        if (sobreNome.length()<3){
            editUltimoNome.setError(getString(R.string.msg_erro_min_tres_letras));
            MetodosUsados.mostrarMensagemSnackBar(register_root,"'Sobrenome', três letras no mínimo");
            return false;
        }

        if (telefone.isEmpty()){
            editTelefone.setError(getString(R.string.msg_erro_campo_vazio));
            MetodosUsados.mostrarMensagemSnackBar(register_root,"Preencha o campo: 'Nº de telefone'");
            return false;
        }

        if (!telefone.matches("9[1-9][0-9]\\d{6}")){
            editTelefone.setError(getString(R.string.msg_erro_num_telefone_invalido));
            MetodosUsados.mostrarMensagemSnackBar(register_root,"'Nº de telefone' inválido");
            return false;
        }




        if (email.isEmpty()){
            editEmail.setError(getString(R.string.msg_erro_campo_vazio));
            MetodosUsados.mostrarMensagemSnackBar(register_root,"Preencha o campo: 'Seu e-mail'");
            return false;
        }

        if (!MetodosUsados.validarEmail(email)){
            editEmail.setError(getString(R.string.msg_erro_email_invalido));
            MetodosUsados.mostrarMensagemSnackBar(register_root,"'Seu-email' inválido");
            return false;
        }






        if (senha.isEmpty()){
            editPass.setError(getString(R.string.msg_erro_campo_vazio));
            MetodosUsados.mostrarMensagemSnackBar(register_root,"Preencha o campo: 'Palavra-passe'");
            return false;
        }

        if (senha.length()<=5){
            editPass.setError(getString(R.string.msg_erro_password_fraca));
            MetodosUsados.mostrarMensagemSnackBar(register_root,"'Palavra-passe' fraca");
            return false;
        }

        if (confirmSenha.isEmpty()){
            editConfirmPass.setError(getString(R.string.msg_erro_campo_vazio));
            MetodosUsados.mostrarMensagemSnackBar(register_root,"Preencha o campo: 'Repita a palavra-passe'");
            return false;
        }

        if (!confirmSenha.equals(senha)){
            editConfirmPass.setError(getString(R.string.msg_erro_password_diferentes));
            MetodosUsados.mostrarMensagemSnackBar(register_root,"'Palavra-passe' diferente");
            return false;
        }


        editConfirmPass.onEditorAction(EditorInfo.IME_ACTION_DONE);


        if (!checkboxAcceptTerms.isChecked()){
            checkboxAcceptTerms.setError("Termos e Condições.");
            MetodosUsados.mostrarMensagemSnackBar(register_root,"Por favor aceite os termos e condições para continuar.");
            return false;
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
        if (checkboxAcceptTerms.isChecked()){
            if (selectedImage == null) {
                mensagemRegistoSemFoto();
            }else{
                registrandoUsuario(true);
            }
        }else{
            checkboxAcceptTerms.setError("Termos e Condições.");
            MetodosUsados.mostrarMensagem(this,"Por favor aceite os termos e condições para continuar.");
        }

    }

    private void mensagemRegistoSemFoto() {

//        imgConfirm.setImageResource(R.drawable.ic_baseline_outline_person_add_24);
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
        registerRequest.primeiroNome = primeiroNome;
        registerRequest.ultimoNome = sobreNome;
        registerRequest.email = email;
        registerRequest.contactoMovel = telefone;
        registerRequest.password = senha;
        registerRequest.sexo = String.valueOf(valorGeneroItem.charAt(0));

        RequestBody primeiroNome = RequestBody.create(MediaType.parse("text/plain"),registerRequest.primeiroNome);
        RequestBody ultimoNome = RequestBody.create(MediaType.parse("text/plain"), registerRequest.ultimoNome);
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), registerRequest.email);
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), registerRequest.password);
        RequestBody contactoMovel = RequestBody.create(MediaType.parse("text/plain"), registerRequest.contactoMovel);
        RequestBody sexo = RequestBody.create(MediaType.parse("text/plain"), registerRequest.sexo);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<Void> call = null;

        if (hasPhoto){

            registerRequest.imagem = new File(postPath);
//            String filename  = registerRequest.imagem.getName();
            String filename  = System.currentTimeMillis() + ".jpg";
//            filepart = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImage)),file);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"),registerRequest.imagem);
//            RequestBody requestFile = RequestBody.create(MultipartBody.FORM, registerRequest.imagem);

            MultipartBody.Part imagem  = MultipartBody.Part.createFormData("Imagem",filename,requestFile);

            call = apiInterface.registrarUsuarioComImg(primeiroNome,ultimoNome,
                    email,password,contactoMovel,sexo,imagem);
        } else {
            call = apiInterface.registrarUsuarioSemImg(primeiroNome,ultimoNome,
                    email,password,contactoMovel,sexo);
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
                                    imgUserPhoto.setImageResource(R.drawable.ic_baseline_outline_person_add_24);
                                    editPrimeiroNome.setText("");
                                    editUltimoNome.setText("");
                                    editEmail.setText("");
                                    editTelefone.setText("");
                                    editPass.setText("");



                                    dialog_txtConfirmSucesso.setText(getString(R.string.msg_conta_criada_sucesso));
                                    dialogLayoutSuccess.show();
                                }
                            });



                } else {

                    MetodosUsados.hideLoadingDialog();

                    String responseErrorMsg ="";

                    try {
                        responseErrorMsg = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(responseErrorMsg);
                        responseErrorMsg = jsonObject.getString("message");

                        Log.d(TAG,"Error code: "+response.code()+", ErrorBody msg: "+responseErrorMsg);





                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (JSONException err){
                        Log.d(TAG, err.toString());
                    }

                    Snackbar.make(register_root, responseErrorMsg, 4000)
                            .setActionTextColor(Color.WHITE)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                MetodosUsados.hideLoadingDialog();
                if (!MetodosUsados.conexaoInternetTrafego(RegisterActivity.this,TAG)){
                    MetodosUsados.mostrarMensagem(RegisterActivity.this,R.string.msg_erro_internet);
                }else  if (t.getMessage().contains("timeout")) {
                    MetodosUsados.mostrarMensagem(RegisterActivity.this,R.string.msg_erro_internet_timeout);
                }else {
                    MetodosUsados.mostrarMensagem(RegisterActivity.this,R.string.msg_erro);
                }
                Log.d(TAG,"onFailure" + t.getMessage());

            }
        });
    }

    private void loadProfile(String url) {
        Log.d(TAG, "Image cache path: " + url);


        Picasso.with(this).load(url).fit().centerCrop().into(imgUserPhoto);

    }



    @Override
    protected void onResume() {
        MetodosUsados.spotsDialog(this);

        // Clearing older images from cache directory
        // don't call this line if you want to choose multiple images in the same activity
        // call this once the bitmap(s) usage is over
        ImagePickerActivity.clearCache(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        MetodosUsados.hideLoadingDialog();
        dialogLayoutConfirmarProcesso.cancel();
        dialogLayoutSuccess.cancel();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}