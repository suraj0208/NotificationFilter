package com.suraj.notificationfilter.data;


import android.graphics.drawable.Drawable;

/**
 * Created by nill on 30/1/16.
 */
public class AppInfo implements Comparable<AppInfo>{

    private String applicationName;

    private Drawable appIcon;

    private  String appPackage;


    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public int compareTo(AppInfo another) {
        return this.applicationName.toLowerCase().compareTo(another.applicationName.toLowerCase());
    }

}
