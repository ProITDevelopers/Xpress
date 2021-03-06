package ao.co.proitconsulting.xpress.fragmentos.estab;

import android.app.AlertDialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.adapters.CategoryEstabAdapter;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.CategoriaEstabelecimento;
import ao.co.proitconsulting.xpress.modelos.Estabelecimento;
import ao.co.proitconsulting.xpress.modelos.MenuCategory;
import dmax.dialog.SpotsDialog;

public class CategoryEstabFragment extends Fragment {

    private CategoryEstabViewModel categoryEstabViewModel;
    private View view;

    private AlertDialog waitingDialog;

    private List<Estabelecimento> estabelecimentoList = new ArrayList<>();
//    private EstabelecimentoAdapter estabelecimentoAdapter;
    private CategoryEstabAdapter categoryEstabAdapter;

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
//    private SearchView searchView;
//    private LoopingViewPager loopingViewPager;

    private MenuCategory menuCategory;


    public CategoryEstabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        categoryEstabViewModel =
                new ViewModelProvider(this).get(CategoryEstabViewModel.class);
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_categoryestab, container, false);

        if (getActivity()!=null){
            if (((AppCompatActivity)getActivity())
                    .getSupportActionBar()!=null){
                if (getContext()!=null){
                    final Drawable upArrow = ContextCompat.getDrawable(getContext(), R.drawable.ic_menu_burguer);;
                    assert upArrow != null;
                    upArrow.setColorFilter(getResources().getColor(R.color.ic_menu_burguer_color), PorterDuff.Mode.SRC_ATOP);
                    ((AppCompatActivity)getActivity())
                            .getSupportActionBar().setHomeAsUpIndicator(upArrow);

                }

            }
        }

        initViews();

//        categoryEstabViewModel.getListMutableLiveData().observe(this, new Observer<List<TopSlideImages>>() {
//            @Override
//            public void onChanged(List<TopSlideImages> topSlideImages) {
//                TopImageSlideAdapter topImageSlideAdapter = new TopImageSlideAdapter(getContext(),topSlideImages,true);
//                loopingViewPager.setAdapter(topImageSlideAdapter);
//            }
//        });

        categoryEstabViewModel.getMutableLiveDataCatEstab().observe(this, new Observer<CategoriaEstabelecimento>() {
            @Override
            public void onChanged(CategoriaEstabelecimento categoriaEstabelecimento) {
                ((AppCompatActivity)getActivity())
                        .getSupportActionBar()
                        .setTitle(categoriaEstabelecimento.getMenuCategory().getDescricao());
                if (estabelecimentoList!=null)
                    estabelecimentoList.clear();


                menuCategory = categoriaEstabelecimento.getMenuCategory();

                estabelecimentoList.addAll(categoriaEstabelecimento.getEstabelecimentoList());
                setAdapters(estabelecimentoList);
            }
        });




        return view;
    }

    private void initViews(){
//        loopingViewPager = view.findViewById(R.id.loopingViewPager);
        gridLayoutManager = new GridLayoutManager(getContext(), AppPrefsSettings.getInstance().getListGridViewMode());
        recyclerView = view.findViewById(R.id.recyclerViewEstab);


        waitingDialog = new SpotsDialog.Builder().setContext(getContext()).setTheme(R.style.CustomSpotsDialog).build();
        waitingDialog.setMessage("Carregando...");
        waitingDialog.setCancelable(false);
        waitingDialog.show();


//        searchView = view.findViewById(R.id.search_bar);
//        searchView.setQueryHint(getString(R.string.pesquisar));
//
//        SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete)searchView.findViewById(R.id.search_src_text);
//        theTextArea.setHintTextColor(Color.WHITE);
//        theTextArea.setTextColor(Color.WHITE);
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
//                if (categoryEstabAdapter!=null)
//                    categoryEstabAdapter.getFilter().filter(newText);
//                return false;
//            }
//        });
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
//        loopingViewPager.resumeAutoScroll();


    }

    @Override
    public void onPause() {
//        loopingViewPager.pauseAutoScroll();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().removeAllStickyEvents();
        super.onDestroyView();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem filterItem = menu.findItem(R.id.action_filtros);
        filterItem.setVisible(false);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(menuCategory.getDescricao());
//        searchView.setQueryHint(getString(R.string.pesquisar));

        SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete)searchView.findViewById(R.id.search_src_text);

        if (getContext()!=null)
            theTextArea.setTextColor(ContextCompat.getColor(getContext(), R.color.search_text_color));
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);


// Does help!
        ImageView searchClose = (ImageView) searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchClose.setImageResource(R.drawable.ic_baseline_clear_search_24);

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
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_options_search, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        switch (id) {
//            case R.id.action_filtros:
//                Toast.makeText(getContext(), "Filtros", Toast.LENGTH_SHORT).show();
//                return true;
//
//        }
//        return super.onOptionsItemSelected(item);
//    }


}
