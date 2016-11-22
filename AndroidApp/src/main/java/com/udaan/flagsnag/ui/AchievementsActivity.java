package com.udaan.flagsnag.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.udaan.flagsnag.logic.Achievements;

public class AchievementsActivity extends Activity{
	private LinearLayout achievements_ll;
	private Achievements achieve;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_achievements);
		
		achieve = new Achievements(this);
		achievements_ll = (LinearLayout) findViewById(R.id.achievement_rl);
		
		for (int i = 0; i < achieve.getCount(); i++) {
			
			LinearLayout achievementHolder = new LinearLayout(this);
			achievementHolder.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
			        ViewGroup.LayoutParams.WRAP_CONTENT));
			achievementHolder.setOrientation(LinearLayout.HORIZONTAL);
			
			ImageView medal = new ImageView(this);
			if (achieve.isAchieved(i))
				medal.setImageResource(getResources().getIdentifier(achieve.getMedal(i), "drawable", this.getPackageName()));
			else
				medal.setImageResource(getResources().getIdentifier("nomedal", "drawable", this.getPackageName()));
			
			LinearLayout achievement = new LinearLayout(this);
			achievement.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			achievement.setOrientation(LinearLayout.VERTICAL);
			
			TextView achievementName = new TextView(this);
			achievementName.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			achievementName.setText(achieve.getAchievementName(i));
			achievementName.setTextSize((float)23);
			achievementName.setTextColor(Color.WHITE);
			
			TextView achievementDesc = new TextView(this);
			achievementDesc.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			achievementDesc.setText(achieve.getAchievementDesc(i));
			achievementDesc.setTextColor(Color.WHITE);
			
			LinearLayout line = new LinearLayout(this);
			line.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 2));
			line.setBackgroundColor(Color.WHITE);
			
			achievement.addView(achievementName);
			achievement.addView(achievementDesc);
			
			achievementHolder.addView(medal);
			achievementHolder.addView(achievement);
			
			achievements_ll.addView(achievementHolder);
			achievements_ll.addView(line);
		}
	}
	
	public void onDestroy() {
		super.onDestroy();
		achieve.close();
	}
}
