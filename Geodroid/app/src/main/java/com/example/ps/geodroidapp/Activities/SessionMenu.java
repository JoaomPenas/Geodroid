package com.example.ps.geodroidapp.Activities;
import com.example.ps.geodroidapp.BussulaApi;
import com.example.ps.geodroidapp.DB.SqlDataBase;
import com.example.ps.geodroidapp.Domain.Discontinuity;
import com.example.ps.geodroidapp.Domain.DtoDiscontinuity;
import com.example.ps.geodroidapp.R;
import com.example.ps.geodroidapp.Utils.AuthenticateResponse;
import com.example.ps.geodroidapp.Utils.Utils;
import com.google.gson.Gson;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionMenu extends AppCompatActivity {

    private String usermail="",token = "";
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
    private Intent loginActIntent;
    private final BussulaApi service = BussulaApi.Factory.getInstance();
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

        dataTableIntent  = new Intent(this, DataTable.class);
        compassIntent    = new Intent(this, Compass.class);
        dataMapIntent    = new Intent(this, DataMap.class);
        statisticsIntent = new Intent(this, StatisticTable.class);
        loginActIntent = new Intent(this, Login.class);

        Intent aux = getIntent();
        Bundle extras = aux.getExtras();
        if(extras!=null){
            session = extras.getString("SessionName");
            usermail=extras.getString("usermail");
            token = extras.getString("token");
            sessionName.setText(session);
        }

        newRegistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compassIntent.putExtra("Session", session);
                compassIntent.putExtra("User",usermail);
                startActivity(compassIntent);
            }
        });

        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataTableIntent.putExtra("Session", session);
                dataTableIntent.putExtra("User", usermail);
                startActivity(dataTableIntent);
            }
        });

        statisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statisticsIntent.putExtra("Session", session);
                statisticsIntent.putExtra("User", usermail);
                startActivity(statisticsIntent);
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataMapIntent.putExtra("Session", session);
                dataMapIntent.putExtra("User", usermail);
                startActivity(dataMapIntent);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.saveOnFile(SqlDataBase.getInstance(getApplicationContext()).getAllDiscontinuities(session,usermail),SessionMenu.this);
                String fileName = "Session"+".csv";
                File f = new File(SessionMenu.this.getExternalCacheDir(), fileName);
                Intent emailIntent = ShareCompat.IntentBuilder
                        .from(SessionMenu.this)
                        .setType("text/plain")
                        .setStream(Uri.fromFile(f))
                        .setText("teste")
                        .getIntent();
                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                     startActivity(emailIntent);
                }
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View buttonUpload = v;
                final ArrayList <Discontinuity>list = db.getDiscontinuitysNotUploaded(session,usermail);
                DtoDiscontinuity dtoDiscontinuity = new DtoDiscontinuity(list);
                if(token == null ) {
                    token = db.getUserToken(usermail);
                }
                postDiscontinuity(token,dtoDiscontinuity,buttonUpload,list);
            }
        });
    }

    /**
     * Private method to upload the discontinuities to server
     * @param token to validate on API
     * @param dtoDiscontinuity dto with the discontinuities to upload to server
     * @param buttonUpload button to set invisible if the response is sucessfull
     * @param list to set sent on device data base
     */
    private void postDiscontinuity(String token, DtoDiscontinuity dtoDiscontinuity,final View buttonUpload, final ArrayList <Discontinuity> list){

        Call<ResponseBody> requestCatalogg = service.postDiscontinuities(token,dtoDiscontinuity);
        requestCatalogg.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                        Toast.makeText(SessionMenu.this,response.message()+", dados enviados para o Servidor!", Toast.LENGTH_LONG).show();
                        db.setAllSentDiscontinuity(list);
                        buttonUpload.setVisibility(View.INVISIBLE);
                }
                else{
                    try {
                        AuthenticateResponse authenticate = gson.fromJson(response.errorBody().string(),AuthenticateResponse.class);
                        loginActIntent.putExtra("SessionName",session);
                        startActivity(loginActIntent);
                        finish();
                        //Adicionar campo ao erro da mensagem na API para saber se o erro foi do token não estar válido; se foi erro da password...
                        Toast.makeText(SessionMenu.this,response.message()+authenticate.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(SessionMenu.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(!Utils.isOnline(getApplicationContext())) {
                    Toast.makeText(SessionMenu.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(SessionMenu.this, "Not upload! Please try again later..." + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(db.getDiscontinuitysNotUploaded(session,usermail).size()!=0){
            uploadButton.setVisibility(View.VISIBLE);
        }else
            uploadButton.setVisibility(View.INVISIBLE);
    }
}
