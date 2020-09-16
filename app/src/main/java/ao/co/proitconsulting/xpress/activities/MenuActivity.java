package ao.co.proitconsulting.xpress.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.fragmentos.EstabelecimentoFragment;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.localDB.AppDatabase;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;
import ao.co.proitconsulting.xpress.modelos.UsuarioPerfil;
import ao.co.proitconsulting.xpress.utilityClasses.AddBadgeCartConverter;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MenuActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = "TAG_MenuActivity";
    private static final String BACK_STACK_ROOT_TAG = "root_fragment";
    private Toolbar toolbar;
    private NavigationView navigationView;

    private CircleImageView imgUserPhoto;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);


        View view = navigationView.getHeaderView(0);
        imgUserPhoto = view.findViewById(R.id.imgUserPhoto);
        txtUserNameInitial = view.findViewById(R.id.txtUserNameInitial);
        txtUserName = view.findViewById(R.id.txtUserName);
        txtUserEmail = view.findViewById(R.id.txtUserEmail);



        Fragment fragment = new EstabelecimentoFragment();
        goToEstabelecimentoFragment(fragment);

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

    private void goToEstabelecimentoFragment(Fragment fragment) {
        if (getSupportActionBar()!=null)
            toolbar.setTitle(getString(R.string.txt_estabelecimentos));

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(BACK_STACK_ROOT_TAG);
        transaction.commit();
    }

    private void carregarDadosOffline(UsuarioPerfil usuarioPerfil) {
        if (usuarioPerfil!=null){
            txtUserName.setText(usuarioPerfil.primeiroNome.concat(" "+usuarioPerfil.ultimoNome));
            txtUserEmail.setText(usuarioPerfil.email);
            if (usuarioPerfil.imagem.equals(getString(R.string.sem_imagem))){
                if (!usuarioPerfil.primeiroNome.isEmpty()){
                    String userNameInitial = String.valueOf(usuarioPerfil.primeiroNome.charAt(0));
                    txtUserNameInitial.setText(userNameInitial.toUpperCase());
                    txtUserNameInitial.setVisibility(View.VISIBLE);

                }else {
                    String userNameInitial = String.valueOf(usuarioPerfil.email.charAt(0));
                    txtUserNameInitial.setText(userNameInitial.toUpperCase());
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


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int fragments = getSupportFragmentManager().getBackStackEntryCount();
            if (fragments == 1) {
                finish();
            } else if (getFragmentManager().getBackStackEntryCount() > 1) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }

        }



    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            MetodosUsados.mostrarMensagem(this,"nav_home");

        }
        else if (id == R.id.nav_menu_perfil) {
            MetodosUsados.mostrarMensagem(this,"nav_menu_perfil");

        }

        else if (id == R.id.nav_menu_pedidos) {

            MetodosUsados.mostrarMensagem(this,"nav_menu_encomendas");

        }

        else if (id == R.id.nav_menu_mapa) {
            MetodosUsados.mostrarMensagem(this,"nav_menu_mapa");
        }

//        else if (id == R.id.nav_menu_favoritos) {
//            if (getSupportActionBar()!=null)
//                toolbar.setTitle(getString(R.string.nav_menu_favoritos));
//            FavoritosFragment favoritosFragment = new FavoritosFragment();
//            gotoFragment(favoritosFragment);
//        }

        else if (id == R.id.nav_menu_share) {
            MetodosUsados.mostrarMensagem(this,"nav_menu_share");
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_options; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_options, menu);
        MenuItem menuItem = menu.findItem(R.id.action_cart);
        menuItem.setIcon(AddBadgeCartConverter.convertLayoutToImage(MenuActivity.this,cart_count,R.drawable.ic_baseline_shopping_cart_white_24));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

//        if (item.getItemId() == R.id.action_settings) {
//            MetodosUsados.mostrarMensagem(this,getString(R.string.action_settings));
//            return true;
//        }
//
//
        if (id == R.id.action_cart) {
            MetodosUsados.mostrarMensagem(this,getString(R.string.carrinho));

            return true;
        }

        if (id == R.id.action_logout) {
            mensagemLogOut();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

        checkNavigationViewSelection();
        UsuarioPerfil usuarioPerfil = AppPrefsSettings.getInstance().getUser();
        carregarDadosOffline(usuarioPerfil);

        if (cartItems != null) {
            cartItems.addChangeListener(cartRealmChangeListener);
        }
        super.onResume();
    }

    private void checkNavigationViewSelection() {
        if (getSupportActionBar()!=null){

            if (toolbar.getTitle().equals(getString(R.string.txt_estabelecimentos))){
                navigationView.setCheckedItem(R.id.nav_home);
            }

        }
        navigationView.getMenu().getItem(1).setCheckable(false);
        navigationView.getMenu().getItem(2).setCheckable(false);
        navigationView.getMenu().getItem(3).setCheckable(false);
        navigationView.getMenu().getItem(4).setCheckable(false);
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
        super.onDestroy();
    }


}