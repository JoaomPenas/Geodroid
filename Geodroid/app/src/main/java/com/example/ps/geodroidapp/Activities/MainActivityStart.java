package com.example.ps.geodroidapp.Activities;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.ps.geodroidapp.DB.SqlDataBase;
import com.example.ps.geodroidapp.R;

public class MainActivityStart extends AppCompatActivity {

    private String usermail ="",token="";

    ImageButton existSessionbutton;
    ImageButton createSessionbutton;
    Intent listSession, createSession;
    private SqlDataBase sql=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_start);
        Log.d("HPS", "2nd level MainActivityStart Activity oncreate");

        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            usermail = extras.getString("usermail");
            token = extras.getString("token");
            Toast.makeText(MainActivityStart.this,"Wellcome "+ usermail,Toast.LENGTH_LONG).show();
        }


        listSession     = new Intent(this, ListSession.class);
        createSession   = new Intent(this, CreateSession.class);

        existSessionbutton = (ImageButton) findViewById(R.id.button_existingSession);
        createSessionbutton = (ImageButton) findViewById(R.id.button_createSession);


        existSessionbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listSession.putExtra("usermail", usermail);
                listSession.putExtra("token", token);
                startActivity(listSession);
                //MainActivityStart.this.startActionMode(mActionModeCallback);
                //registerForContextMenu(v);
            }
        });

        createSessionbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSession.putExtra("usermail", usermail);
                createSession.putExtra("token", token);
                startActivity(createSession);
            }
        });

    }
    /*
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_def, menu);                                        // VER QUESTAO DOS MENUS
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Toast.makeText(MainActivityStart.this,"ENTER", Toast.LENGTH_SHORT).show();
        //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_edit:
                Toast.makeText(MainActivityStart.this, "EDIT", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_delete:
                Toast.makeText(MainActivityStart.this, "DELETE", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    */
}
