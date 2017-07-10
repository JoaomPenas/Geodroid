package com.example.ps.geodroidapp.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ps.geodroidapp.BussulaApi;
import com.example.ps.geodroidapp.DB.SqlDataBase;
import com.example.ps.geodroidapp.Domain.User;
import com.example.ps.geodroidapp.R;
import com.example.ps.geodroidapp.Utils.AuthenticateResponse;
import com.example.ps.geodroidapp.Utils.Utils;

import java.util.Random;

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
    private String session;
    private AuthenticateResponse authenticateResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = SqlDataBase.getInstance(this);

        PopulateDatabase(); // TODO APAGAR NA VERSÃO FINAL

        mainActivityStartIntent = new Intent(this, MainActivityStart.class);
        sessionMenu             = new Intent(this, SessionMenu.class);

        email                   = (EditText) findViewById(R.id.intro_et_email);
        pass                    = (EditText) findViewById(R.id.intro_et_password);
        enterButton             = (Button)   findViewById(R.id.intro_button_enter);

        Intent aux              = getIntent();
        extras                  = aux.getExtras();
        final Boolean[]haveSession ={false};
        haveSession[0]         = aux.hasExtra("SessionName");

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(Login.this, DialogInterface.BUTTON_NEUTRAL);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage(Login.this.getString(R.string.login_authenticating));
                progressDialog.show();

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                String userEmail = email.getText().toString();
                                String userPass = pass.getText().toString();

                                if ( db.isUserAvailable(userEmail, userPass) &&  !haveSession[0]){
                                    mainActivityStartIntent.putExtra("usermail", userEmail);
                                    Toast.makeText(Login.this,Login.this.getString(R.string.login_wellcome) +" "+ db.getUserName(userEmail)+"!", Toast.LENGTH_LONG).show();
                                    startActivity(mainActivityStartIntent);
                                    finish();
                                }
                                else {
                                    requestApiToken(new User(userEmail, userPass));
                                }
                                progressDialog.dismiss();
                            }
                        }, 3000);
            }
        });

    }

    /**
     * Populates the database (FOR DEMONSTRATION PROPOSE ONLY!)
     * TODO: APAGAR MÉTODO NA VERSÃO FINAL
     */
    private void PopulateDatabase() {
        db.insertSession("Arrabida");
        db.insertSession("Foz Coa");
        db.insertDiscontinuity(RandInt(360),RandInt(90),RandRange(38.52,38.6),RandRange(-9,-8.9),RandInt(5),RandInt(5),RandInt(5),RandInt(5),RandInt(5),"Random by AndroidApp", Utils.getCurrentDateTime(),0,"w@mail.com", "Arrabida");
        db.insertDiscontinuity(RandInt(360),RandInt(90),RandRange(38.52,38.6),RandRange(-9,-8.9),RandInt(5),RandInt(5),RandInt(5),RandInt(5),RandInt(5),"Random by AndroidAppZ@mail", Utils.getCurrentDateTime(),0,"z@mail.com", "Arrabida");
        db.insertDiscontinuity(RandInt(360),RandInt(90),RandRange(38.52,38.6),RandRange(-9,-8.9),RandInt(5),RandInt(5),RandInt(5),RandInt(5),RandInt(5),"Random by AndroidApp", Utils.getCurrentDateTime(),0,"w@mail.com", "Foz Coa");
        Toast.makeText(Login.this,"Inserted 3 new discontinuities: 2 in Arrabida (2 diferent users) and 1 in Foz Coa!", Toast.LENGTH_SHORT).show();
    }
    private double RandRange(double Low, double High) {
        return Math.random()*(High-Low) + Low;
    }
    private int RandInt(int max) {
        return (int)Math.round(Math.random()*(max));
    }

    /**
     * Get's a token from Api, and start the next Activity (mainActivity or sessionMenu)
     * @param user - instance of User (class in Domain package)
     */
    private void requestApiToken(User user){
        BussulaApi service = BussulaApi.Factory.getInstance();
        Call<AuthenticateResponse> requestCatalog = service.postAuthenticate(user);
        requestCatalog.enqueue(new Callback<AuthenticateResponse>() {
            @Override
            public void onResponse(Call<AuthenticateResponse> call, Response<AuthenticateResponse> response) {
                //Log.d("JJJ",response.message());
                authenticateResponse = response.body();
                if(authenticateResponse.isSuccess()){

                    String token = authenticateResponse.getToken();
                    String username = authenticateResponse.getMessage();
                    String userEmail = email.getText().toString();

                    if(extras != null){
                        Toast.makeText(Login.this,Login.this.getString(R.string.login_authenticated), Toast.LENGTH_LONG).show();
                        sessionMenu.putExtra("usermail", userEmail);
                        sessionMenu.putExtra("token", token);
                        sessionMenu.putExtra("SessionName", extras.getString("SessionName"));
                        startActivity(sessionMenu);
                        finish();
                    }
                    else {
                        Toast.makeText(Login.this,Login.this.getString(R.string.login_wellcome_to_geodroid) + " "+username+"!", Toast.LENGTH_LONG).show();
                        long randomSalt = new Random().nextLong();
                        String hashPass = Utils.createPassHash(pass.getText().toString(),randomSalt);
                        db.insertUser(userEmail,username,hashPass,randomSalt,token);
                        mainActivityStartIntent.putExtra("usermail", userEmail);
                        mainActivityStartIntent.putExtra("token", token);
                        startActivity(mainActivityStartIntent);
                        finish();
                    }
                }
                else{
                    Toast.makeText(Login.this,"Not authenticated...", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<AuthenticateResponse> call, Throwable t) {
                if(!Utils.isOnline(getApplicationContext())) {
                        Toast.makeText(Login.this, "No Internet Connection...", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(Login.this,"Authenticate Fail...(requestApiToken)", Toast.LENGTH_LONG).show();
                }
                authenticateResponse = null;
            }
        });
    }

}
