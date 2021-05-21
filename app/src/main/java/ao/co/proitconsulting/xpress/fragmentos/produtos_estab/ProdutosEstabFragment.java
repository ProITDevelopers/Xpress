package ao.co.proitconsulting.xpress.fragmentos.produtos_estab;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.activities.ProdutoDetailsActivity;
import ao.co.proitconsulting.xpress.adapters.ProdutosViewAdapter;
import ao.co.proitconsulting.xpress.adapters.RecyclerViewOnItemClickListener;
import ao.co.proitconsulting.xpress.adapters.topSlide.TopImageSlideAdapter;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.localDB.AppDatabase;
import ao.co.proitconsulting.xpress.modelos.Estabelecimento;
import ao.co.proitconsulting.xpress.modelos.Produtos;
import ao.co.proitconsulting.xpress.modelos.TopSlideImages;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//public class ProdutosEstabFragment extends Fragment implements ProdutosViewAdapter.ProductsAdapterListener {
public class ProdutosEstabFragment extends Fragment  {

    private static final String TAG = "TAG_ProdutosFragment";

    private ProdutosEstabViewModel produtosEstabViewModel;
    private View view;

    private AlertDialog waitingDialog;
//    private LoopingViewPager loopingViewPager;
    private List<Produtos> produtosList = new ArrayList<>();
    private ProdutosViewAdapter itemAdapter;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView recyclewProdutos;

    @SerializedName("estabelecimentoID")
    public int estabelecimentoID;

    private ConstraintLayout coordinatorLayout;
    private RelativeLayout errorLayout;
    private TextView btnTentarDeNovo;

    private String errorMessage;






//    private Realm realm;
//    private RealmResults<CartItemProdutos> cartItems;
//    private RealmChangeListener<RealmResults<CartItemProdutos>> cartRealmChangeListener;


    public ProdutosEstabFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        produtosEstabViewModel =
                new ViewModelProvider(this).get(ProdutosEstabViewModel.class);

        view = inflater.inflate(R.layout.fragment_produtos_estab, container, false);
        initViews();

//        produtosEstabViewModel.getTopImagesMutableLiveData().observe(this, new Observer<List<TopSlideImages>>() {
//            @Override
//            public void onChanged(List<TopSlideImages> topSlideImages) {
//                TopImageSlideAdapter topImageSlideAdapter = new TopImageSlideAdapter(getContext(),topSlideImages,true);
//                loopingViewPager.setAdapter(topImageSlideAdapter);
//            }
//        });

        produtosEstabViewModel.getEstabelecimentoMutableLiveData().observe(this, new Observer<Estabelecimento>() {
            @Override
            public void onChanged(Estabelecimento estabelecimento) {
                ((AppCompatActivity)getActivity())
                        .getSupportActionBar()
                        .setTitle(estabelecimento.nomeEstabelecimento);
                estabelecimentoID = estabelecimento.estabelecimentoID;


            }
        });



//        cartRealmChangeListener = cartItems -> {
//
//            if (cartItems != null && cartItems.size() > 0) {
//                setCartInfoBar(cartItems);
//            }
//
//            if (itemAdapter!=null)
//                itemAdapter.setCartItems(cartItems);
//
//        };

