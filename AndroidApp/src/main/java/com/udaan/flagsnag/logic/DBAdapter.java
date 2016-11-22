package com.udaan.flagsnag.logic;

import java.nio.charset.Charset;

import com.csvreader.CsvReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter extends SQLiteOpenHelper{
	private static final int DATABASE_VER = 4;
	private static final String DATABASE_NAME = "FlagSnag.db";
	private Context context;
	
	public static final String FLAG_TABLE = "Flags";
	public static final String FLAG_TABLE_PK = "Flag_PK";
	public static final String FLAG_COL_FLAG = "Flag";
	public static final String FLAG_COL_NAME = "Name";
	public static final String FLAG_COL_CATEGORY = "Category";
	public static final String FLAG_COL_NAME_JA = "Name_JA";
	public static final String FLAG_COL_NAME_RU = "Name_RU";
	public static final String FLAG_COL_NAME_KO = "Name_KO";
	public static final String FLAG_COL_NAME_ES = "Name_ES";
	
	public static final String ACHIEVEMENTS_TABLE = "Achievements";
	public static final String ACHIEVEMENTS_PK = "Achievements_PK";
	public static final String ACHIEVEMENTS_ID = "Id";
	public static final String ACHIEVEMENTS_NAME = "Name";
	public static final String ACHIEVEMENTS_DESC = "Description";
	public static final String ACHIEVEMENTS_MEDAL = "Medal";
	public static final String ACHIEVEMENTS_ACHIEVED = "Achieved";

	public DBAdapter(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VER);
		this.context = context;
		getReadableDatabase();
		close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String FLAG_TABLE_CREATE = "CREATE TABLE " + FLAG_TABLE + "(" + FLAG_COL_FLAG + " TEXT NOT NULL, " + 
				FLAG_COL_NAME + " TEXT NOT NULL, " + FLAG_COL_CATEGORY + " TEXT NOT NULL, " + 
				FLAG_COL_NAME_JA + " TEXT NOT NULL, " +
				FLAG_COL_NAME_RU + " TEXT NOT NULL, " +
				FLAG_COL_NAME_KO + " TEXT NOT NULL, " +
				FLAG_COL_NAME_ES + " TEXT NOT NULL, " +
				"CONSTRAINT " + FLAG_TABLE_PK + " PRIMARY KEY (" + FLAG_COL_FLAG + "," + FLAG_COL_CATEGORY + "));";
		
		String ACHIVEMENTS_TABLE_CREATE = "CREATE TABLE " + ACHIEVEMENTS_TABLE + "(" + ACHIEVEMENTS_ID + " NUMBER NOT NULL, " +
				ACHIEVEMENTS_NAME + " TEXT NOT NULL, " + ACHIEVEMENTS_DESC + " TEXT NOT NULL, " +
				ACHIEVEMENTS_MEDAL + " TEXT NOT NULL, " + ACHIEVEMENTS_ACHIEVED + " NUMBER NOT NULL, " +
				"CONSTRAINT " + ACHIEVEMENTS_PK + " PRIMARY KEY (" + ACHIEVEMENTS_ID + "));";
		
		Log.d(getClass().toString(), FLAG_TABLE_CREATE);
		Log.d(getClass().toString(), ACHIVEMENTS_TABLE_CREATE);
		
		db.execSQL(FLAG_TABLE_CREATE);
		db.execSQL(ACHIVEMENTS_TABLE_CREATE);
		
		loadFlagData(db);
		loadAchievementsData(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS "+ FLAG_TABLE);
		
		onCreate(db);
	}
	
	public void execSQL(SQLiteDatabase db, String sql) {
		Log.d(getClass().toString(), "sql:" + sql);
		db.execSQL(sql);
	}
	
	public String[] getOneRow(SQLiteDatabase db, String sql) {
		String[] whereArgs = null;
		
		Log.d(getClass().toString(), "sql:" + sql);
		Cursor result = db.rawQuery(sql, whereArgs);

		String data[] = new String[result.getColumnCount()];
		
		if (result.getCount() != 0) {
			result.moveToFirst();
			for(int c = 0; c < result.getColumnCount(); c++) {
				data[c] = result.getString(c);
			}
		}
		else {
			data = null;
		}
		result.close();
		return data;
	}
	
	public String[][] getRows(SQLiteDatabase db, String sql) {
		String[] whereArgs = null;
		
		Log.d(getClass().toString(), "sql:" + sql);
		Cursor result = db.rawQuery(sql, whereArgs);
		
		String data[][] = new String[result.getCount()][result.getColumnCount()];
		Log.d(getClass().toString(), "Returning " + result.getCount() + " rows");
		if (result.getCount() != 0) {
			result.moveToFirst();
			for(int r = 0; r < result.getCount(); r++) {
				for(int c = 0; c < result.getColumnCount(); c++) {
					data[r][c] = result.getString(c);
				}
				result.moveToNext();
			}
		}
		else {
			data = null;
		}
		result.close();
		return data;
	}

	private boolean loadFlagData(SQLiteDatabase db) {
		boolean result = false;
		String values[] = null;
		ContentValues row = new ContentValues();
		Log.i(getClass().toString(),"Loading Flag table data...");

		try {
			CsvReader file = new CsvReader(context.getAssets().open("data.csv"), Charset.defaultCharset());
			file.readHeaders();
			
			while(file.readRecord()) {
				values = file.getValues();
				
				row.put(FLAG_COL_FLAG, values[0]);
				row.put(FLAG_COL_NAME, values[1]);
				row.put(FLAG_COL_CATEGORY, values[2]);
				row.put(FLAG_COL_NAME_JA, values[3]);
				row.put(FLAG_COL_NAME_RU, values[4]);
				row.put(FLAG_COL_NAME_KO, values[5]);
				row.put(FLAG_COL_NAME_ES, values[6]);
				
				Log.d(getClass().toString(), "Loading: " + values[1]);

				if (db.insert(FLAG_TABLE, null, row) != -1) {
					result = true;
				}
				else {
					result = false;
					break;
				}
			}
			file.close();
		} catch (Exception e) {
			Log.e(getClass().toString(),e.toString());
			result = false;
		}
		
		Log.i(getClass().toString(),"Loading Flag table complete");
		return result;
	}
	
	private boolean loadAchievementsData (SQLiteDatabase db) {
		boolean result = false;
		String values[] = null;
		ContentValues row = new ContentValues();
		Log.i(getClass().toString(),"Loading Achievements table data...");

		try {
			CsvReader file = new CsvReader(context.getAssets().open("achievements.csv"), Charset.defaultCharset());
			file.readHeaders();
			
			while(file.readRecord()) {
				values = file.getValues();
				
				row.put(ACHIEVEMENTS_ID, values[0]);
				row.put(ACHIEVEMENTS_NAME, values[1]);
				row.put(ACHIEVEMENTS_DESC, values[2]);
				row.put(ACHIEVEMENTS_MEDAL, values[3]);
				row.put(ACHIEVEMENTS_ACHIEVED, values[4]);
				
				Log.d(getClass().toString(), "Loading: " + values[1]);

				if (db.insert(ACHIEVEMENTS_TABLE, null, row) != -1) {
					result = true;
				}
				else {
					result = false;
					break;
				}
			}
			file.close();
		} catch (Exception e) {
			Log.e(getClass().toString(),e.toString());
			result = false;
		}
		
		Log.i(getClass().toString(),"Loading Achievements table complete");
		return result;
	}
}


