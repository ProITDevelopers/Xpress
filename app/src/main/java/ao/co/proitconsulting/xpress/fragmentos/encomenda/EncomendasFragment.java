package ao.co.proitconsulting.xpress.fragmentos.encomenda;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ao.co.proitconsulting.xpress.R;

public class EncomendasFragment extends Fragment {

    private View view;

    public EncomendasFragment() {}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_encomendas, container, false);

        return view;
    }
}
