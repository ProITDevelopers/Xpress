package ao.co.proitconsulting.xpress.fragmentos.encomenda_detail;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.adapters.EncomendaDetailListAdapter;
import ao.co.proitconsulting.xpress.helper.Utils;
import ao.co.proitconsulting.xpress.modelos.Factura;

public class EncomendaDetailFragment extends Fragment {

    private EncomendaDetailViewModel encomendaDetailViewModel;
    private View view;

    private TextView txtMetododPagamento,txtEstadoPagamento,txtSubtotal, txtEntrega,txtTotal;


    private RecyclerView recyclerViewOrderItems;
    private Button btnTrackEncomenda;

    private Factura factura;

    public EncomendaDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        encomendaDetailViewModel =
                new ViewModelProvider(this).get(EncomendaDetailViewModel.class);

        view = inflater.inflate(R.layout.fragment_encomenda_detail, container, false);
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

        encomendaDetailViewModel.getEncomendaMutableLiveData().observe(this, new Observer<Factura>() {
            @Override
            public void onChanged(Factura facturaModel) {
                ((AppCompatActivity)getActivity())
                        .getSupportActionBar()
                        .setTitle("Encomenda #".concat(String.valueOf(facturaModel.idFactura)));

                factura = facturaModel;

            }
        });




        return view;
    }

    private void initViews() {



        txtMetododPagamento = view.findViewById(R.id.txtMetododPagamento);
        txtEstadoPagamento = view.findViewById(R.id.txtEstadoPagamento);

        txtSubtotal = view.findViewById(R.id.txtSubtotal);
        txtEntrega = view.findViewById(R.id.txtEntrega);

        txtTotal = view.findViewById(R.id.txtTotal);


        recyclerViewOrderItems = view.findViewById(R.id.recyclerViewOrderItems);
        btnTrackEncomenda = view.findViewById(R.id.btnTrackEncomenda);

    }

    @Override
    public void onResume() {
        fillUpviews();
        super.onResume();
    }

    private void fillUpviews() {
//        txtDataPedido.setText(Utils.getOrderTimestamp(factura.dataPedido));
//        txtEstado.setText(factura.estado);

        if (factura.metododPagamento!=null){

            if(factura.metododPagamento.equals("Wallet"))
                txtMetododPagamento.setText(getString(R.string.carteira_xpress));
            else if(factura.metododPagamento.equals("TPA"))
                txtMetododPagamento.setText(getString(R.string.multicaixa));
        }

        txtEstadoPagamento.setText(factura.estadoPagamento);

        if (factura.estado.equals("À Caminho")){
            btnTrackEncomenda.setVisibility(View.VISIBLE);
        }


        double valorTaxaPrice = Double.parseDouble(String.valueOf(factura.taxaEntrega));
        double subtotalPrice = Double.parseDouble(String.valueOf(factura.total));

        double totalPrice = subtotalPrice + valorTaxaPrice;



//        String total = String.valueOf(factura.total);
//        txtTotal.setText(getContext().getString(R.string.price_with_currency, Float.parseFloat(total)).concat(" AKZ"));

        txtEntrega.setText(new StringBuilder("")
                .append(Utils.formatPrice(valorTaxaPrice))
                .append(" AKZ").toString());

        txtSubtotal.setText(new StringBuilder("")
                .append(Utils.formatPrice(subtotalPrice))
                .append(" AKZ").toString());

        txtTotal.setText(new StringBuilder("")
                .append(Utils.formatPrice(totalPrice))
                .append(" AKZ").toString());


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewOrderItems.setLayoutManager(mLayoutManager);
        recyclerViewOrderItems.setItemAnimator(new DefaultItemAnimator());
        if (getContext()!=null)
            recyclerViewOrderItems.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        EncomendaDetailListAdapter mAdapter = new EncomendaDetailListAdapter(getContext(), factura.itens);
        recyclerViewOrderItems.setAdapter(mAdapter);

        btnTrackEncomenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (factura.estado.equals("À Caminho")){
                    NavHostFragment.findNavController(EncomendaDetailFragment.this)
                            .navigate(R.id.nav_menu_encomenda_tracker);
                }else{
                    Toast.makeText(getContext(), "Não é possível acompanhar o rastreio da encomenda.", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}
