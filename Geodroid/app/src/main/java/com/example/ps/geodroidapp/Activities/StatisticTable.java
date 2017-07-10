package com.example.ps.geodroidapp.Activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ps.geodroidapp.DB.SqlDataBase;
import com.example.ps.geodroidapp.Domain.Discontinuity;
import com.example.ps.geodroidapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class StatisticTable extends AppCompatActivity {
    private SqlDataBase db;
    private String session="",user ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_table);


        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            session = extras.getString("Session");
            user = extras.getString("User");
        }

        db = SqlDataBase.getInstance(this);
        ArrayList<Discontinuity> x = db.getAllDiscontinuities(session,user);

        int count = x.size();
        float _0_29=0f, _30_59=0f, _60_89=0f, _90_119=0f,_120_149=0f,_150_179=0f,
              _180_209=0f,_210_239=0f,_240_269=0f,_270_299=0f,_300_329=0f,_330_359=0f;

        BarChart chart = (BarChart) findViewById(R.id.chart);
        chart.setDescription(null);          // this takes up too much space, so clear it

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        for (Discontinuity data : x) {
            if (data.getDirection()>=0 && data.getDirection()<=29) ++_0_29;
            if (data.getDirection()>=30 && data.getDirection()<=59) ++_30_59;
            if (data.getDirection()>=60 && data.getDirection()<=89) ++_60_89;
            if (data.getDirection()>=90 && data.getDirection()<=119) ++_90_119;
            if (data.getDirection()>=120 && data.getDirection()<=149) ++_120_149;
            if (data.getDirection()>=150 && data.getDirection()<=179) ++_150_179;
            if (data.getDirection()>=180 && data.getDirection()<=209) ++_180_209;
            if (data.getDirection()>=210 && data.getDirection()<=239) ++_210_239;
            if (data.getDirection()>=240 && data.getDirection()<=269) ++_240_269;
            if (data.getDirection()>=270 && data.getDirection()<=299) ++_270_299;
            if (data.getDirection()>=300 && data.getDirection()<=329) ++_300_329;
            if (data.getDirection()>=330 && data.getDirection()<=359) ++_330_359;
            // turn your data into Entry objects
        }
        /*
        Toast.makeText(this,        "_0_29="+_0_29
                                +"\n_30_59="+_30_59
                                +"\n_60_89="+_60_89
                                +"\n_90_119="+_90_119
                                +"\n_120_149="+_120_149
                                +"\n_150_179="+_150_179
                                +"\n_180_209="+_180_209
                                +"\n_210_239="+_210_239
                                +"\n_240_269="+_240_269
                                +"\n_270_299="+_270_299
                                +"\n_300_329="+_300_329
                                +"\n_330_359="+_330_359, Toast.LENGTH_SHORT).show();
        */

        entries.add(new BarEntry(29f, _0_29));
        entries.add(new BarEntry(59f, _30_59));
        entries.add(new BarEntry(89f, _60_89));
        entries.add(new BarEntry(119f, _90_119));
        entries.add(new BarEntry(149f, _120_149));
        entries.add(new BarEntry(179f, _150_179));
        entries.add(new BarEntry(209f, _180_209));
        entries.add(new BarEntry(239f, _210_239));
        entries.add(new BarEntry(269f, _240_269));
        entries.add(new BarEntry(299f, _270_299));
        entries.add(new BarEntry(329f, _300_329));
        entries.add(new BarEntry(359f, _330_359));


        BarDataSet dataSet = new BarDataSet(entries, session);
        dataSet.setBarBorderWidth(4f);


        dataSet.setBarShadowColor(Color.rgb(255, 0, 0));
        dataSet.setColor(Color.rgb(0, 255, 0));
        dataSet.setValueTextColor(Color.rgb(0, 0, 255));

        BarData barData = new BarData();
        barData.addDataSet(dataSet);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        chart.setData(barData);
        chart.invalidate();
    }
}
