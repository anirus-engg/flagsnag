package com.udaan.flagsnag.logic;

import com.udaan.flagsnag.ui.R;
import android.content.Context;
import android.util.Log;

public class Achievements {
	private DBAdapter dbAdapter;
	private String data[][];
	
	public Achievements(Context context) {
		dbAdapter = new DBAdapter(context);
		String sql = "SELECT * FROM " + DBAdapter.ACHIEVEMENTS_TABLE + " ORDER BY " + DBAdapter.ACHIEVEMENTS_ID;
		data = dbAdapter.getRows(dbAdapter.getReadableDatabase(), sql);
	}

	public int getCount() {
		return data.length;
	}
	
	public String getAchievement (int row, int column) {
		return data[row][column];
	}

	public boolean isAchieved(int row) {
		return !data[row][4].equals("0");
	}

	public String getMedal(int row) {
		return data[row][3];
	}

	public CharSequence getAchievementDesc(int row) {
		return data[row][2];
	}
	
	public CharSequence getAchievementName(int row) {
		return data[row][1];
	}

	public void close() {
		dbAdapter.close();
		data = null;
	}

	public void setAchievement(String category, int continuousScoreAchieved, Context context) {
		//TODO
		int id = 0;
		Log.d(getClass().toString(), "continuousScoreAchieved:" + continuousScoreAchieved);
		
		if (category.equals(context.getString(R.string.world_flags))) {
			if (continuousScoreAchieved >= 105) id = 4;
			else if (continuousScoreAchieved >= 75) id = 3;
			else if (continuousScoreAchieved >= 35) id = 2;
			else if (continuousScoreAchieved >= 15) id = 1;
		}
		else if (category.equals(context.getString(R.string.us_flags))) {
			if (continuousScoreAchieved >= 35) id = 8;
			else if (continuousScoreAchieved >= 25) id = 7;
			else if (continuousScoreAchieved >= 15) id = 6;
			else if (continuousScoreAchieved >= 5) id = 5;
		}
		else if (category.equals(context.getString(R.string.uk_flags))) {
			if (continuousScoreAchieved >= 35) id = 12;
			else if (continuousScoreAchieved >= 25) id = 11;
			else if (continuousScoreAchieved >= 15) id = 10;
			else if (continuousScoreAchieved >= 5) id = 9;
		}
		
		String sql = "UPDATE " + DBAdapter.ACHIEVEMENTS_TABLE + " SET " + DBAdapter.ACHIEVEMENTS_ACHIEVED + "=1 WHERE " + 
				DBAdapter.ACHIEVEMENTS_ID + "=" + id;
		if (id != 0) dbAdapter.execSQL(dbAdapter.getWritableDatabase(), sql);
	}
}
