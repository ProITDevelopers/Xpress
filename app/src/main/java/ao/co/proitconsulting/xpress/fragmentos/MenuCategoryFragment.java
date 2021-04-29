package ao.co.proitconsulting.xpress.fragmentos;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.adapters.MenuCategoryAdapter;
import ao.co.proitconsulting.xpress.adapters.RecyclerViewOnItemClickListener;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.fragmentos.estab.EstabelecimentoFragment;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.MenuCategory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuCategoryFragment extends Fragment {

    private static final String TAG = "TAG_MenuFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;

    private ImageView img_list_mode, img_grid_mode;
    private RecyclerView recyclerViewMenu;
    private ProgressBar progressBar;

    private List<MenuCategory> menuCategoryList = new ArrayList<>();
    private MenuCategoryAdapter menuCategoryAdapter;


    private GridLayoutManager gridLayoutManager;

    private ConstraintLayout coordinatorLayout;
    private RelativeLayout errorLayout;
    private TextView btnTentarDeNovo;




    public MenuCategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EstabelecimentoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuCategoryFragment newInstance(String param1, String param2) {
        MenuCategoryFragment fragment = new MenuCategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//            idTipoEstabelecimento = getArguments().getInt("categoriaID", 0);
//            categoriaNome = getArguments().getString("categoria");
//        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_menucategory, container, false);

        if (getActivity()!=null)
            getActivity().setTitle(getString(R.string.txt_xpress));



        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        errorLayout = view.findViewById(R.id.erroLayout);
        btnTentarDeNovo = view.findViewById(R.id.btn);




//        gridLayoutManager = new GridLayoutManager(getContext(), AppPrefsSettings.getInstance().getListGridViewMode());



        img_list_mode = view.findViewById(R.id.img_list_mode);
        img_grid_mode = view.findViewById(R.id.img_grid_mode);
        recyclerViewMenu = view.findViewById(R.id.recyclerViewMenu);
        progressBar = view.findViewById(R.id.progressBar);

        gridLayoutManager = new GridLayoutManager(getContext(), AppPrefsSettings.getInstance().getListGridViewMode());
        //verifConecxaoMenuCategory();

        img_list_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gridLayoutManager.setSpanCount(Common.SPAN_COUNT_ONE);
                AppPrefsSettings.getInstance().saveChangeView(gridLayoutManager.getSpanCount());

                if (menuCategoryAdapter!=null)
                    menuCategoryAdapter.notifyItemRangeChanged(0, menuCategoryAdapter.getItemCount());
            }
        });

        img_grid_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gridLayoutManager.setSpanCount(Common.SPAN_COUNT_TWO);

                AppPrefsSettings.getInstance().saveChangeView(gridLayoutManager.getSpanCount());

                if (menuCategoryAdapter!=null)
                    menuCategoryAdapter.notifyItemRangeChanged(0, menuCategoryAdapter.getItemCount());
            }
        });






        return view;
    }

    private void verifConecxaoMenuCategory() {

        if (getActivity()!=null) {
            ConnectivityManager conMgr =  (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
                    mostarMsnErro();
                } else {
                    carregarListaMenuCategory();
                }
            }
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
                verifConecxaoMenuCategory();
            }
        });
    }

    private void carregarListaMenuCategory() {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<MenuCategory>> rv = apiInterface.getMenuCategories();
        rv.enqueue(new Callback<List<MenuCategory>>() {
            @Override
            public void onResponse(@NonNull Call<List<MenuCategory>> call, @NonNull Response<List<MenuCategory>> response) {

                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    if (menuCategoryList!=null)
                        menuCategoryList.clear();

                    if (response.body()!=null){

                        if (response.body().size()>0){
                            for (MenuCategory menuCategory: response.body()) {
                                if (menuCategory!=null){
                                    menuCategoryList.add(menuCategory);
                                }
                            }

                            setAdapters(menuCategoryList);
                        }

                    }

                } else {

                    progressBar.setVisibility(View.GONE);
                }


            }

            @Override
            public void onFailure(@NonNull Call<List<MenuCategory>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
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



    private void setAdapters(List<MenuCategory> menuCategoryList) {

        menuCategoryAdapter = new MenuCategoryAdapter(getContext(), menuCategoryList, gridLayoutManager);
        recyclerViewMenu.setAdapter(menuCategoryAdapter);
        recyclerViewMenu.setLayoutManager(gridLayoutManager);

        menuCategoryAdapter.setItemClickListener(new RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                MenuCategory menuCategory = menuCategoryList.get(position);
                goToFragment(menuCategory);
            }
        });

    }

    private void goToFragment(MenuCategory menuCategory){
        if (getActivity()!=null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            if (fragmentManager != null){
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                EstabelecimentoFragment estabelecimentoFragment = new EstabelecimentoFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("categoriaID", menuCategory.getIdTipo());
                bundle.putString("categoria", menuCategory.getDescricao());
                estabelecimentoFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.frameLayout, estabelecimentoFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }

    }



    @Override
    public void onResume() {

        if (errorLayout.getVisibility() == View.VISIBLE){
            coordinatorLayout.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
        }
        verifConecxaoMenuCategory();

        super.onResume();
    }



}
