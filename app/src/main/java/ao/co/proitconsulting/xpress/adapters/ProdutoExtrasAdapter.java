package ao.co.proitconsulting.xpress.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ao.co.proitconsulting.xpress.Callback.IRecyclerClickListener;
import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.helper.Common;
import ao.co.proitconsulting.xpress.helper.Utils;
import ao.co.proitconsulting.xpress.modelos.ProdutoListExtras;

public class ProdutoExtrasAdapter extends RecyclerView.Adapter<ProdutoExtrasAdapter.MyViewHolder> {

    private Context context;
    private List<ProdutoListExtras> produtoListExtrasList;
    private IRecyclerClickListener iRecyclerClickListener;





    public ProdutoExtrasAdapter(Context context, List<ProdutoListExtras> produtoListExtrasList) {
        this.context = context;
        this.produtoListExtrasList = produtoListExtrasList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produto_extra, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        ProdutoListExtras produtoListExtras = produtoListExtrasList.get(position);
        if (produtoListExtras != null){

            holder.txtExtraTitle.setText(produtoListExtras.produtoextras.descricao);

//            String preco = String.valueOf(produtoListExtras.produtoextras.preco);
//            holder.txtExtraPrice.setText(context.getString(R.string.price_with_currency, Float.parseFloat(preco)).concat(" AKZ"));

            double displayPrice = Double.parseDouble(String.valueOf(produtoListExtras.produtoextras.preco));
            holder.txtExtraPrice.setText(new StringBuilder("").append(Utils.formatPrice(displayPrice)).append(" AKZ").toString());


            if (Common.selectedProduto.getUserSelectedAddon() != null){
                for (ProdutoListExtras produtoListExtras1 : Common.selectedProduto.getUserSelectedAddon()){
                    if (produtoListExtras.produtoextras.iDextras== produtoListExtras1.produtoextras.iDextras)
                        holder.imgExtraSelected.setVisibility(View.VISIBLE);
                }
            }


//            holder.setListener(new IRecyclerClickListener() {
//                @Override
//                public void onItemClickListener(View view, int position) {
//                    ProdutoListExtras produtoListExtras = produtoListExtrasList.get(position);
//                    Toast.makeText(context, "Extra: "+produtoListExtras.produtoextras.descricao+"\n"+
//                                    "Preço: "+produtoListExtras.produtoextras.preco,
//                            Toast.LENGTH_SHORT).show();
//
//
//                    if (holder.imgExtraSelected.getVisibility() == View.INVISIBLE){
//                        if (Common.selectedProduto.getUserSelectedAddon() == null)
//                            Common.selectedProduto.setUserSelectedAddon(new RealmList<>());
//                        Common.selectedProduto.getUserSelectedAddon().add(produtoListExtras);
//                        holder.imgExtraSelected.setVisibility(View.VISIBLE);
//                    }else {
//                        Common.selectedProduto.getUserSelectedAddon().remove(produtoListExtras);
//                        holder.imgExtraSelected.setVisibility(View.INVISIBLE);
//                    }
//
//
//
//                }
//            });

        }


    }

    @Override
    public int getItemCount() {
        return produtoListExtrasList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        private TextView txtExtraTitle, txtExtraPrice;
        ImageView imgExtraSelected;
//        private IRecyclerClickListener listener;


        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);


            txtExtraTitle = itemView.findViewById(R.id.txtExtraTitle);
            txtExtraPrice = itemView.findViewById(R.id.txtExtraPrice);
            imgExtraSelected = itemView.findViewById(R.id.imgExtraSelected);

            itemView.setOnClickListener(this);



        }



//        public void setListener(IRecyclerClickListener listener) {
//            this.listener = listener;
//        }
//
//        @Override
//        public void onClick(View view) {
//            listener.onItemClickListener(view,getAdapterPosition());
//        }

        @Override
        public void onClick(View view) {
            if (iRecyclerClickListener != null) iRecyclerClickListener.onItemClickListener(view,getAdapterPosition());
        }
    }


    public void setItemClickListener(IRecyclerClickListener itemClickListener) {
        this.iRecyclerClickListener = itemClickListener;
    }



}
