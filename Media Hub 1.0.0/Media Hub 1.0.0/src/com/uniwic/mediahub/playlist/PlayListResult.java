package com.uniwic.mediahub.playlist;

import java.util.ArrayList;
import java.util.List;

import services.Helper;
import settergetter.Category;
import ws.munday.slidingmenu.SlidingMenuActivity;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.uniwic.mediahub.InternetCheck;
import com.uniwic.mediahub.R;
import com.uniwic.mediahub.youtube.GetYoutubeData;
import com.uniwic.mediahub.youtube.TubePlayerActivity;

public class PlayListResult extends SlidingMenuActivity {
	
	ImageView imageView;
	
	ListView listView_genreitems;
	PullToRefreshListView pullToRefreshListView;
	
	PullToRefreshGridView pullToRefreshGridView;
	GridView gridViewChannels;
	
	ArrayList<String> genreItemList,channelList;
	GenreAdapter genreAdapter;
	ChannelAdapter channelAdapter;
	
	GetYoutubeData getYoutubeData;
	List<Category> categoryList;
	List<Category> categoryListFinal;
	List<Category> listMedia;
	List<Category> listMediaFinal;
	InternetCheck check;
	DisplayImageOptions options;
	
	String incomingChannel="",incomingCHANNELNAME="",incomingIMAGEPATH="";
	
	TextView textViewChannel;
	
	Helper helper;
	String imageUrl;
	Bitmap myLogo;
	
	String playlistCategoryUrl = "";
	
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
		
		setLayoutIds(R.layout.ws_munday_slidingmenu_test_menu, R.layout.ws_munday_slidingmenu_test_content);
		setAnimationDuration(600);
		setAnimationType(MENU_TYPE_SLIDEOVER);
		super.onCreate(savedInstanceState);
		
		AdView adView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
		
