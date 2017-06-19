package com.example.ps.geodroidapp.Activities;
import com.example.ps.geodroidapp.DB.SqlDataBase;
import com.example.ps.geodroidapp.Domain.Session;
import com.example.ps.geodroidapp.R;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListSession extends AppCompatActivity {

    String usermail,token="";
    private TextView userTextView;
    private ListView list;
    private ArrayAdapter<String> adapter;
    private Intent sessionAct;
    private SqlDataBase db;
    SqlDataBase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_session);
        db = SqlDataBase.getInstance(this);
        Log.d("HPS", "3rd level - ListSession Activity oncreate");

        userTextView = (TextView) findViewById(R.id.list_session_userName);

        Intent aux = getIntent();
        Bundle extras = aux.getExtras();
        if(extras!=null) {
            usermail = extras.getString("usermail");
            token = extras.getString("token");
            userTextView.setText(usermail);
        }

        list = (ListView)findViewById(R.id.listview_list);
        dataBase =SqlDataBase.getInstance(getApplicationContext());

        ArrayList<Session> sessions = dataBase.getAllSessions();
        List<String> listSessions = new ArrayList<>();
        for (Session s:sessions) {
            listSessions.add(s.getName());
        }
        sessionAct = new Intent(this, SessionMenu.class);

        adapter = new ArrayAdapter<String>(this,R.layout.list_item,R.id.textView_item,new ArrayList<>(listSessions));
        list.setAdapter(adapter);

        registerForContextMenu(list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sessionAct.putExtra("SessionName",((TextView)view.findViewById(R.id.textView_item)).getText().toString());
                sessionAct.putExtra(("usermail"), usermail);
                sessionAct.putExtra(("token"), token);
                startActivity(sessionAct);
            }

        });

    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_session, menu);
    }

    String sessionName;

    /**
     * to delete session when long press
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_deleteSessionRow:
                adapter.remove(adapter.getItem(info.position));
                sessionName = ((TextView)info.targetView.findViewById(R.id.textView_item)).getText().toString();
                if(db.getDiscontinuitysNotUploaded(sessionName).size() != 0 ){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ListSession.this);
                    builder.setMessage("Some discontinuities have not uploaded! Do you want to proceed?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    db.deleteSessionDiscontinuity(sessionName,usermail);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
