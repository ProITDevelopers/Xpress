package ao.co.proitconsulting.xpress.fragmentos.home;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.asksira.loopingviewpager.indicator.CustomShapePagerIndicator;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.adapters.homeEstab.MainRecyclerAdapter;
import ao.co.proitconsulting.xpress.adapters.topSlide.TopImageSlideAdapter;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.modelos.CategoriaEstabelecimento;
import ao.co.proitconsulting.xpress.modelos.Estabelecimento;
import ao.co.proitconsulting.xpress.modelos.TopSlideImages;
import ao.co.proitconsulting.xpress.modelos.MenuCategory;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "TAG_HomeFragment";
    private HomeViewModel homeViewModel;
    private View view;

    private LoopingViewPager loopingViewPager;
    private TabLayout tabLayout;
    private RecyclerView recyclerViewMenu;

    private AlertDialog waitingDialog;


//    private List<MenuCategory> menuCategoryList = new ArrayList<>();
    private List<Estabelecimento> estabelecimentoList = new ArrayList<>();
    private List<CategoriaEstabelecimento> categoriaEstabelecimentoList = new ArrayList<>();

    private ConstraintLayout coordinatorLayout;
    private RelativeLayout errorLayout;
    private TextView btnTentarDeNovo;

    private String errorMessage;

    private MainRecyclerAdapter mainRecyclerAdapter;


    public HomeFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews();


        homeViewModel.getListMutableLiveData().observe(this, new Observer<List<TopSlideImages>>() {
            @Override
            public void onChanged(List<TopSlideImages> topSlideImages) {
                TopImageSlideAdapter topImageSlideAdapter = new TopImageSlideAdapter(getContext(),topSlideImages,true);
                loopingViewPager.setAdapter(topImageSlideAdapter);


            }
        });



        verifConecxaoCategoryEstabelecimento();

        return view;
    }

    private void initViews() {
        waitingDialog = new SpotsDialog.Builder().setContext(getContext()).build();
        waitingDialog.setMessage("Por favor aguarde...");
        waitingDialog.setCancelable(false);

        loopingViewPager = view.findViewById(R.id.loopingViewPager);
        recyclerViewMenu = view.findViewById(R.id.recyclerViewMenu);

        tabLayout = view.findViewById(R.id.tab_layout);

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
                verifConecxaoCategoryEstabelecimento();
            }
        });
    }

    private void verifConecxaoCategoryEstabelecimento() {

        if (getActivity()!=null) {
            ConnectivityManager conMgr =  (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
                    mostarMsnErro();
                } else {
//                    carregarListaMenuCategory();
                    carregarListaEstabelicimentos();
                }
            }
        }

    }