		initControls();
		initControlsForMenuItems();
		initData();
		fillGenreListAdapter();
		
	}
	
	private void initControls() {
		try {
			helper = new Helper(getApplicationContext());
			StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			
			textViewChannel = (TextView) findViewById(R.id.textViewChannel);
			options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.card_background)
				.showImageForEmptyUri(R.drawable.card_background)
				.showImageOnFail(R.drawable.card_background)
				.cacheInMemory()
				.cacheOnDisc()
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();
			
			categoryListFinal = new ArrayList<Category>();
			listMediaFinal = new ArrayList<Category>();
			
			getYoutubeData = new GetYoutubeData();
			GetYoutubeData.nextPageCategory = null;
			
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
				incomingChannel = bundle.getString("CHANNEL");
				incomingCHANNELNAME = bundle.getString("CHANNELNAME");
				incomingIMAGEPATH = bundle.getString("IMAGEPATH");
			}else{
				incomingChannel = "MotherGooseClub";
			}
			
			textViewChannel.setText(incomingCHANNELNAME.toUpperCase());
			if(check.checkNetworkStatus()){
				playlistCategoryUrl = "https://gdata.youtube.com/feeds/api/users/"+incomingChannel+"/playlists?&max-results=50&v=2";
				CategoryAsync categoryAsync=new CategoryAsync();
				categoryAsync.execute(playlistCategoryUrl);
			}else{
				Toast.makeText(getApplicationContext(), "Please connect your internet and restart your app again",20).show();
			}
		}catch(Exception e){
			
		}
	}

	@SuppressWarnings("unchecked")
	private void initControlsForMenuItems() {
		
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.listView_genreitems);
		pullToRefreshGridView = (PullToRefreshGridView) findViewById(R.id.gridViewChannel);
		
		gridViewChannels = pullToRefreshGridView.getRefreshableView();
		listView_genreitems = pullToRefreshListView.getRefreshableView();
	
		imageView=(ImageView) findViewById(R.id.imageView1);
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				toggleMenu();
			}
		});
		
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				pullToRefreshListView.onRefreshComplete();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				
				if(check.checkNetworkStatus()){
					CategoryAsync categoryAsync=new CategoryAsync();
					categoryAsync.execute(GetYoutubeData.nextPageCategory);
				}else{
					Toast.makeText(getApplicationContext(), "Please connect your internet and restart your app again",20).show();
				}
				
			}
		});
	}

	
	private void fillGenreListAdapter() {
		genreAdapter=new GenreAdapter(getApplicationContext());
		listView_genreitems.setAdapter(genreAdapter);
		listView_genreitems.startLayoutAnimation();
	}
	
	
	private void fillGridAdapter() {
		channelAdapter = new ChannelAdapter(getApplicationContext());
		gridViewChannels.setAdapter(channelAdapter);
		gridViewChannels.startLayoutAnimation();
	}
	
	class GenreAdapter extends ArrayAdapter{
		Context context;
		ImageLoader imageLoader;
		public GenreAdapter(Context context) {
			super(context, R.layout.item_genre_playlist_list,categoryListFinal);
			this.context=context;
			imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if(convertView == null){
				holder=new ViewHolder();
				convertView= (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.item_genre_playlist_list,null);
				holder.channeTitle=(TextView)convertView.findViewById(R.id.textView1);
				holder.channelImage=(ImageView)convertView.findViewById(R.id.imageView1);
				holder.video_count=(TextView)convertView.findViewById(R.id.textViewCount);
				convertView.setTag(holder);
				
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.channeTitle.setText(categoryListFinal.get(position).getCategoryName());
			holder.video_count.setText("Toatal Video : "+categoryListFinal.get(position).getCount());
			
			try {
				imageLoader.displayImage("assets://"+incomingIMAGEPATH, holder.channelImage, options,new SimpleImageLoadingListener(){
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
							Animation anim = AnimationUtils.loadAnimation(PlayListResult.this,R.anim.fade_in);
							holder.channelImage.setAnimation(anim);
					        anim.start();
						super.onLoadingComplete(imageUri, view, loadedImage);
					}
				});
			}catch(Exception e){
			}
			
			listView_genreitems.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,int pos, long arg3) {
					try{
						
						incomingCHANNELNAME = categoryListFinal.get(pos-1).getCategoryName();
						textViewChannel.setText(incomingCHANNELNAME.toUpperCase());
						
						if(listMediaFinal.size() > 0){
							listMediaFinal.clear();
							fillGridAdapter();
							toggleMenu();
						}
						
						MediayAsync mediayAsync=new MediayAsync();
						mediayAsync.execute(categoryListFinal.get(pos-1).getMediaUrl());
					}catch(Exception e){
					}
				}
			});
			
			return convertView;
		}
	}
	
	class ChannelAdapter extends ArrayAdapter {
		Context context;
		ImageLoader imageLoader;
	
		public ChannelAdapter(Context context) {
			super(context, R.layout.item_channel_grid,listMediaFinal);
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
		
				String path ="http://img.youtube.com/vi/"+listMediaFinal.get(position).getMediaUrl()+"/0.jpg";
				
				holder.channeTitle.setText(listMediaFinal.get(position).getCategoryName());
				imageLoader.displayImage(path, holder.channelImage, options,new SimpleImageLoadingListener(){
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
							Animation anim = AnimationUtils.loadAnimation(PlayListResult.this,R.anim.fade_in);
							holder.channelImage.setAnimation(anim);
					        anim.start();
						super.onLoadingComplete(imageUri, view, loadedImage);
					}
				});
			
				gridViewChannels.setOnItemClickListener(new OnItemClickListener() {
	
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
						String vids[]=new String[1];
						vids[0] = listMediaFinal.get(arg2).getMediaUrl().trim();
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
			try{
				categoryList = getYoutubeData.GetPlaylistCategory(params[0]);
				categoryListFinal.addAll(categoryList);
				if(listMediaFinal.size() == 0) {
					listMedia = getYoutubeData.GetPlaylistData(categoryListFinal.get(0).getMediaUrl());
					listMediaFinal.addAll(listMedia);
				}
			}catch(Exception e){
			}
			
			return categoryListFinal;
		}
		
		@Override
		protected void onPostExecute(List<Category> result) {
			if(result.size() > 0){
				stopAnimator();
				genreAdapter.notifyDataSetChanged();
				fillGridAdapter();
			}else{
				setError();
			}
			
			pullToRefreshListView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}
	
	class MediayAsync extends AsyncTask<String,Void,List<Category>>{
		
		@Override
		protected void onPreExecute() {
			waveAnimation();
			super.onPreExecute();
		}

		@Override
		protected List<Category> doInBackground(String... params) {
			listMedia = getYoutubeData.GetPlaylistData(params[0]);
			listMediaFinal.addAll(listMedia);
			
			return listMediaFinal;
		}
		
		@Override
		protected void onPostExecute(List<Category> result) {
			if(result.size() > 0){
				stopAnimator();
				channelAdapter.notifyDataSetChanged();
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
