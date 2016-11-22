package com.udaan.flagsnag.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class FavoritesActivity extends Activity{
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorites);
	}
	
	public void helpClicked(View v) {
		Intent intent = new Intent(this, HelpActivity.class);
		startActivity(intent);
	}
	
	public void aboutClicked(View v) {
		Intent intent = new Intent(this, AboutActivity.class);
		startActivity(intent);
	}
	
	public void achievementsClicked(View v) {
		Intent intent = new Intent(this, AchievementsActivity.class);
		startActivity(intent);
	}
}
