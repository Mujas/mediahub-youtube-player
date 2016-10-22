package com.uniwic.mediahub.mychannel;

import java.util.List;

import settergetter.Category;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.uniwic.mediahub.InternetCheck;
import com.uniwic.mediahub.R;
import com.uniwic.mediahub.youtube.GetYoutubeData;
import com.uniwic.mediahub.youtube.TubePlayerActivity;

public class MyChannelListResult extends SherlockActivity {
	ImageView imageView;
	
	GridView gridViewChannels;
	PullToRefreshGridView pullToRefreshGridView;
	
	ChannelAdapter channelAdapter;
	GetYoutubeData getYoutubeData;
	List<Category> listMedia;
	InternetCheck check;
	DisplayImageOptions options;
	String incomingChannel="",incomingURL="";
	TextView textViewChannel;
	String imageUrl;
	Bitmap myLogo;
	RelativeLayout topPane;
	
	//Hangout Anim
	private TextView hangoutTvOne;
	private TextView hangoutTvTwo;
	private TextView hangoutTvThree;
	private ObjectAnimator waveOneAnimator;
	private ObjectAnimator waveTwoAnimator;
	private ObjectAnimator waveThreeAnimator;
	RelativeLayout waveAnimation;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		setContentView(R.layout.ws_munday_slidingmenu_test_content);
		super.onCreate(savedInstanceState);
		