//    private void carregarListaMenuCategory() {
//        waitingDialog.show();
//
//        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        Call<List<MenuCategory>> rv = apiInterface.getMenuCategories();
//        rv.enqueue(new Callback<List<MenuCategory>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<MenuCategory>> call, @NonNull Response<List<MenuCategory>> response) {
//
//                if (response.isSuccessful()) {
//
//                    if (menuCategoryList!=null)
//                        menuCategoryList.clear();
//
//                    if (response.body()!=null){
//
//                        if (response.body().size()>0){
//                            for (MenuCategory menuCategory: response.body()) {
//                                if (menuCategory!=null){
//                                    menuCategoryList.add(menuCategory);
//                                    Log.d(TAG, "onResponseMenuCategory: "+menuCategory.getDescricao());
//                                }
//                            }
//
//                            carregarListaEstabelicimentos();
//
//
//                        }
//
//                    }
//
//                } else {
//
//                    waitingDialog.dismiss();
//                }
//
//
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<MenuCategory>> call, @NonNull Throwable t) {
//                waitingDialog.dismiss();
//                if (!MetodosUsados.conexaoInternetTrafego(getContext(),TAG)){
//                    MetodosUsados.mostrarMensagem(getContext(),R.string.msg_erro_internet);
//                }else  if ("timeout".equals(t.getMessage())) {
//                    MetodosUsados.mostrarMensagem(getContext(),R.string.msg_erro_internet_timeout);
//                }else {
//                    MetodosUsados.mostrarMensagem(getContext(),R.string.msg_erro);
//                }
//            }
//        });
//    }

    private void carregarListaEstabelicimentos() {

        waitingDialog.show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Estabelecimento>> rv = apiInterface.getAllEstabelecimentos();
        rv.enqueue(new Callback<List<Estabelecimento>>() {
            @Override
            public void onResponse(@NonNull Call<List<Estabelecimento>> call, @NonNull Response<List<Estabelecimento>> response) {

                if (response.isSuccessful()) {
                    waitingDialog.setMessage("Carregando...");
                    if (estabelecimentoList!=null)
                        estabelecimentoList.clear();

                    if (response.body()!=null && response.body().size()>0){


                        for (Estabelecimento estab: response.body()) {
                            if (estab!=null){
                                if (estab.estadoEstabelecimento!=null){
                                    estabelecimentoList.add(estab);

//                                    Log.d(TAG, "onResponseEstab: "+estab.nomeEstabelecimento+" - "+estab.tipoDeEstabelecimento.descricao);
                                }
                            }
                        }


                        getCategoriesFromEstabelecimento();



                    }else{
                        waitingDialog.dismiss();
                        MetodosUsados.mostrarMensagem(getContext(),"Sem estabelecimentos");
                    }

                } else {

//                    progressBar.setVisibility(View.GONE);
                    waitingDialog.dismiss();
                    try {
                        errorMessage = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.d(TAG, "onResponseEstabError: "+errorMessage+", ResponseCode: "+response.code());
                }


            }

            @Override
            public void onFailure(@NonNull Call<List<Estabelecimento>> call, @NonNull Throwable t) {
                waitingDialog.dismiss();
                Log.d(TAG, "onResponseEstabFailed: "+t.getMessage());
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

    private void getCategoriesFromEstabelecimento() {
        List<MenuCategory> menuCategoryList = new ArrayList<>();

        for (int i = 0; i <estabelecimentoList.size() ; i++) {
            Estabelecimento estab = estabelecimentoList.get(i);
            MenuCategory menuCategory = new MenuCategory();
            menuCategory.setIdTipo(estab.tipoDeEstabelecimento.idTipo);
            menuCategory.setDescricao(estab.tipoDeEstabelecimento.descricao);
            menuCategoryList.add(menuCategory);
        }

//        // Order the list by regist date.
//        Collections.sort(menuCategories, new MenuCategory());

//        List<MenuCategory> allEvents = new ArrayList<>(menuCategoryList);
        List<MenuCategory> noRepeat = new ArrayList<>();

        for (MenuCategory event : menuCategoryList) {
            boolean isFound = false;
            // check if the event name exists in noRepeat
            for (MenuCategory e : noRepeat) {
                if (e.getDescricao().equals(event.getDescricao()) || (e.equals(event))) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound) noRepeat.add(event);
        }


        menuCategoryList.clear();
        fillList(noRepeat);
    }

    private void fillList(List<MenuCategory> menuCategoryList) {

        if (categoriaEstabelecimentoList!=null)
            categoriaEstabelecimentoList.clear();

        for (int i = 0; i <menuCategoryList.size() ; i++) {

            List<Estabelecimento> newEstab = new ArrayList<>();
            for (Estabelecimento e : estabelecimentoList) {
                if (e.tipoDeEstabelecimento.idTipo == menuCategoryList.get(i).getIdTipo()){
                    if (!newEstab.contains(e)){
                        newEstab.add(e);
                    }
                }
            }
            categoriaEstabelecimentoList.add(new CategoriaEstabelecimento(menuCategoryList.get(i), newEstab));
        }

        menuCategoryList.clear();
        setUpAdapters();



    }

    private void setUpAdapters() {
        waitingDialog.dismiss();
        recyclerViewMenu.setHasFixedSize(true);
        recyclerViewMenu.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        mainRecyclerAdapter = new MainRecyclerAdapter(getContext(),categoriaEstabelecimentoList);
        recyclerViewMenu.setAdapter(mainRecyclerAdapter);
    }




    @Override
    public void onResume() {
        tabLayout.setupWithViewPager(loopingViewPager);
        super.onResume();
        loopingViewPager.resumeAutoScroll();
    }

    @Override
    public void onPause() {
        loopingViewPager.pauseAutoScroll();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_options_search, menu);
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchView.setQueryHint(getString(R.string.pesquisar));
//
//        SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete)searchView.findViewById(R.id.search_src_text);
//        theTextArea.setHintTextColor(ContextCompat.getColor(getContext(), R.color.xpress_green));
//        theTextArea.setTextColor(ContextCompat.getColor(getContext(), R.color.perfil_text_color));
//        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
//
//
//
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if (mainRecyclerAdapter!=null)
//                    mainRecyclerAdapter.getFilter().filter(newText);
//
//
//                return false;
//            }
//        });
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
