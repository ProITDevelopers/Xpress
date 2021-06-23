package ao.co.proitconsulting.xpress.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import ao.co.proitconsulting.xpress.fragmentos.encomenda_actual.EncomendasActuaisFragment;
import ao.co.proitconsulting.xpress.fragmentos.encomenda_history.EncomendasHistoricoFragment;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter  {

    private String mWordUpdated;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);


    }

    //call this method to update fragments in ViewPager dynamically
    public void update(String word) {
        this.mWordUpdated = word;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        //don't return POSITION_NONE, avoid fragment recreation.
        return super.getItemPosition(object);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Fragment fragment = null;
        switch (position){
            case 0 :
                fragment = new EncomendasActuaisFragment();

                break;
            case 1 :
                fragment = new EncomendasHistoricoFragment();

                break;

        }
        return fragment;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Actuais";
            case 1:
                return "Histórico";


        }
        return "";
    }
}
