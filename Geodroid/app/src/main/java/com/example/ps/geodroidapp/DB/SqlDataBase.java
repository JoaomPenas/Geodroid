package com.example.ps.geodroidapp.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ps.geodroidapp.Domain.Discontinuity;
import com.example.ps.geodroidapp.Domain.Session;
import com.example.ps.geodroidapp.Domain.User;
import com.example.ps.geodroidapp.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SqlDataBase extends SQLiteOpenHelper {

    private static SqlDataBase sInstance;

    public static synchronized SqlDataBase getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new SqlDataBase(context.getApplicationContext());
        }
        return sInstance;
    }


    private SqlDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //init();
    }

    //Discontinuity TABLE
    public static final String DISCONTINUITY_TABLE_NAME     = "discontinuity";

    public static final String DISCONTINUITY_ID             = "id";
    public static final String FK_DISCONTINUITY_ID_SESSION  = "idSession";
    public static final String FK_ID_USER                   = "idUser";

    public static final String DIRECTION                    = "direction";      // Direção (entre 0 e 360)
    public static final String DIP                          = "dip";            // Inclinação (Entre 0 e 90)
    public static final String LATITUDE                     = "latitude";
    public static final String LONGITUDE                    = "longitude";
    public static final String PERSISTENCE                  = "persistence";
    public static final String APERTURE                     = "aperture";
    public static final String ROUGHNESS                    = "roughness";
    public static final String INFLILLING                   = "infilling";
    public static final String WEATHERING                   = "weathering";
    public static final String SENT                         = "sent";


    //USER TABLE
    private static final String USER_TABLE_NAME             = "user";
    private static final String USER_ID_EMAIL               = "email";
    private static final String USER_PASS                   = "pass";
    private static final String USER_SALT                   = "salt";

    //SESSION TABLE
    private static final String SESSION_TABLE_NAME          = "session";
    private static final String SESSION_ID_NAME             = "name";

    //database
    private static final String DATABASE_NAME           = "geo5.db";
    private static final int DATABASE_VERSION           = 1;

    // create Discontinuity table
    public static final String CREATE_DISCONTINUITY_TABLE = "CREATE TABLE "
            +DISCONTINUITY_TABLE_NAME       + " ("
            +DISCONTINUITY_ID               + " INTEGER, "
            +FK_DISCONTINUITY_ID_SESSION    + " TEXT, "
            +FK_ID_USER                     + " TEXT, "
            +DIRECTION                      + " INTEGER NOT NULL, "
            +DIP                            + " INTEGER NOT NULL, "
            +LATITUDE                       + " REAL NOT NULL, "
            +LONGITUDE                      + " REAL NOT NULL, "
            +PERSISTENCE                    + " INTEGER, "
            +APERTURE                       + " INTEGER, "
            +ROUGHNESS                      + " INTEGER, "
            +INFLILLING                     + " INTEGER, "
            +WEATHERING                     + " INTEGER, "
            +SENT                           + " INTEGER,"
            +" PRIMARY KEY ("+ DISCONTINUITY_ID+", "+FK_DISCONTINUITY_ID_SESSION+", "+FK_ID_USER + "), "
            +" FOREIGN KEY ("+ FK_DISCONTINUITY_ID_SESSION +") REFERENCES "+SESSION_TABLE_NAME+" ("+ SESSION_ID_NAME +"),"
            +" FOREIGN KEY ("+ FK_ID_USER                  +") REFERENCES "+USER_TABLE_NAME   +" ("+ DISCONTINUITY_ID+") );";


    //Create user table (OK)
    private static final String CREATE_USER_TABLE = "CREATE TABLE "
        +USER_TABLE_NAME + " ("
        +USER_ID_EMAIL + " TEXT PRIMARY KEY, "
        +USER_PASS + " TEXT NOT NULL,"
        +USER_SALT + " BIGINT NOT NULL);";
    // create Session table
    private static final String CREATE_SESSION_TABLE = "CREATE TABLE "
            +SESSION_TABLE_NAME + " ("
            +SESSION_ID_NAME + " TEXT PRIMARY KEY);";


    @Override
    public void onCreate(SQLiteDatabase db) {
        // chamado pelo SQLite se a base de dados ainda nao existir...
            db.execSQL(CREATE_USER_TABLE);
            db.execSQL(CREATE_SESSION_TABLE);
            db.execSQL(CREATE_DISCONTINUITY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // chamado se a versao da BD aumentar
    }

    //TODO PARA EFEITO DE TESTES (PARA APAGAR)
    public void init(){
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();

            db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + SESSION_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + DISCONTINUITY_TABLE_NAME);

            db.execSQL(CREATE_USER_TABLE);
            db.execSQL(CREATE_SESSION_TABLE);
            db.execSQL(CREATE_DISCONTINUITY_TABLE);

            //db.execSQL(CREATE_DISCONTINUITY_TABLE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

    }

    //
    // ------------------------------------------ Insert's ----------------------------------
    //
    public boolean insertUser(String userEmail, String pass,long salt) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(USER_ID_EMAIL, userEmail);
            values.put(USER_PASS, pass);
            values.put(USER_SALT,salt);
            db.insert(USER_TABLE_NAME, null, values);
            return true;
        } catch (Exception e) {
        } finally {
            db.close();
        }
        return false;
    }

    public boolean insertSession(String name) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(SESSION_ID_NAME, name);
            db.insert(SESSION_TABLE_NAME, null, values);
            return true;
        } catch (Exception e) {
        } finally {
            db.close();
        }
        return false;
    }


    public boolean insertDiscontinuity(Discontinuity discontinuity) {
        return insertDiscontinuity(discontinuity.getDirection(), discontinuity.getDip(),
                discontinuity.getLatitude(), discontinuity.getLongitude(),discontinuity.getPersistence(), discontinuity.getAperture(),
                discontinuity.getRoughness(),discontinuity.getInfilling(), discontinuity.getWeathreing(), discontinuity.getSent(),discontinuity.getIdUser(),discontinuity.getIdSession() );

    }
    public boolean insertDiscontinuity(double direction, int dip, double latitude, double longitude, int persistence, int aperture, int roughness,
                                       int infilling, int weathering, int changed, String idUser,String idSession) {
        SQLiteDatabase db = null;
        SQLiteDatabase db2 = null;
        try {
            db = this.getWritableDatabase();
            db2 = this.getReadableDatabase();
            ContentValues values = new ContentValues();

            // CALCULATE NEXT ID:
            String queryString = "SELECT MAX("+ DISCONTINUITY_ID +") AS MAX FROM "+DISCONTINUITY_TABLE_NAME;
            Cursor cursor =  db2.rawQuery(queryString, null);
            int calculatedId=1;
            if (cursor!=null) {
                cursor.moveToFirst();
                calculatedId = (cursor.getInt(cursor.getColumnIndex("MAX")))+1;
            }

            values.put(DISCONTINUITY_ID, calculatedId);
            values.put(FK_DISCONTINUITY_ID_SESSION,idSession);
            values.put(FK_ID_USER,idUser);
            values.put(DIRECTION, direction);
            values.put(DIP, dip);
            values.put(LATITUDE, latitude);
            values.put(LONGITUDE,longitude);
            values.put(PERSISTENCE,persistence);
            values.put(APERTURE,aperture);
            values.put(ROUGHNESS, roughness);
            values.put(INFLILLING,infilling);
            values.put(WEATHERING,weathering);
            values.put(SENT,changed);

            db.insert(DISCONTINUITY_TABLE_NAME, null, values);
            cursor.close();
            return true;
        } catch (Exception e) {
        } finally {
            db.close();
        }
        return false;
    }
    int num =0;
    public boolean updateDiscontinuity(int id,int persistence, int aperture, int roughness,
                                       int infilling, int weathering, int changed) {
        SQLiteDatabase db = null;
        try {

            ContentValues values = new ContentValues();
            Discontinuity discontinuity = getDiscontinuity(id);
            db = this.getWritableDatabase();
            //values.put(FK_DISCONTINUITY_ID_SESSION,discontinuity.getIdSession());
            //values.put(FK_ID_USER,discontinuity.getIdUser());
            //values.put(DIRECTION, discontinuity.getDirection());
            //values.put(DIP, discontinuity.getDip());
            //values.put(LATITUDE, discontinuity.getLatitude());
            //values.put(LONGITUDE,discontinuity.getLongitude());
            values.put(PERSISTENCE,persistence);
            values.put(APERTURE,aperture);
            values.put(ROUGHNESS, roughness);
            values.put(INFLILLING,infilling);
            values.put(WEATHERING,weathering);
            values.put(SENT,changed);
            //db.execSQL("UPDATE "+DISCONTINUITY_TABLE_NAME+" set "+PERSISTENCE+" = 0 where id = 1");

            db.update(DISCONTINUITY_TABLE_NAME,values,DISCONTINUITY_ID+" = ?",new String[]{""+id});
            return true;
        } catch (Exception e) {
        } finally {
            db.close();
        }
        return false;
    }

    //------------------------------------------ Getter's -------------------------------------------------------------

    //--------------------------------------Discontinuity-------------------------------------------
    public Discontinuity getDiscontinuity(int id){
        SQLiteDatabase db = null;
        Discontinuity desc = new Discontinuity();
        try {
            db = this.getReadableDatabase();
            String queryString = "SELECT * FROM "+DISCONTINUITY_TABLE_NAME+" WHERE "+DISCONTINUITY_ID+" = ?";

            String[] descId = {""+id};
            Cursor cursor = db.rawQuery(queryString, descId);
            cursor.moveToFirst();

            desc.setId          (Integer.parseInt       (cursor.getString(cursor.getColumnIndex(DISCONTINUITY_ID))));
            desc.setDirection   (Integer.parseInt       (cursor.getString(cursor.getColumnIndex(DIRECTION))));
            desc.setDip         (Integer.parseInt       (cursor.getString(cursor.getColumnIndex(DIP))));
            desc.setLatitude    (Double.parseDouble     (cursor.getString(cursor.getColumnIndex(LATITUDE))));
            desc.setLongitude   (Double.parseDouble     (cursor.getString(cursor.getColumnIndex(LONGITUDE))));
            desc.setPersistence (Integer.parseInt       (cursor.getString(cursor.getColumnIndex(PERSISTENCE))));
            desc.setAperture    (Integer.parseInt       (cursor.getString(cursor.getColumnIndex(APERTURE))));
            desc.setRoughness   (Integer.parseInt       (cursor.getString(cursor.getColumnIndex(ROUGHNESS))));
            desc.setInfilling   (Integer.parseInt       (cursor.getString(cursor.getColumnIndex(INFLILLING))));
            desc.setWeathreing  (Integer.parseInt       (cursor.getString(cursor.getColumnIndex(WEATHERING))));
            desc.setSent(Integer.parseInt       (cursor.getString(cursor.getColumnIndex(SENT))));
            desc.setIdSession   (cursor.getString(cursor.getColumnIndex(FK_DISCONTINUITY_ID_SESSION)));
            desc.setIdUser      (cursor.getString(cursor.getColumnIndex(FK_ID_USER)));

        }catch (Exception e){}
        finally {
            db.close();
        }
        return desc;
    }




    public ArrayList<Discontinuity> getAllDiscontinuities(String serie){
        ArrayList<Discontinuity> list = new ArrayList<>();
        Discontinuity desc;
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Cursor cursor =  db.rawQuery("select * from "+DISCONTINUITY_TABLE_NAME+" WHERE "+FK_DISCONTINUITY_ID_SESSION+"=\""+serie+"\"", null);
            cursor.moveToFirst();

            while(cursor.isAfterLast() == false){
                desc = new Discontinuity();

                desc.setId          (Integer.parseInt       (cursor.getString(cursor.getColumnIndex(DISCONTINUITY_ID))));
                desc.setDirection   (Integer.parseInt       (cursor.getString(cursor.getColumnIndex(DIRECTION))));
                desc.setDip         (Integer.parseInt       (cursor.getString(cursor.getColumnIndex(DIP))));
                desc.setLatitude    (Double.parseDouble     (cursor.getString(cursor.getColumnIndex(LATITUDE))));
                desc.setLongitude   (Double.parseDouble     (cursor.getString(cursor.getColumnIndex(LONGITUDE))));
                desc.setPersistence (Integer.parseInt       (cursor.getString(cursor.getColumnIndex(PERSISTENCE))));
                desc.setAperture    (Integer.parseInt       (cursor.getString(cursor.getColumnIndex(APERTURE))));
                desc.setRoughness   (Integer.parseInt       (cursor.getString(cursor.getColumnIndex(ROUGHNESS))));
                desc.setInfilling   (Integer.parseInt       (cursor.getString(cursor.getColumnIndex(INFLILLING))));
                desc.setWeathreing  (Integer.parseInt       (cursor.getString(cursor.getColumnIndex(WEATHERING))));
                desc.setSent        (Integer.parseInt       (cursor.getString(cursor.getColumnIndex(SENT))));
                desc.setIdSession                           (cursor.getString(cursor.getColumnIndex(FK_DISCONTINUITY_ID_SESSION)));
                desc.setIdUser                              (cursor.getString(cursor.getColumnIndex(FK_ID_USER)));

                list.add(desc);
                cursor.moveToNext();
            }
            cursor.close();
        }catch(Exception e) {
        }finally {
            db.close();
        }
        return list;
    }
    //--------------------------------------User's-------------------------------------------
    public ArrayList<User> getAllUsers(){
        ArrayList<User> list = new ArrayList<>();
        User user;
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Cursor cursor =  db.rawQuery("select * from "+USER_TABLE_NAME, null);

            cursor.moveToFirst();
            while(cursor.isAfterLast() == false){
                user = new User();

                user.setEmail(cursor.getString(cursor.getColumnIndex(USER_ID_EMAIL)));
                user.setPass(cursor.getString(cursor.getColumnIndex(USER_PASS)));
                user.setSalt(Long.parseLong(cursor.getString(cursor.getColumnIndex(USER_SALT))));

                list.add(user);
                cursor.moveToNext();
            }
            cursor.close();
        }catch(Exception e) {
        }finally {
            db.close();
        }
        return list;
    }

    public boolean IsUserAvailable(String email, String pass) {
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor cursor = db.rawQuery("select * from user where "+USER_ID_EMAIL+" =\"" + email + "\" and "+USER_PASS+" =\"" + pass + "\"" , null);
        Cursor cursor = db.rawQuery("select * from user where "+USER_ID_EMAIL+" =\"" + email+"\"", null);
        cursor.moveToFirst();
        if (cursor.isFirst()) {
            String hash = cursor.getString(cursor.getColumnIndex(USER_PASS));
            long salt = Long.parseLong(cursor.getString(cursor.getColumnIndex(USER_SALT)));
            pass += salt;
            return Utils.verifyPassword(hash,pass);
            //Log.d("HS", cursor.getString(cursor.getColumnIndex(USER_ID_EMAIL)));
            //return true;
        }
        else
        return false;
    }

    public void insertUsers(List<User> list) {
        for (User u:list) {
           insertUser(u.getEmail(),u.getPass(),u.getSalt());
        }
    }
    //--------------------------------------Session's-------------------------------------------

    public ArrayList<Session> getAllSessions(){
        ArrayList<Session> list = new ArrayList<>();
        Session session;
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Cursor cursor =  db.rawQuery("select * from "+SESSION_TABLE_NAME, null);

            cursor.moveToFirst();
            while(cursor.isAfterLast() == false){
                session = new Session();

                session.setName(cursor.getString(cursor.getColumnIndex(SESSION_ID_NAME)));

                list.add(session);
                cursor.moveToNext();
            }
            cursor.close();
        }catch(Exception e) {
        }finally {
            db.close();
        }
        return list;
    }

    public ArrayList<Discontinuity> areUploads(String sessionId) {
        ArrayList<Discontinuity> list = new ArrayList<>();
        for (Discontinuity dicont: getAllDiscontinuities(sessionId)) {
            if(dicont.getSent() == 0)
                list.add(dicont);
        }
        return list;
    }

    public boolean putSent(ArrayList<Discontinuity> list) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            for (Discontinuity disc:list) {
                values.put(SENT,1);
                db.update(DISCONTINUITY_TABLE_NAME,values,DISCONTINUITY_ID+" = ?",new String[]{""+disc.getId()});
            }
            return true;
        } catch (Exception e) {
        } finally {
            db.close();
        }
        return false;
    }

    /**
     * Apaga a Discontinuidade do dispositivo
     * @param discontinuityId
     */
    public boolean deleteDiscontinuity(int discontinuityId) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.delete(DISCONTINUITY_TABLE_NAME,DISCONTINUITY_ID+"= ?",new String[]{""+discontinuityId});
            return true;
        } catch (Exception e) {
        } finally {
            db.close();
        }
        return false;
    }
}
