package com.example.ps.geodroidapp.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.ps.geodroidapp.Domain.Discontinuity;
import com.example.ps.geodroidapp.Domain.Session;
import com.example.ps.geodroidapp.Utils.Utils;

import java.util.ArrayList;

public class SqlDataBase extends SQLiteOpenHelper {

    private static SqlDataBase sInstance;

    /**
     * Method following the Singleton pattern to retrieve allways the same SqlDataBase instance
     * @param context
     * @return SqlDataBase (unique instance)
     */
    public static synchronized SqlDataBase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SqlDataBase(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Private constructor (to prevent direct instantiation)
     * Should be used getInstance() instead!
     * @param context
     */
    private SqlDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //init();   // TODO LINHA PARA EFEITO DE TESTES (REMOVE E CRIA SEMPRE AS TABELAS). PARA APAGAR NA VERSÃO FINAL
    }

    //DISCONTINUITY TABLE
    public static final String DISCONTINUITY_TABLE_NAME     = "discontinuity";

    public static final String DISCONTINUITY_ID             = "id";
    public static final String FK_DISCONTINUITY_ID_SESSION  = "idSession";
    public static final String FK_ID_USER                   = "idUser";

    public static final String DIRECTION                    = "direction";      // (values between 0 - 359)
    public static final String DIP                          = "dip";            // (values between 0 - 90)
    public static final String LATITUDE                     = "latitude";
    public static final String LONGITUDE                    = "longitude";
    public static final String PERSISTENCE                  = "persistence";
    public static final String APERTURE                     = "aperture";
    public static final String ROUGHNESS                    = "roughness";
    public static final String INFLILLING                   = "infilling";
    public static final String WEATHERING                   = "weathering";
    public static final String NOTE                         = "description";
    public static final String DATE                         = "datetime";
    public static final String SENT                         = "sent";

    //USER TABLE
    private static final String USER_TABLE_NAME             = "user";
    private static final String USER_ID_EMAIL               = "email";
    private static final String USER_PASS                   = "pass";
    private static final String USER_SALT                   = "salt";
    private static final String USER_TOKEN_API              = "apitoken";

    //SESSION TABLE
    private static final String SESSION_TABLE_NAME          = "session";
    private static final String SESSION_ID_NAME             = "name";

    //DATABASE
    private static final String DATABASE_NAME               = "geo.db";
    private static final int DATABASE_VERSION               = 1;

    // create Discontinuity table String
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
            +NOTE                           + " TEXT, "
            +DATE                           + " TEXT, "
            +SENT                           + " INTEGER,"
            +" PRIMARY KEY ("+ DISCONTINUITY_ID+", "+FK_DISCONTINUITY_ID_SESSION+", "+FK_ID_USER + "), "
            +" FOREIGN KEY ("+ FK_DISCONTINUITY_ID_SESSION +") REFERENCES "+SESSION_TABLE_NAME+" ("+ SESSION_ID_NAME +"),"
            +" FOREIGN KEY ("+ FK_ID_USER                  +") REFERENCES "+USER_TABLE_NAME   +" ("+ DISCONTINUITY_ID+") );";


    //Create user table String
    private static final String CREATE_USER_TABLE = "CREATE TABLE "
        +USER_TABLE_NAME + " ("
        +USER_ID_EMAIL + " TEXT PRIMARY KEY, "
        +USER_PASS + " TEXT NOT NULL,"
        +USER_SALT + " BIGINT NOT NULL,"
        +USER_TOKEN_API + " TEXT );";

    // create Session table String
    private static final String CREATE_SESSION_TABLE = "CREATE TABLE "
            +SESSION_TABLE_NAME + " ("
            +SESSION_ID_NAME + " TEXT PRIMARY KEY);";

