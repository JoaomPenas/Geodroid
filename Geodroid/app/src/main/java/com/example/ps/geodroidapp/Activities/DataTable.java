package com.example.ps.geodroidapp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ps.geodroidapp.DB.SqlDataBase;
import com.example.ps.geodroidapp.Domain.Discontinuity;
import com.example.ps.geodroidapp.R;

import java.util.ArrayList;

public class DataTable extends AppCompatActivity {
    String session="",user = "";
    private TableLayout tl;
    private TableRow row2;
    SqlDataBase mydb2;
    private Intent extraData;
    private View tbrSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabela);

        mydb2 =  SqlDataBase.getInstance(getApplicationContext());

        tl = (TableLayout) findViewById(R.id.tabela);

        final Bundle extras = getIntent().getExtras();
        if (extras!=null){
            session = extras.getString("Session");
            user = extras.getString("User");
        }

        ArrayList<Discontinuity> serieDescontinuidades = mydb2.getAllDiscontinuities(session,user);
        Toast.makeText(DataTable.this,session, Toast.LENGTH_SHORT).show();

        extraData = new Intent(this, com.example.ps.geodroidapp.Activities.ExtraData.class);

        for (int i = 0; i < serieDescontinuidades.size(); i++){
            row2 = (TableRow) getLayoutInflater().inflate(R.layout.tl_row, tl, false);
            tl.addView(row2);
            setTextOnTableRow(row2,serieDescontinuidades.get(i));
        }
    }

    public void setTextOnTableRow(TableRow tbr, Discontinuity discontinuity){
        ((TextView)row2.findViewById(R.id.tv_id)).setText(""+discontinuity.getId());
        ((TextView)row2.findViewById(R.id.tv_direction)).setText(""+discontinuity.getDirection());
        ((TextView)row2.findViewById(R.id.tv_dip)).setText(""+discontinuity.getDip());
        ((TextView)row2.findViewById(R.id.tv_persist)).setText(""+discontinuity.getPersistence());
        ((TextView)row2.findViewById(R.id.tv_aper)).setText(""+discontinuity.getAperture());
        ((TextView)row2.findViewById(R.id.tv_roug)).setText(""+discontinuity.getRoughness());
        ((TextView)row2.findViewById(R.id.tv_infil)).setText(""+discontinuity.getInfilling());
        ((TextView)row2.findViewById(R.id.tv_weath)).setText(""+discontinuity.getWeathreing());
        registerForContextMenu(tbr);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_datatable, menu);
        tbrSelected = v;
        tbrSelected.setBackgroundColor(Color.GRAY);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_editrow:
                editExtraData(tbrSelected);
                return true;
            case R.id.menu_deleteRow:
                deleteDiscontinuity(tbrSelected);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
        tbrSelected.setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * Delete Discontinuity
     * @param v to delete
     */
    private void deleteDiscontinuity(View v) {
        if(v instanceof TableRow) {
            TableRow tbr = (TableRow) v;
            if (tbr.getChildCount() > 7) {
                int id = Integer.parseInt(((TextView) tbr.getChildAt(0)).getText().toString());
                mydb2.deleteDiscontinuity(id);
                tl.removeView(tbrSelected);
            }
        }
    }

    /**
     * Edit Discontinuity
     * @param v to edit
     */
    private void editExtraData(View v){
        if(v instanceof TableRow){
            TableRow tbr = (TableRow) v;
            if (tbr.getChildCount()>7) {
                int id      = Integer.parseInt(((TextView)tbr.getChildAt(0)).getText().toString());
                int pers    = Integer.parseInt(((TextView)tbr.getChildAt(3)).getText().toString());
                int aper    = Integer.parseInt(((TextView)tbr.getChildAt(4)).getText().toString());
                int roug    = Integer.parseInt(((TextView)tbr.getChildAt(5)).getText().toString());
                int infil   = Integer.parseInt(((TextView)tbr.getChildAt(6)).getText().toString());
                int weat    = Integer.parseInt(((TextView)tbr.getChildAt(7)).getText().toString());

                extraData.putExtra("Session",session);
                extraData.putExtra("User",user);
                extraData.putExtra("id",id);
                extraData.putExtra("pers",pers);
                extraData.putExtra("aper",aper);
                extraData.putExtra("roug",roug);
                extraData.putExtra("infil",infil);
                extraData.putExtra("weat",weat);
                startActivity(extraData);
                finish();
            }
        }
    }
}
