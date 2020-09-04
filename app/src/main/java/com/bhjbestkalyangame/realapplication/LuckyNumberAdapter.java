package com.bhjbestkalyangame.realapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Random;


class LuckyNumberAdapter extends BaseAdapter {

        Context mContext;
        List<String> values;

    public LuckyNumberAdapter(Context mContext, List<String> values) {
        this.mContext = mContext;
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(view == null){
            view = mInflater.inflate(R.layout.lucky_number_item, null);

            TextView LuckyNumber = view.findViewById(R.id.luckynumber_textview);
            LuckyNumber.setText(values.get(i));
            Random rnd = new Random();
            int currentStrokeColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            LuckyNumber.setTextColor(currentStrokeColor);
        }
        return view;

    }
}
