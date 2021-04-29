package ao.co.proitconsulting.xpress.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import ao.co.proitconsulting.xpress.EventBus.EstabelecimentoClick;
import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.fragmentos.MenuCategoryFragment;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.localDB.AppDatabase;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;
import ao.co.proitconsulting.xpress.modelos.UsuarioPerfil;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {


    private AppBarConfiguration mAppBarConfiguration;
    private UsuarioPerfil usuarioPerfil;
//    private CircleImageView imgUserPhoto;
    private RoundedImageView imgUserPhoto;
    private TextView txtUserNameInitial, txtUserName, txtUserEmail;

    //DIALOG_LAYOUT_CONFIRMAR_PROCESSO
    private Dialog dialogLayoutConfirmarProcesso;
    private ImageView imgConfirm;
    private TextView txtConfirmTitle,txtConfirmMsg;
    private Button dialog_btn_deny_processo,dialog_btn_accept_processo;

    private Realm realm;
    private RealmResults<CartItemProdutos> cartItems;
    private RealmChangeListener<RealmResults<CartItemProdutos>> cartRealmChangeListener;
    int cart_count = 0;
    private NotificationBadge badge;
    private ImageView cart_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_menu_home, R.id.nav_estabelecimento, R.id.nav_menu_perfil, R.id.nav_editar_perfil)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



        View view = navigationView.getHeaderView(0);
        imgUserPhoto = view.findViewById(R.id.imgUserPhoto);
        txtUserNameInitial = view.findViewById(R.id.txtUserNameInitial);
        txtUserName = view.findViewById(R.id.txtUserName);
        txtUserEmail = view.findViewById(R.id.txtUserEmail);


        usuarioPerfil = AppPrefsSettings.getInstance().getUser();
        carregarDadosOffline(usuarioPerfil);


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

        realm = Realm.getDefaultInstance();
        cartItems = realm.where(CartItemProdutos.class).findAllAsync();

        cartRealmChangeListener = cartItems -> {

            if (cartItems != null && cartItems.size() > 0) {
                setCartInfoBar(cartItems);
            } else {
                cart_count = 0;
                invalidateOptionsMenu();
            }

            invalidateOptionsMenu();
        };

    }

    private void setCartInfoBar(RealmResults<CartItemProdutos> cartItems) {

        cart_count = cartItems.size();

    }



    private void carregarDadosOffline(UsuarioPerfil usuarioPerfil) {
        if (usuarioPerfil!=null){
            txtUserName.setText(usuarioPerfil.primeiroNome.concat(" "+usuarioPerfil.ultimoNome));
            txtUserEmail.setText(usuarioPerfil.email);
            if (usuarioPerfil.imagem.equals(getString(R.string.sem_imagem)) || usuarioPerfil.imagem == null){
                if (usuarioPerfil.primeiroNome != null && usuarioPerfil.ultimoNome != null){
                    String userNameInitial = String.valueOf(usuarioPerfil.primeiroNome.charAt(0));
                    String userLastNameInitial = String.valueOf(usuarioPerfil.ultimoNome.charAt(0));
                    txtUserNameInitial.setText(userNameInitial.toUpperCase().concat(userLastNameInitial.toUpperCase()));
                    txtUserNameInitial.setVisibility(View.VISIBLE);

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

        AppDatabase.clearData();
        AppPrefsSettings.getInstance().clearAppPrefs();
        Intent intent = new Intent(this, SplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
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
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<UsuarioPerfil>> usuarioCall = apiInterface.getMeuPerfil();
        usuarioCall.enqueue(new Callback<List<UsuarioPerfil>>() {
            @Override
            public void onResponse(@NonNull Call<List<UsuarioPerfil>> call, @NonNull Response<List<UsuarioPerfil>> response) {

                if (response.isSuccessful()) {
                    if (response.body()!=null){
                        usuarioPerfil = response.body().get(0);

                        AppPrefsSettings.getInstance().saveUser(usuarioPerfil);

                        carregarDadosOffline(usuarioPerfil);

                    }

                } else {

                    if (response.code()==401){
                        mensagemTokenExpirado();
                    }

                }





            }

            @Override
            public void onFailure(@NonNull Call<List<UsuarioPerfil>> call, @NonNull Throwable t) {

            }
        });
    }

    private void mensagemTokenExpirado() {
        imgConfirm.setImageResource(R.drawable.xpress_logo);
        txtConfirmTitle.setText(getString(R.string.a_sessao_expirou));
        txtConfirmMsg.setText(getString(R.string.inicie_outra_vez_a_sessao));

        dialog_btn_accept_processo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLayoutConfirmarProcesso.cancel();
                logOutTemp();
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

    private void logOutTemp() {

        AppPrefsSettings.getInstance().deleteToken();
        Intent intent = new Intent(this, SplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEstabelecimentoItemClick(EstabelecimentoClick event){
        if (event.isSuccess()){
            Toast.makeText(this, "Click to: "+event.getEstabelecimento().nomeEstabelecimento, Toast.LENGTH_SHORT).show();
//            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//            navController.navigate(R.id.nav_revista_detail);
        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_options; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_options, menu);
        View view = menu.findItem(R.id.action_cart).getActionView();
        badge = (NotificationBadge)view.findViewById(R.id.badge);
        cart_icon = (ImageView) view.findViewById(R.id.cart_icon);
        cart_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this,ShoppingCartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            Intent intent = new Intent(this, ShopCartActivity.class);
                startActivity(intent);
            }
        });
        updateCartCount();

//        MenuItem menuItem = menu.findItem(R.id.action_cart);
//        menuItem.setIcon(AddBadgeCartConverter.convertLayoutToImage(MenuActivity.this,cart_count,R.drawable.ic_baseline_shopping_cart_white_24));
        return true;
    }

    private void updateCartCount() {
        if (badge == null) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (cart_count == 0)
                    badge.setVisibility(View.INVISIBLE);
                else{
                    badge.setVisibility(View.VISIBLE);
                    badge.setText(String.valueOf(cart_count));
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


//        if (id == R.id.action_cart) {
//            Intent intent = new Intent(this,ShoppingCartActivity.class);
////            Intent intent = new Intent(this, ShopCartActivity.class);
//            startActivity(intent);
//
//            return true;
//        }

        if (id == R.id.action_logout) {
            mensagemLogOut();
            return true;
        }

        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this,ConfiguracoesActivity.class);
            startActivity(intent);
            return true;
        }




        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

        updateCartCount();

        if (cartItems != null) {
            cartItems.addChangeListener(cartRealmChangeListener);
        }

        usuarioPerfil = AppPrefsSettings.getInstance().getUser();
        carregarDadosOffline(usuarioPerfil);

//        checkNavigationViewSelection();
//        verificaoPerfil();
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        if (cartItems != null) {
            cartItems.addChangeListener(cartRealmChangeListener);
        }
        if (realm != null) {
            realm.close();
        }
        dialogLayoutConfirmarProcesso.cancel();

        if (!AppPrefsSettings.getInstance().getLoggedIn()){
            AppDatabase.clearData();
            AppPrefsSettings.getInstance().clearAppPrefs();
        }

        super.onDestroy();


    }


}