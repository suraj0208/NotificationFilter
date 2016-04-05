package com.suraj.notificationfilter.ui.widget;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.suraj.notificationfilter.data.AppInfo;
import com.suraj.notificationfilter.R;
import com.suraj.notificationfilter.ui.MainDrawerActivity;

/**
 * Created by nill on 30/1/16.
 */

public class ApplicationListAdapter extends ArrayAdapter<AppInfo> {
    private ArrayList<AppInfo> appInfoArrayList;
    private SharedPreferences settings;
    private SharedPreferences.Editor settings_editor;

    public ApplicationListAdapter(Context context, ArrayList<AppInfo> appInfoArrayList) {
        super(context, R.layout.custom_approw);
        this.appInfoArrayList = appInfoArrayList;

        settings = context.getSharedPreferences(MainDrawerActivity.PREFS_NAME, 0);
        settings_editor = settings.edit();
    }

    @Override
    public int getCount() {
        return appInfoArrayList.size();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_approw, null);

        ((TextView) view.findViewById(R.id.tvappname)).setText((appInfoArrayList.get(position).getApplicationName()));
        ((ImageView) view.findViewById(R.id.imgvappicon)).setImageDrawable(appInfoArrayList.get(position).getAppIcon());

        final CheckBox chkenabled = (CheckBox) (view.findViewById(R.id.chkboxenabled));

        if(settings.getBoolean(appInfoArrayList.get(position).getAppPackage(),false)){
            chkenabled.setChecked(true);
        }

        chkenabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkenabled.isChecked()){
                    settings_editor.putBoolean(appInfoArrayList.get(position).getAppPackage(),true);
                }else{
                    try {
                        settings_editor.remove(appInfoArrayList.get(position).getAppPackage());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                settings_editor.apply();
            }
        });

        return view;
    }
}