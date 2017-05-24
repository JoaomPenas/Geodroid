package com.example.ps.geodroidapp.Activities;
import com.example.ps.geodroidapp.BussulaApi;
import com.example.ps.geodroidapp.DB.SqlDataBase;
import com.example.ps.geodroidapp.Domain.Discontinuity;
import com.example.ps.geodroidapp.Domain.DtoDiscontinuity;
import com.example.ps.geodroidapp.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                Uri uri = Uri.parse("drawable/compass_des.png");
                emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{"23984@alunos.isel.ipl.pt"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.putExtra(Intent.EXTRA_STREAM,uri);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Text");
                startActivity(emailIntent);
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
}
