package ao.co.proitconsulting.xpress.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.adapters.DefinicoesSobreNosAdapter;
import ao.co.proitconsulting.xpress.adapters.RecyclerViewOnItemClickListener;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.modelos.SobreNos;

public class ConfiguracoesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewSobre;
    private LinearLayoutManager layoutManager;
    private SobreNos sobreNos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //showBackground in status bar
        MetodosUsados.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.white));
        setContentView(R.layout.activity_configuracoes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(getString(R.string.action_settings));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        layoutManager = new LinearLayoutManager(this);
        recyclerViewSobre =  findViewById(R.id.recyclerViewSobre);
        recyclerViewSobre.setHasFixedSize(true);
        recyclerViewSobre.setLayoutManager(layoutManager);

        DefinicoesSobreNosAdapter definicoesSobreNosAdapter = new DefinicoesSobreNosAdapter(this, Common.getSobreNosList());
        recyclerViewSobre.setAdapter(definicoesSobreNosAdapter);
        definicoesSobreNosAdapter.notifyDataSetChanged();

        definicoesSobreNosAdapter.setItemClickListener(new RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                sobreNos = Common.getSobreNosList().get(position);

                goToOptionSelected(sobreNos.getId());

            }
        });
    }

       private void goToOptionSelected(int id) {
        switch (id){
            case 1:
                Intent intent = new Intent(this,AlterarPalavraPasseActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case 2:
//                MetodosUsados.mostrarMensagem(this,getString(R.string.xpress_co_ao));
                break;
//            case 3:
//                MetodosUsados.mostrarMensagem(this,sobreNos.getDescription());
//                break;
            case 3:
//                MetodosUsados.mostrarMensagem(this,sobreNos.getTitle().concat(": "+sobreNos.getDescription()));
                break;
            case 4:
//                MetodosUsados.mostrarMensagem(this,sobreNos.getDescription());
                break;
            case 5:
                sendFeedback(this);
                break;
            case 6:
                MetodosUsados.shareTheApp(this);
                break;



        }
    }

    private void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;

            body = "\n\n-------------------------------------------------\nPor favor não remova essa informação\n SO do Dispositivo: Android \n Versão do SO do Dispositivo: " +
                    Build.VERSION.RELEASE + "\n Versão da Aplicação: " + body + "\n Marca do Dispositivo: " + Build.BRAND +
                    "\n Modelo do Dispositivo: " + Build.MODEL + "\n Fabricante do Dispositivo: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"proitdevelopers2@gmail.com"});
        /*intent.putExtra(Intent.EXTRA_SUBJECT, "Query from android app");*/
        intent.putExtra(Intent.EXTRA_SUBJECT, "Consulta do aplicativo Android");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.enviar_email_client)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}