package ao.co.proitconsulting.xpress.fragmentos.perfil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ao.co.proitconsulting.xpress.R;

import ao.co.proitconsulting.xpress.activities.EditarPerfilActivity;
import ao.co.proitconsulting.xpress.activities.imagePicker.ImagePickerActivity;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.UsuarioPerfil;
import ao.co.proitconsulting.xpress.modelos.UsuarioPerfilRequest;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarPerfilFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "TAG_EditPerf_Fragment";
    private View view;
    public static final int REQUEST_IMAGE = 100;
    private UsuarioPerfil usuarioPerfil;

    private ConstraintLayout editPerfil_root;
    private TextView txtUserNameInitial;
    private CircleImageView imageView;
    private AppCompatEditText editPrimeiroNome,editUltimoNome;
    private RadioGroup radioGroup;
    private RadioButton radioBtnFem,radioBtnMasc;
    private AppCompatEditText editTelefoneAlternativo;
    private Spinner editCidadeSpiner;
    private AppCompatEditText editMunicipio,editBairro,editRua,editNCasa;
    private Button btnSalvar;


    private String primeiroNome,sobreNome,telefoneAlternativo;
    private String valorGeneroItem,valorCidadeItem;
    private String municipio,bairro,rua,nCasa;

    private Uri selectedImage;
    private String postPath="";
    private String toolbarTitle;

    //DIALOG_LAYOUT_CONFIRMAR_PROCESSO
    private Dialog dialogLayoutConfirmarProcesso;
    private ImageView imgConfirm;
    private TextView txtConfirmTitle,txtConfirmMsg;
    private Button dialog_btn_deny_processo,dialog_btn_accept_processo;

    //DIALOG_LAYOUT_SUCESSO
    private Dialog dialogLayoutSuccess;
    private TextView dialog_txtConfirmSucesso;
    private Button dialog_btn_sucesso;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_editar_perfil, container, false);
        initViews();


        usuarioPerfil = AppPrefsSettings.getInstance().getUser();
        carregarMeuPerfilOffline(usuarioPerfil);

        return view;
    }

    private void initViews() {
        editPerfil_root = view.findViewById(R.id.editPerfil_root);

        imageView = view.findViewById(R.id.imgUserPhoto);
        txtUserNameInitial = view.findViewById(R.id.txtUserNameInitial);
        editPrimeiroNome = view.findViewById(R.id.editPrimeiroNome);
        editUltimoNome = view.findViewById(R.id.editUltimoNome);

        editTelefoneAlternativo = view.findViewById(R.id.editTelefoneAlternativo);

        radioGroup = view.findViewById(R.id.radioGroup);
        radioBtnFem = view.findViewById(R.id.radioBtnFem);
        radioBtnMasc = view.findViewById(R.id.radioBtnMasc);

        editMunicipio = view.findViewById(R.id.editMunicipio);
        editBairro = view.findViewById(R.id.editBairro);
        editRua = view.findViewById(R.id.editRua);
        editNCasa = view.findViewById(R.id.editNCasa);

        btnSalvar = view.findViewById(R.id.btnSalvar);

        radioBtnFem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    radioBtnFem.setTextColor(ContextCompat.getColor(getContext(),R.color.login_register_text_color));
                    radioBtnFem.setError(null);
                    radioBtnMasc.setError(null);
                }else{
                    radioBtnFem.setTextColor(ContextCompat.getColor(getContext(),R.color.transparentBlack));
                }
            }
        });

        radioBtnMasc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    radioBtnMasc.setTextColor(ContextCompat.getColor(getContext(),R.color.login_register_text_color));
                    radioBtnMasc.setError(null);
                    radioBtnFem.setError(null);
                }else{
                    radioBtnMasc.setTextColor(ContextCompat.getColor(getContext(),R.color.transparentBlack));
                }
            }
        });



        editCidadeSpiner = view.findViewById(R.id.editCidadeSpiner);
        ArrayAdapter<CharSequence> adapterCidade = ArrayAdapter.createFromResource(getContext(),
                R.array.cidade, android.R.layout.simple_spinner_item);
        adapterCidade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editCidadeSpiner.setAdapter(adapterCidade);
        editCidadeSpiner.setOnItemSelectedListener(this);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarPermissaoFotoCameraGaleria();

            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificarCampos()){
                    verificarConecxaoInternet();
                }
            }
        });

        //-------------------------------------------------------------//
        //-------------------------------------------------------------//
        //DIALOG_LAYOUT_CONFIRMAR_PROCESSO
        dialogLayoutConfirmarProcesso = new Dialog(getContext());
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
        dialogLayoutSuccess = new Dialog(getContext());
        dialogLayoutSuccess.setContentView(R.layout.layout_sucesso);
        dialogLayoutSuccess.setCancelable(false);
        if (dialogLayoutSuccess.getWindow()!=null)
            dialogLayoutSuccess.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog_txtConfirmSucesso = dialogLayoutSuccess.findViewById(R.id.dialog_txtConfirmSucesso);
        dialog_btn_sucesso = dialogLayoutSuccess.findViewById(R.id.dialog_btn_sucesso);




    }

    private void carregarMeuPerfilOffline(UsuarioPerfil usuarioPerfil) {
        String userNameInitial, userLastNameInicial;
        if (usuarioPerfil!=null){

            if (usuarioPerfil.imagem.equals(getString(R.string.sem_imagem))){
                if (usuarioPerfil.primeiroNome!=null && usuarioPerfil.ultimoNome != null){
                    userNameInitial = String.valueOf(usuarioPerfil.primeiroNome.charAt(0));
                    userLastNameInicial = String.valueOf(usuarioPerfil.ultimoNome.charAt(0));
                    txtUserNameInitial.setText(userNameInitial.toUpperCase().concat(userLastNameInicial.toUpperCase()));
                    txtUserNameInitial.setVisibility(View.VISIBLE);

                }

            }else {
                txtUserNameInitial.setVisibility(View.GONE);
                Picasso.with(getContext())
                        .load(usuarioPerfil.imagem)
//                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .fit().centerCrop()
                        .placeholder(R.drawable.photo_placeholder)
                        .into(imageView);
            }

            editPrimeiroNome.setText(usuarioPerfil.primeiroNome);
            editUltimoNome.setText(usuarioPerfil.ultimoNome);

            if (usuarioPerfil.contactoAlternativo==null)
                editTelefoneAlternativo.setText("");
            else
                editTelefoneAlternativo.setText(usuarioPerfil.contactoAlternativo);


            if (usuarioPerfil.sexo.equals("F")){
                radioBtnFem.setChecked(true);
            } else {
                radioBtnMasc.setChecked(true);
            }

            if (usuarioPerfil.municipio==null)
                editMunicipio.setText("");
            else
                editMunicipio.setText(usuarioPerfil.municipio);


            if (usuarioPerfil.bairro==null)
                editBairro.setText("");
            else
                editBairro.setText(usuarioPerfil.bairro);

            if (usuarioPerfil.rua==null)
                editRua.setText("");
            else
                editRua.setText(usuarioPerfil.rua);

            if (usuarioPerfil.nCasa==null)
                editNCasa.setText("");
            else
                editNCasa.setText(usuarioPerfil.nCasa);

            if (usuarioPerfil.provincia==null)
                valorCidadeItem = "Luanda";
            else
                valorCidadeItem = usuarioPerfil.provincia;

            for(int i=0; i < editCidadeSpiner.getAdapter().getCount(); i++) {
                if(valorCidadeItem.trim().equals(editCidadeSpiner.getItemAtPosition(i).toString())){
                    editCidadeSpiner.setSelection(i);
                    break;
                }
            }


        }


    }

    private boolean verificarCampos() {

        primeiroNome = editPrimeiroNome.getText().toString().trim();
        sobreNome = editUltimoNome.getText().toString().trim();

        telefoneAlternativo = editTelefoneAlternativo.getText().toString().trim();
        municipio = editMunicipio.getText().toString().trim();
        bairro = editBairro.getText().toString().trim();
        rua = editRua.getText().toString().trim();
        nCasa = editNCasa.getText().toString().trim();



        if (primeiroNome.isEmpty()){
            editPrimeiroNome.setError(getString(R.string.msg_erro_campo_vazio));
            MetodosUsados.mostrarMensagemSnackBar(editPerfil_root,"Preencha o campo: 'Primeiro nome'");
            return false;
        }

//        if (!primeiroNome.matches("^[a-zA-Z\\s]+$")){
        if (primeiroNome.matches(".*\\d.*")){
            editPrimeiroNome.setError(getString(R.string.msg_erro_campo_com_letras));
            MetodosUsados.mostrarMensagemSnackBar(editPerfil_root,"Verifique o campo: 'Primeiro nome'");
            return false;
        }

        if (primeiroNome.length()<3){
            editPrimeiroNome.setError(getString(R.string.msg_erro_min_tres_letras));
            MetodosUsados.mostrarMensagemSnackBar(editPerfil_root,"'Primeiro nome', três letras no mínimo");
            return false;
        }

        if (sobreNome.isEmpty()){
            editUltimoNome.setError(getString(R.string.msg_erro_campo_vazio));
            MetodosUsados.mostrarMensagemSnackBar(editPerfil_root,"Preencha o campo: 'Sobrenome'");
            return false;
        }

        if (sobreNome.matches(".*\\d.*")){
            editUltimoNome.setError(getString(R.string.msg_erro_campo_com_letras));
            MetodosUsados.mostrarMensagemSnackBar(editPerfil_root,"Verifique o campo: 'Sobrenome'");
            return false;
        }

        if (sobreNome.length()<3){
            editUltimoNome.setError(getString(R.string.msg_erro_min_tres_letras));
            MetodosUsados.mostrarMensagemSnackBar(editPerfil_root,"'Sobrenome', três letras no mínimo");
            return false;
        }





//        if (telefoneAlternativo.isEmpty()){
//            editTelefoneAlternativo.setError(getString(R.string.msg_erro_campo_vazio));
//            MetodosUsados.mostrarMensagemSnackBar(editPerfil_root,"Preencha o campo: 'Nº alternativo'");
//            return false;
//        }
//
//        if (!telefoneAlternativo.matches("9[1-9][0-9]\\d{6}")){
//            editTelefoneAlternativo.setError(getString(R.string.msg_erro_num_telefone_invalido));
//            MetodosUsados.mostrarMensagemSnackBar(editPerfil_root,"'Nº alternativo' inválido");
//            return false;
//        }

        if (!telefoneAlternativo.isEmpty()){
            if (!telefoneAlternativo.matches("9[1-9][0-9]\\d{6}")){
                editTelefoneAlternativo.setError(getString(R.string.msg_erro_num_telefone_invalido));
                MetodosUsados.mostrarMensagemSnackBar(editPerfil_root,"'Nº alternativo' inválido");
                return false;
            }
        }








        if (!radioBtnMasc.isChecked() && !radioBtnFem.isChecked()){
            radioBtnMasc.setError(getString(R.string.msg_selecione_genero));
            radioBtnFem.setError(getString(R.string.msg_selecione_genero));
            MetodosUsados.mostrarMensagemSnackBar(editPerfil_root,"'Sexo', selecione o género");
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

        if (municipio.isEmpty()){
            editMunicipio.setError(getString(R.string.msg_erro_campo_vazio));
            MetodosUsados.mostrarMensagemSnackBar(editPerfil_root,"Preencha o campo: 'Município'");
            return false;
        }

        if (municipio.length()<3){
            editMunicipio.setError(getString(R.string.msg_erro_min_tres_letras));
            MetodosUsados.mostrarMensagemSnackBar(editPerfil_root,"'Município', três letras no mínimo");
            return false;
        }


        if (bairro.isEmpty()){
            editBairro.setError(getString(R.string.msg_erro_campo_vazio));
            MetodosUsados.mostrarMensagemSnackBar(editPerfil_root,"Preencha o campo: 'Bairro'");
            return false;
        }

        if (bairro.length()<3){
            editBairro.setError(getString(R.string.msg_erro_min_tres_letras));
            MetodosUsados.mostrarMensagemSnackBar(editPerfil_root,"'Bairro', três letras no mínimo");
            return false;
        }


        if (rua.isEmpty()){
            editRua.setError(getString(R.string.msg_erro_campo_vazio));
            MetodosUsados.mostrarMensagemSnackBar(editPerfil_root,"Preencha o campo: 'Rua'");
            return false;
        }


        if (nCasa.isEmpty()){
            editNCasa.setError(getString(R.string.msg_erro_campo_vazio));
            MetodosUsados.mostrarMensagemSnackBar(editPerfil_root,"Preencha o campo: 'Nº da casa'");
            return false;
        }




        return true;
    }

    private void verificarConecxaoInternet(){
        ConnectivityManager conMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr!=null){
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null) {
                MetodosUsados.mostrarMensagem(getContext(),R.string.msg_erro_internet);
            } else {
                salvarDados();
            }
        }
    }

    private void salvarDados() {

        MetodosUsados.showLoadingDialog(getString(R.string.msg_register_quase_pronto));

        UsuarioPerfilRequest usuarioPerfilRequest = new UsuarioPerfilRequest();
        usuarioPerfilRequest.primeiroNome = primeiroNome;
        usuarioPerfilRequest.ultimoNome = sobreNome;
        usuarioPerfilRequest.contactoAlternativo = telefoneAlternativo;
        usuarioPerfilRequest.provincia = valorCidadeItem;
        usuarioPerfilRequest.municipio = municipio;
        usuarioPerfilRequest.bairro = bairro;
        usuarioPerfilRequest.rua = rua;
        usuarioPerfilRequest.nCasa = nCasa;
        usuarioPerfilRequest.sexo = String.valueOf(valorGeneroItem.charAt(0));



        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.actualizarPerfil(usuarioPerfilRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    MetodosUsados.hideLoadingDialog();

                    mensagemSucesso(getString(R.string.dados_salvos_com_sucesso));





                } else {
                    MetodosUsados.hideLoadingDialog();

                    if (response.code()==401){
                        mensagemTokenExpirado();
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                MetodosUsados.hideLoadingDialog();
                if (!MetodosUsados.conexaoInternetTrafego(getContext(),TAG)){
                    MetodosUsados.mostrarMensagem(getContext(),R.string.msg_erro_internet);
                }else  if ("timeout".equals(t.getMessage())) {
                    MetodosUsados.mostrarMensagem(getContext(),R.string.msg_erro_internet_timeout);
                }else {
                    MetodosUsados.mostrarMensagem(getContext(),R.string.msg_erro);
                }


            }
        });
    }

    private void mensagemSucesso(String message) {



        dialog_txtConfirmSucesso.setText(message);
        dialog_btn_sucesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLayoutSuccess.cancel();

            }
        });
        dialogLayoutSuccess.show();
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


    private void verificarPermissaoFotoCameraGaleria() {
        Dexter.withContext(getContext())
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
        ImagePickerActivity.showImagePickerOptions(getContext(), new ImagePickerActivity.PickerOptionListener() {
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
        Intent intent = new Intent(getContext(), ImagePickerActivity.class);
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
        Intent intent = new Intent(getContext(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);


                    selectedImage = uri;
                    postPath = selectedImage.getPath();

                    Log.d("TAG", "Image Get Uri path: " + uri.getPath());
                    Log.d("TAG", "Image Get Uri toString(): " + uri.toString());

//                    // loading profile image from local cache
//                    loadProfile(uri.toString());

                    salvarFoto(postPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void salvarFoto(String postPath) {

        AlertDialog waitingDialog = new SpotsDialog.Builder().setContext(getContext()).build();
        waitingDialog.setMessage(getString(R.string.salvando_foto));
        waitingDialog.setCancelable(false);
        waitingDialog.show();

        File file = new File(postPath);

//        String filename  = file.getName();
        String filename  = System.currentTimeMillis() + ".jpg";
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"),file);
        MultipartBody.Part imagem  = MultipartBody.Part.createFormData("FotoCapa",filename,requestFile);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> enviarFoto = apiInterface.actualizarFotoPerfil(imagem);

        enviarFoto.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    // loading profile image from local cache
                    loadProfile(selectedImage.toString());
                    waitingDialog.cancel();

                    mensagemSucesso(getString(R.string.foto_atualizada_com_sucesso));




                } else {
                    waitingDialog.cancel();
                    if (response.code()==401){
                        mensagemTokenExpirado();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                waitingDialog.cancel();
                if (!MetodosUsados.conexaoInternetTrafego(getContext(),TAG)) {
                    MetodosUsados.mostrarMensagem(getContext(), R.string.msg_erro_internet);
                } else if ("timeout".equals(t.getMessage())) {
                    MetodosUsados.mostrarMensagem(getContext(), R.string.msg_erro_internet_timeout);
                } else {
                    MetodosUsados.mostrarMensagem(getContext(), R.string.msg_erro);
                }
                Log.i(TAG, "onFailure" + t.getMessage());
            }
        });

    }

    private void loadProfile(String url) {
        Log.d(TAG, "Image cache path: " + url);

        txtUserNameInitial.setVisibility(View.GONE);
        Picasso.with(getContext()).load(url).into(imageView);

    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(getContext(), R.color.login_register_text_color));
        if (parent.getId() == R.id.editCidadeSpiner) {
            valorCidadeItem = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    @Override
    public void onResume() {
        MetodosUsados.spotsDialog(getContext());

        // Clearing older images from cache directory
        // don't call this line if you want to choose multiple images in the same activity
        // call this once the bitmap(s) usage is over
        ImagePickerActivity.clearCache(getContext());
        super.onResume();
    }

    @Override
    public void onDestroy() {
        MetodosUsados.hideLoadingDialog();
        dialogLayoutConfirmarProcesso.cancel();
        dialogLayoutSuccess.cancel();

        super.onDestroy();
    }
}