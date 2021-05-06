package ao.co.proitconsulting.xpress.fragmentos.produtos_estab;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.activities.ProdutoDetailsActivity;
import ao.co.proitconsulting.xpress.activities.ProdutosActivity;
import ao.co.proitconsulting.xpress.adapters.ProdutosViewAdapter;
import ao.co.proitconsulting.xpress.adapters.RecyclerViewOnItemClickListener;
import ao.co.proitconsulting.xpress.adapters.menuBanner.MenuBannerAdapter;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.fragmentos.estab.CategoryEstabViewModel;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.localDB.AppDatabase;
import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;
import ao.co.proitconsulting.xpress.modelos.Estabelecimento;
import ao.co.proitconsulting.xpress.modelos.Produtos;
import ao.co.proitconsulting.xpress.modelos.TopSlideImages;
import dmax.dialog.SpotsDialog;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProdutosEstabFragment extends Fragment implements ProdutosViewAdapter.ProductsAdapterListener {

    private static final String TAG = "TAG_ProdutosFragment";

    private ProdutosEstabViewModel produtosEstabViewModel;
    private View view;

    private AlertDialog waitingDialog;
    private LoopingViewPager loopingViewPager;
    private List<Produtos> produtosList = new ArrayList<>();
    private ProdutosViewAdapter itemAdapter;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView recyclewProdutos;

    @SerializedName("estabelecimentoID")
    public int estabelecimentoID;




    private Realm realm;
    private RealmResults<CartItemProdutos> cartItems;
    private RealmChangeListener<RealmResults<CartItemProdutos>> cartRealmChangeListener;


    public ProdutosEstabFragment() {}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        produtosEstabViewModel =
                new ViewModelProvider(this).get(ProdutosEstabViewModel.class);

        view = inflater.inflate(R.layout.fragment_produtos_estab, container, false);
        initViews();

        produtosEstabViewModel.getTopImagesMutableLiveData().observe(this, new Observer<List<TopSlideImages>>() {
            @Override
            public void onChanged(List<TopSlideImages> topSlideImages) {
                MenuBannerAdapter menuBannerAdapter = new MenuBannerAdapter(getContext(),topSlideImages,true);
                loopingViewPager.setAdapter(menuBannerAdapter);
            }
        });

        produtosEstabViewModel.getEstabelecimentoMutableLiveData().observe(this, new Observer<Estabelecimento>() {
            @Override
            public void onChanged(Estabelecimento estabelecimento) {
                ((AppCompatActivity)getActivity())
                        .getSupportActionBar()
                        .setTitle(estabelecimento.nomeEstabelecimento);
                estabelecimentoID = estabelecimento.estabelecimentoID;
            }
        });



        cartRealmChangeListener = cartItems -> {

            if (cartItems != null && cartItems.size() > 0) {
                setCartInfoBar(cartItems);
            }

            if (itemAdapter!=null)
                itemAdapter.setCartItems(cartItems);

        };

        return view;
    }

    private void initViews() {
        realm = Realm.getDefaultInstance();
        cartItems = realm.where(CartItemProdutos.class).findAllAsync();

        loopingViewPager = view.findViewById(R.id.loopingViewPager);
        recyclewProdutos = view.findViewById(R.id.recyclewProdutos);
        gridLayoutManager = new GridLayoutManager(getContext(), 1);

        waitingDialog = new SpotsDialog.Builder().setContext(getContext()).build();
        waitingDialog.setMessage("Por favor aguarde...");
        waitingDialog.setCancelable(false);
    }

    private void verifConecxaoProdutos() {
        if (getContext()!=null){
            ConnectivityManager conMgr =  (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
//                    mostarMsnErro();
                } else {
                    carregarProductsList();
                }
            }
        }

    }

    private void carregarProductsList() {
        waitingDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Produtos>> rv = apiInterface.getAllProdutosDoEstabelecimento(estabelecimentoID);
        rv.enqueue(new Callback<List<Produtos>>() {
            @Override
            public void onResponse(@NonNull Call<List<Produtos>> call, @NonNull Response<List<Produtos>> response) {

                if (response.isSuccessful()) {

                    if (response.body()!=null){

                        produtosList = response.body();

                        AppDatabase.saveProducts(produtosList);
                        setProdutosAdapters(produtosList);

                    } else {
                        Toast.makeText(getContext(), ""+response.message(), Toast.LENGTH_SHORT).show();
                    }
                    waitingDialog.dismiss();
                } else {

                    waitingDialog.dismiss();
//                    if (response.code()==401){
//                        mensagemTokenExpirado();
//                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Produtos>> call, @NonNull Throwable t) {
                waitingDialog.dismiss();
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


    private void setProdutosAdapters(List<Produtos> produtosList) {

        if (produtosList.size()>0){

//            itemAdapter = new ProdutosAdapter(this, produtosList,this, gridLayoutManager);
            itemAdapter = new ProdutosViewAdapter(getContext(), produtosList,this);
            recyclewProdutos.setAdapter(itemAdapter);
            recyclewProdutos.setLayoutManager(gridLayoutManager);

            itemAdapter.setItemClickListener(new RecyclerViewOnItemClickListener() {
                @Override
                public void onItemClickListener(int position) {
                    Produtos produto = produtosList.get(position);
                    Intent intent = new Intent(getContext(), ProdutoDetailsActivity.class);
                    intent.putExtra("toolbarTitle",produto.estabelecimento);
                    intent.putExtra("produtoId",produto.idProduto);
                    intent.putExtra("position",position);
                    startActivity(intent);

                }
            });


        }


    }

    private void setCartInfoBar(RealmResults<CartItemProdutos> cartItems) {
        int itemCount = 0;
        for (CartItemProdutos cartItem : cartItems) {
            itemCount += cartItem.quantity;
        }


    }



    @Override
    public void onProductAddedCart(int index, Produtos product) {
        AppDatabase.addItemToCart(getContext(),product);
        if (cartItems != null) {
            itemAdapter.updateItem(index, cartItems);

        }
    }

    @Override
    public void onProductRemovedFromCart(int index, Produtos product) {
        AppDatabase.removeCartItem(product);
        if (cartItems != null) {
            itemAdapter.updateItem(index, cartItems);

        }

    }

    @Override
    public void onResume() {
        verifConecxaoProdutos();
        super.onResume();
        loopingViewPager.resumeAutoScroll();
        if (cartItems != null) {
            cartItems.addChangeListener(cartRealmChangeListener);
        }
    }

    @Override
    public void onPause() {
        loopingViewPager.pauseAutoScroll();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().removeAllStickyEvents();
        if (cartItems != null) {
            cartItems.addChangeListener(cartRealmChangeListener);
        }
        if (realm != null) {
            realm.close();
        }
        super.onDestroyView();
    }
}
