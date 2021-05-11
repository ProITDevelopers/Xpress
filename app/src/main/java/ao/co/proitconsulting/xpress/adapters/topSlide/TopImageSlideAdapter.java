package ao.co.proitconsulting.xpress.adapters.topSlide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.modelos.TopSlideImages;

public class TopImageSlideAdapter extends LoopingPagerAdapter<TopSlideImages> {


    public TopImageSlideAdapter(Context context, List<? extends TopSlideImages> itemList, boolean isInfinite) {
        super(context, itemList, isInfinite);
    }



    @NonNull
    @Override
    protected View inflateView(int viewType, @NonNull ViewGroup container, int listPosition) {

        return LayoutInflater.from(getContext()).inflate(R.layout.item_menu_banner,container,false);
    }

    @Override
    protected void bindView(View convertView, int listPosition, int viewType) {

        ImageView img_top_banner = convertView.findViewById(R.id.img_top_banner);
        TopSlideImages topSlideImages = getItemList().get(listPosition);

        //Set data

        if (topSlideImages.mImagesLinks!=null){
            Picasso.with(convertView.getContext()).load(topSlideImages.mImagesLinks)
                    .placeholder(R.drawable.store_placeholder)
                    .into(img_top_banner);
        }else{
            Picasso.with(convertView.getContext()).load(topSlideImages.mImagesRes)
                    .placeholder(R.drawable.store_placeholder)
                    .into(img_top_banner);
        }



    }
}
