package ao.co.proitconsulting.xpress.fragmentos.estab;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.activities.ProdutosActivity;
import ao.co.proitconsulting.xpress.adapters.EstabelecimentoAdapter;
import ao.co.proitconsulting.xpress.adapters.RecyclerViewOnItemClickListener;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.Estabelecimento;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstabelecimentoFragment extends Fragment {

    private static final String TAG = "TAG_EstabelecimentoFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;

    private ProgressBar progressBar;

    private List<Estabelecimento> estabelecimentoList = new ArrayList<>();
    private EstabelecimentoAdapter estabelecimentoAdapter;


    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;

    private ConstraintLayout coordinatorLayout;
    private RelativeLayout errorLayout;
    private TextView btnTentarDeNovo;


    private TextView txtNotFound;

    private SearchView searchView;

    private int idTipoEstabelecimento;
    private String categoriaNome;


    public EstabelecimentoFragment() {
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
    public static EstabelecimentoFragment newInstance(String param1, String param2) {
        EstabelecimentoFragment fragment = new EstabelecimentoFragment();
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            idTipoEstabelecimento = getArguments().getInt("categoriaID", 0);
            categoriaNome = getArguments().getString("categoria");
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_estabelecimento, container, false);

        ((AppCompatActivity)getActivity())
                .getSupportActionBar()
                .setTitle(categoriaNome);

        txtNotFound = view.findViewById(R.id.txtNotFound);

        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        errorLayout = view.findViewById(R.id.erroLayout);
        btnTentarDeNovo = view.findViewById(R.id.btn);




//        gridLayoutManager = new GridLayoutManager(getContext(), AppPrefsSettings.getInstance().getListGridViewMode());
        gridLayoutManager = new GridLayoutManager(getContext(), AppPrefsSettings.getInstance().getListGridViewMode());
        recyclerView = view.findViewById(R.id.recyclerViewEstab);
        progressBar = view.findViewById(R.id.progressBar);


        searchView = view.findViewById(R.id.search_bar);
        searchView.setQueryHint(getString(R.string.pesquisar));
//        searchView.onActionViewExpanded();
        //searchView.setIconifiedByDefault(true);

        SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete)searchView.findViewById(R.id.search_src_text);
        theTextArea.setHintTextColor(Color.WHITE);
        theTextArea.setTextColor(Color.WHITE);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);


//        verifConecxaoEstabelecimentos();



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (estabelecimentoAdapter!=null)
                    estabelecimentoAdapter.getFilter().filter(newText);
                return false;
            }
        });



        return view;
    }

    private void verifConecxaoEstabelecimentos() {

        if (getActivity()!=null) {
            ConnectivityManager conMgr =  (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
                    mostarMsnErro();
                } else {
                    carregarListaEstabelicimentos();
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
                verifConecxaoEstabelecimentos();
            }
        });
    }

    private void carregarListaEstabelicimentos() {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        Call<List<Estabelecimento>> rv = apiInterface.getAllEstabelecimentos();
        Call<List<Estabelecimento>> rv = apiInterface.getEstabelecimentosPorTipo(idTipoEstabelecimento);
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
                                }
                            }
                        }


                        progressBar.setVisibility(View.GONE);
                        setAdapters(estabelecimentoList);


                    }

                } else {

                    progressBar.setVisibility(View.GONE);
                }


            }

            @Override
            public void onFailure(@NonNull Call<List<Estabelecimento>> call, @NonNull Throwable t) {
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



    private void setAdapters(List<Estabelecimento> estabelecimentoList) {

        estabelecimentoAdapter = new EstabelecimentoAdapter(getContext(), estabelecimentoList, gridLayoutManager);
        recyclerView.setAdapter(estabelecimentoAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);

        estabelecimentoAdapter.setItemClickListener(new RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                Estabelecimento estabelecimento = estabelecimentoList.get(position);

                if (estabelecimento.estadoEstabelecimento!=null){

                    if (estabelecimento.estadoEstabelecimento.equals("Aberto")){
                        Intent intent = new Intent(getContext(), ProdutosActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("estabelecimento",estabelecimento);
                        startActivity(intent);
                    }else{
                        MetodosUsados.mostrarMensagem(getContext(),"O estabelecimento encontra-se ".concat(estabelecimento.estadoEstabelecimento));
                    }

                }




            }
        });

    }

    @Override
    public void onResume() {

        if (errorLayout.getVisibility() == View.VISIBLE){
            coordinatorLayout.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
        }
        verifConecxaoEstabelecimentos();


        MetodosUsados.esconderTeclado(getActivity());
        super.onResume();
    }
}
