package ao.co.proitconsulting.xpress.adapters.menuBanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.modelos.HomeTopSlide;

public class MenuBannerAdapter extends LoopingPagerAdapter<HomeTopSlide> {


    private ImageView img_top_banner;

    public MenuBannerAdapter(Context context, List<? extends HomeTopSlide> itemList, boolean isInfinite) {
        super(context, itemList, isInfinite);
    }



    @Override
    protected View inflateView(int viewType, ViewGroup container, int listPosition) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_menu_banner,container,false);

        img_top_banner = view.findViewById(R.id.img_top_banner);

        return view;
    }

    @Override
    protected void bindView(View convertView, int listPosition, int viewType) {

        //Set data
        Picasso.with(convertView.getContext()).load(getItemList().get(listPosition).getImage())
                .placeholder(R.drawable.store_placeholder)
                .into(img_top_banner);

    }
}
