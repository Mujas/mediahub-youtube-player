package com.uniwic.mediahub;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import services.Helper;
import settergetter.Category;
import settergetter.Medias;
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
import com.uniwic.mediahub.youtube.GetYoutubeData;
import com.uniwic.mediahub.youtube.TubePlayerActivity;

public class LandingResultActivity extends SlidingMenuActivity {
	
	ImageView imageView;
	PullToRefreshListView pullToRefreshListView;
	ListView listView_genreitems;
	PullToRefreshGridView pullToRefreshGridView;
	GridView gridViewChannels;
	
	ArrayList<String> genreItemList,channelList;
	GenreAdapter genreAdapter;
	ChannelAdapter channelAdapter;
	
	GetYoutubeData getYoutubeData;
	List<Category> categoryList;
	List<Category> categoryListFinal;
	
	InternetCheck check;
	DisplayImageOptions options;
	
	String incomingChannel="",incomingCHANNELNAME="";
	int incomingselectedCategory;
	TextView textViewChannel;
	List<Medias> listChannels;
	Helper helper;
	String imageUrl;
	Bitmap myLogo;
	
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
		fillGridAdapter();
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
			getYoutubeData = new GetYoutubeData();
			
			GetYoutubeData.nextPageUploads = null;
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
				incomingselectedCategory = bundle.getInt("selectedCategory");
			}else{
				incomingChannel = "MotherGooseClub";
			}
			
			textViewChannel.setText(incomingCHANNELNAME.toUpperCase());
			if(check.checkNetworkStatus()){
				
				incomingChannel = "https://gdata.youtube.com/feeds/api/users/"+incomingChannel+"/uploads?start-index=1&max-results=50&v=2";
				CategoryAsync categoryAsync=new CategoryAsync();
				categoryAsync.execute(incomingChannel);
				
			}else{
				Toast.makeText(getApplicationContext(), "Please connect your internet and restart your app again",20).show();
			}
		}catch(Exception e){
			
		}
	}

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
		
		pullToRefreshGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {
			

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				//Toast.makeText(getApplicationContext(), "Pulling down",20).show();
				pullToRefreshGridView.onRefreshComplete();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
				//Toast.makeText(getApplicationContext(), "Pulling up",20).show();
				if(check.checkNetworkStatus()){
					
					CategoryAsync categoryAsync=new CategoryAsync();
					categoryAsync.execute(GetYoutubeData.nextPageUploads);
					
					
				}else{
					Toast.makeText(getApplicationContext(), "Please connect your internet and restart your app again",20).show();
				}
			}
			
			
		});
	}

	private void fillGenreListAdapter() {
		listChannels = new ArrayList<Medias>();
		listChannels = helper.getMedia(incomingselectedCategory);
		genreAdapter=new GenreAdapter(getApplicationContext());
		listView_genreitems.setAdapter(genreAdapter);
		listView_genreitems.startLayoutAnimation();
	}
	
	
	private void fillGridAdapter() {
		channelAdapter = new ChannelAdapter(getApplicationContext());
		gridViewChannels.setAdapter(channelAdapter);
		//gridViewChannels.startLayoutAnimation();
		
	}
	
	class GenreAdapter extends ArrayAdapter{
		Context context;
		ImageLoader imageLoader;
		public GenreAdapter(Context context) {
			super(context, R.layout.item_genre_list,listChannels);
			this.context=context;
			imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if(convertView == null){
				holder=new ViewHolder();
				convertView= (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.item_genre_list,null);
				holder.channeTitle=(TextView)convertView.findViewById(R.id.textView1);
				holder.channelImage=(ImageView)convertView.findViewById(R.id.imageView1);
				
				convertView.setTag(holder);
				
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.channeTitle.setText(listChannels.get(position).getName());
			
			try {
				imageLoader.displayImage("assets://"+listChannels.get(position).getDrawable().trim()+".jpg", holder.channelImage, options,new SimpleImageLoadingListener(){
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
							Animation anim = AnimationUtils.loadAnimation(LandingResultActivity.this,R.anim.fade_in);
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
						if(myLogo != null)
							myLogo.recycle();
						incomingCHANNELNAME = listChannels.get(pos-1).getName();
						textViewChannel.setText(incomingCHANNELNAME.toUpperCase());
						if(categoryListFinal.size() > 0){
							categoryListFinal.clear();
							fillGridAdapter();
							toggleMenu();
						}
						
						incomingChannel = "https://gdata.youtube.com/feeds/api/users/"+listChannels.get(pos-1).getChannelId()+"/uploads?start-index=1&max-results=50&v=2";
						GetYoutubeData.nextPageUploads = null;
						
						CategoryAsync categoryAsync = new CategoryAsync();
						categoryAsync.execute(incomingChannel);
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
			super(context, R.layout.item_channel_grid,categoryListFinal);
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
				
				String vid = getYoutubeVideoId(categoryListFinal.get(position).getMediaUrl().trim());
				
				holder.channeTitle.setText(categoryListFinal.get(position).getCategoryName());
				String path ="http://img.youtube.com/vi/"+vid+"/0.jpg";
				
				imageLoader.displayImage(path, holder.channelImage, options,new SimpleImageLoadingListener(){
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
							Animation anim = AnimationUtils.loadAnimation(LandingResultActivity.this,R.anim.fade_in);
					        holder.channelImage.setAnimation(anim);
					        anim.start();
						super.onLoadingComplete(imageUri, view, loadedImage);
					}
				});
				
				gridViewChannels.setOnItemClickListener(new OnItemClickListener() {
	
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
						String vids[]=new String[1];
						vids[0] = getYoutubeVideoId(categoryListFinal.get(arg2).getMediaUrl().trim());
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
	
	public  String getYoutubeVideoId(String youtubeUrl) {

		String video_id="";
		try 
		{
			if (youtubeUrl != null && youtubeUrl.trim().length() > 0 && youtubeUrl.startsWith("http"))
			{
		 		String expression = "^.*((youtu.be"+ "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
		 		CharSequence input = youtubeUrl;
		 		Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
		 		Matcher matcher = pattern.matcher(input);
		 			if (matcher.matches())
		 				{
		 					String groupIndex1 = matcher.group(7);
		 						if(groupIndex1!=null && groupIndex1.length()==11)
		 							video_id = groupIndex1;
		 				}
			}
		}catch(Exception e){
		}
		return video_id;
	 }
	
	class ViewHolder{
		TextView genreTitle,channeTitle;
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
			categoryList = getYoutubeData.GetFeedData(params[0]);
			categoryListFinal.addAll(categoryList);
			
			return categoryListFinal;
		}
		
		@Override
		protected void onPostExecute(List<Category> result) {
			if(result.size() > 0){
				//fillGridAdapter();
				stopAnimator();
				channelAdapter.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), "Added some more videos",20).show();
			}else{
				setError();
			}
			
			pullToRefreshGridView.onRefreshComplete();
			
			
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
