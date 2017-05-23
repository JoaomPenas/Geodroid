package com.example.ps.geodroidapp.Utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static android.util.Base64.DEFAULT;
import static android.util.Base64.NO_WRAP;
import static android.util.Base64.encodeToString;

public class Utils {

    public static String getNormaliedAtitudeFromRawAtitude(int azimuth, int dip) {
        String res ="";
        //(char)0x00B0 caracter do grau º
        if (azimuth >=0 && azimuth<90)   res = ("N" + azimuth       + (char)0x00B0 + "E" + ", " + dip + (char)0x00B0 + "SE");
        if (azimuth >=90 && azimuth<180) res = ("N" + (180-azimuth) + (char)0x00B0 + "W" + ", " + dip + (char)0x00B0 + "SW");
        if (azimuth >=180 && azimuth<270)res = ("N" + (azimuth-180) + (char)0x00B0 + "E" + ", " + dip + (char)0x00B0 + "NW");
        if (azimuth >=270 && azimuth<=360)res = ("N" + (360-azimuth) + (char)0x00B0 + "W" + ", " + dip + (char)0x00B0 + "NE");
        return res;
    }
    /*
    public static String getNormaliedAtitudeFromRawAtitude(int azimuth, int dip) {
        String res ="";
        if (azimuth >=0 && azimuth<90)    res = ("N" + azimuth       +   (char) 0x00B0+ "E" +", "+ (dip<0? -dip+"SE":dip+"NW"));
        if (azimuth >=90 && azimuth<180)   res = ("N" + (180-azimuth) +   (char) 0x00B0 +"W" +", "+ (dip<0? -dip+"SW":dip+"NE"));
        if (azimuth >=180 && azimuth<270)  res = ("N" + (azimuth-180) +   (char) 0x00B0+ "E" +", "+ (dip<0? -dip+"NW":dip+"SE"));
        if (azimuth >=270 && azimuth<=360)  res = ("N" + (360-azimuth) +   (char) 0x00B0+ "W" +", "+ (dip<0? -dip+"NE":dip+"SW"));
        return res;
    }
    */

    public static boolean phoneIsOnline(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static boolean verifyPassword(String original, String attempt){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String attemptPass = encodeToString(md.digest(attempt.getBytes()),NO_WRAP);
        return  original.equals(attemptPass);
    }
}
