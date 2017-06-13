package com.example.ps.geodroidapp.Activities;
import com.example.ps.geodroidapp.BussulaApi;
import com.example.ps.geodroidapp.DB.SqlDataBase;
import com.example.ps.geodroidapp.Domain.Discontinuity;
import com.example.ps.geodroidapp.Domain.DtoDiscontinuity;
import com.example.ps.geodroidapp.Domain.User;
import com.example.ps.geodroidapp.R;
import com.example.ps.geodroidapp.Utils.AuthenticateResponse;
import com.example.ps.geodroidapp.Utils.Utils;
import com.google.gson.Gson;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    private Intent loginAct;
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
        loginAct         = new Intent(this, Login.class);

        Intent aux = getIntent();
        Bundle extras = aux.getExtras();
        if(extras!=null){
            session = extras.getString("SessionName");
            usermail=extras.getString("usermail");
            token = extras.getString("token");
            sessionName.setText("Session: "+ session);

        }
        compassIntent.putExtra("Session", session);
        compassIntent.putExtra("usermail",usermail);
        statisticsIntent.putExtra("Session", session);
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
                Utils.saveOnFile(SqlDataBase.getInstance(getApplicationContext()).getAllDiscontinuities(session),SessionMenu.this);
                String fileName = "Session"+".csv";
                //File f = new File(SessionMenu.this.getFilesDir().getAbsolutePath(), fileName);
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
                final ArrayList <Discontinuity>list = db.areUploads(session);// db.getAllDiscontinuities(session);
                DtoDiscontinuity dtoDiscontinuity = new DtoDiscontinuity(list);
                //requestApiToken(db.getUser(usermail));
                //postDiscont(service,dtoDiscontinuity,list,buttonUpload);
                //Call <ResponseBody> postDisc = service.postDiscontinuities(dtoDiscontinuity);
                if(token == null){
                    loginAct.putExtra("SessionName",session);
                    startActivity(loginAct);
                    finish();
                }else
                    postDiscontinuity(token,dtoDiscontinuity,buttonUpload,list);
                /*
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
                });*/


            }
        });

    }
    private void postDiscontinuity(String token, DtoDiscontinuity dtoDiscontinuity,final View buttonUpload, final ArrayList <Discontinuity> list){
        Log.d("TTTT",token);
        Call<ResponseBody> requestCatalogg = service.postDiscontinuities(token,dtoDiscontinuity);
        requestCatalogg.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                        Toast.makeText(SessionMenu.this,response.message()+", dados enviados para o Servidor!", Toast.LENGTH_LONG).show();
                        db.putSent(list);
                        buttonUpload.setVisibility(View.INVISIBLE);
                }
                else{
                    try {
                        AuthenticateResponse authenticate = gson.fromJson(response.errorBody().string(),AuthenticateResponse.class);
                        //Adicionar campo ao erro da mensagem para saber se o erro foi do token não estar válido; se foi erro da password...
                        Toast.makeText(SessionMenu.this,response.message()+authenticate.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(SessionMenu.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("JX",t.getMessage());
                Toast.makeText(SessionMenu.this,"Cannot update users from Database...(requestGetAllUsers)"+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void requestApiToken(User user){
        BussulaApi servicee = BussulaApi.Factory.getInstance();
        Call<AuthenticateResponse> requestCatalog = servicee.postAuthenticate(user);
        requestCatalog.enqueue(new Callback<AuthenticateResponse>() {
            @Override
            public void onResponse(Call<AuthenticateResponse> call, Response<AuthenticateResponse> response) {
                AuthenticateResponse authenticateResponse = response.body();
                if(authenticateResponse.isSuccess()){
                    token = authenticateResponse.getToken();
                }
                else{
                    Toast.makeText(SessionMenu.this,"Sorry try again...", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<AuthenticateResponse> call, Throwable t) {
                Log.d("JJ",t.getMessage());
                Toast.makeText(SessionMenu.this,"Cannot update users from Database...(requestApiToken)", Toast.LENGTH_LONG).show();
            }
        });
        //return reqTokenRes;
    }
    /*
    private void postDiscont(BussulaApi service, DtoDiscontinuity dtoDiscontinuity,final ArrayList <Discontinuity>list,final View buttonUpload){
        Call <AuthenticateResponse> postDisc = service.postDiscontinuities(token,dtoDiscontinuity);
        postDisc.enqueue(new Callback<AuthenticateResponse>() {
            @Override
            public void onResponse(Call<AuthenticateResponse> call, Response<AuthenticateResponse> response) {
                AuthenticateResponse authenticateResponse = response.body();
                if(authenticateResponse.isSuccess()) {
                    Toast.makeText(SessionMenu.this, response.message() + ", dados enviados para o Servidor!", Toast.LENGTH_LONG).show();
                    db.putSent(list);
                    buttonUpload.setVisibility(View.INVISIBLE);
                }

            }
            @Override
            public void onFailure(Call<AuthenticateResponse> call, Throwable t) {
                Toast.makeText(SessionMenu.this,t.getMessage()+", dados NAO enviados para o Servidor!", Toast.LENGTH_LONG).show();
                Log.d("JJJJJ",""+t);
            }
        });
    }*/
    @Override
    protected void onResume() {
        super.onResume();
        if(db.areUploads(session).size()!=0){
            uploadButton.setVisibility(View.VISIBLE);
        }else
            uploadButton.setVisibility(View.INVISIBLE);
    }


}
