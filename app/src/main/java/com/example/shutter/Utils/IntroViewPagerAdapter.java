package com.example.shutter.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shutter.R;
import com.tomer.fadingtextview.FadingTextView;

import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class IntroViewPagerAdapter extends PagerAdapter {

    Context mContext;
    List<ScreenItem> mListScreen;

    public IntroViewPagerAdapter(Context mContext, List<ScreenItem> mListScreen) {
        this.mContext = mContext;
        this.mListScreen = mListScreen;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutscreen= inflater.inflate(R.layout.layout_screen,null);

        ImageView img=layoutscreen.findViewById(R.id.intro_img);
        TextView title=layoutscreen.findViewById(R.id.into_title);
        FadingTextView decriptionFadingTextView=layoutscreen.findViewById(R.id.intro_description);
        decriptionFadingTextView.setTimeout(1000, FadingTextView.MILLISECONDS);
        title.setText(mListScreen.get(position).getTitle());
        decriptionFadingTextView.setTexts(mListScreen.get(position).getDescription());
        img.setImageResource(mListScreen.get(position).getScreenImg());
        container.addView(layoutscreen);
        return layoutscreen;
    }

    @Override
    public int getCount() {
        return mListScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view==o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View) object);
    }
}
