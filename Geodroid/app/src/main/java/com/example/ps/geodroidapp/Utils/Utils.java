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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.util.Base64.NO_WRAP;
import static android.util.Base64.encodeToString;

public class Utils {

    /**
     * Retorna uma string com representação de uma atitude clássica (e.g. N45W, 30NE) de
     * uma descontinuidade a partir de uma atitude expressa na forma Hand Right Rule
     * (a forma raw do sistema)
     * @param azimuth - azimute segundo a regra Hand Right Rule
     * @param dip -  angulo referente à inclinação máxima do plano
     * @return - string com representação de uma atitude de uma descontinuidade
     */
    public static String getNormaliedAtitudeFromRawAtitude(int azimuth, int dip) {
        String res ="";
        if (azimuth ==0 )                   res = ("N-S" + ", " + dip  + (char)0x00B0 + "E");
        if (azimuth >0 && azimuth<90)       res = ("N" + azimuth       + (char)0x00B0 + "E" + ", " + dip + (char)0x00B0 + "SE");
        if (azimuth ==90)                   res = ("E-W" + ", " + dip  + (char)0x00B0 + "S");
        if (azimuth >90 && azimuth<180)     res = ("N" + (180-azimuth) + (char)0x00B0 + "W" + ", " + dip + (char)0x00B0 + "SW");
        if (azimuth ==180)                  res = ("N-S" + ", " + dip  + (char)0x00B0 + "W");
        if (azimuth >180 && azimuth<270)    res = ("N" + (azimuth-180) + (char)0x00B0 + "E" + ", " + dip + (char)0x00B0 + "NW");
        if (azimuth ==270)                  res = ("E-W" + ", " + dip  + (char)0x00B0 + "N");
        if (azimuth >270 && azimuth<360)    res = ("N" + (360-azimuth) + (char)0x00B0 + "W" + ", " + dip + (char)0x00B0 + "NE");
        return res;
    }

    /**
     * Verifica se existe ligação à internet
     * @return boolean (true se esta ligado a internet)
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Compara a password original (codificada com SHA-256) e outra não codificada (concatenada com um salt)
     * @param original password original (codificada com SHA-256)
     * @param attempt password não codificada (concatenada com um salt)
     * @return boolean (retorna true se corresponde à mesma password)
     */
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

    /**
     * Codifica, com algoritmo SHA-256, a palavra pass juntando o salt (numero aleatório recebido em parâmetro)
     * @param pass - palavra pass
     * @param salt - número aleátorio
     * @return string com hash da palavra pass concatenada com o salt
     */
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
     * Guarda ficheiro no dispositivo com conjunto de descontinuidades em formato csv
     * @param discontinuitys - ArrayList de descontinuidaes
     * @return boolean (true se conseguiu escrever no dispositivo)
     */
    public static boolean saveOnFile(ArrayList<Discontinuity> discontinuitys,Context ctx){
        String filename = "Session"+".csv";
        String saveContent = "DiscontinuityId,idSession,idUser,direction,dip,latitude,longitude,persistence,aperture,roughness,infilling,weathering,note,datatime\n";
        File file = new File(ctx.getExternalCacheDir(), filename);
        for (Discontinuity disc: discontinuitys) {
            saveContent+= disc.toString();
        }
        try {
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(saveContent);
            bw.close();
            Toast.makeText(ctx,"Saved on file",Toast.LENGTH_LONG).show();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ctx,"Not Saved!"+e.getMessage(),Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /**
     * Método para obter uma String da data currente num formato tipo yyyy-MM-dd HH:mm:ss
     * @return String com a data corrente
     */
    public static String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
