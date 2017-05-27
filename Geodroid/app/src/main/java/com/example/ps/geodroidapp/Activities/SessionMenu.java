package com.example.ps.geodroidapp.Activities;
import com.example.ps.geodroidapp.BussulaApi;
import com.example.ps.geodroidapp.DB.SqlDataBase;
import com.example.ps.geodroidapp.Domain.Discontinuity;
import com.example.ps.geodroidapp.Domain.DtoDiscontinuity;
import com.example.ps.geodroidapp.Domain.Session;
import com.example.ps.geodroidapp.R;
import com.github.mikephil.charting.utils.FileUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionMenu extends AppCompatActivity {

    private String usermail="";
    private String session ="";
    private TextView sessionName;
    private Button newRegistButton;
    private Button dataButton;
    private Button statisticsButton;
    private Button mapButton;
    private ImageButton uploadButton;
    private ImageButton shareButton;
    private Intent compassIntent;
    private Intent dataTableIntent;
    private Intent dataMapIntent;
    private Intent statisticsIntent;

    private SqlDataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_menu);
        db = SqlDataBase.getInstance(this);


        sessionName         = (TextView) findViewById(R.id.session_menu_tv_session_name);
        newRegistButton     = (Button)findViewById(R.id.session_menu_button_new_regist);
        dataButton          = (Button)findViewById(R.id.session_menu_button_sessiondata);
        statisticsButton    = (Button)findViewById(R.id.session_menu_button_statistics);
        mapButton           = (Button)findViewById(R.id.session_menu_button_map);
        uploadButton        = (ImageButton)findViewById(R.id.imageButton_upload);
        shareButton         = (ImageButton)findViewById(R.id.imageButton_share);

        dataTableIntent = new Intent(this, DataTable.class);
        compassIntent       = new Intent(SessionMenu.this,Compass.class);
        dataMapIntent = new Intent(SessionMenu.this, DataMap.class);
        statisticsIntent=new Intent(SessionMenu.this, StatisticTable.class);

        Intent aux = getIntent();
        Bundle extras = aux.getExtras();
        if(extras!=null){
            session = extras.getString("SessionName");
            usermail=extras.getString("usermail");
            sessionName.setText("Session: "+ session);

        }
        compassIntent.putExtra("Session", session);
        compassIntent.putExtra("usermail",usermail);

        newRegistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(compassIntent);
            }
        });
        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataTableIntent.putExtra("Session", session);
                startActivity(dataTableIntent);
            }
        });
        statisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(statisticsIntent);
            }
        });
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataMapIntent.putExtra("Session", session);
                startActivity(dataMapIntent);
            }
        });
        final Activity act = this;
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOnFile(SqlDataBase.getInstance(getApplicationContext()).getAllDiscontinuities(session));
                String fileName = "Session"+".csv";
                //File f = new File(SessionMenu.this.getFilesDir().getAbsolutePath(), fileName);
                File f = new File(SessionMenu.this.getExternalCacheDir(), fileName);
                //f.setReadable(true,false);
                Intent emailIntent = ShareCompat.IntentBuilder
                        .from(SessionMenu.this)
                        .setType("text/plain")
                        .setStream(Uri.fromFile(f))
                        .setText("teste")
                        .getIntent();

                /*String fileName = "Session"+".csv";
                saveOnFile(SqlDataBase.getInstance(getApplicationContext()).getAllDiscontinuities(session));
                String filePath = SessionMenu.this.getFilesDir().getAbsolutePath();//returns current directory.
               // String filePath = .this.getFilesDir().getAbsolutePath();//returns current directory.
               // File file = new File(SessionMenu.this.getCacheDir(), fileName);
                File file = new File(filePath, fileName);
                file.setReadable(true,false);
                String na = file.toString();
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{"23984@alunos.isel.ipl.pt"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                emailIntent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(file));
                emailIntent.setType("plain/text");
                //emailIntent.putExtra(Intent.EXTRA_TEXT, "Text");
                */
                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                     startActivity(emailIntent);
                }

            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BussulaApi service = BussulaApi.Factory.getInstance();
                final View buttonUpload = v;
                final ArrayList <Discontinuity>list =db.areUploads(session);// db.getAllDiscontinuities(session);

                DtoDiscontinuity dtoDiscontinuity = new DtoDiscontinuity(list);

                Call <ResponseBody> postDisc = service.postDiscontinuities(dtoDiscontinuity);

                postDisc.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Toast.makeText(SessionMenu.this,response.message()+", dados enviados para o Servidor!", Toast.LENGTH_LONG).show();
                        db.putSent(list);
                        buttonUpload.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(SessionMenu.this,t.getMessage()+", dados NAO enviados para o Servidor!", Toast.LENGTH_LONG).show();
                        Log.d("JJJJJ",""+t);
                    }
                });

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(db.areUploads(session).size()!=0){
            uploadButton.setVisibility(View.VISIBLE);
        }else
            uploadButton.setVisibility(View.INVISIBLE);
    }

    /**
     * guarda o ficheiro .csv no dispositivo
     * @param discontinuitys
     * @return
     */
    public boolean saveOnFile(ArrayList<Discontinuity> discontinuitys){
        String filename = "Session"+".csv";
        String saveContent = "Discontinuity,id,idSession,idUser,direction,dip,latitude,longitude,persistence,aperture,roughness,infilling,weathering\n";
        //FileOutputStream outputStream;
        //File file = new File(this.getCacheDir(), filename);
        File file = new File(this.getExternalCacheDir(), filename);
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
            Toast.makeText(this,"Shared",Toast.LENGTH_LONG).show();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"Not Shared",Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