    // create discontinuityMaxId table string
    // discontinuityMaxId TABLE
    private static final String DISCMAXID_TABLE_NAME          = "discontinuityMaxId";
    private static final String DISCMAXID_CURRMAX             = "currentMax";
    private static final String CREATE_CURRMAXID_TABLE=  "CREATE TABLE "
            +DISCMAXID_TABLE_NAME + " ("
            +DISCMAXID_CURRMAX + " integer);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        // called by SQLite if the database doesn't exist yet...
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_SESSION_TABLE);
        db.execSQL(CREATE_DISCONTINUITY_TABLE);
        db.execSQL(CREATE_CURRMAXID_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // called by SQLite if the database version increase
    }

    /**
     * TODO PARA EFEITO DE TESTES (PARA APAGAR NA VERSÃO FINAL)
     */
    public void init(){
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();

            db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + SESSION_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + DISCONTINUITY_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + DISCMAXID_TABLE_NAME);

            db.execSQL(CREATE_USER_TABLE);
            db.execSQL(CREATE_SESSION_TABLE);
            db.execSQL(CREATE_DISCONTINUITY_TABLE);
            db.execSQL(CREATE_CURRMAXID_TABLE);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            db.close();
        }
    }

    /**
     * Insert one User in database.
     * @param userEmail - user name
     * @param pass - user pass (hash from password+salt)
     * @param salt - salt used to get the pass hash
     * @param token - current token given by the Api, if exists
     * @return - returns true if was well inserted in SQLiteDatabase or false if not
     */
    public boolean insertUser(String userEmail, String pass,long salt,String token) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(USER_ID_EMAIL, userEmail);
            values.put(USER_PASS, pass);
            values.put(USER_SALT,salt);
            values.put(USER_TOKEN_API,token);

            db.insert(USER_TABLE_NAME, null, values);
            return true;
        }
        catch (Exception e) {
            Log.d("error",e.getMessage());
        }
        finally {
            db.close();
        }
        return false;
    }

    /**
     * Insert one Session in database.
     * @param name - Session name (session id in the database)
     * @return - returns true if was well inserted in SQLiteDatabase or false if not
     */
    public boolean insertSession(String name) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(SESSION_ID_NAME, name);
            db.insert(SESSION_TABLE_NAME, null, values);
            return true;
        }
        catch (Exception e) {
        }
        finally {
            db.close();
        }
        return false;
    }

    /**
     * Insert one Discontinuity in database.
     * @param direction - The direction of the Discontinuity
     * @param dip - The dip of the Discontinuity
     * @param latitude - the latitude of the Discontinuity (in degrees)
     * @param longitude - the longitude of the Discontinuity (ib degrees)
     * @param persistence - the persistence of the Discontinuity (values between 1 and 5; 0 if not available)
     * @param aperture - the aperture of the Discontinuity (values between 1 and 5; 0 if not available)
     * @param roughness - the roughness of the Discontinuity (values between 1 and 5; 0 if not available)
     * @param infilling - the infilling of the Discontinuity (values between 1 and 5; 0 if not available)
     * @param weathering - the weathering of the Discontinuity (values between 1 and 5; 0 if not available)
     * @param note - an observation about the Discontinuity
     * @param datetime - string with the date and time when the discontinuity was measured
     * @param sent - integer indicating if the discountinuity was sent to server
     * @param idUser - the id of the user responsible for the measure
     * @param idSession - The id of the Session where it was measure the Discontinuity
     * @return - returns true if was well inserted in SQLiteDatabase or false if not
     *
     */
    public boolean insertDiscontinuity(double direction, int dip, double latitude, double longitude, int persistence, int aperture, int roughness,
                                       int infilling, int weathering,String note, String datetime, int sent, String idUser,String idSession) {
        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(DISCONTINUITY_ID, getNextDiscontinuityId(db));
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
            values.put(NOTE,note);
            values.put(DATE, datetime);
            values.put(SENT,sent);

            db.insert(DISCONTINUITY_TABLE_NAME, null, values);
            return true;
        }
        catch (Exception e) {}
        finally {
            db.close();
        }
        return false;
    }

    /**
     * Private method - get's the next Discountinuity Identifier (initialize or updates the database counter)
     * @param db - database used by the caller method
     * @return the next discountinuityId to be used in an new discontinuity insertion
     */
    private int getNextDiscontinuityId(SQLiteDatabase db){
        ContentValues values = new ContentValues();
        try {
            String queryString = "select "+DISCMAXID_CURRMAX+" from "+DISCMAXID_TABLE_NAME;
            Cursor cursor =  db.rawQuery(queryString, null);

            if (cursor!=null) {
                if (cursor.moveToFirst() == true) {
                    // increases the DB Counter by one and returns that same value
                    int currentId = cursor.getInt(0);
                    int nextId=currentId+1;
                    values.put (DISCMAXID_CURRMAX, nextId);
                    db.update(DISCMAXID_TABLE_NAME,values,DISCMAXID_CURRMAX+" = ?",new String[]{""+currentId});
                    cursor.close();
                    return nextId;
                }
                else {
                    // initializes the DB counter
                    int initialValue =1;
                    values.put (DISCMAXID_CURRMAX, initialValue);
                    db.insert(DISCMAXID_TABLE_NAME,DISCMAXID_CURRMAX,values);
                    cursor.close();
                    return initialValue;
                }
            }
        }
        catch (Exception e){
        }
        return -1;
    }

    /**
     * Updates one Discontinuity in database. (Doesn't change the Date and Time field.)
     * @param persistence - the persistence of the Discontinuity (values between 1 and 5; 0 if not available)
     * @param aperture - the aperture of the Discontinuity (values between 1 and 5; 0 if not available)
     * @param roughness - the roughness of the Discontinuity (values between 1 and 5; 0 if not available)
     * @param infilling - the infilling of the Discontinuity (values between 1 and 5; 0 if not available)
     * @param weathering - the weathering of the Discontinuity (values between 1 and 5; 0 if not available)
     * @param note - an observation about the Discontinuity
     * @param sent - information about if the Discontinuity was sent to server or not
     * @return - returns true if was well inserted in SQLiteDatabase or false if not
     *
     */
    public boolean updateDiscontinuity(int id,int persistence, int aperture, int roughness,
                                       int infilling, int weathering,String note, int sent) {
        SQLiteDatabase db = null;
        try {
            ContentValues values = new ContentValues();
            db = this.getWritableDatabase();

            values.put(PERSISTENCE,persistence);
            values.put(APERTURE,aperture);
            values.put(ROUGHNESS, roughness);
            values.put(INFLILLING,infilling);
            values.put(WEATHERING,weathering);
            values.put(NOTE,note);
            // It is assumed that the date remains unchanged
            values.put(SENT,sent);

            db.update(DISCONTINUITY_TABLE_NAME,values,DISCONTINUITY_ID+" = ?",new String[]{""+id});
            return true;
        }
        catch (Exception e) {
        }
        finally {
            db.close();
        }
        return false;
    }

    /**
     * Gets one Discontinuity in database from the id.
     * (Because the id locally is unique we don't need more information to select the discountinuity)
     * @param id - the id of the Discontinuity
     * @return - returns the respective Discontinuity
     *
     */
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
            desc.setNote                                (cursor.getString(cursor.getColumnIndex(NOTE)));
            desc.setDatetime                            (cursor.getString(cursor.getColumnIndex(DATE)));
            desc.setSent        (Integer.parseInt       (cursor.getString(cursor.getColumnIndex(SENT))));
            desc.setIdSession   (cursor.getString(cursor.getColumnIndex(FK_DISCONTINUITY_ID_SESSION)));
            desc.setIdUser      (cursor.getString(cursor.getColumnIndex(FK_ID_USER)));
        }
        catch (Exception e){}
        finally {
            db.close();
        }
        return desc;
    }

    /**
     * Get's one list of Discontinuities from the session identification
     * @param sessionId
     * @return a list of session discontinuities
     */
    public ArrayList<Discontinuity> getAllDiscontinuities(String sessionId){
        ArrayList<Discontinuity> list = new ArrayList<>();
        Discontinuity desc;
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Cursor cursor =  db.rawQuery("select * from "+DISCONTINUITY_TABLE_NAME+" WHERE "+FK_DISCONTINUITY_ID_SESSION+"=\""+sessionId+"\"", null);
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
                desc.setNote                                (cursor.getString(cursor.getColumnIndex(NOTE)));
                desc.setDatetime                            (cursor.getString(cursor.getColumnIndex(DATE)));
                desc.setSent        (Integer.parseInt       (cursor.getString(cursor.getColumnIndex(SENT))));
                desc.setIdSession                           (cursor.getString(cursor.getColumnIndex(FK_DISCONTINUITY_ID_SESSION)));
                desc.setIdUser                              (cursor.getString(cursor.getColumnIndex(FK_ID_USER)));

                list.add(desc);
                cursor.moveToNext();
            }
            cursor.close();
        }
        catch(Exception e) {}
        finally {
            db.close();
        }
        return list;
    }

    /**
     * Verifies if the user exists in database
     * @param email - the identification of the user
     * @param pass - password of the user (without salt)
     * @return
     */
    public boolean IsUserAvailable(String email, String pass) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from user where "+USER_ID_EMAIL+" =\"" + email+"\"", null);
        cursor.moveToFirst();
        if (cursor.isFirst()) {
            String hash = cursor.getString(cursor.getColumnIndex(USER_PASS));
            long salt = Long.parseLong(cursor.getString(cursor.getColumnIndex(USER_SALT)));
            pass += salt;
            return Utils.verifyPassword(hash,pass);
        }
        else
        return false;
    }

    /**
     * Gets from database the list of the existing sessions
     * @return the list of the existing sessions
     */
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
        }
        catch(Exception e) {
        }
        finally {
            db.close();
        }
        return list;
    }

    /**
     * Gets from database the list of the existing sessions of a user
     * @param usermail
     * @return the list of the existing sessions of a user
     */
    public ArrayList<Session> getUserSessions(String usermail){
        ArrayList<Session> list = new ArrayList<>();
        Session session;
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Cursor cursor =  db.rawQuery("select * from "+SESSION_TABLE_NAME +"where " +SESSION_ID_NAME+ " =\"" + usermail+"\"", null);
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false){
                session = new Session();
                session.setName(cursor.getString(cursor.getColumnIndex(SESSION_ID_NAME)));
                list.add(session);
                cursor.moveToNext();
            }
            cursor.close();
        }
        catch(Exception e) {
        }
        finally {
            db.close();
        }
        return list;
    }

    /**
     * Get's one list with the discontinuities that doesn't were sent to server (or with changes)
     * @param sessionId
     * @return one list with the discontinuities that are not, or are not update, in the server
     */
    public ArrayList<Discontinuity> getDiscontinuitysNotUploaded(String sessionId) {
        ArrayList<Discontinuity> list = new ArrayList<>();
        for (Discontinuity dicont: getAllDiscontinuities(sessionId)) {
            if(dicont.getSent() == 0)
                list.add(dicont);
        }
        return list;
    }

    /**
     * Chages the Sent flag to 1 to inform that the Discontinuity was sent to server
     * @param list
     * @return bolean (true if no exceptions)
     */
    public boolean setAllSentDiscontinuity(ArrayList<Discontinuity> list) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            for (Discontinuity disc:list) {
                values.put(SENT,1);
                db.update(DISCONTINUITY_TABLE_NAME,values,DISCONTINUITY_ID+" = ?",new String[]{""+disc.getId()});
            }
            return true;
        }
        catch (Exception e) {}
        finally {
            db.close();
        }
        return false;
    }

    /**
     * Deletes one discontinuity from the database
     * @param discontinuityId - the discontinuity identification
     */
    public boolean deleteDiscontinuity(int discontinuityId) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.delete(DISCONTINUITY_TABLE_NAME,DISCONTINUITY_ID+"= ?",new String[]{""+discontinuityId});
            return true;
        }
        catch (Exception e) {}
        finally {
            db.close();
        }
        return false;
    }

    /**
     * Delete the discontinuities of one session and user from the database
     * @param sessionName - the session identifier
     * @param user - the user identifier
     */
    public boolean deleteSessionDiscontinuity(String sessionName, String user) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.delete(DISCONTINUITY_TABLE_NAME,FK_DISCONTINUITY_ID_SESSION+"= ? and "+FK_ID_USER+"=?",new String[]{""+sessionName,""+user});
            return true;
        }
        catch (Exception e) {
            String ee = e.toString();
        }
        finally {
            db.close();
        }
        return false;
    }

    /**
     * Gets the token of one user
     * @param usermail - user identifier
     * @return the token string
     */
    public String getUserToken(String usermail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from user where "+USER_ID_EMAIL+" =\"" + usermail+"\"", null);
        cursor.moveToFirst();
        if (cursor.isFirst()) {
            return cursor.getString(cursor.getColumnIndex(USER_TOKEN_API));
        }
        else
            return null;
    }
}
