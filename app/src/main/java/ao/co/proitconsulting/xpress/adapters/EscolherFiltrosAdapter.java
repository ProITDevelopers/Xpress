package ao.co.proitconsulting.xpress.adapters;

import android.Manifest;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.EscolherFiltros;

public class EscolherFiltrosAdapter extends RecyclerView.Adapter<EscolherFiltrosAdapter.MyViewHolder> {

    private Context context;
    private List<EscolherFiltros> escolherFiltrosList;
    private static SingleClickListener sClickListener;
    private static int sSelected = AppPrefsSettings.getInstance().getEstabFilterView();
    boolean isPermited = false;



    public EscolherFiltrosAdapter(Context context, List<EscolherFiltros> escolherFiltrosList) {
        this.context = context;
        this.escolherFiltrosList = escolherFiltrosList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_escolher_filtro, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        EscolherFiltros escolherFiltros = escolherFiltrosList.get(position);
        holder.txtFilterName.setText(escolherFiltros.getFilterName());



        if (sSelected == position) {
            holder.imgFilterSelected.setVisibility(View.VISIBLE);
        } else {
            holder.imgFilterSelected.setVisibility(View.INVISIBLE);
        }




//        holder.setListener(new IRecyclerClickListener() {
//            @Override
//            public void onItemClickListener(View view, int position) {
//                sSelected = position;
//
//                if (escolherFiltrosList.get(position).isFilterSelected()){
//                    escolherFiltrosList.get(position).setFilterSelected(false);
//                    holder.imgFilterSelected.setVisibility(View.INVISIBLE);
//                }else{
//                    escolherFiltrosList.get(position).setFilterSelected(true);
//                    holder.imgFilterSelected.setVisibility(View.VISIBLE);
//                }
//
//
//                Toast.makeText(context, ""+escolherFiltrosList.get(position), Toast.LENGTH_SHORT).show();
//            }
//        });





    }

    @Override
    public int getItemCount() {
        return escolherFiltrosList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        private TextView txtFilterName;
        private ImageView imgFilterSelected;



        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);


            txtFilterName = itemView.findViewById(R.id.txtFilterName);
            imgFilterSelected = itemView.findViewById(R.id.imgFilterSelected);


            itemView.setOnClickListener(this);



        }



        @Override
        public void onClick(View view) {

            if (getAdapterPosition() == 1){
                if (verificarPermissao()){
                    sSelected = getAdapterPosition();
                    sClickListener.onItemClickListener(getAdapterPosition(), view);

                    AppPrefsSettings.getInstance().saveEstabFilterView(sSelected);

                }
            }else{
                sSelected = getAdapterPosition();
                sClickListener.onItemClickListener(getAdapterPosition(), view);

                AppPrefsSettings.getInstance().saveEstabFilterView(sSelected);
            }


        }


    }

    private boolean verificarPermissao(){

        Dexter.withContext(context)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            isPermited = true;
                        }else{
                            isPermited = false;
                            Toast.makeText(context, context.getString(R.string.msg_permissao_localizacao), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                        isPermited = false;
                        Toast.makeText(context, context.getString(R.string.msg_permissao_localizacao), Toast.LENGTH_SHORT).show();
                    }
                }).check();
        return isPermited;
    }


    public void selectedItem() {
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(SingleClickListener clickListener) {
        sClickListener = clickListener;
    }

    public interface SingleClickListener {
        void onItemClickListener(int position, View view);
    }




}
