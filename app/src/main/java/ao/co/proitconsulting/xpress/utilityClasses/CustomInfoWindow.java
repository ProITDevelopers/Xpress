package ao.co.proitconsulting.xpress.utilityClasses;

import android.content.Context;
import android.view.LayoutInflater;
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
        myView = LayoutInflater.from(context)
                .inflate(R.layout.custom_info_window, null) ;
    }

    @Override
    public View getInfoWindow(Marker marker) {


        String foto = (String) marker.getTag();

        CircleImageView circleImageView = myView.findViewById(R.id.img);
        TextView txtUserNameInitial = myView.findViewById(R.id.txtUserNameInitial);

        if (marker.getTitle().equals("Eu")){

            if (usuarioPerfil!=null){
                if (usuarioPerfil.imagem.equals(myView.getContext().getString(R.string.sem_imagem))){
                    if (!usuarioPerfil.primeiroNome.isEmpty()){
                        String userNameInitial = String.valueOf(usuarioPerfil.primeiroNome.charAt(0));
                        txtUserNameInitial.setText(userNameInitial.toUpperCase());
                        txtUserNameInitial.setVisibility(View.VISIBLE);

                    }else {
                        String userNameInitial = String.valueOf(usuarioPerfil.email.charAt(0));
                        txtUserNameInitial.setText(userNameInitial.toUpperCase());
                    }

                }else {
                    txtUserNameInitial.setVisibility(View.GONE);
                    Picasso.with(myView.getContext()).load(foto).placeholder(R.drawable.photo_placeholder).into(circleImageView);
                }
            }



        } else if (marker.getTitle().equals("Local de entrega")){

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