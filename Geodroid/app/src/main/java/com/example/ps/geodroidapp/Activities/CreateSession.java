package com.example.ps.geodroidapp.Activities;
import com.example.ps.geodroidapp.DB.SqlDataBase;
import com.example.ps.geodroidapp.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateSession extends AppCompatActivity {

    Button b;
    EditText et;
    String usermail;
    String session;
    Intent sessionMenuIntent;
    SqlDataBase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);

        b = (Button) findViewById(R.id.createsession_button_create);
        et = (EditText) findViewById(R.id.create_session_editText_createSession_name);

         dataBase = SqlDataBase.getInstance(getApplicationContext());

        sessionMenuIntent = new Intent(this,SessionMenu.class);

        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            usermail = extras.getString("usermail");
            Toast.makeText(CreateSession.this,"Create sesseion "+ usermail,Toast.LENGTH_LONG).show();
        }

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session=et.getText().toString();
                sessionMenuIntent.putExtra("SessionName", session);
                sessionMenuIntent.putExtra("usermail",usermail);
                dataBase.insertSession(session);
                startActivity(sessionMenuIntent);
                finish();
            }
        });
    }
}
