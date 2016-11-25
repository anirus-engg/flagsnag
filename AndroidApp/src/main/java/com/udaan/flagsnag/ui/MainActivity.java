package com.udaan.flagsnag.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import com.udaan.flagsnag.logic.Board;
import com.udaan.flagsnag.logic.CountDownTimerPausable;
import com.udaan.flagsnag.logic.Translations;
import com.udaan.flagsnag.logic.Achievements;

public class MainActivity extends Activity {
	private static final boolean IS_PAID = false;
	private static final int MAX_CHOICE = 4;
	private static final String MY_AD_UNIT_ID = "ca-app-pub-8996795250788622/8832979093";
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private static final String BLANK_TIMER = "      ";
	private static String sTimer = "3:00.0";
	private static int gameTime = 180000;
	
	private boolean pendingPublishReauthorization = false;
	//private boolean pendingPublishAction = true;
	private ImageView flag0, flag1, flag2, flag3;
	private TextView option0, option1, option2, option3;
	private TextView timer, scoreField, scoreText;
	private ImageView settings, favorites;// barcode;
	private LinearLayout adLL;
	private AdView adView;
	private AdRequest adRequest;
	private Board board;
	private String[] flag;
	private String[] option;
	private int score;
	private int continuousScore;
	private int continuousScoreAchieved;
	private SharedPreferences prefs;
	private CountDownTimerPausable countDown;
	private boolean settingsClicked;
	private boolean showResumeAlert = false;
    private boolean showTimer = true;
	private String longCategory = null;
	private Locale currentLocale = null;
	private String playStoreLink = "http://www.amazon.com/gp/product/B00D3TTLXC";
	Configuration config;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		scoreField = (TextView)findViewById(R.id.score);
		timer = (TextView)findViewById(R.id.timer);
		
		flag0 = (ImageView)findViewById(R.id.flag0);
		flag1 = (ImageView)findViewById(R.id.flag1);
		flag2 = (ImageView)findViewById(R.id.flag2);
		flag3 = (ImageView)findViewById(R.id.flag3);
		
		option0 = (TextView)findViewById(R.id.option0);
		option1 = (TextView)findViewById(R.id.option1);
		option2 = (TextView)findViewById(R.id.option2);
		option3 = (TextView)findViewById(R.id.option3);
		
		scoreText = (TextView)findViewById(R.id.score_text);
		settings = (ImageView)findViewById(R.id.settings);
		favorites = (ImageView)findViewById(R.id.favorites);
		//barcode = (ImageView)findViewById(R.id.barcode);
		
		if (!IS_PAID) {
			adView = new AdView(this);
			adView.setAdSize(AdSize.SMART_BANNER);
			adView.setAdUnitId(MY_AD_UNIT_ID);
			adLL = (LinearLayout) findViewById(R.id.ad_ll);
			adLL.addView(adView);
			adRequest = new com.google.android.gms.ads.AdRequest.Builder()
					.addTestDevice("A4163BE3E608B682241FF4D4EA7BD69D")
					.build();
			//adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
			adView.loadAd(adRequest);
		    playStoreLink = "https://play.google.com/store/apps/details?id=com.udaan.flagsnag.ui";
		}
	    
