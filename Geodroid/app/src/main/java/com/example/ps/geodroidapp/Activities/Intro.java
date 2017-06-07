package com.example.ps.geodroidapp.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ps.geodroidapp.BussulaApi;
import com.example.ps.geodroidapp.DB.SqlDataBase;
import com.example.ps.geodroidapp.Domain.Discontinuity;
import com.example.ps.geodroidapp.Domain.DtoCatalog;
import com.example.ps.geodroidapp.Domain.User;
import com.example.ps.geodroidapp.R;
import com.example.ps.geodroidapp.Utils.AuthenticateResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Intro extends AppCompatActivity {

    private Intent mainActivityStartIntent, sessionMenu;
    private EditText email;
    private EditText pass;
    private Button enterButton;
    private SqlDataBase db;
    private Bundle extras;
    private final BussulaApi service = BussulaApi.Factory.getInstance();
    private AuthenticateResponse authenticateResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        db = SqlDataBase.getInstance(this);

        Log.d("HPS", "1st level Intro Activity oncreate");
        // POPULATE DATABASE
        db.insertSession("Arrabida");
        db.insertSession("Foz Coa");
        db.insertDiscontinuity(new Discontinuity(10,(int)(Math.random()*(360)),(int)(Math.random()*(90)),38.52,-8.99, 2,4, 1, 2, 2,"",0,"w@mail.com", "Arrabida"));

        Toast.makeText(Intro.this,"Inserted 6 discontinuities in SqliteDB \n(3 in Arrabida and 3 in Foz Coa)" , Toast.LENGTH_SHORT).show();

        mainActivityStartIntent = new Intent(this, MainActivityStart.class);
        sessionMenu = new Intent(this, SessionMenu.class);
        email = (EditText) findViewById(R.id.intro_et_email);
        pass = (EditText) findViewById(R.id.intro_et_password);
        enterButton = (Button) findViewById(R.id.intro_button_enter);

        Intent aux = getIntent();
        extras = aux.getExtras();


        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* final ProgressDialog progressDialog = new ProgressDialog(Intro.this,
                        DialogInterface.BUTTON_NEUTRAL);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                // On complete call either onLoginSuccess or onLoginFailed
                                if (db.IsUserAvailable(email.getText().toString(), pass.getText().toString())){
                                    mainActivityStartIntent.putExtra("usermail",email.getText().toString());
                                    Toast.makeText(Intro.this,"Wellcome!" + email.getText().toString(), Toast.LENGTH_LONG).show();
                                    startActivity(mainActivityStartIntent);
                                }
                                else{
                                    requestApiToken(new User(email.getText().toString(), pass.getText().toString()));
                                }
                                progressDialog.dismiss();
                            }
                        }, 3000);*/
                if (db.IsUserAvailable(email.getText().toString(), pass.getText().toString()) &&  extras == null){
                    mainActivityStartIntent.putExtra("usermail",email.getText().toString());
                    Toast.makeText(Intro.this,"Wellcome!" + email.getText().toString(), Toast.LENGTH_LONG).show();
                    startActivity(mainActivityStartIntent);
                    //finish();
                }
                else{
                    Toast.makeText(Intro.this,"ELSE",Toast.LENGTH_SHORT).show();
                    requestApiToken(new User(email.getText().toString(), pass.getText().toString()));
                    //requestGetAllUsers("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6IndAbWFpbC5jb20iLCJpYXQiOjE0OTYyMzkyMzYsImV4cCI6MTQ5NjMyNTYzNn0.w3vaAhH-0SDfagFOCZGfQAtRpsb96Y28P0bzGI0Ns8Y");
                }

            }
        });

    }
    private void requestGetAllUsers(final String token){
        Log.d("TTTT",token);
        Call<ResponseBody> requestCatalogg = service.getUser(token);
        requestCatalogg.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    try {
                        DtoCatalog xpto = gson.fromJson(response.body().string(), DtoCatalog.class);
                        List<User> list = xpto.getUser();
                        db.insertUsers(list);
                        Log.d("JY", response.message());
                        if(extras != null){
                            sessionMenu.putExtra("usermail", email.getText().toString());
                            sessionMenu.putExtra("token", token);//authenticateResponse.getToken());
                            sessionMenu.putExtra("SessionName", extras.getString("SessionName"));//authenticateResponse.getToken());
                            startActivity(sessionMenu);
                            finish();
                        }else {
                            Toast.makeText(Intro.this, "Updated internal DB from Remote DB!\nAvailable users: \n", Toast.LENGTH_SHORT).show();
                            mainActivityStartIntent.putExtra("usermail", email.getText().toString());
                            mainActivityStartIntent.putExtra("token", token);//authenticateResponse.getToken());
                            startActivity(mainActivityStartIntent);
                        }
                        //finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(Intro.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    try {
                        AuthenticateResponse authenticate = gson.fromJson(response.errorBody().string(),AuthenticateResponse.class);
                        Toast.makeText(Intro.this,response.message()+authenticate.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(Intro.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("JX",t.getMessage());
                Toast.makeText(Intro.this,"Cannot update users from Database...(requestGetAllUsers)"+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void requestApiToken(User user){
        BussulaApi servicee = BussulaApi.Factory.getInstance();
        Call<AuthenticateResponse> requestCatalog = servicee.postAuthenticate(user);
        requestCatalog.enqueue(new Callback<AuthenticateResponse>() {
            @Override
            public void onResponse(Call<AuthenticateResponse> call, Response<AuthenticateResponse> response) {
                Log.d("JJJ",response.message());
                authenticateResponse = response.body();
                if(authenticateResponse.isSuccess()){
                    requestGetAllUsers(authenticateResponse.getToken());
                }
                else{
                    Toast.makeText(Intro.this,"Sorry try again...", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<AuthenticateResponse> call, Throwable t) {
                authenticateResponse = null;
                Log.d("JJ",t.getMessage());
                Toast.makeText(Intro.this,"Authenticate Fail...(requestApiToken)", Toast.LENGTH_LONG).show();
            }
        });
        //return reqTokenRes;
    }

}
