package com.uniwic.mediahub;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class About extends SherlockActivity{
	
	TextView developer,eztube;
	Typeface font;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		getSupportActionBar().hide();
		
		developer = (TextView) findViewById(R.id.developer);
		eztube = (TextView) findViewById(R.id.eztube);
		try {
			font = Typeface.createFromAsset(getAssets(), "segoe_print.ttf");  
			developer.setTypeface(font);
			eztube.setTypeface(font);
		}catch(Exception e){
		}
	}

}
