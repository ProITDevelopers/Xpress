package ao.co.proitconsulting.xpress.fragmentos.wallet_xpress_conta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.andremion.counterfab.CounterFab;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.activities.MenuActivity;

public class CarteiraXpressContaFragment extends Fragment {

    private View view;

    public CarteiraXpressContaFragment() {}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_carteira_xpress_conta, container, false);

        CounterFab floatingActionButton = ((MenuActivity) getActivity()).getFloatingActionButton();
        if (floatingActionButton != null) {
            floatingActionButton.show();
        }

        return view;
    }
}
