package ao.co.proitconsulting.xpress;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ao.co.proitconsulting.xpress.activities.SplashScreenActivity;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.UsuarioPerfil;

public class MainActivity extends AppCompatActivity {
    
    private TextView txtHelloWorld;
    private Button btnLogOut,btnClearToken;
    private UsuarioPerfil usuarioPerfil;

    //DIALOG_LAYOUT_CONFIRMAR_PROCESSO
    private Dialog dialogLayoutConfirmarProcesso;
    private ImageView imgConfirm;
    private TextView txtConfirmTitle,txtConfirmMsg;
    private Button dialog_btn_deny_processo,dialog_btn_accept_processo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuarioPerfil = AppPrefsSettings.getInstance().getUser();
        
        txtHelloWorld = findViewById(R.id.txtHelloWorld);
        btnLogOut = findViewById(R.id.btnLogOut);
        btnClearToken = findViewById(R.id.btnClearToken);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mensagemLogOut();
            }
        });

        btnClearToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppPrefsSettings.getInstance().deleteToken();
                Toast.makeText(MainActivity.this, "Token deleted!", Toast.LENGTH_SHORT).show();
            }
        });

        if (usuarioPerfil!=null){
            txtHelloWorld.setText(usuarioPerfil.primeiroNome.concat(" "+usuarioPerfil.ultimoNome)+"\n"+
            usuarioPerfil.email
            );
        }

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

    private void mensagemLogOut() {

        imgConfirm.setImageResource(R.drawable.xpress_logo);
        txtConfirmTitle.setText(R.string.terminar_sessao);
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

        Intent intent = new Intent(MainActivity.this, SplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        dialogLayoutConfirmarProcesso.cancel();
        super.onDestroy();
    }
}