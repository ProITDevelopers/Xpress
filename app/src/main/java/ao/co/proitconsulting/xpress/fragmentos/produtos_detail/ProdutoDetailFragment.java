package ao.co.proitconsulting.xpress.fragmentos.produtos_detail;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andremion.counterfab.CounterFab;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

import ao.co.proitconsulting.xpress.Callback.IRecyclerClickListener;
import ao.co.proitconsulting.xpress.Callback.ProdutoAddRemoveCartListener;
import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.activities.MenuActivity;
import ao.co.proitconsulting.xpress.activities.ShoppingCartActivity;
import ao.co.proitconsulting.xpress.adapters.ProdutoExtrasAdapter;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.helper.Utils;
import ao.co.proitconsulting.xpress.localDB.AppDatabase;
import ao.co.proitconsulting.xpress.modelos.CartItemProdutos;
import ao.co.proitconsulting.xpress.modelos.ProdutoListExtras;
import ao.co.proitconsulting.xpress.modelos.Produtos;
import dmax.dialog.SpotsDialog;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProdutoDetailFragment extends Fragment implements ProdutoAddRemoveCartListener {

    private static final String TAG = "TAG_Prod_DetailFragment";

    private ProdutoDetailViewModel produtoDetailViewModel;
    private View view;

    private CounterFab fabAddToCart;
    private ImageView productImg;
    private TextView txtProdutoNome,txtProdutoPrice,txtDescricao;
    private ImageView ic_remove,ic_add;
    private TextView product_count,product_count_info;
    private Button btn_comprar;

    private Realm realm;
    private RealmResults<CartItemProdutos> cartItems;
    private RealmChangeListener<RealmResults<CartItemProdutos>> cartRealmChangeListener;
    private int cart_count = 1;
    private Produtos produtos;
    private RealmResults<ProdutoListExtras> produtoListExtras;
    private CartItemProdutos cartItem;
    private ProdutoAddRemoveCartListener listener;
    private int position;

    private ConstraintLayout product_detail_root;


    private TextView textViewTitle;
    private TextView txtExtras;
    private RecyclerView recyclerViewExtras;
    private ProdutoExtrasAdapter produtoExtrasAdapter;
//    private List<ProdutoListExtras> produtoListExtrasList = new ArrayList<>();
    String errorMessage;

    private AlertDialog waitingDialog;

    private CoordinatorLayout coordinatorLayout;
    private RelativeLayout errorLayout;
    private TextView btnTentarDeNovo;





    public ProdutoDetailFragment() {
        Log.d(TAG, "produtoDetailViewModel: "+"ProdutoDetailFragment()");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "produtoDetailViewModel: "+"onCreate()");
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "produtoDetailViewModel: "+"onCreateView()");
        produtoDetailViewModel =
                new ViewModelProvider(this).get(ProdutoDetailViewModel.class);
        view = inflater.inflate(R.layout.fragment_produto_detail, container, false);
        initViews();

        produtoDetailViewModel.getProdutoMutableLiveData().observe(this, new Observer<Produtos>() {
            @Override
            public void onChanged(Produtos produto) {
                Log.d(TAG, "produtoDetailViewModel: "+"Prod: "+produto.descricaoProdutoC+", id: "+produto.idProduto);

                ((AppCompatActivity)getActivity())
                        .getSupportActionBar()
                        .setTitle(produto.getEstabelecimento());


                if (produto.produtoListExtras!=null){
                    txtExtras.setVisibility(View.VISIBLE);
                    recyclerViewExtras.setVisibility(View.VISIBLE);
                }




                fillAllViews(produto);


            }
        });

        produtoDetailViewModel.getMessageError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                waitingDialog.dismiss();
                MetodosUsados.mostrarMensagem(getContext(),errorMessage);

            }
        });



        return view;
    }

    private void initViews() {

        Log.d(TAG, "produtoDetailViewModel: "+"_initViews()");

        waitingDialog = new SpotsDialog.Builder().setContext(getContext()).setTheme(R.style.CustomSpotsDialog).build();
        waitingDialog.setMessage("Por favor aguarde...");
        waitingDialog.setCancelable(false);
        waitingDialog.show();

        listener = this;
        position = Common.selectedProdutoPosition;

        product_detail_root = view.findViewById(R.id.product_detail_root);
        textViewTitle = view.findViewById(R.id.textViewTitle);
        productImg = view.findViewById(R.id.productImg);
        txtProdutoNome = view.findViewById(R.id.txtProdutoNome);
        txtProdutoPrice = view.findViewById(R.id.txtProdutoPrice);
        txtDescricao = view.findViewById(R.id.txtDescricao);

        txtExtras = view.findViewById(R.id.txtExtras);
        recyclerViewExtras = view.findViewById(R.id.recyclerViewExtras);
        txtExtras.setVisibility(View.GONE);
        recyclerViewExtras.setVisibility(View.GONE);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewExtras.setLayoutManager(mLayoutManager);
        recyclerViewExtras.setItemAnimator(new DefaultItemAnimator());
        recyclerViewExtras.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));



        ic_remove = view.findViewById(R.id.ic_remove);
        product_count = view.findViewById(R.id.product_count);
        product_count_info = view.findViewById(R.id.product_count_info);
        ic_add = view.findViewById(R.id.ic_add);

        btn_comprar = view.findViewById(R.id.btn_comprar);
        fabAddToCart = view.findViewById(R.id.fabAddToCart);




        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        errorLayout = view.findViewById(R.id.erroLayout);
        btnTentarDeNovo = view.findViewById(R.id.btn);
        coordinatorLayout.setVisibility(View.INVISIBLE);

        CounterFab floatingActionButton = ((MenuActivity) getActivity()).getFloatingActionButton();
        if (floatingActionButton != null) {
            floatingActionButton.hide();
        }

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
                carregarProductsListExtras(Common.selectedProduto);
            }
        });
    }

    private void verifConecxaoProdutoExtras(Produtos produto) {
        if (getContext()!=null){
            ConnectivityManager conMgr =  (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
                    mostarMsnErro();
                } else {
                    carregarProductsListExtras(produto);
                }
            }
        }

    }

    private void carregarProductsListExtras(Produtos produto) {
        waitingDialog.show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<ProdutoListExtras>> rv = apiInterface.getProdutosExtras(produto.idProduto);
        rv.enqueue(new Callback<List<ProdutoListExtras>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProdutoListExtras>> call, @NonNull Response<List<ProdutoListExtras>> response) {

//                if (produtoListExtrasList != null)
//                    produtoListExtrasList.clear();

//                Common.selectedProduto.produtoListExtras = new RealmList<>();

                if (response.isSuccessful()) {

                    if (response.body()!=null && response.body().size()>0){
//                        Common.selectedProduto.produtoListExtras.addAll(response.body());
//                        AppDatabase.updateSingleProduct(Common.selectedProduto);

//                        produtoListExtrasList.addAll(new ArrayList<>(response.body()));
//                        txtExtras.setVisibility(View.VISIBLE);
//                        recyclerViewExtras.setVisibility(View.VISIBLE);

//                        MutableLiveData<Produtos> mutableLiveDataProduto = new MutableLiveData<>();
//                        produto.produtoListExtras = new RealmList<>();
//                        produto.produtoListExtras.addAll(produtoListExtrasList);
//                        mutableLiveDataProduto.setValue(produto);
//                        produtoDetailViewModel.setMutableLiveDataProduto(mutableLiveDataProduto);
//
//                        AppDatabase.updateSingleProduct(produto);

                        for (ProdutoListExtras produtoListExtras: response.body()) {
                            Log.d(TAG, "onResponseProductExtra: "+"Prod: "+produto.descricaoProdutoC+", id: "+produto.idProduto+", Extra: "+produtoListExtras.produtoextras.descricao+" - "+produtoListExtras.produtoextras.preco);
                        }

                        waitingDialog.dismiss();


                    }else {

                        MetodosUsados.mostrarMensagem(getContext(),"Sem Extras.");
                        waitingDialog.dismiss();

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

                    Log.d(TAG, "onResponseProductExtraError: "+errorMessage+", ResponseCode: "+response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProdutoListExtras>> call, @NonNull Throwable t) {

                Log.d(TAG, "onResponseProductFailed: "+t.getMessage());
                waitingDialog.dismiss();

            }
        });
    }




    private void fillAllViews(Produtos produtoSelected) {
        Log.d(TAG, "produtoDetailViewModel: "+"fillAllViews(Produtos produtoSelected)");
        realm = Realm.getDefaultInstance();
        cartItems = realm.where(CartItemProdutos.class).findAllAsync();
        waitingDialog.dismiss();
        coordinatorLayout.setVisibility(View.VISIBLE);
//        produtos = realm.where(Produtos.class).equalTo("idProduto", produtoSelected.getIdProduto()).findFirst();
//        produtoListExtras = realm.where(ProdutoListExtras.class).equalTo("idProduto", produtoSelected.getIdProduto()).findAll();

        cartItem = realm.where(CartItemProdutos.class).equalTo("produtos.idProduto", produtoSelected.getIdProduto()).findFirst();



        if (produtoSelected.produtoListExtras!=null){
            produtoExtrasAdapter = new ProdutoExtrasAdapter(getContext(),produtoSelected.produtoListExtras);
            recyclerViewExtras.setAdapter(produtoExtrasAdapter);
            produtoExtrasAdapter.notifyDataSetChanged();
            produtoExtrasAdapter.setItemClickListener(new IRecyclerClickListener() {
                @Override
                public void onItemClickListener(View view, int position) {
                    ProdutoListExtras produtoListExtras = produtoSelected.produtoListExtras.get(position);
                    Toast.makeText(getContext(), "Extra: "+produtoListExtras.produtoextras.descricao+"\n"+
                                    "Preço: "+produtoListExtras.produtoextras.preco,
                            Toast.LENGTH_SHORT).show();

                    if (view.findViewById(R.id.imgExtraSelected).getVisibility() == View.INVISIBLE){
                        if (Common.selectedProduto.getUserSelectedAddon() == null)
                            Common.selectedProduto.setUserSelectedAddon(new RealmList<>());
                        Common.selectedProduto.getUserSelectedAddon().add(produtoListExtras);

                        view.findViewById(R.id.imgExtraSelected).setVisibility(View.VISIBLE);
                    }else {

                        view.findViewById(R.id.imgExtraSelected).setVisibility(View.INVISIBLE);
                        Common.selectedProduto.getUserSelectedAddon().remove(produtoListExtras);

                    }
                    calculateTotalPrice();
                }
            });

        }

        Picasso.with(getContext()).load(produtoSelected.imagemProduto)
                .fit().centerCrop()
                .placeholder(R.drawable.store_placeholder).into(productImg);

        textViewTitle.setText(produtoSelected.getDescricaoProdutoC());
        txtProdutoNome.setText(produtoSelected.getDescricaoProdutoC());
        txtDescricao.setText(produtoSelected.getDescricaoProduto());
//        String preco = String.valueOf(produtoSelected.getPrecoUnid());
//        txtProdutoPrice.setText(getString(R.string.price_with_currency, Float.parseFloat(preco)).concat(" AKZ"));

        double displayPrice = Double.parseDouble(String.valueOf(produtoSelected.getPrecoUnid()));
        txtProdutoPrice.setText(new StringBuilder("").append(Utils.formatPrice(displayPrice)).append(" AKZ").toString());

        if (cartItem != null){
            btn_comprar.setText("Finalizar Compra");
            cart_count = cartItem.quantity;
            if (cartItem.quantity == 1){
                product_count.setText(String.valueOf(cart_count));
                product_count_info.setText(" Item");
            }

            if (cartItem.quantity > 1){
                product_count.setText(String.valueOf(cart_count));
                product_count_info.setText(" Items");
            }

            product_count.setVisibility(View.VISIBLE);
            product_count_info.setVisibility(View.VISIBLE);
        }else{
            btn_comprar.setText("Adicionar ao Carrinho");
            cart_count = 1;
            product_count.setText(String.valueOf(cart_count));
            product_count_info.setText(" Item");
        }


        ic_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                listener.onProductRemovedFromCart(position,produtoSelected);
                cart_count-=1;

                if (cart_count <= 1){
                    cart_count = 1;
                    product_count.setText(String.valueOf(cart_count));
                    product_count_info.setText(" Item");

                }else{
                    product_count.setText(String.valueOf(cart_count));
                    product_count_info.setText(" Items");
                }


            }
        });

        ic_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cart_count+=1;
                if (cart_count > 1 ){
                    if (cart_count<=produtoSelected.emStock){
                        product_count.setText(String.valueOf(cart_count));
                        product_count_info.setText(" Items");
                    }else{
                        Toast.makeText(getContext(), "Atingiu o limite de items!", Toast.LENGTH_SHORT).show();
                        cart_count = produtoSelected.emStock;
                    }

                }




            }
        });

        fabAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                listener.onProdutoAddedCart(position,produtoSelected,cart_count);
            }
        });



        btn_comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btn_comprar.getText().equals("Adicionar ao Carrinho")){
                    btn_comprar.setText("Finalizar Compra");
                    listener.onProdutoAddedCart(position,produtoSelected,cart_count);
                }else{
                    listener.onProdutoAddedCart(position,produtoSelected,cart_count);
                    Intent intent = new Intent(getContext(), ShoppingCartActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }






            }
        });



