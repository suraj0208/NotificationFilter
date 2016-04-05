package com.suraj.notificationfilter.ui;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import com.suraj.notificationfilter.R;

/**
 * Created by nill on 1/2/16.
 */
public class GraphActivity extends AppCompatActivity {

    private static int[] COLORS = new int[]{Color.parseColor("#FF5252"), Color.parseColor("#C2185B"), Color.parseColor("#4CAF50"), Color.parseColor("#7B1FA2")};


    private static String[] NAME_LIST = new String[]{"Greetings", "Wishes", "Emoticons", "Ads/Other"};

    private CategorySeries mSeries = new CategorySeries("");

    private DefaultRenderer mRenderer = new DefaultRenderer();

    private GraphicalView mChartView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.parseColor("#00000000"));
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setLabelsTextSize(60);
        mRenderer.setLegendTextSize(60);
        mRenderer.setMargins(new int[]{20, 30, 15, 0});
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setStartAngle(90);

        SharedPreferences settings = getSharedPreferences(MainDrawerActivity.PREFS_NAME, 0);


        double[] VALUES = new double[]{settings.getInt("GREETINGS", 0), settings.getInt("WISHES", 0), settings.getInt("EMOTICONS", 0), settings.getInt("ADS_OTHER", 0)};


        for (int i = 0; i < VALUES.length; i++) {
            mSeries.add(NAME_LIST[i] + " " + VALUES[i], VALUES[i]);
            SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
            renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
            mRenderer.addSeriesRenderer(renderer);
            mRenderer.setLabelsColor(Color.BLACK);
        }

        if (mChartView != null) {
            mChartView.repaint();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mChartView == null) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
            mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
            mRenderer.setClickEnabled(true);
            mRenderer.setSelectableBuffer(10);

            mChartView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();

                    if (seriesSelection == null) {
                    } else {
                    }
                }
            });

            mChartView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
                    if (seriesSelection == null) {
                        return false;
                    }
                    return true;
                }
            });
            layout.addView(mChartView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        } else {
            mChartView.repaint();
        }
    }
}
