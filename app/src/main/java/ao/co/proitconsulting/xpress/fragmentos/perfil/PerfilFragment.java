package ao.co.proitconsulting.xpress.fragmentos.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;


import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.localDB.AppPrefsSettings;
import ao.co.proitconsulting.xpress.modelos.UsuarioPerfil;
import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {

    private View view;

    //    private CircleImageView imageView;
    private RoundedImageView imageView;

    private TextView txtUserNameInitial,txtNomeCompleto,txtSobrenome,txtTelefone,txtTelefoneAlternativo;
    private TextView txtEmail,txtSexo,txtUserAddress,txtEndereco;
    private Button btnEditPerfil;
    private UsuarioPerfil usuarioPerfil;

    public PerfilFragment(){
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_perfil, container, false);

        initViews();






        return view;
    }

    private void initViews(){

        imageView = view.findViewById(R.id.imgUserPhoto);

        txtUserNameInitial = view.findViewById(R.id.txtUserNameInitial);
        txtNomeCompleto = view.findViewById(R.id.txtNomeCompleto);
        txtSobrenome = view.findViewById(R.id.txtSobrenome);
        txtTelefone = view.findViewById(R.id.txtTelefone);
        txtTelefoneAlternativo = view.findViewById(R.id.txtTelefoneAlternativo);

        txtEmail = view.findViewById(R.id.txtEmail);
        txtSexo = view.findViewById(R.id.txtSexo);


        txtUserAddress = view.findViewById(R.id.txtUserAddress);
        txtEndereco = view.findViewById(R.id.txtEndereco);



        btnEditPerfil = view.findViewById(R.id.btnEditPerfil);



        btnEditPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(PerfilFragment.this)
                        .navigate(R.id.action_nav_perfil_to_nav_editar_perfil);
            }
        });
    }

    @Override
    public void onResume() {
        usuarioPerfil = AppPrefsSettings.getInstance().getUser();
        carregarDadosOffline(usuarioPerfil);
        super.onResume();
    }

    private void carregarDadosOffline(UsuarioPerfil usuarioPerfil) {
        String userNameInitial, userLastNameInitial;
        if (usuarioPerfil!=null){

            if (usuarioPerfil.imagem.equals(getString(R.string.sem_imagem)) || usuarioPerfil.imagem == null){
                if (usuarioPerfil.primeiroNome != null && usuarioPerfil.ultimoNome != null){
                    userNameInitial = String.valueOf(usuarioPerfil.primeiroNome.charAt(0));
                    userLastNameInitial = String.valueOf(usuarioPerfil.ultimoNome.charAt(0));
                    txtUserNameInitial.setText(userNameInitial.toUpperCase().concat(userLastNameInitial.toUpperCase()));
                    txtUserNameInitial.setVisibility(View.VISIBLE);

                }

            }else {
                txtUserNameInitial.setVisibility(View.GONE);
                Picasso.with(getContext())
                        .load(usuarioPerfil.imagem)
//                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .fit().centerCrop()
                        .placeholder(R.drawable.photo_placeholder)
                        .into(imageView);
            }

            txtNomeCompleto.setText(usuarioPerfil.primeiroNome);
            txtSobrenome.setText(usuarioPerfil.ultimoNome);
            txtTelefone.setText(usuarioPerfil.contactoMovel);

            if (usuarioPerfil.contactoAlternativo==null || usuarioPerfil.contactoAlternativo.isEmpty()){
                txtTelefoneAlternativo.setVisibility(View.GONE);
                txtTelefoneAlternativo.setText("");
            } else{
                txtTelefoneAlternativo.setText(usuarioPerfil.contactoAlternativo);
            }

            txtEmail.setText(usuarioPerfil.email);

            if (usuarioPerfil.sexo.equals("F"))
                txtSexo.setText("Feminino");
            else
                txtSexo.setText("Masculino");





            if (usuarioPerfil.provincia==null || usuarioPerfil.municipio==null || usuarioPerfil.bairro==null ||
                    usuarioPerfil.rua==null||usuarioPerfil.nCasa==null){

                txtUserAddress.setVisibility(View.GONE);
                txtEndereco.setVisibility(View.GONE);
                txtEndereco.setText("");

            }else{
                txtEndereco.setText("Província "+
                        usuarioPerfil.provincia +", Município "+
                        usuarioPerfil.municipio +", "+
                        "Bairro "+usuarioPerfil.bairro+", "+
                        "Rua "+usuarioPerfil.rua+", "+
                        "Casa nº"+usuarioPerfil.nCasa);
            }




        }
    }




}