        return view;
    }

    private void initViews() {
        waitingDialog = new SpotsDialog.Builder().setContext(getContext()).build();
        waitingDialog.setMessage("Por favor aguarde...");
        waitingDialog.setCancelable(false);

//        realm = Realm.getDefaultInstance();
//        cartItems = realm.where(CartItemProdutos.class).findAllAsync();

//        loopingViewPager = view.findViewById(R.id.loopingViewPager);
        recyclewProdutos = view.findViewById(R.id.recyclewProdutos);
        gridLayoutManager = new GridLayoutManager(getContext(), 1);


        coordinatorLayout = view.findViewById(R.id.constraintLayout);
        errorLayout = view.findViewById(R.id.erroLayout);
        btnTentarDeNovo = view.findViewById(R.id.btn);
    }

    private void mostarMsnErro(){

        if (errorLayout.getVisibility() == View.GONE){
            errorLayout.setVisibility(View.VISIBLE);

            coordinatorLayout.setVisibility(View.GONE);


        }

        btnTentarDeNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coordinatorLayout.setVisibility(View.VISIBLE);

                errorLayout.setVisibility(View.GONE);
                verifConecxaoProdutos();
            }
        });
    }

    private void verifConecxaoProdutos() {
        if (getContext()!=null){
            ConnectivityManager conMgr =  (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
                    mostarMsnErro();
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

                    if (response.body()!=null && response.body().size()>0){

                        produtosList = response.body();

                        AppDatabase.saveProducts(produtosList);
                        setProdutosAdapters(produtosList);

                        for (Produtos produtos: response.body()) {
                            Log.d(TAG, "onResponseProduct: "+produtos.descricaoProdutoC+" - "+produtos.estabelecimento);
                        }


                        Log.d(TAG, "onResponseProduct2: "+response.body());
                    }else {
                        waitingDialog.dismiss();
                        MetodosUsados.mostrarMensagem(getContext(),"Sem produtos.");
                        Log.d(TAG, "onResponseProduct3: "+response.body());
                    }

                } else {

                    waitingDialog.dismiss();
//                    if (response.code()==401){
//                        mensagemTokenExpirado();
//                    }
                    try {
                        errorMessage = response.errorBody().string();
                        Log.d(TAG, "onResponseProductError: "+errorMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.d(TAG, "onResponseProductError: "+errorMessage+", ResponseCode: "+response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Produtos>> call, @NonNull Throwable t) {
                waitingDialog.dismiss();
                Log.d(TAG, "onResponseProductFailed: "+t.getMessage());
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

        waitingDialog.dismiss();
        if (produtosList.size()>0){

//            itemAdapter = new ProdutosAdapter(this, produtosList,this, gridLayoutManager);
//            itemAdapter = new ProdutosViewAdapter(getContext(), produtosList,this);
            itemAdapter = new ProdutosViewAdapter(getContext(), produtosList);
            recyclewProdutos.setAdapter(itemAdapter);
            recyclewProdutos.setLayoutManager(gridLayoutManager);

//            itemAdapter.setItemClickListener(new RecyclerViewOnItemClickListener() {
//                @Override
//                public void onItemClickListener(int position) {
//                    Produtos produto = produtosList.get(position);
//                    Intent intent = new Intent(getContext(), ProdutoDetailsActivity.class);
//                    intent.putExtra("toolbarTitle",produto.estabelecimento);
//                    intent.putExtra("produtoId",produto.idProduto);
//                    intent.putExtra("position",position);
//                    startActivity(intent);
//
//                }
//            });


        }


    }

//    private void setCartInfoBar(RealmResults<CartItemProdutos> cartItems) {
//        int itemCount = 0;
//        for (CartItemProdutos cartItem : cartItems) {
//            itemCount += cartItem.quantity;
//        }
//
//
//    }
//
//
//
//    @Override
//    public void onProductAddedCart(int index, Produtos product) {
//        AppDatabase.addItemToCart(getContext(),product);
//        if (cartItems != null) {
//            itemAdapter.updateItem(index, cartItems);
//
//        }
//    }
//
//    @Override
//    public void onProductRemovedFromCart(int index, Produtos product) {
//        AppDatabase.removeCartItem(product);
//        if (cartItems != null) {
//            itemAdapter.updateItem(index, cartItems);
//
//        }
//
//    }

    @Override
    public void onResume() {
        if (errorLayout.getVisibility() == View.VISIBLE){
            coordinatorLayout.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
        }
        verifConecxaoProdutos();
        super.onResume();
//        loopingViewPager.resumeAutoScroll();
//        if (cartItems != null) {
//            cartItems.addChangeListener(cartRealmChangeListener);
//        }

    }

    @Override
    public void onPause() {
//        loopingViewPager.pauseAutoScroll();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().removeAllStickyEvents();
//        if (cartItems != null) {
//            cartItems.addChangeListener(cartRealmChangeListener);
//        }
//        if (realm != null) {
//            realm.close();
//        }
        super.onDestroyView();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Produto, Categoria");
//        searchView.setQueryHint(getString(R.string.pesquisar));

        SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete)searchView.findViewById(R.id.search_src_text);
//        theTextArea.setHintTextColor(ContextCompat.getColor(getContext(), R.color.xpress_green));
        theTextArea.setTextColor(ContextCompat.getColor(getContext(), R.color.perfil_text_color));
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (itemAdapter!=null)
                    itemAdapter.getFilter().filter(newText);



                return false;
            }
        });
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_options_search, menu);


        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_filtros:
                Toast.makeText(getContext(), "Filtros", Toast.LENGTH_SHORT).show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
