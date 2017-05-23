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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Intro extends AppCompatActivity {

    Intent mainActivityStartIntent;
    EditText email;
    EditText pass;
    Button enterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        final SqlDataBase db = SqlDataBase.getInstance(this);

        Log.d("HPS", "1st level Intro Activity oncreate");
        // POPULATE DATABASE
        db.insertSession("Arrabida");
        db.insertSession("Foz Coa");
        //db.insertSession("Barragem de Parada");
        //db.insertSession("TGV PK 23500");
        //db.insertSession("TGV PK 25000");

        db.insertDiscontinuity(new Discontinuity(10,(int)(Math.random()*(360)),(int)(Math.random()*(90)),38.52,-8.99, 2,4, 1, 2, 2,0,"w@mail.com", "Arrabida"));
      /*  db.insertDiscontinuity(new Discontinuity(20,(int)(Math.random()*(360)),(int)(Math.random()*(90)),38.51,-8.98, 2,4, 2, 3, 4,0,"w@mail.com", "Arrabida"));
        db.insertDiscontinuity(new Discontinuity(30,(int)(Math.random()*(360)),(int)(Math.random()*(90)),38.55,-8.97, 5,4, 2, 2, 5,0,"w@mail.com", "Arrabida"));

        db.insertDiscontinuity(new Discontinuity(30,(int)(Math.random()*(360)),(int)(Math.random()*(90)),41.070,-7.139, 5,4, 2, 2, 5,0,"w@mail.com", "Foz Coa"));
        db.insertDiscontinuity(new Discontinuity(130,(int)(Math.random()*(360)),(int)(Math.random()*(90)),41.069,-7.135, 1,2, 2, 2, 5,0,"w@mail.com", "Foz Coa"));
        db.insertDiscontinuity(new Discontinuity(50,(int)(Math.random()*(360)),(int)(Math.random()*(90)),41.075,-7.131, 5,5, 5, 2, 5,0,"w@mail.com", "Foz Coa"));
*/
        Toast.makeText(Intro.this,"Inserted 6 discontinuities in SqliteDB \n(3 in Arrabida and 3 in Foz Coa)" , Toast.LENGTH_SHORT).show();

        mainActivityStartIntent = new Intent(this, MainActivityStart.class);
       // if (Utils.phoneIsOnline(this)){

            BussulaApi service = BussulaApi.Factory.getInstance();
            Call<DtoCatalog> requestCatalog = service.getUser();

            requestCatalog.enqueue(new Callback<DtoCatalog>() {
                @Override
                public void onResponse(Call<DtoCatalog> call, Response<DtoCatalog> response) {
                    DtoCatalog xpto = response.body();
                    List<User> list = xpto.getUser();
                    db.insertUsers(list);
                    ArrayList<User> list2 = db.getAllUsers();

                    String u="";
                    for (User us: list2) {
                        u=u+us.getEmail()+"/"+us.getPass()+"/"+us.getSalt()+"\n";
                    }
                    //Toast.makeText(Intro.this,"Updated internal DB from Remote DB!\nAvailable users: \n"+u , Toast.LENGTH_SHORT).show();
                    Toast.makeText(Intro.this,"Updated internal DB from Remote DB!\nAvailable users: \n" , Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<DtoCatalog> call, Throwable t) {
                    Toast.makeText(Intro.this,"Cannot update users from Database...", Toast.LENGTH_LONG).show();
                }
            });
        //};

        email = (EditText) findViewById(R.id.intro_et_email);
        pass = (EditText) findViewById(R.id.intro_et_password);
        enterButton = (Button) findViewById(R.id.intro_button_enter);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (db.IsUserAvailable(email.getText().toString(), pass.getText().toString())){
                    mainActivityStartIntent.putExtra("usermail",email.getText().toString());
                    Toast.makeText(Intro.this,"Wellcome!" + email.getText().toString(), Toast.LENGTH_LONG).show();
                    startActivity(mainActivityStartIntent);
                }
                else Toast.makeText(Intro.this,"Sorry try again...", Toast.LENGTH_LONG).show();
            }
        });
    }
}
