package com.go.sdmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter
{
	static final String KEY_ROWID = "_id";
	static final String KEY_NAME = "name";
	static final String KEY_ACTION = "action";
	static final String KEY_ACTION_TIME = "actiontime";
	static final String TAG = "DBAdapter";

	static final String DATABASE_NAME = "file_oper.db";
	static final String DATABASE_TABLE = "actions";
	static final int DATABASE_VERSION = 1;

	static final String DATABASE_CREATE = "create table actions (_id integer primary key autoincrement, " + "name text not null, action text not null, actiontime text not null);";
	final Context mContext;
	DatabaseHelper mDBHelper;
	SQLiteDatabase mDB;

	public DBAdapter(Context ctx)
	{
		this.mContext = ctx;
		mDBHelper = new DatabaseHelper(mContext);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper
	{

		public DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			try
			{
				db.execSQL(DATABASE_CREATE);
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			Log.i(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS actions");
			onCreate(db);
		}
	}

	// ---opens the database---
	public DBAdapter open() throws SQLException
	{
		mDB = mDBHelper.getWritableDatabase();
		return this;
	}

	// ---closes the database---
	public void close()
	{
		mDBHelper.close();
	}

	// ---insert a contact into the database---
	public long insertAction(String name, String action, String actiontime)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_ACTION, action);
		initialValues.put(KEY_ACTION_TIME, actiontime);

		return mDB.insert(DATABASE_TABLE, null, initialValues);
	}

	// ---deletes a particular contact---
	public boolean deletelAction(String rowId)
	{
		return mDB.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	// ---retrieves all the contacts---
	public Cursor getAllActions()
	{
		return mDB.query(DATABASE_TABLE, new String[]
		{ KEY_ROWID, KEY_NAME, KEY_ACTION, KEY_ACTION_TIME }, null, null, null, null, null);
	}

	// ---retrieves a particular contact---
	public Cursor getAction(long rowId) throws SQLException
	{
		Cursor mCursor = mDB.query(true, DATABASE_TABLE, new String[]
		{ KEY_ROWID, KEY_NAME, KEY_ACTION, KEY_ACTION_TIME }, KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null)
		{
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// ---updates a contact---
	public boolean updateAction(long rowId, String name, String action, String actiontime)
	{
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, name);
		args.put(KEY_ACTION, action);
		args.put(KEY_ACTION_TIME, actiontime);
		return mDB.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

}
