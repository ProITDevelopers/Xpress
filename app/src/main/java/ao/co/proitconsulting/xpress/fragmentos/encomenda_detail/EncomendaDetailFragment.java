package ao.co.proitconsulting.xpress.fragmentos.encomenda_detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.adapters.EncomendaDetailListAdapter;
import ao.co.proitconsulting.xpress.fragmentos.produtos_detail.ProdutoDetailViewModel;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.helper.Utils;
import ao.co.proitconsulting.xpress.modelos.Factura;
import ao.co.proitconsulting.xpress.modelos.Produtos;
import ao.co.proitconsulting.xpress.utilityClasses.OrderItemsListView;

public class EncomendaDetailFragment extends Fragment {

    private EncomendaDetailViewModel encomendaDetailViewModel;
    private View view;

    private TextView txtDataPedido,txtEstado,txtEstadoPagamento, txtMetododPagamento;
    private TextView textView4,txtRefereciaCode,textView5,txtEntidadeCode;
    private TextView txtTotal;

    private RecyclerView recyclerViewOrderItems;

    private Factura factura;

    public EncomendaDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        encomendaDetailViewModel =
                new ViewModelProvider(this).get(EncomendaDetailViewModel.class);

        view = inflater.inflate(R.layout.fragment_encomenda_detail, container, false);
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
        txtDataPedido = view.findViewById(R.id.txtDataPedido);
        txtEstado = view.findViewById(R.id.txtEstado);
        txtEstadoPagamento = view.findViewById(R.id.txtEstadoPagamento);
        txtMetododPagamento = view.findViewById(R.id.txtMetododPagamento);

        textView4 = view.findViewById(R.id.textView4);
        txtRefereciaCode = view.findViewById(R.id.txtRefereciaCode);
        textView5 = view.findViewById(R.id.textView5);
        txtEntidadeCode = view.findViewById(R.id.txtEntidadeCode);

        txtTotal = view.findViewById(R.id.txtTotal);

        recyclerViewOrderItems = view.findViewById(R.id.recyclerViewOrderItems);

    }

    @Override
    public void onResume() {
        fillUpviews();
        super.onResume();
    }

    private void fillUpviews() {
        txtDataPedido.setText(Utils.getOrderTimestamp(factura.dataPedido));
        txtEstado.setText(factura.estado);

        if (factura.estadoPagamento.equals("Pagamento Efectuado")){
            txtEstadoPagamento.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            txtEstadoPagamento.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.login_register_text_color));
        }else{
            txtEstadoPagamento.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            txtEstadoPagamento.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.yellow));
        }
        txtEstadoPagamento.setText(factura.estadoPagamento);

        if (factura.metododPagamento!=null){
            txtMetododPagamento.setText(factura.metododPagamento);

            if (factura.metododPagamento.equals("Referencia")){
                if (factura.identificacaoPagamento!=null){

                    txtRefereciaCode.setText(factura.identificacaoPagamento);
                    txtEntidadeCode.setText(factura.entidade);

                    textView4.setVisibility(View.VISIBLE);
                    txtRefereciaCode.setVisibility(View.VISIBLE);
                    textView5.setVisibility(View.VISIBLE);
                    txtEntidadeCode.setVisibility(View.VISIBLE);
                }else{
                    textView4.setVisibility(View.GONE);
                    txtRefereciaCode.setVisibility(View.GONE);
                    textView5.setVisibility(View.GONE);
                    txtEntidadeCode.setVisibility(View.GONE);

                    txtRefereciaCode.setText("");
                    txtEntidadeCode.setText("");


                }
            }else{
                textView4.setVisibility(View.GONE);
                txtRefereciaCode.setVisibility(View.GONE);
                textView5.setVisibility(View.GONE);
                txtEntidadeCode.setVisibility(View.GONE);

                txtRefereciaCode.setText("");
                txtEntidadeCode.setText("");
            }
        }




        String total = String.valueOf(factura.total);
        txtTotal.setText(getContext().getString(R.string.price_with_currency, Float.parseFloat(total)).concat(" AKZ"));


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewOrderItems.setLayoutManager(mLayoutManager);
        recyclerViewOrderItems.setItemAnimator(new DefaultItemAnimator());
        recyclerViewOrderItems.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        EncomendaDetailListAdapter mAdapter = new EncomendaDetailListAdapter(getContext(), factura.itens);
        recyclerViewOrderItems.setAdapter(mAdapter);


    }
}