		initControls();
		initData();
		
	}
	
	private void initControls() {
		topPane = (RelativeLayout) findViewById(R.id.topPane);
		pullToRefreshGridView = (PullToRefreshGridView) findViewById(R.id.gridViewChannel);
		gridViewChannels = pullToRefreshGridView.getRefreshableView();
		
		try {
			StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			
			options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.card_background)
				.showImageForEmptyUri(R.drawable.card_background)
				.showImageOnFail(R.drawable.card_background)
				.cacheInMemory()
				.cacheOnDisc()
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();
			
			getYoutubeData = new GetYoutubeData();
			check = new InternetCheck(getApplicationContext());
			
			//Hangout Animation
			waveAnimation = (RelativeLayout) findViewById(R.id.waveAnimation);
			
			hangoutTvOne = (TextView) findViewById(R.id.hangoutTvOne);
			hangoutTvTwo = (TextView) findViewById(R.id.hangoutTvTwo);
			hangoutTvThree = (TextView) findViewById(R.id.hangoutTvThree);
			
		}catch(Exception e){
		}
		
	}
	
	private void initData() {
		try {
			
			Bundle bundle = getIntent().getExtras();
			if(bundle != null){
				incomingChannel = bundle.getString("Name");
				incomingURL = bundle.getString("MediaUrl");
			}
		
			getSupportActionBar().setTitle(incomingChannel);
			topPane.setVisibility(View.GONE);
			if(check.checkNetworkStatus()){
				CategoryAsync CategoryAsync = new CategoryAsync();
				CategoryAsync.execute(incomingURL);
			}else{
				Toast.makeText(getApplicationContext(), "Please connect your internet and restart your app again",20).show();
			}
		}catch(Exception e){
			
		}
	}

	private void fillGridAdapter() {
		channelAdapter = new ChannelAdapter(getApplicationContext());
		gridViewChannels.setAdapter(channelAdapter);
		gridViewChannels.startLayoutAnimation();
		
	}
	
	class ChannelAdapter extends ArrayAdapter {
		Context context;
		ImageLoader imageLoader;
	
		public ChannelAdapter(Context context) {
			super(context, R.layout.item_channel_grid,listMedia);
			this.context=context;
			imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			try {
				if(convertView == null){
					holder=new ViewHolder();
					convertView= (LinearLayout)LayoutInflater.from(context).inflate(R.layout.item_channel_grid,null);
					holder.channeTitle=(TextView)convertView.findViewById(R.id.textView1);
					holder.channelImage=(ImageView)convertView.findViewById(R.id.imageView1);
					
					convertView.setTag(holder);
					
				}else{
					holder = (ViewHolder) convertView.getTag();
				}
		
				String path ="http://img.youtube.com/vi/"+listMedia.get(position).getMediaUrl()+"/0.jpg";
				
				holder.channeTitle.setText(listMedia.get(position).getCategoryName());
				imageLoader.displayImage(path, holder.channelImage, options,new SimpleImageLoadingListener(){
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
							Animation anim = AnimationUtils.loadAnimation(MyChannelListResult.this,R.anim.fade_in);
							holder.channelImage.setAnimation(anim);
					        anim.start();
						super.onLoadingComplete(imageUri, view, loadedImage);
					}
				});
			
				gridViewChannels.setOnItemClickListener(new OnItemClickListener() {
	
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
						String vids[]=new String[1];
						vids[0] = listMedia.get(arg2).getMediaUrl().trim();
						Intent intent=new Intent(getApplicationContext(),TubePlayerActivity.class);
						Bundle bundle=new Bundle();
						bundle.putStringArray("vids",vids);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});
			}catch(Exception e){
			}
			return convertView;
		}
	}

	class ViewHolder{
		TextView genreTitle,channeTitle,video_count;
		ImageView genreImage,channelImage;
	}
	
	
	class CategoryAsync extends AsyncTask<String,Void,List<Category>>{
		
		@Override
		protected void onPreExecute() {
			waveAnimation();
			super.onPreExecute();
		}

		@Override
		protected List<Category> doInBackground(String... params) {
			listMedia = getYoutubeData.GetPlaylistData(params[0]);
			return listMedia;
		}
		
		@Override
		protected void onPostExecute(List<Category> result) {
			if(result.size() > 0){
				stopAnimator();
				fillGridAdapter();
			}else{
				setError();
			}
			super.onPostExecute(result);
		}
	}
	
	private void setError() {
		Toast.makeText(getApplicationContext(), "Sorry Videos are not available ",20).show();
	}
	
	public void waveAnimation() {
		
		waveAnimation.setVisibility(View.VISIBLE);
			
		if (waveOneAnimator == null && waveTwoAnimator == null && waveThreeAnimator == null) {
			
			PropertyValuesHolder tvOne_Y = PropertyValuesHolder.ofFloat(hangoutTvOne.TRANSLATION_Y, -40.0f);
			PropertyValuesHolder tvOne_X = PropertyValuesHolder.ofFloat(hangoutTvOne.TRANSLATION_X, 0);
			waveOneAnimator = ObjectAnimator.ofPropertyValuesHolder(hangoutTvOne, tvOne_X, tvOne_Y);
			waveOneAnimator.setRepeatCount(ValueAnimator.INFINITE);
			waveOneAnimator.setRepeatMode(ValueAnimator.REVERSE);
			waveOneAnimator.setDuration(300);
			waveOneAnimator.start();
	
			PropertyValuesHolder tvTwo_Y = PropertyValuesHolder.ofFloat(hangoutTvTwo.TRANSLATION_Y, -40.0f);
			PropertyValuesHolder tvTwo_X = PropertyValuesHolder.ofFloat(hangoutTvTwo.TRANSLATION_X, 0);
			waveTwoAnimator = ObjectAnimator.ofPropertyValuesHolder(hangoutTvTwo, tvTwo_X, tvTwo_Y);
			waveTwoAnimator.setRepeatCount(ValueAnimator.INFINITE);
			waveTwoAnimator.setRepeatMode(ValueAnimator.REVERSE);
			waveTwoAnimator.setDuration(300);
			waveTwoAnimator.setStartDelay(100);
			waveTwoAnimator.start();
			
			PropertyValuesHolder tvThree_Y = PropertyValuesHolder.ofFloat(hangoutTvThree.TRANSLATION_Y, -40.0f);
			PropertyValuesHolder tvThree_X = PropertyValuesHolder.ofFloat(hangoutTvThree.TRANSLATION_X, 0);
			waveThreeAnimator = ObjectAnimator.ofPropertyValuesHolder(hangoutTvThree, tvThree_X, tvThree_Y);
			waveThreeAnimator.setRepeatCount(ValueAnimator.INFINITE);
			waveThreeAnimator.setRepeatMode(ValueAnimator.REVERSE);
			waveThreeAnimator.setDuration(300);
			waveThreeAnimator.setStartDelay(200);
			waveThreeAnimator.start();
		}
	}
	
	public void stopAnimator(){
		waveAnimation.setVisibility(View.GONE);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (waveOneAnimator != null && waveTwoAnimator != null && waveThreeAnimator != null) {
	
			waveOneAnimator.cancel();
			waveOneAnimator.removeAllListeners();
	
			waveTwoAnimator.cancel();
			waveTwoAnimator.removeAllListeners();
		
			waveThreeAnimator.cancel();
			waveThreeAnimator.removeAllListeners();
		}
	}
}
