package ao.co.proitconsulting.xpress.fragmentos.home;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asksira.loopingviewpager.LoopingViewPager;

import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.adapters.homeEstab.MainRecyclerAdapter;
import ao.co.proitconsulting.xpress.adapters.menuBanner.MenuBannerAdapter;
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
    private RecyclerView recyclerViewMenu;

    private AlertDialog waitingDialog;


    private List<MenuCategory> menuCategoryList = new ArrayList<>();
    private List<Estabelecimento> estabelecimentoList = new ArrayList<>();
    private List<CategoriaEstabelecimento> categoriaEstabelecimentoList = new ArrayList<>();


    public HomeFragment(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        view = inflater.inflate(R.layout.fragment_home, container, false);
        loopingViewPager = view.findViewById(R.id.loopingViewPager);
        recyclerViewMenu = view.findViewById(R.id.recyclerViewMenu);


        homeViewModel.getListMutableLiveData().observe(this, new Observer<List<TopSlideImages>>() {
            @Override
            public void onChanged(List<TopSlideImages> topSlideImages) {
                MenuBannerAdapter menuBannerAdapter = new MenuBannerAdapter(getContext(),topSlideImages,true);
                loopingViewPager.setAdapter(menuBannerAdapter);
            }
        });
        waitingDialog = new SpotsDialog.Builder().setContext(getContext()).build();
        waitingDialog.setMessage("Carregando...");
        waitingDialog.setCancelable(false);


        verifConecxaoMenuCategory();

        return view;
    }

    private void verifConecxaoMenuCategory() {

        if (getActivity()!=null) {
            ConnectivityManager conMgr =  (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
//                    mostarMsnErro();
                } else {
                    carregarListaMenuCategory();
                }
            }
        }

    }

    private void carregarListaMenuCategory() {
        waitingDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<MenuCategory>> rv = apiInterface.getMenuCategories();
        rv.enqueue(new Callback<List<MenuCategory>>() {
            @Override
            public void onResponse(@NonNull Call<List<MenuCategory>> call, @NonNull Response<List<MenuCategory>> response) {

                if (response.isSuccessful()) {

                    if (menuCategoryList!=null)
                        menuCategoryList.clear();

                    if (response.body()!=null){

                        if (response.body().size()>0){
                            for (MenuCategory menuCategory: response.body()) {
                                if (menuCategory!=null){
                                    menuCategoryList.add(menuCategory);
                                    Log.d(TAG, "onResponseMenuCategory: "+menuCategory.getDescricao());
                                }
                            }

                            carregarListaEstabelicimentos();


                        }

                    }

                } else {

                    waitingDialog.dismiss();
                }


            }

            @Override
            public void onFailure(@NonNull Call<List<MenuCategory>> call, @NonNull Throwable t) {
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

    private void carregarListaEstabelicimentos() {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Estabelecimento>> rv = apiInterface.getAllEstabelecimentos();
        rv.enqueue(new Callback<List<Estabelecimento>>() {
            @Override
            public void onResponse(@NonNull Call<List<Estabelecimento>> call, @NonNull Response<List<Estabelecimento>> response) {

                if (response.isSuccessful()) {

                    if (estabelecimentoList!=null)
                        estabelecimentoList.clear();

                    if (response.body()!=null && response.body().size()>0){


                        for (Estabelecimento estab: response.body()) {
                            if (estab!=null){
                                if (estab.estadoEstabelecimento!=null){
                                    estabelecimentoList.add(estab);

                                    Log.d(TAG, "onResponseEstab: "+estab.nomeEstabelecimento+", Categoria - "+estab.tipoDeEstabelecimento.descricao);
                                }
                            }
                        }


//                        progressBar.setVisibility(View.GONE);
//                        setAdapters(estabelecimentoList);

                        fillList();



                    }

                } else {

//                    progressBar.setVisibility(View.GONE);
                    waitingDialog.dismiss();
                }


            }

            @Override
            public void onFailure(@NonNull Call<List<Estabelecimento>> call, @NonNull Throwable t) {
//                progressBar.setVisibility(View.GONE);
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


    private void fillList() {

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

        setUpAdapters();



    }

    private void setUpAdapters() {
        waitingDialog.dismiss();
        recyclerViewMenu.setHasFixedSize(true);
        recyclerViewMenu.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        MainRecyclerAdapter mainRecyclerAdapter = new MainRecyclerAdapter(getContext(),categoriaEstabelecimentoList);
        recyclerViewMenu.setAdapter(mainRecyclerAdapter);
    }




    @Override
    public void onResume() {
        super.onResume();
        loopingViewPager.resumeAutoScroll();
    }

    @Override
    public void onPause() {
        loopingViewPager.pauseAutoScroll();
        super.onPause();
    }
}
