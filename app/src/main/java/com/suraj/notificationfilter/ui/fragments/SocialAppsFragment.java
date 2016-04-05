package com.suraj.notificationfilter.ui.fragments;

/**
 * Created by suraj on 31/1/16.
 */

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import com.suraj.notificationfilter.R;
import com.suraj.notificationfilter.data.AppInfo;
import com.suraj.notificationfilter.services.OnFragmentInteractionListener;
import com.suraj.notificationfilter.ui.widget.ApplicationListAdapter;


public class SocialAppsFragment extends Fragment {

    private Context context;
    private OnFragmentInteractionListener mListener;
    private ListView lstsocialapps;
    private HashMap<String, Boolean> packageNames;
    private ApplicationListAdapter socialAppsAdapter;
    private List<PackageInfo> PackList;

    public SocialAppsFragment() {
        packageNames = new HashMap<>();

        //currently implemented for com.whatsapp and com.facebook.orca
        packageNames.put("com.whatsapp", Boolean.valueOf(true));
        packageNames.put("com.facebook.orca", Boolean.valueOf(true));
        packageNames.put("com.facebook.lite", Boolean.valueOf(true));

    }

    public static SocialAppsFragment newInstance() {

        return  new SocialAppsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_social_apps, container, false);
        context = getActivity().getApplicationContext();

        lstsocialapps = (ListView) view.findViewById(R.id.lstsocialapps);
        PackList = context.getPackageManager().getInstalledPackages(0);




        class FetchApps extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... params) {

                socialAppsAdapter = new ApplicationListAdapter(context, getInstalledApps());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                lstsocialapps.setAdapter(socialAppsAdapter);
            }
        }

        (new FetchApps()).execute();


        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof com.suraj.notificationfilter.services.OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public ArrayList<AppInfo> getInstalledApps() {

        ArrayList<AppInfo> SocialAppsArrayList = new ArrayList<>();
        ArrayList<AppInfo> OtherAppsArrayList = new ArrayList<>();
        PackageManager pm = context.getApplicationContext().getPackageManager();
        for (int i = 0; i < PackList.size(); i++) {
            PackageInfo PackInfo = PackList.get(i);
            if ((PackInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String AppName = PackInfo.applicationInfo.loadLabel(pm).toString();
                AppName = AppName.trim();

                AppInfo app = new AppInfo();
                app.setApplicationName(AppName);
                Drawable AppIcon = PackInfo.applicationInfo.loadIcon(pm);

                if (packageNames.get(PackInfo.packageName) != null) {
                    app.setAppPackage(PackInfo.packageName);
                    app.setAppIcon(AppIcon);
                    SocialAppsArrayList.add(app);
                } else {
                    app.setAppPackage(PackInfo.packageName);
                    app.setAppIcon(AppIcon);
                    OtherAppsArrayList.add(app);
                }

            }
        }

        Collections.sort(SocialAppsArrayList);
        Collections.sort(OtherAppsArrayList);


        return SocialAppsArrayList;
    }


}
