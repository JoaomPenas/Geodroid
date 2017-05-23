package com.example.ps.geodroidapp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ps.geodroidapp.DB.SqlDataBase;
import com.example.ps.geodroidapp.R;
import com.example.ps.geodroidapp.Utils.Utils;

public class ExtraData extends AppCompatActivity {

    SqlDataBase db;
    private final int NOTSENT = 0;
    CheckedTextView tv_persist_1class,tv_persist_2class,tv_persist_3class,tv_persist_4class,tv_persist_5class;
    CheckedTextView tv_aperture_1class,tv_aperture_2class,tv_aperture_3class,tv_aperture_4class,tv_aperture_5class;
    CheckedTextView tv_roughness_1class,tv_roughness_2class,tv_roughness_3class,tv_roughness_4class,tv_roughness_5class;
    CheckedTextView tv_infilling_1class,tv_infilling_2class,tv_infilling_3class,tv_infilling_4class,tv_infilling_5class;
    CheckedTextView tv_weathering_1class,tv_weathering_2class,tv_weathering_3class,tv_weathering_4class,tv_weathering_5class;
    TableRow tbrPercistence, tbrAperture, tbrRoughness,tbrInfiling,tbrWeathering;
    int azimut, dip_;
    int id = -1;        //inicia a -1 para sabermos se estamos perante um update ou um insert
    double latitude, longitude;
    int persistence = 0, aperture = 0, roughness = 0, infilling = 0, weathering = 0;
    Intent dataTable;
    private String session, usermail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_data);

        db= SqlDataBase.getInstance(getApplicationContext());
        dataTable = new Intent(this, DataTable.class);
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if (extras!=null){
            //Toast.makeText(ExtraData.this,"Azinute: "+extras.getString("Dip"), Toast.LENGTH_SHORT).show();
            session = extras.getString("Session");
            usermail= extras.getString("usermail");
            id = extras.getInt("id",id);

        }

        // botão
        Button b = (Button) findViewById(R.id.extra_data_save_button);

        //TableRow
        tbrPercistence = (TableRow) findViewById(R.id.tableRow_persistence);
        tbrAperture = (TableRow) findViewById(R.id.tableRow_aperture);
        tbrRoughness = (TableRow) findViewById(R.id.tableRow_roughness);
        tbrInfiling = (TableRow) findViewById(R.id.tableRow_infiling);
        tbrWeathering = (TableRow) findViewById(R.id.tableRow_weathering);

        //CheckedTextView Percistence
        tv_persist_1class = (CheckedTextView) findViewById(R.id.Desc_class_tv_persist_1class);
        tv_persist_2class = (CheckedTextView) findViewById(R.id.Desc_class_tv_persist_2class);
        tv_persist_3class = (CheckedTextView) findViewById(R.id.Desc_class_tv_persist_3class);
        tv_persist_4class = (CheckedTextView) findViewById(R.id.Desc_class_tv_persist_4class);
        tv_persist_5class = (CheckedTextView) findViewById(R.id.Desc_class_tv_persist_5class);

        //CheckedTextView Aperture
        tv_aperture_1class =(CheckedTextView) findViewById(R.id.Desc_class_tv_aperture_1class);
        tv_aperture_2class =(CheckedTextView) findViewById(R.id.Desc_class_tv_aperture_2class);
        tv_aperture_3class =(CheckedTextView) findViewById(R.id.Desc_class_tv_aperture_3class);
        tv_aperture_4class =(CheckedTextView) findViewById(R.id.Desc_class_tv_aperture_4class);
        tv_aperture_5class =(CheckedTextView) findViewById(R.id.Desc_class_tv_aperture_5class);

        //CheckedTextView Roughness
        tv_roughness_1class =(CheckedTextView) findViewById(R.id.Desc_class_tv_rougness_1class);
        tv_roughness_2class =(CheckedTextView) findViewById(R.id.Desc_class_tv_rougness_2class);
        tv_roughness_3class =(CheckedTextView) findViewById(R.id.Desc_class_tv_rougness_3class);
        tv_roughness_4class =(CheckedTextView) findViewById(R.id.Desc_class_tv_rougness_4class);
        tv_roughness_5class =(CheckedTextView) findViewById(R.id.Desc_class_tv_rougness_5class);

        //CheckedTextView Infiling
        tv_infilling_1class =(CheckedTextView) findViewById(R.id.Desc_class_tv_infilling_1class);
        tv_infilling_2class =(CheckedTextView) findViewById(R.id.Desc_class_tv_infilling_2class);
        tv_infilling_3class =(CheckedTextView) findViewById(R.id.Desc_class_tv_infilling_3class);
        tv_infilling_4class =(CheckedTextView) findViewById(R.id.Desc_class_tv_infilling_4class);
        tv_infilling_5class =(CheckedTextView) findViewById(R.id.Desc_class_tv_infilling_5class);

        //CheckedTextView Weathering
        tv_weathering_1class =(CheckedTextView) findViewById(R.id.Desc_class_tv_weathering_1class);
        tv_weathering_2class =(CheckedTextView) findViewById(R.id.Desc_class_tv_weathering_2class);
        tv_weathering_3class =(CheckedTextView) findViewById(R.id.Desc_class_tv_weathering_3class);
        tv_weathering_4class = (CheckedTextView) findViewById(R.id.Desc_class_tv_weathering_4class);
        tv_weathering_5class =(CheckedTextView) findViewById(R.id.Desc_class_tv_weathering_5class);

        if(id != -1) {
            persistence = extras.getInt("pers");
            aperture = extras.getInt("aper");
            roughness = extras.getInt("roug");
            infilling = extras.getInt("infil");
            weathering = extras.getInt("weat");
            initTable(persistence,aperture,roughness,infilling,weathering);

        }else {
            azimut = Integer.parseInt(extras.getString("Azimute"));
            dip_ = Integer.parseInt(extras.getString("Dip"));
            latitude = Double.parseDouble(extras.getString("Latitude"));
            longitude = Double.parseDouble(extras.getString("Longitude"));

            // atitudes e sessao
            TextView tv = (TextView) findViewById(R.id.AtitudeDaDesco);
            TextView tv2 = (TextView) findViewById(R.id.LocalizacaoDaDescont);
            TextView tv3 = (TextView) findViewById(R.id.extra_data_session);

            tv.setText("Raw atitude: " + azimut + ", " + dip_ + "\nNormalized atitude: " + Utils.getNormaliedAtitudeFromRawAtitude(azimut, dip_));
            tv2.setText("Localization: " + latitude + "," + longitude);
            tv3.setText("user:" + usermail + " / Session: " + session);
        }


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v instanceof CheckedTextView){
                    CheckedTextView ckt = (CheckedTextView)v;
                    TableRow tbr = (TableRow)ckt.getParent();
                    putExtraValue(ckt,tbr);
                    chekOrUncheck(ckt,tbr);
                    //Toast.makeText(com.example.ps.geodroidapp.Activities.ExtraData.this,"persistence = " + persistence , Toast.LENGTH_SHORT).show();
                }
            }

            /**
             * Verifica qual a CheckedTextView slecionada de uma TableRow para que se consiga saber o valor que se quer guardar
             * i = 1 porque é onde inicia o CheckedTextView, se fosse 0 iria dar a textView que contem o nome da linha
             * @param ckt CheckedTextView slecionada
             * @param tbr TableRow onde está a CheckedTextView
             */
            private void putExtraValue(CheckedTextView ckt, TableRow tbr) {
                int index = 0;
                for (int i = 1 ; i< tbr.getChildCount();i++){
                    if(ckt == tbr.getChildAt(i) && !ckt.isChecked()){
                        index = i;
                    }
                }
                switch (tbr.getTag().toString()) {
                    case ("persistence") : persistence = index;
                                           break;
                    case ("aperture") : aperture = index;
                                        break;
                    case ("roughness") : roughness = index;
                                         break;
                    case ("infiling") : infilling = index;
                                        break;
                    case ("weathering") : weathering = index;
                                          break;

                }
            }
        };

        //////////////////// PERSISTENCE ////////////////////
        tv_persist_1class.setOnClickListener(listener);
        tv_persist_2class.setOnClickListener(listener);
        tv_persist_3class.setOnClickListener(listener);
        tv_persist_4class.setOnClickListener(listener);
        tv_persist_5class.setOnClickListener(listener);

        //////////////////// APERTURE //////////////////////
        tv_aperture_1class.setOnClickListener(listener);
        tv_aperture_2class.setOnClickListener(listener);
        tv_aperture_3class.setOnClickListener(listener);
        tv_aperture_4class.setOnClickListener(listener);
        tv_aperture_5class.setOnClickListener(listener);

        //////////////////// ROUGHNESS ////////////////////
        tv_roughness_1class.setOnClickListener(listener);
        tv_roughness_2class.setOnClickListener(listener);
        tv_roughness_3class.setOnClickListener(listener);
        tv_roughness_4class.setOnClickListener(listener);
        tv_roughness_5class.setOnClickListener(listener);

        //////////////////// INFILLING ////////////////////
        tv_infilling_1class.setOnClickListener(listener);
        tv_infilling_2class.setOnClickListener(listener);
        tv_infilling_3class.setOnClickListener(listener);
        tv_infilling_4class.setOnClickListener(listener);
        tv_infilling_5class.setOnClickListener(listener);

        //////////////////// WEATHREING ////////////////////
        tv_weathering_1class.setOnClickListener(listener);
        tv_weathering_2class.setOnClickListener(listener);
        tv_weathering_3class.setOnClickListener(listener);
        tv_weathering_4class.setOnClickListener(listener);
        tv_weathering_5class.setOnClickListener(listener);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    /*Toast.makeText(ExtraData.this,"Saved in database!"
                            +"\nSessão:"        +session
                            +"\nUser:"          +usermail
                            +"\nPersistencia: " +persistence
                            +"\nAperture: "     +aperture
                            +"\nRougness: "     +roughness
                            +"\nInfilling: "    +infilling
                            +"\nWeathering: "   +weathering, Toast.LENGTH_LONG).show();*/

                if(id!=-1){
                    //Toast.makeText(com.example.ps.geodroidapp.Activities.ExtraData.this,"UPDATE",Toast.LENGTH_LONG).show();
                    db.updateDiscontinuity(id,persistence,aperture,roughness,infilling,weathering,NOTSENT);
                    dataTable.putExtra("Session",session);
                    startActivity(dataTable);
                }
                else{
                    db.insertDiscontinuity(azimut, dip_,latitude,longitude,persistence,aperture,roughness,infilling,weathering,NOTSENT,usermail,session);
                }
                finish();
            }
        });
    }

    /**
     * Caso seja para editar os dados inicia a tabela já preenchida com os dados atuais
     */
    private void initTable(int persistence, int aperture, int roughness, int infilling, int weathering) {
        CheckedTextView cktvPers = (CheckedTextView) tbrPercistence.getChildAt(persistence);
        cktvPers.setChecked(true);
        cktvPers.setTextColor(Color.GRAY);
        CheckedTextView cktvAper = (CheckedTextView) tbrAperture.getChildAt(aperture);
        cktvAper.setChecked(true);
        cktvAper.setTextColor(Color.GRAY);
        CheckedTextView cktvRoug = (CheckedTextView) tbrRoughness.getChildAt(roughness);
        cktvRoug.setChecked(true);
        cktvRoug.setTextColor(Color.GRAY);
        CheckedTextView cktvInfil = (CheckedTextView) tbrInfiling.getChildAt(infilling);
        cktvInfil.setChecked(true);
        cktvInfil.setTextColor(Color.GRAY);
        CheckedTextView cktvWeat = (CheckedTextView) tbrWeathering.getChildAt(weathering);
        cktvWeat.setChecked(true);
        cktvWeat.setTextColor(Color.GRAY);
    }

    /**
     * Desseleciona uma CheckedTextView previamente selecionada
     * @param tbr Table Row em questão
     */
    public void resetCheck(TableRow tbr){
        for (int i = 0; i < tbr.getChildCount(); i++) {
            if(tbr.getChildAt(i) instanceof CheckedTextView) {
                CheckedTextView cb = (CheckedTextView) (tbr.getChildAt(i));
                cb.setChecked(false);
                cb.setTextColor(Color.BLACK);
            }
        }
    }

    /**
     * Verifica se é par selecionar ou desselecionar
     * @param ckt CheckedTextView que foi selecionada
     * @param tbr TableRow ao qual a CheckedTextView pertence
     */
    public void chekOrUncheck(CheckedTextView ckt, TableRow tbr){
        if(ckt.isChecked()){
            ckt.setChecked(false);
            ckt.setTextColor(Color.BLACK);
        }else {
            resetCheck(tbr);
            ckt.setTextColor(Color.GRAY);
            ckt.setChecked(true);
        }
    }
}
