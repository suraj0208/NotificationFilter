package com.suraj.notificationfilter.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.suraj.notificationfilter.R;
import com.suraj.notificationfilter.services.OnFragmentInteractionListener;
import com.suraj.notificationfilter.ui.MainDrawerActivity;

/**
 * Created by suraj on 31/1/16.
 */
public class StatisticsFragment extends Fragment {
    private Context context;
    private OnFragmentInteractionListener mListener;
    private TextView tvemoticons, tvgreetings, tvwishes, tvaddsother;
    private SharedPreferences settings;
    private SharedPreferences.Editor settings_editor;

    public StatisticsFragment() {
        // Required empty public constructor
    }


    public static StatisticsFragment newInstance() {

        return new StatisticsFragment();
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

        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        settings = getActivity().getSharedPreferences(MainDrawerActivity.PREFS_NAME, 0);
        settings_editor = settings.edit();

        tvemoticons = (TextView) view.findViewById(R.id.tv_emoticons);
        tvgreetings = (TextView) view.findViewById(R.id.tv_greetings);
        tvwishes = (TextView) view.findViewById(R.id.tv_wishes);
        tvaddsother = (TextView) view.findViewById(R.id.tv_adsother);


        updateTextView();

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

    public void resetStats() {
        settings_editor.putInt("EMOTICONS", 0);
        settings_editor.putInt("GREETINGS", 0);
        settings_editor.putInt("WISHES", 0);
        settings_editor.putInt("ADS_OTHER", 0);
        settings_editor.apply();
        updateTextView();
    }


    public void updateTextView() {
        tvemoticons.setText(Integer.toString(settings.getInt("EMOTICONS", 0)));
        tvgreetings.setText(Integer.toString(settings.getInt("GREETINGS", 0)));
        tvwishes.setText(Integer.toString(settings.getInt("WISHES", 0)));
        tvaddsother.setText(Integer.toString(settings.getInt("ADS_OTHER", 0)));
    }
}
