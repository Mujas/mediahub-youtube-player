package com.uniwic.mediahub.theme;



import android.app.Activity;
import android.content.Intent;

import com.uniwic.mediahub.R;

public class Theme {
	private static int sTheme;

	public final static int THEME_DEFAULT = 0;
	public final static int THEME_WHITE = 1;
	public final static int THEME_BLACK = 2;

	public static void changeToTheme(Activity activity, int theme) {
		sTheme = theme;
		activity.finish();
		activity.startActivity(new Intent(activity, activity.getClass()));
	}

	/** Set the theme of the activity, according to the configuration. */
	public static void onActivityCreateSetTheme(Activity activity) {
		switch (sTheme) {
		default:
		case THEME_DEFAULT:
			activity.setTheme(R.style.Theme_White);
			break;
		case THEME_WHITE:
			activity.setTheme(R.style.Theme_White);
			break;
		case THEME_BLACK:
			activity.setTheme(R.style.Theme_Black);
			break;
		}
	}
}
