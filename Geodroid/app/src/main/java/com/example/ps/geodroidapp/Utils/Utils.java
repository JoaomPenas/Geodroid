package com.example.ps.geodroidapp.Utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.ps.geodroidapp.Domain.Discontinuity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
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

    public static String createPassHash(String pass, long salt){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String hash = pass+salt;
        return encodeToString(md.digest(hash.getBytes()),NO_WRAP);
    }
    /**
     * Guarda o ficheiro .csv no dispositivo
     * @param discontinuitys
     * @return
     */
    public static boolean saveOnFile(ArrayList<Discontinuity> discontinuitys,Context ctx){
        String filename = "Session"+".csv";
        String saveContent = "Discontinuity,id,idSession,idUser,direction,dip,latitude,longitude,persistence,aperture,roughness,infilling,weathering\n";
        //FileOutputStream outputStream;
        //File file = new File(this.getCacheDir(), filename);
        File file = new File(ctx.getExternalCacheDir(), filename);
        for (Discontinuity disc: discontinuitys) {
            saveContent+= disc.toString();
        }
        try {
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(saveContent);
            bw.close();
            /*outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(saveContent.getBytes());
            outputStream.close();*/
            Toast.makeText(ctx,"Shared",Toast.LENGTH_LONG).show();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ctx,"Not Shared",Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
