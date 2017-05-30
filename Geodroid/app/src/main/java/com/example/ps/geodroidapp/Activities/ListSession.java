package com.example.ps.geodroidapp.Activities;
import com.example.ps.geodroidapp.DB.SqlDataBase;
import com.example.ps.geodroidapp.Domain.Session;
import com.example.ps.geodroidapp.R;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListSession extends AppCompatActivity {

    String usermail,token="";
    private TextView userTv;
    private ListView list;
    private ArrayAdapter<String> adapter;
    private Toast t;
    private Intent sessionAct;

    SqlDataBase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_session);

        Log.d("HPS", "3rd level - ListSession Activity oncreate");

        userTv = (TextView) findViewById(R.id.list_session_userName);

        Intent aux = getIntent();
        Bundle extras = aux.getExtras();
        if(extras!=null) {
            usermail = extras.getString("usermail");
            token = extras.getString("token");
            userTv.setText(usermail);
        }

        list = (ListView)findViewById(R.id.listview_list);
        dataBase =SqlDataBase.getInstance(getApplicationContext());

        ArrayList<Session> sessions = dataBase.getAllSessions();
        List<String> listSessions = new ArrayList<>();
        for (Session s:sessions) {
            listSessions.add(s.getName());
        }

        sessionAct = new Intent(this, SessionMenu.class);

        adapter = new ArrayAdapter<String>(this,R.layout.list_item,R.id.textView_item,new ArrayList<>(listSessions));//
        list.setAdapter(adapter);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                registerForContextMenu(view);
                return true;
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_SHORT).show();
                sessionAct.putExtra("SessionName",((TextView)view.findViewById(R.id.textView_item)).getText().toString());
                sessionAct.putExtra(("usermail"), usermail);
                sessionAct.putExtra(("token"), token);
               /* view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        registerForContextMenu(v);
                        return false;
                    }
                });*/
                startActivity(sessionAct);
            }

        });

    }
    View textSelect;
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_session, menu);
        // idTbr = (int) v.getTag();
        textSelect = v;
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_deleteSessionRow:
                list.removeView(textSelect);
                list.invalidate();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
