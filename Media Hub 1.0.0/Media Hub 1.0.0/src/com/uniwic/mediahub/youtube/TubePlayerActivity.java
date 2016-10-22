
package com.uniwic.mediahub.youtube;



import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.uniwic.mediahub.R;

public class TubePlayerActivity extends YouTubeFailureRecoveryActivity implements
        YouTubePlayer.OnFullscreenListener {

    private ActionBarPaddedFrameLayout viewContainer;
    private YouTubePlayerSupportFragment playerFragment;
    private YouTubePlayer player;
 
    Bundle vidsArray;
	String vids[];
	int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.contents);
        initControl();
        initActionBar();
    }

    private void initControl() {
    	
    	vidsArray=getIntent().getExtras();
 		vids = vidsArray.getStringArray("vids");
 		viewContainer = (ActionBarPaddedFrameLayout) findViewById(R.id.view_container);
		playerFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.player_fragment);
	    playerFragment.initialize(DeveloperKey.DEVELOPER_KEY, this);
    }

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
	}

	@Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
            YouTubePlayer player, boolean wasRestored) {
        player.setFullscreen(true);
        player.setPlayerStateChangeListener(new VideoListener());
        player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
        player.setOnFullscreenListener(this);
        this.player = player;
    
        if (!wasRestored) {
            player.loadVideo(vids[0]);
        }
    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(
                R.id.player_fragment);
    }

    @Override
    public void onFullscreen(boolean fullscreen) {
    	
    	try {
    		 viewContainer.setEnablePadding(!fullscreen);
    		 ViewGroup.LayoutParams playerParams = playerFragment.getView()
    	                .getLayoutParams();
    		 if (fullscreen) {
    			 playerParams.width = LayoutParams.MATCH_PARENT;
    			 playerParams.height = LayoutParams.MATCH_PARENT;
    		 } else {
    			 playerParams.width = LayoutParams.MATCH_PARENT;
    			 playerParams.height = LayoutParams.WRAP_CONTENT;
    		 }
    	}catch(Exception e){
    	}
    }

    public static final class ActionBarPaddedFrameLayout extends FrameLayout {

        private ActionBar actionBar;
        private boolean paddingEnabled;

        public ActionBarPaddedFrameLayout(Context context) {
            this(context, null);
        }

        public ActionBarPaddedFrameLayout(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public ActionBarPaddedFrameLayout(Context context, AttributeSet attrs,
                int defStyle) {
            super(context, attrs, defStyle);
            paddingEnabled = true;
        }

        public void setActionBar(ActionBar actionBar) {
            this.actionBar = actionBar;
            requestLayout();
        }

        public void setEnablePadding(boolean enable) {
            paddingEnabled = enable;
            requestLayout();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int topPadding = paddingEnabled && actionBar != null
                    && actionBar.isShowing() ? actionBar.getHeight() : 0;
            setPadding(0, topPadding, 0, 0);

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                return false;
        }
        return true;
    }
    
    private final class VideoListener implements YouTubePlayer.PlayerStateChangeListener {

		public void onLoaded(String videoId) {
		
		}
		
		public void onVideoEnded() {
			if(vids.length > i){
				player.loadVideo(vids[i]);
				++i;
			}else{
				Toast.makeText(getApplicationContext(), "No more videos to play", 20).show();
			}
		}
		
		public void onError(YouTubePlayer.ErrorReason errorReason) {
		}
		
		public void onVideoStarted() {
		}
		
		public void onAdStarted() {
		}
		
		public void onLoading() {
		}
	}
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                if (player.isPlaying())
                    player.pause();
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                if (!player.isPlaying())
                    player.play();
                return true;
            case KeyEvent.KEYCODE_MEDIA_STOP:
                finish();
                return true;
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                player.seekToMillis(player.getCurrentTimeMillis() + 5000);
                return true;
            case KeyEvent.KEYCODE_MEDIA_REWIND:
                player.seekToMillis(player.getCurrentTimeMillis() - 5000);
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }

    }
}
