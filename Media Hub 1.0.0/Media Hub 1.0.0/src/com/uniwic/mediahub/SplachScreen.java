package com.uniwic.mediahub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplachScreen extends Activity{
	
	
	ImageView connect_small_gear,connect_big_gear;
	Handler handler;
	Runnable r;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		connect_small_gear = (ImageView) findViewById(R.id.connect_small_gear);
		connect_big_gear = (ImageView) findViewById(R.id.connect_big_gear);
		
	
		Animation rotation = AnimationUtils.loadAnimation(this, R.anim.infinite_rotate_rev);
		rotation.setRepeatCount(Animation.INFINITE);
		connect_small_gear.startAnimation(rotation);
		
		Animation rotation2 = AnimationUtils.loadAnimation(this, R.anim.infinite_rotate_slow);
		rotation2.setRepeatCount(Animation.INFINITE);
		connect_big_gear.startAnimation(rotation2);
		launchEz();
		
	}
	
	void launchEz() {
		handler = new Handler();
		r = new Runnable() {
			public void run() {
		    	startActivity(new Intent(getApplicationContext(),MainPagerActivity.class));
		    	handler.removeCallbacks(r);
		    	finish();
		    }
		};
		handler.postDelayed(r, 3000);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		handler.removeCallbacks(r);
	}
		
}
	
