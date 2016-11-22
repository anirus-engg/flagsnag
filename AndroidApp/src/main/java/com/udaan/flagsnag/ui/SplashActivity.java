package com.udaan.flagsnag.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

public class SplashActivity extends Activity{
	private boolean showSplash = true;
	CountDownTimer splashTimer = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		splashTimer = new CountDownTimer(2000, 1000) {

		     public void onTick(long millisUntilFinished) {}

		     public void onFinish() {
		         startGame(null);
		     }
		  };
	}
	
	protected void onStart() {
		super.onStart();
		if (showSplash) splashTimer.start();
		else finish();
	}
	
	public void startGame(View v) {
		showSplash = false;
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
}
