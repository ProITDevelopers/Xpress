package ao.co.proitconsulting.xpress.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.squareup.picasso.Picasso;

import java.util.List;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.modelos.TopSlideImages;

public class ViewPagerAdapterSlider extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<TopSlideImages> topSlideImagesList;

    public ViewPagerAdapterSlider(Context context, List<TopSlideImages> topSlideImagesList) {
        this.context = context;
        this.topSlideImagesList = topSlideImagesList;
    }

    @Override
    public int getCount() {
        return topSlideImagesList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_slide_layout, null);
        ImageView imageView =  view.findViewById(R.id.imageView);

        Picasso.with(view.getContext()).load(topSlideImagesList.get(position).mImagesLinks)
                .placeholder(R.drawable.store_placeholder)
                .into(imageView);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                if(position == 0){
//                    Toast.makeText(context, "Slide 1 Clicked", Toast.LENGTH_SHORT).show();
//                } else if(position == 1){
//                    Toast.makeText(context, "Slide 2 Clicked", Toast.LENGTH_SHORT).show();
//                } else if (position == 2){
//                    Toast.makeText(context, "Slide 3 Clicked", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(context, "Slide 4 Clicked", Toast.LENGTH_SHORT).show();
//                }

            }
        });

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}
