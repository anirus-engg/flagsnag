/**
 * 
 */
package com.udaan.flagsnag.logic;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.udaan.flagsnag.ui.R;

import android.content.Context;
import android.util.Log;

/**
 * This class is the main class for the logic behind this quiz game.
 *
 */
public class Board {
	public static String category = "world";
	private String[][] data;
	private DBAdapter dbAdapter;
	private String flag_col_name;
	
	Integer[] randInt;
	List<Integer> list;
	
	/**
	 * Reads the flag data from the assets/data.csv and stores in the data[][] for quick retrieval
	 */
	public Board(Context context) {
		dbAdapter = new DBAdapter(context);
		flag_col_name = context.getResources().getString(R.string.flag_table_col_name);
		Log.i(getClass().toString(),"Loading data...");
		
		String sql = "SELECT " + DBAdapter.FLAG_COL_FLAG + "," + flag_col_name + 
				" FROM " + DBAdapter.FLAG_TABLE + 
				" WHERE " + DBAdapter.FLAG_COL_CATEGORY + "='" + category + "';";
		
		data = dbAdapter.getRows(dbAdapter.getReadableDatabase(), sql);
		
		Log.i(getClass().toString(),"Loading data complete. Total rows: " + data.length);
		
		randInt = new Integer[data.length];
		for (int i = 0; i < data.length; i++) randInt[i] = i;
		list = new LinkedList<Integer>(Arrays.asList(randInt));
		Collections.shuffle(list);
	}
	
	public void close() {
		dbAdapter.close();
	}

	public String[] getFlags() {
		String[] flags = {data[list.get(0)][0], data[list.get(1)][0], data[list.get(2)][0], data[list.get(3)][0]};
		Log.d(getClass().toString(), "data[list.get[0]][0]" + data[list.get(0)][0]);
		Log.d(getClass().toString(), "data[list.get[1]][0]" + data[list.get(1)][0]);
		Log.d(getClass().toString(), "data[list.get[2]][0]" + data[list.get(2)][0]);
		Log.d(getClass().toString(), "data[list.get[3]][0]" + data[list.get(3)][0]);
		return flags;
	}

	public String[] getOptions() {
		String[] options = {data[list.get(0)][1], data[list.get(1)][1], data[list.get(2)][1], data[list.get(3)][1]};
		Log.d(getClass().toString(), "data[list.get[0]][1]" + data[list.get(0)][1]);
		Log.d(getClass().toString(), "data[list.get[1]][1]" + data[list.get(1)][1]);
		Log.d(getClass().toString(), "data[list.get[2]][1]" + data[list.get(2)][1]);
		Log.d(getClass().toString(), "data[list.get[3]][1]" + data[list.get(3)][1]);
		return options;
	}
	
	public String getName(String flag) {
		String sql = "SELECT " + flag_col_name + " FROM " + DBAdapter.FLAG_TABLE +
				" WHERE " + DBAdapter.FLAG_COL_FLAG + "='" + flag + "';";
		String[] name = dbAdapter.getOneRow(dbAdapter.getReadableDatabase(), sql);
		return name[0];
	}
	
	public boolean removeFlag(int index) {
		Log.d(getClass().toString(), "Removing flag index: " + data[randInt[index]][0] + " randInt:" + randInt[index]);
		Log.d(getClass().toString(), "Removed:" + list.remove(index));
		Collections.shuffle(list);
		return true;
	}
}
