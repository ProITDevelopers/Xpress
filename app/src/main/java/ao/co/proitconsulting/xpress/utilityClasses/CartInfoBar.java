package ao.co.proitconsulting.xpress.utilityClasses;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import ao.co.proitconsulting.xpress.R;
import ao.co.proitconsulting.xpress.helper.Utils;

public class CartInfoBar extends RelativeLayout implements View.OnClickListener {

    private CartInfoBarListener listener;

    TextView cartInfo,cart_total;

    RelativeLayout relativeLayout;

    public CartInfoBar(Context context) {
        super(context);
        init(context, null);
    }

    public CartInfoBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void init(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.view_cart_info_bar, null);
        View view = View.inflate(context,R.layout.view_cart_info_bar, null);
        cartInfo = view.findViewById(R.id.cart_price);
        cart_total = view.findViewById(R.id.cart_total);
        relativeLayout = view.findViewById(R.id.container);
        relativeLayout.setOnClickListener(this);
        addView(view);
    }

    public void setListener(CartInfoBarListener listener) {
        this.listener = listener;
    }


    public void setData(int itemCount, String price) {
        if (itemCount == 1){
            cartInfo.setText(new StringBuilder("")
                    .append(itemCount)
                    .append(" Item").toString());
        }

        if (itemCount > 1){
            cartInfo.setText(getContext().getString(R.string.cart_info_bar_items, itemCount));
        }
//        cart_total.setText(getContext().getString(R.string.cart_info_bar_total, Float.parseFloat(price)).concat(" AKZ"));

        double totalPrice = Double.parseDouble(price);
        cart_total.setText(new StringBuilder("")
                .append("Total: ")
                .append(Utils.formatPrice(totalPrice)).append(" AKZ").toString());

    }

    @Override
    public void onClick(View view) {
        if (listener != null)
            listener.onClick();
    }

    public interface CartInfoBarListener {
        void onClick();
    }
}