	    settings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				settingsClicked = true;
				showResumeAlert = false;
				Intent intent = new Intent(v.getContext(), SettingsActivity.class);
				startActivity(intent);
			}
		});
	    
		favorites.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), FavoritesActivity.class);
				startActivity(intent);
			}
		});
	    
		alertNewGame();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	protected void onStart() {
		super.onStart();
		Log.d(getClass().toString(), "onStart");
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    	String category = prefs.getString("category", "world");
        showTimer = prefs.getBoolean("enable_timer", true);
        switch (category) {
            case "world":
                sTimer = "3:00.0";
                gameTime = 180000;
                longCategory = getString(R.string.world_flags);
                break;
            case "us_flags":
                sTimer = "1:00.0";
                gameTime = 60000;
                longCategory = getString(R.string.us_flags);
                break;
            case "uk_flags":
                sTimer = "1:00.0";
                gameTime = 60000;
                longCategory = getString(R.string.uk_flags);
                break;
        }
    	
    	String languageCode = prefs.getString("language", "en");
        switch (languageCode) {
            case "en":
                Log.d(getClass().toString(), "Setting language to en");
                config = new Configuration(getResources().getConfiguration());
                config.locale = Locale.ENGLISH;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                currentLocale = Locale.ENGLISH;
                break;
            case "ja":
                Log.d(getClass().toString(), "Setting language to ja");
                config = new Configuration(getResources().getConfiguration());
                config.locale = Locale.JAPANESE;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                currentLocale = Locale.JAPANESE;
                break;
            case "ko":
                Log.d(getClass().toString(), "Setting language to ko");
                config = new Configuration(getResources().getConfiguration());
                config.locale = Locale.KOREAN;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                currentLocale = Locale.KOREAN;
                break;
            case "ru": {
                Log.d(getClass().toString(), "Setting language to ru");
                Locale locale = new Locale("ru");
                config = new Configuration(getResources().getConfiguration());
                config.locale = locale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                currentLocale = locale;
                break;
            }
            case "es": {
                Log.d(getClass().toString(), "Setting language to es");
                Locale locale = new Locale("es");
                config = new Configuration(getResources().getConfiguration());
                config.locale = locale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                currentLocale = locale;
                break;
            }
        }
    	
    	scoreText.setText(R.string.score_text);
    			
    	Board.category = category;
    	
    	if (showResumeAlert && countDown.isPaused()) alertResume();
    	if (settingsClicked) {
    		alertNewGame();
    		settingsClicked = false;
    	}
	}
	
	protected void onResume() {
		super.onResume();
		Log.d(getClass().toString(), "onResume");
	}
	
	protected void onStop() {
		super.onStop();
		Log.d(getClass().toString(), "onStop");
	}
	
	protected void onDestroy() {
		super.onDestroy();
		if (countDown != null) {
			countDown.cancel();
			countDown = null;
		}
	}
	
	protected void onPause() {
		super.onPause();
		Log.d(getClass().toString(), "onPause");
		if (countDown != null && !countDown.isPaused()) countDown.pause();
	}
	
	public void newGame() {
		if (board != null) board.close();
		board = new Board(this);
		
		countDown = new CountDownTimerPausable(gameTime, 100) {

			@Override
			public void onTick(long millisUntilFinished) {
				timer.setText(formatTime(millisUntilFinished));
			}

			@Override
			public void onFinish() {
				timer.setText("0:00.0");
				alertGameOver();
			}
    		
    	};

        if(showTimer) {
            countDown.start();
        }
        else {
            timer.setText(BLANK_TIMER);
        }
		loadBoard();
	}
	
	public void alertNewGame() {
		Log.d(getClass().toString(), "alertNewGame");
		timer.setText(sTimer);
		score = 0;
		continuousScore = 0;
		continuousScoreAchieved = 0;
		AlertDialog.Builder alertConfirm = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		alertConfirm.setTitle(R.string.new_game);
		alertConfirm.setView(inflater.inflate(R.layout.alert_newgame, null));
		alertConfirm.setCancelable(false);
		alertConfirm.setNeutralButton(getResources().getString(R.string.start), new DialogInterface.OnClickListener() { 
			@Override
			public void onClick(DialogInterface dialog, int which) {
				showResumeAlert = true;
				newGame();
			}
		});
		alertConfirm.show();
	}
	
	public void alertGameOver() {
		Log.d(getClass().toString(), "alertGameOver");
		showResumeAlert = false;
		board.close();
		
		Achievements achievement = new Achievements(this);
		achievement.setAchievement(longCategory, continuousScoreAchieved > continuousScore ? continuousScoreAchieved : continuousScore, this);
		achievement.close();
		
		AlertDialog.Builder alertConfirm = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		alertConfirm.setTitle(R.string.times_up);
		alertConfirm.setView(inflater.inflate(R.layout.alert_gameover, null));
		alertConfirm.setCancelable(false);
		alertConfirm.setNeutralButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() { 
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertNewGame();
			}
		});
		alertConfirm.show();
	}

    public void alertGameComplete() {
        Log.d(getClass().toString(), "alertGameComplete");
        showResumeAlert = false;
        board.close();

        /*Achievements achievement = new Achievements(this);
        achievement.setAchievement(longCategory, continuousScoreAchieved > continuousScore ? continuousScoreAchieved : continuousScore, this);
        achievement.close();*/

        AlertDialog.Builder alertConfirm = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        alertConfirm.setTitle(R.string.game_complete);
        alertConfirm.setView(inflater.inflate(R.layout.alert_gamecomplete, null));
        alertConfirm.setCancelable(false);
        alertConfirm.setNeutralButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertNewGame();
            }
        });
        alertConfirm.show();
    }
	
	public void alertResume() {
		showResumeAlert = false;
		AlertDialog.Builder alertConfirm = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		alertConfirm.setTitle(R.string.resume);
		alertConfirm.setView(inflater.inflate(R.layout.alert_resume, null));
		alertConfirm.setCancelable(false);
		alertConfirm.setNeutralButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() { 
			@Override
			public void onClick(DialogInterface dialog, int which) {
				showResumeAlert = true;
                if(showTimer) {
                    countDown.start();
                }
                else {
                    timer.setText(BLANK_TIMER);
                }
			}
		});
		alertConfirm.show();
		Log.d(getClass().toString(), "alertResume");
	}
	
	private void loadBoard() {
		flag = board.getFlags();
		option = board.getOptions();
		
		scoreField.setText("" + score);
		
		flag0.setImageResource(getResources().getIdentifier(flag[0], "drawable", this.getPackageName()));
		flag1.setImageResource(getResources().getIdentifier(flag[1], "drawable", this.getPackageName()));
		flag2.setImageResource(getResources().getIdentifier(flag[2], "drawable", this.getPackageName()));
		flag3.setImageResource(getResources().getIdentifier(flag[3], "drawable", this.getPackageName()));
		
		//Randomize the options
		Integer[] randInt = new Integer[MAX_CHOICE];
		for (int i = 0; i < MAX_CHOICE; i++) randInt[i] = i;
		
		flag0.setId(randInt[0]);
		flag0.setTag(board.getName(flag[0]));
		flag0.setOnDragListener(new MyDragListener());
		flag1.setId(randInt[1]);
		flag1.setTag(board.getName(flag[1]));
		flag1.setOnDragListener(new MyDragListener());
		flag2.setId(randInt[2]);
		flag2.setTag(board.getName(flag[2]));
		flag2.setOnDragListener(new MyDragListener());
		flag3.setId(randInt[3]);
		flag3.setTag(board.getName(flag[3]));
		flag3.setOnDragListener(new MyDragListener());
		
		Collections.shuffle(Arrays.asList(randInt));

		option0.setText(option[randInt[0]]);
		option0.setTag(option[randInt[0]]);
		option0.setOnTouchListener(new MyTouchListener());
		option1.setText(option[randInt[1]]);
		option1.setTag(option[randInt[1]]);
		option1.setOnTouchListener(new MyTouchListener()); 
		option2.setText(option[randInt[2]]);
		option2.setTag(option[randInt[2]]);
		option2.setOnTouchListener(new MyTouchListener()); 
		option3.setText(option[randInt[3]]);
		option3.setTag(option[randInt[3]]);
		option3.setOnTouchListener(new MyTouchListener()); 
		
	}
	
	private void isMatched(int index) {
		score++;
		continuousScore++;
        if(board.removeFlag(index)) {
            loadBoard();
        }
        else {
            alertGameComplete();
        }
	}
	
	private CharSequence formatTime(long millisUntilFinished) {
		int timer100mili = (int) (millisUntilFinished / 100);
		int timerSec = timer100mili / 10;
		int timerMin = timerSec / 60;
		
		return timerMin + ":" + String.format("%02d", timerSec % 60) + "." + (timer100mili % 10);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	private boolean isSubsetOf(List<String> subset, List<String> superset) {
		for (String string : subset) {
		     if (!superset.contains(string)) {
		         return false;
		     }
		}
		return false;
	}
	
	/*
	private void showAlert(String msg) {
		AlertDialog.Builder alertConfirm = new AlertDialog.Builder(this);
		//alertConfirm.setTitle(R.string.times_up);
		alertConfirm.setMessage(msg);
		alertConfirm.setCancelable(false);
		alertConfirm.setNeutralButton("OK", new DialogInterface.OnClickListener() { 
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		alertConfirm.show();
	}
	*/
	
	private class MyTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				ClipData data = ClipData.newPlainText((String)v.getTag(), (String)v.getTag());
			    DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
			    v.startDrag(data, shadowBuilder, v, 0);
			    v.setVisibility(View.DRAWING_CACHE_QUALITY_AUTO);
			    return true;
			} 
			else {
				return false;
			}
		}
	}
	
	private class MyDragListener implements OnDragListener {
		@Override
		public boolean onDrag(View v, DragEvent event) {
		    switch (event.getAction()) {
		    case DragEvent.ACTION_DROP:
		    	//Dropped, reassign View to ViewGroup
		    	ClipData.Item item = event.getClipData().getItemAt(0);
		    	Log.d(getClass().toString(), item.getText() + " " + (String) v.getTag() + " " + v.getId());
		    	if (item.getText().equals(v.getTag())) {
					isMatched(v.getId());
		    	}
		    	else {
		    		MediaPlayer player = MediaPlayer.create(v.getContext(), R.raw.wrong);
					player.start();
					player = null;
					
					continuousScoreAchieved = continuousScoreAchieved > continuousScore ? continuousScoreAchieved : continuousScore;
					continuousScore = 0;
		    	}
		    	break;
		    case DragEvent.ACTION_DRAG_STARTED:
		    case DragEvent.ACTION_DRAG_ENTERED:
		    case DragEvent.ACTION_DRAG_EXITED:        
		    case DragEvent.ACTION_DRAG_ENDED:
		    default:
		      break;
		    }
		    return true;
		}
	}
}
