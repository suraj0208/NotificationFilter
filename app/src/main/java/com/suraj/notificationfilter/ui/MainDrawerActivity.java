package com.suraj.notificationfilter.ui;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.suraj.notificationfilter.R;
import com.suraj.notificationfilter.services.OnFragmentInteractionListener;
import com.suraj.notificationfilter.ui.fragments.OtherAppsFragment;
import com.suraj.notificationfilter.ui.fragments.SocialAppsFragment;
import com.suraj.notificationfilter.ui.fragments.StatisticsFragment;

public class MainDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener {

    public static String PREFS_NAME = "preferences";
    private static boolean isFragmentOpen = false;
    private SocialAppsFragment socialAppsFragment;
    private OtherAppsFragment otherAppsFragment;
    private StatisticsFragment statisticsFragment;

    private static MenuItem currentMenuItem;
    private SharedPreferences settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        settings = getSharedPreferences(MainDrawerActivity.PREFS_NAME, 0);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        socialAppsFragment = SocialAppsFragment.newInstance();
        fragmentTransaction.replace(R.id.activity_main_drawer_frag_container, socialAppsFragment).commit();

        otherAppsFragment = otherAppsFragment.newInstance();
        statisticsFragment = StatisticsFragment.newInstance();


        if (!isNotificationServiceRunning()) {
            showMessage();
        }

        //code to restore fragment
        if(savedInstanceState!=null  && currentMenuItem !=null){
            this.onNavigationItemSelected(currentMenuItem);
        }


        //startService(new Intent(this, NLService.class));
    }

    private void showMessage() {
        new AlertDialog.Builder(this)
                .setTitle("Give Permission to Access Notification")
                .setMessage("Give permissions in settings to access notifications")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MainDrawerActivity.this.finish();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        MainDrawerActivity.this.finish();
                    }
                })
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }

    @Override
    public void onBackPressed() {


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();


        if (isFragmentOpen) {
            isFragmentOpen = false;
            fragmentTransaction.replace(R.id.activity_main_drawer_frag_container, socialAppsFragment).commit();
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        currentMenuItem=item;

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();


        if (id == R.id.nav_socialapps) {
            isFragmentOpen = false;
            fragmentTransaction.replace(R.id.activity_main_drawer_frag_container, socialAppsFragment).commit();

        } else if (id == R.id.nav_otherapps) {
            isFragmentOpen = true;
            fragmentTransaction.replace(R.id.activity_main_drawer_frag_container, otherAppsFragment).commit();

        } else if (id == R.id.nav_stats) {
            MainDrawerActivity.isFragmentOpen = true;
            fragmentTransaction.replace(R.id.activity_main_drawer_frag_container, statisticsFragment).commit();

        } else if (id == R.id.nav_graph) {
            startActivity(new Intent(MainDrawerActivity.this,GraphActivity.class));
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private boolean isNotificationServiceRunning() {
        ContentResolver contentResolver = getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = getPackageName();
        return enabledNotificationListeners != null && enabledNotificationListeners.contains(packageName);
    }
}
