package ao.co.proitconsulting.xpress.fragmentos.telafiltros;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.adapters.EscolherFiltrosAdapter;
import ao.co.proitconsulting.xpress.helper.Common;

public class EscolherFiltrosFragment extends Fragment implements EscolherFiltrosAdapter.SingleClickListener{

    private View view;

    private RecyclerView recyclerViewFindBy;
    private Button btn_aplicar_filters;
    private RecyclerView.LayoutManager mLayoutManager;
    private EscolherFiltrosAdapter escolherFiltrosAdapter;

    public EscolherFiltrosFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_escolher_filtros, container, false);


        recyclerViewFindBy = view.findViewById(R.id.recyclerViewFindBy);
        btn_aplicar_filters = view.findViewById(R.id.btn_aplicar_filters);


        mLayoutManager = new LinearLayoutManager(getContext());

        recyclerViewFindBy.setLayoutManager(mLayoutManager);
        recyclerViewFindBy.setItemAnimator(new DefaultItemAnimator());
        if (getContext()!=null)
            recyclerViewFindBy.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        escolherFiltrosAdapter = new EscolherFiltrosAdapter(getContext(), Common.getEncontrarFiltrosPorList());
        recyclerViewFindBy.setAdapter(escolherFiltrosAdapter);
//        escolherFiltrosAdapter.notifyDataSetChanged();
        escolherFiltrosAdapter.setOnItemClickListener(this);





        btn_aplicar_filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(EscolherFiltrosFragment.this)
                        .navigate(R.id.nav_menu_home);
            }
        });

        return view;
    }

    @Override
    public void onItemClickListener(int position, View view) {
        escolherFiltrosAdapter.selectedItem();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem filterItem = menu.findItem(R.id.action_filtros);
        filterItem.setVisible(false);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_options_search, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }
}
