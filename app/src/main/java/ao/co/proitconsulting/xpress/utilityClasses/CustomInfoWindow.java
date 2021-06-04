package ao.co.proitconsulting.xpress.utilityClasses;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.UsuarioPerfil;
import de.hdodenhof.circleimageview.CircleImageView;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    private View myView;
    private UsuarioPerfil usuarioPerfil;

    public CustomInfoWindow(Context context) {
        usuarioPerfil = AppPrefsSettings.getInstance().getUser();
//        myView = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null) ;
        myView = View.inflate(context,R.layout.custom_info_window, null) ;
    }

    @Override
    public View getInfoWindow(Marker marker) {


        CircleImageView circleImageView = myView.findViewById(R.id.img);
        TextView txtUserNameInitial = myView.findViewById(R.id.txtUserNameInitial);

        if (marker.getTitle().equals("Eu")){

            circleImageView.setImageResource(R.color.white);

            if (usuarioPerfil!=null){
                if (usuarioPerfil.imagem.equals(myView.getContext().getString(R.string.sem_imagem)) || usuarioPerfil.imagem == null){
                    if (usuarioPerfil.primeiroNome != null && usuarioPerfil.ultimoNome != null){
                        String userNameInitial = String.valueOf(usuarioPerfil.primeiroNome.charAt(0));
                        String userLastNameInitial = String.valueOf(usuarioPerfil.ultimoNome.charAt(0));
                        txtUserNameInitial.setText(userNameInitial.toUpperCase().concat(userLastNameInitial.toUpperCase()));
                        txtUserNameInitial.setVisibility(View.VISIBLE);

                    }

                }else {
                    txtUserNameInitial.setVisibility(View.GONE);
                    Picasso.with(myView.getContext()).load(usuarioPerfil.imagem).placeholder(R.drawable.photo_placeholder).into(circleImageView);
                }
            }



        } else if (marker.getTitle().equals("Local de entrega")){
            txtUserNameInitial.setVisibility(View.GONE);
            circleImageView.setImageResource(R.drawable.ic_baseline_location_on_24);

        }




        TextView txtPickupTitle = myView.findViewById(R.id.txtNameInfo);
        txtPickupTitle.setText(marker.getTitle());

        TextView txtPickupSnippet = myView.findViewById(R.id.txtEnderecoSnippet);
        txtPickupSnippet.setText(marker.getSnippet());


        return myView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
