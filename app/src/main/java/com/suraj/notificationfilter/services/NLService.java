package com.suraj.notificationfilter.services;

/**
 * Created by suraj on 31/1/16.
 */

import android.content.SharedPreferences;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

import com.suraj.notificationfilter.ui.MainDrawerActivity;


public class NLService extends NotificationListenerService {

    //hashmap to store common slangs
    //*********achieves  O(1) complexity
    private HashMap<String, Boolean> slangsHashMap;

    //REGEX to block messages like happy diwali,birthday etc.
    private ArrayList<String> spamDetectorREs;


    //decide whether to block or not
    private boolean fbflag;

    private String text;
    private SharedPreferences settings;
    private SharedPreferences.Editor settings_editor;


    //constant specifies offset for whatasapp specific REs;
    private final int WHATSAPP_RE_LENGTH = 5;

    private int[] stats;


    //stores index into stats array-- to be incremented in settings
    int currentIndex;

    @Override
    public void onCreate() {
        super.onCreate();

        slangsHashMap = new HashMap<>();
        spamDetectorREs = new ArrayList<>();
        stats = new int[5];

        currentIndex = 0;
        fbflag = false;
        text = null;

        settings = getSharedPreferences(MainDrawerActivity.PREFS_NAME, 0);
        settings_editor = settings.edit();

        String slangsArray[] = new String[]{"gn", "gm", "ga", "gnsd", "gnsdtc", "gn sd tc", "gn sd", "OK","ok","Ok","Okay","ohkay", "k", "K", "kk", "tc", "sd", "hand"};


        //Define regular expressions

        //good morning/afternoon/night
        spamDetectorREs.add("g{1}[a-z]{0,8}d\\s*[nma][a-zA-Z0-9]*");

        //special case for birthday
        spamDetectorREs.add("hbd[a-z\\s]*");

        // happy Birthday and happy diwali, sankrant etc
        spamDetectorREs.add("hap[p]*[iey]+[\\sa-zA-Z0-9]+");


        //thank you
        spamDetectorREs.add("th[a]?[n]?[xk][s]?[\\\\S]?[you]?[u]?");

        //special FB notifications
        spamDetectorREs.add("Start\\sa\\sconversation");
        spamDetectorREs.add("[0-9]*\\sconversation");

        //special whatsapp notifications
        spamDetectorREs.add("[0-9]*\\snew message[s]*[\\sa-zA-Z0-9]*");
        spamDetectorREs.add("[0-9]*\\smessage[s]*\\sfrom[\\sa-zA-Z0-9]*");

        for (String slang : slangsArray) {
            slangsHashMap.put(slang, Boolean.valueOf(true));
        }

        initilizestats();

        Log.i("service", "started");

        Log.i("0", Integer.toString(stats[0]));
        Log.i("1", Integer.toString(stats[1]));
        Log.i("2", Integer.toString(stats[2]));
        Log.i("3", Integer.toString(stats[3]));

    }

    public void initilizestats() {
        stats[2] = settings.getInt("EMOTICONS", 0);

        stats[0] = settings.getInt("GREETINGS", 0);

        stats[1] = settings.getInt("WISHES", 0);

        stats[3] = settings.getInt("ADS_OTHER", 0);
    }

    public void updateStats(int position) {
        Log.i("where", "update");

        switch (position) {
            case 0:
                settings_editor.putInt("GREETINGS", (++stats[0]));
                break;
            case 1:
                settings_editor.putInt("WISHES", (++stats[1]));
                break;
            case 2:
                settings_editor.putInt("WISHES", (++stats[1]));
                break;
            case 3:
                settings_editor.putInt("EMOTICONS", (++stats[2]));
                break;
        }
        settings_editor.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i("package", sbn.getNotification().extras.toString());

        try {
            byte[] bytes = sbn.getNotification().extras.get("android.text").toString().getBytes("UTF-8");
            text = new String(bytes, "UTF-8");

            //text = sbn.getNotification().extras.get("android.text").toString();

            Log.i("original", text);
            Log.i("full text", sbn.toString());


            switch (sbn.getPackageName()) {
                case "com.whatsapp":
                    if (settings.getBoolean("com.whatsapp", false))
                        handleFBWA(sbn);
                    break;

                case "com.facebook.orca":
                    if (settings.getBoolean("com.facebook.orca", false))
                        handleFBWA(sbn);
                    break;

                default:
                    if (settings.getBoolean(sbn.getPackageName(), false)) {
                        settings_editor.putInt("ADS_OTHER", (++stats[3]));
                        cancelSpamNotification(sbn);
                        settings_editor.commit();
                    }
            }


        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("flag", "reset");
        fbflag = false;
    }


    //removes leading white spaces, imoticons, and some symbols
    public String cleanUpString(String impuretext) {

        //replace everything except charcters from utf range x30-x7A
        impuretext = impuretext.trim().toLowerCase(Locale.US).replaceAll("[^\\x20\\x30-\\x7A]", "");
        return impuretext;
    }


    public boolean handleFBWA(StatusBarNotification sbn) {
        int i = 0;

        if (text.contains(":"))
            text = text.substring(text.indexOf(":") + 1);


        Log.i("FB", text);

        text = cleanUpString(text);


        //text length=0 means contains stats[0].
        if (text.length() == 0) {
            currentIndex = 3;

            //if found in hashmap cancel notification and return
        } else if (slangsHashMap.get(text) != null) {
            currentIndex = 0;
        }

        //else compare with regular expressions
        else {
            Log.i("waiting", "for fb re");
            for (i = 0; i < spamDetectorREs.size(); i++) {
                if (Pattern.matches(spamDetectorREs.get(i), text)) {
                    break;
                }
            }

            Log.i("i", Integer.toString(i));

            if (i == spamDetectorREs.size()) {
                Log.i("flag", "flag set to true");
                fbflag = true;
            } else if (i < 4)
                currentIndex = i;

            //return true;
        }

        if (!fbflag) {
            updateStats(currentIndex);

            cancelSpamNotification(sbn);
        }

        return false;

    }

    public void cancelSpamNotification(StatusBarNotification sbn) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cancelNotification(sbn.getKey());
        } else
            this.cancelNotification(sbn.getPackageName(), sbn.getTag(), sbn.getId());

    }

}