//        toolbarFrag.setTitle(produtos.getDescricaoProdutoC());

        if (produtoExtrasAdapter!=null)
            produtoExtrasAdapter.notifyDataSetChanged();




        cartRealmChangeListener = cartItems -> {
            Log.d(TAG, "produtoDetailViewModel: "+"cartRealmChangeListener: "+"Something changed");
            checkIfCartAsItems(produtoSelected);
        };


    }

    private void calculateTotalPrice() {

        double totalPrice = Double.parseDouble(String.valueOf(Common.selectedProduto.precoUnid)), displayPrice=0.0;

        //Addon
        if (Common.selectedProduto.getUserSelectedAddon() != null && Common.selectedProduto.getUserSelectedAddon().size() > 0)
            for (ProdutoListExtras addonModel: Common.selectedProduto.getUserSelectedAddon())
                totalPrice+=Double.parseDouble(String.valueOf(addonModel.produtoextras.preco));


//           if (cartItem!=null){
//               displayPrice = totalPrice * (cartItem.quantity);
//           }else{
//               displayPrice = totalPrice * (1);
//           }

//        displayPrice = totalPrice * (Integer.parseInt(number_button.getNumber()));
        displayPrice = totalPrice * (1);
        displayPrice = Math.round(displayPrice*100.0/100.0);

        txtProdutoPrice.setText(new StringBuilder("").append(Utils.formatPrice(displayPrice)).append(" AKZ").toString());
    }


    private void checkIfCartAsItems(Produtos produtoSelected){




        cartItem = realm.where(CartItemProdutos.class).equalTo("produtos.idProduto", produtoSelected.getIdProduto()).findFirst();
        if (cartItem != null) {
            btn_comprar.setText("Finalizar Compra");
             cart_count = cartItem.quantity;

            if (cartItem.quantity == 1){
                product_count.setText(String.valueOf(cartItem.quantity));
                product_count_info.setText(" Item");
            }

            if (cartItem.quantity > 1){
                product_count.setText(String.valueOf(cartItem.quantity));
                product_count_info.setText(" Items");
            }

            product_count.setVisibility(View.VISIBLE);
            product_count_info.setVisibility(View.VISIBLE);



        } else {
            btn_comprar.setText("Adicionar ao Carrinho");
            cart_count = 1;
            product_count.setText(String.valueOf(cart_count));
            product_count_info.setText(" Item");


        }

    }


    @Override
    public void onProdutoAddedCart(int index, Produtos product,int quantidadeSelected) {
        AppDatabase.addItemToCart(getContext(),product,cart_count);

        SpannableString message = new SpannableString(product.getDescricaoProdutoC());
        message.setSpan(new ForegroundColorSpan(getActivity().getResources().getColor(R.color.xpress_green)),
                0, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        Snackbar.make(view, message+" adicionado ao Carrinho!"+" Qtd: "+cart_count, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();


    }



    @Override
    public void onResume() {
        Log.d(TAG, "produtoDetailViewModel: "+"onResume()");

        if (cartItems != null) {
            cartItems.addChangeListener(cartRealmChangeListener);
        }

//        checkIfCartAsItems(Common.selectedProduto);

        super.onResume();
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
