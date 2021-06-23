package ao.co.proitconsulting.xpress.fragmentos.telafiltros;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
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
}
