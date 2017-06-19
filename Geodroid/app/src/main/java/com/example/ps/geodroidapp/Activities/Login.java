package com.example.ps.geodroidapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.example.ps.geodroidapp.Utils.Utils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

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
        setContentView(R.layout.activity_login);
        db = SqlDataBase.getInstance(this);

        Log.d("HPS", "1st level Login Activity oncreate");
        // POPULATE DATABASE
        db.insertSession("Arrabida");
        db.insertSession("Foz Coa");
        db.insertDiscontinuity(new Discontinuity(10,(int)(Math.random()*(360)),(int)(Math.random()*(90)),38.52,-8.99, 2,4, 1, 2, 2,"",0,"w@mail.com", "Arrabida"));

        Toast.makeText(Login.this,"Inserted 6 discontinuities in SqliteDB \n(3 in Arrabida and 3 in Foz Coa)" , Toast.LENGTH_SHORT).show();

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
               /* final ProgressDialog progressDialog = new ProgressDialog(Login.this,
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
                                    Toast.makeText(Login.this,"Wellcome!" + email.getText().toString(), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(Login.this,"Wellcome!" + email.getText().toString(), Toast.LENGTH_LONG).show();
                    startActivity(mainActivityStartIntent);
                    //finish();
                }
                else{
                    requestApiToken(new User(email.getText().toString(), pass.getText().toString()));
                 }

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
                    String token = authenticateResponse.getToken();
                    if(extras != null){
                        sessionMenu.putExtra("usermail", email.getText().toString());
                        sessionMenu.putExtra("token", token);//authenticateResponse.getToken());
                        sessionMenu.putExtra("SessionName", extras.getString("SessionName"));//authenticateResponse.getToken());
                        startActivity(sessionMenu);
                        finish();
                    }else {
                        Toast.makeText(Login.this, "Updated internal DB from Remote DB!\nAvailable users: \n", Toast.LENGTH_SHORT).show();
                        long randomSalt = new Random().nextLong();
                        String hashPass = Utils.createPassHash(pass.getText().toString(),randomSalt);
                        db.insertUser(email.getText().toString(),hashPass,randomSalt,token);
                        mainActivityStartIntent.putExtra("usermail", email.getText().toString());
                        mainActivityStartIntent.putExtra("token", token);//);
                        startActivity(mainActivityStartIntent);
                    }
                }
                else{
                    Toast.makeText(Login.this,"Sorry try again...", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<AuthenticateResponse> call, Throwable t) {
                if(!Utils.isOnline(getApplicationContext())) {
                        Toast.makeText(Login.this, "Connect to internet", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Login.this,"Authenticate Fail...(requestApiToken)", Toast.LENGTH_LONG).show();
                }
                authenticateResponse = null;
            }
        });
    }



}
