package ao.co.proitconsulting.xpress.fragmentos.wallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ao.co.proitconsulting.xpress.R;

public class CarteiraXpressFragment extends Fragment {

    private View view;

    public CarteiraXpressFragment() {}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_carteira_xpress, container, false);

        return view;
    }
}
