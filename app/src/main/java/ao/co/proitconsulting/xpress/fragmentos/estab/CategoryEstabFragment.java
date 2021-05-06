package ao.co.proitconsulting.xpress.fragmentos.estab;

import android.app.AlertDialog;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asksira.loopingviewpager.LoopingViewPager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.activities.ProdutosActivity;
import ao.co.proitconsulting.xpress.adapters.CategoryEstabAdapter;
import ao.co.proitconsulting.xpress.adapters.EstabelecimentoAdapter;
import ao.co.proitconsulting.xpress.adapters.RecyclerViewOnItemClickListener;
import ao.co.proitconsulting.xpress.adapters.menuBanner.MenuBannerAdapter;
import ao.co.proitconsulting.xpress.api.ApiClient;
import ao.co.proitconsulting.xpress.api.ApiInterface;
import ao.co.proitconsulting.xpress.helper.MetodosUsados;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.CategoriaEstabelecimento;
import ao.co.proitconsulting.xpress.modelos.Estabelecimento;
import ao.co.proitconsulting.xpress.modelos.TopSlideImages;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryEstabFragment extends Fragment {

    private CategoryEstabViewModel categoryEstabViewModel;
    private View view;
    private ProgressBar progressBar;
    private AlertDialog waitingDialog;

    private List<Estabelecimento> estabelecimentoList = new ArrayList<>();
//    private EstabelecimentoAdapter estabelecimentoAdapter;
    private CategoryEstabAdapter categoryEstabAdapter;

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private SearchView searchView;
    private LoopingViewPager loopingViewPager;

    public CategoryEstabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        categoryEstabViewModel =
                new ViewModelProvider(this).get(CategoryEstabViewModel.class);
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_categoryestab, container, false);
        initViews();

        categoryEstabViewModel.getListMutableLiveData().observe(this, new Observer<List<TopSlideImages>>() {
            @Override
            public void onChanged(List<TopSlideImages> topSlideImages) {
                MenuBannerAdapter menuBannerAdapter = new MenuBannerAdapter(getContext(),topSlideImages,true);
                loopingViewPager.setAdapter(menuBannerAdapter);
            }
        });

        categoryEstabViewModel.getMutableLiveDataCatEstab().observe(this, new Observer<CategoriaEstabelecimento>() {
            @Override
            public void onChanged(CategoriaEstabelecimento categoriaEstabelecimento) {
                ((AppCompatActivity)getActivity())
                        .getSupportActionBar()
                        .setTitle(categoriaEstabelecimento.getMenuCategory().getDescricao());
                estabelecimentoList.addAll(categoriaEstabelecimento.getEstabelecimentoList());
                setAdapters(estabelecimentoList);
            }
        });




        return view;
    }

    private void initViews(){
        loopingViewPager = view.findViewById(R.id.loopingViewPager);
        gridLayoutManager = new GridLayoutManager(getContext(), AppPrefsSettings.getInstance().getListGridViewMode());
        recyclerView = view.findViewById(R.id.recyclerViewEstab);
        progressBar = view.findViewById(R.id.progressBar);

        waitingDialog = new SpotsDialog.Builder().setContext(getContext()).build();
        waitingDialog.setMessage("Carregando...");
        waitingDialog.setCancelable(false);
        waitingDialog.show();


        searchView = view.findViewById(R.id.search_bar);
        searchView.setQueryHint(getString(R.string.pesquisar));

        SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete)searchView.findViewById(R.id.search_src_text);
        theTextArea.setHintTextColor(Color.WHITE);
        theTextArea.setTextColor(Color.WHITE);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);




        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (categoryEstabAdapter!=null)
                    categoryEstabAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void setAdapters(List<Estabelecimento> estabelecimentoList) {

        waitingDialog.dismiss();
        categoryEstabAdapter = new CategoryEstabAdapter(getContext(), estabelecimentoList, gridLayoutManager);
        recyclerView.setAdapter(categoryEstabAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);

//        categoryEstabAdapter.setItemClickListener(new RecyclerViewOnItemClickListener() {
//            @Override
//            public void onItemClickListener(int position) {
//                Estabelecimento estabelecimento = estabelecimentoList.get(position);
//
//                if (estabelecimento.estadoEstabelecimento!=null){
//
//                    if (estabelecimento.estadoEstabelecimento.equals("Aberto")){
//                        Intent intent = new Intent(getContext(), ProdutosActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.putExtra("estabelecimento",estabelecimento);
//                        startActivity(intent);
//                    }else{
//                        MetodosUsados.mostrarMensagem(getContext(),"O estabelecimento encontra-se ".concat(estabelecimento.estadoEstabelecimento));
//                    }
//
//                }
//
//
//
//
//            }
//        });

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

    @Override
    public void onDestroyView() {
        EventBus.getDefault().removeAllStickyEvents();
        super.onDestroyView();
    }


}
