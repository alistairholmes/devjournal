package com.alistairholmes.devjournal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.alistairholmes.devjournal.R;
import com.alistairholmes.devjournal.utils.OnboardingItem;

import java.util.ArrayList;

public class OnboardingAdapter extends PagerAdapter {

    private Context mContext;
    ArrayList<OnboardingItem> onBoardItems=new ArrayList<>();


    public OnboardingAdapter(Context mContext, ArrayList<OnboardingItem> items) {
        this.mContext = mContext;
        this.onBoardItems = items;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.onboard_item, container, false);

        OnboardingItem item=onBoardItems.get(position);

        ImageView imageView = itemView.findViewById(R.id.iv_onboarding);
        imageView.setImageResource(item.getImageID());

        TextView tv_title= itemView.findViewById(R.id.tv_onboarding_header);
        tv_title.setText(item.getHeader());

        TextView tv_content= itemView.findViewById(R.id.tv_onboarding_footer);
        tv_content.setText(item.getFooter());

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

}
