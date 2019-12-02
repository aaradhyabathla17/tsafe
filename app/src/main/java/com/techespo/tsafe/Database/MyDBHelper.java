package com.techespo.tsafe.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class MyDBHelper extends SQLiteOpenHelper {
    private  Context myContext;
    private static final String DATABASE_NAME="text.db.sqlite";
    private static final int DATABASE_VERSION=1;
    private static final String query="CREATE TABLE IF NOT EXISTS Users"+"(Id Integer Primary key AUTOINCREMENT, Name text, PhoneNo text,Email text,Password text,Gender text,ProfileImage BLOB)";
    private static final String query_create_table_ride="CREATE TABLE IF NOT EXISTS ride"+
            "(Id Integer Primary key AUTOINCREMENT, from_address text, to_address_address text," +
            " contact_one_phone text,contact_one_name text,contact_two_phone text, contact_two_name text," +
            " image_one_path text, image_two_path text, ride_status Integer,date text,starttime text,reachtime text)";
    public SQLiteDatabase Mydb;
    //private static final String DATABASE_PATH="";

    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.execSQL(query_create_table_ride);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public void openDatabase()
    {
        try{
            Mydb=this.getWritableDatabase();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public void closeDataBase()
    {
        if(Mydb!=null)
        {
            Mydb.close();
            super.close();
        }
    }
    public long insert(String tablename, ContentValues values)
    {
        long res=0;
        try{
            res=Mydb.insert(tablename,null,values);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }

    public long updateRecord(String tablename, ContentValues values, String id)
    {
        long res=0;
        try{
            res=Mydb.update(tablename,values,"id="+id,null);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }

    public int delete(String tablename,String id)
    {
        int res=0;
        try{
            String[] args={id};
            res=Mydb.delete(tablename,"id=?",args);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }
    public Cursor getUsersData()
    {
        Cursor c=null;
        try{
            String queryForUserData="SELECT * FROM Users";
            c=Mydb.rawQuery(queryForUserData,null);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return c;
    }
    public Cursor getValidateUserData(String username,String Password) {
        Cursor c = null;
        try {
            String quryForValidateUser = "SELECT * FROM Users where Name='" + username + "'  AND Password ='" + Password+"'";
            c = Mydb.rawQuery(quryForValidateUser, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public void updateOneColumn(String TABLE_NAME, String Column, String rowId, String ColumnName, String newValue){
        String sql = "UPDATE "+TABLE_NAME +" SET " + ColumnName+ " = "+newValue+" WHERE "+Column+ " = "+rowId;
        Mydb.beginTransaction();
        SQLiteStatement stmt = Mydb.compileStatement(sql);
        try{
            stmt.execute();
            Mydb.setTransactionSuccessful();
        }finally{
            Mydb.endTransaction();
        }
    }

    public long insertRideDetail(ContentValues values)
    {
        long res=0;
        try{
            res=Mydb.insert("ride",null,values);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }
    public long updateRideDetail(String id, ContentValues values){
        long res=0;
        try{
            res=Mydb.update("ride ",values,"id="+id,null);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }

    public Cursor getAllCompletedRide()
    {
        Cursor c=null;
        try{
            String queryForUserData="SELECT * FROM ride where ride_status = 1";
            c=Mydb.rawQuery(queryForUserData,null);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return c;
    }
    public Cursor getCurrentRide()
    {
        Cursor c=null;
        try{
            String queryForUserData="SELECT * FROM ride where ride_status = 0";
            c=Mydb.rawQuery(queryForUserData,null);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return c;
    }
}
