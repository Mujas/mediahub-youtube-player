package com.uniwic.mediahub;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import services.Helper;
import settergetter.Category;
import settergetter.Medias;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.actionbarsherlock.widget.ShareActionProvider.OnShareTargetSelectedListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.uniwic.mediahub.theme.Theme;
import com.uniwic.mediahub.youtube.GetYoutubeData;
import com.uniwic.mediahub.youtube.TubePlayerActivity;

public class SearchActivity extends SherlockActivity {
	ImageView imageView;
	
	GridView gridViewChannels;
	PullToRefreshGridView pullToRefreshGridView;
	
	ChannelAdapter channelAdapter;
	GetYoutubeData getYoutubeData;

	List<Category> searchList;
	List<Category> searchListFinal;
	
	InternetCheck check;
	DisplayImageOptions options;
	
	String incomingChannel="",incomingCHANNELNAME="";
	int incomingselectedCategory;
	TextView textViewChannel;
	List<Medias> listChannels;
	Helper helper;
	String imageUrl;
	
	Bitmap myLogo;
	
	RelativeLayout topPane;
	View include;
	
	Dialog dialog;
	RadioButton greenRadio,blackRadio;
	
	//OverFlow
	private Menu mainMenu;
	private SubMenu subMenu;
	
	//Hangout Anim
	private TextView hangoutTvOne;
	private TextView hangoutTvTwo;
	private TextView hangoutTvThree;
	private ObjectAnimator waveOneAnimator;
	private ObjectAnimator waveTwoAnimator;
	private ObjectAnimator waveThreeAnimator;
		
	String myAppURL = "http://play.google.com/store/apps/details?id=com.uniwic.mediahub";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Theme.onActivityCreateSetTheme(this);
		
		setContentView(R.layout.ws_munday_slidingmenu_test_content);
		super.onCreate(savedInstanceState);
		
		AdView adView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
		
		initControls();
		initData();
	}
	
	private void initControls() {
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("");
		
		helper = new Helper(getApplicationContext());
		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		

		pullToRefreshGridView = (PullToRefreshGridView) findViewById(R.id.gridViewChannel);
		gridViewChannels = pullToRefreshGridView.getRefreshableView();
		
		topPane = (RelativeLayout) findViewById(R.id.topPane);
		topPane.setVisibility(View.GONE);
		
		textViewChannel = (TextView) findViewById(R.id.textViewChannel);
		options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.card_background)
			.showImageForEmptyUri(R.drawable.card_background)
			.showImageOnFail(R.drawable.card_background)
			.cacheInMemory()
			.cacheOnDisc()
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();
		
		getYoutubeData = new GetYoutubeData();
		searchListFinal = new ArrayList<Category>();
		check = new InternetCheck(getApplicationContext());
		
		GetYoutubeData.nextPageSearch = null;
		
		//Hangout Animation
		hangoutTvOne = (TextView) findViewById(R.id.hangoutTvOne);
		hangoutTvTwo = (TextView) findViewById(R.id.hangoutTvTwo);
		hangoutTvThree = (TextView) findViewById(R.id.hangoutTvThree);
		
	}
	
	private void initData() {
		try {
						
			Bundle bundle = getIntent().getExtras();
			if(bundle != null){
				incomingChannel = bundle.getString("CHANNEL");
			}else{
				incomingChannel = "MotherGooseClub";
			}
			
			
			textViewChannel.setText(incomingChannel.toUpperCase());
			if(check.checkNetworkStatus()){
				
				incomingChannel = incomingChannel.replace(" ", "+");
				incomingChannel = "https://gdata.youtube.com/feeds/api/videos?q="
						+ incomingChannel+"&orderby=published&start-index=1&max-results=50&v=2";
				
				SearchAsync searchAsync = new SearchAsync();
				searchAsync.execute(incomingChannel);
			}else{
				Toast.makeText(getApplicationContext(), "Please connect your internet and restart your app again",20).show();
			}
		}catch(Exception e){
			
		}
		
		
		pullToRefreshGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				Toast.makeText(getApplicationContext(), "Pulling down",20).show();
				pullToRefreshGridView.onRefreshComplete();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
				Toast.makeText(getApplicationContext(), "Pulling up",20).show();
				if(check.checkNetworkStatus()){
					
					SearchAsync searchAsync = new SearchAsync();
					searchAsync.execute(GetYoutubeData.nextPageSearch);
					
				}else{
					Toast.makeText(getApplicationContext(), "Please connect your internet and restart your app again",20).show();
				}
			}
		});
		
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
			super(context, R.layout.item_channel_grid,searchList);
			this.context=context;
			imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if(convertView == null){
				holder=new ViewHolder();
				convertView= (LinearLayout)LayoutInflater.from(context).inflate(R.layout.item_channel_grid,null);
				holder.channeTitle=(TextView)convertView.findViewById(R.id.textView1);
				holder.channelImage=(ImageView)convertView.findViewById(R.id.imageView1);
				
				convertView.setTag(holder);
				
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			String vid = getYoutubeVideoId(searchList.get(position).getMediaUrl().trim());
			holder.channeTitle.setText(searchList.get(position).getCategoryName());
			String path ="http://img.youtube.com/vi/"+vid+"/0.jpg";
			
			try {
				imageLoader.displayImage(path, holder.channelImage, options,new SimpleImageLoadingListener(){
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
							Animation anim = AnimationUtils.loadAnimation(context,R.anim.fade_in);
							holder.channelImage.setAnimation(anim);
					        anim.start();
						super.onLoadingComplete(imageUri, view, loadedImage);
					}
				});
			}catch(Exception e){
				//holder.channelImage.setBackgroundResource(R.drawable.sony);
			}
		
			gridViewChannels.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
					String vids[]=new String[1];
					vids[0] = getYoutubeVideoId(searchList.get(arg2).getMediaUrl().trim());
					Intent intent=new Intent(getApplicationContext(),TubePlayerActivity.class);
					Bundle bundle=new Bundle();
					bundle.putStringArray("vids",vids);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
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
	
	
	class SearchAsync extends AsyncTask<String,Void,List<Category>>{
		
		@Override
		protected void onPreExecute() {
			waveAnimation();
			super.onPreExecute();
		}

		@Override
		protected List<Category> doInBackground(String... params) {
			searchList = getYoutubeData.GetSearchData(params[0]);
			searchListFinal.addAll(searchList);
			return searchListFinal;
		}
		
		@Override
		protected void onPostExecute(List<Category> result) {
			if(result.size() > 0){
				stopAnimator();
				fillGridAdapter();
				channelAdapter.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), "Added some more videos",20).show();
			}else{
				setError();
			}
			
			pullToRefreshGridView.onRefreshComplete();
			
			super.onPostExecute(result);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflator = getSupportMenuInflater();
		inflator.inflate(R.menu.home_menu, menu);
		
		try {
			
			//ShareActionProvider
			
			ShareActionProvider mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.menu_item_share).getActionProvider();
			mShareActionProvider.setShareHistoryFileName(null);
			mShareActionProvider.setShareIntent(getDefaultShareIntent());
			OnShareTargetSelectedListener listener = new OnShareTargetSelectedListener() {
				public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
					startActivity(intent);
			            return true;
			        }
				};
			mShareActionProvider.setShareHistoryFileName(null);
			mShareActionProvider.setOnShareTargetSelectedListener(listener);
			
			//SearchView
		
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
	        if (null != searchView ){
	            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	            searchView.setIconifiedByDefault(true);
	        }
	        
	        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener(){
	            public boolean onQueryTextChange(String newText) {
	                //Toast.makeText(getApplicationContext(), "HI first"+newText, 20).show();
	                return true;
	            }
	     
	            public boolean onQueryTextSubmit(String query) {
	            	try {
		            	if(searchList.size() > 0){
			            	searchList.clear();
				    	}
						
		            	SearchAsync searchAsync = new SearchAsync();
						searchAsync.execute(query);
		            }catch(Exception e){
	            	}
	                return true;
	            }
	        };
	        
	        searchView.setSubmitButtonEnabled(true);
	        searchView.setOnQueryTextListener(queryTextListener);
	        
	        subMenu = menu.addSubMenu("");
		      
	        subMenu.add(0, 10, Menu.NONE, " Rate Us on PlayStore").setIcon(R.drawable.ic_menu_about_rate_star);
		    subMenu.add(0, 15, Menu.NONE, " About").setIcon(R.drawable.ic_menu_about);
		 
		    MenuItem subMenuItem = subMenu.getItem();
		    subMenuItem.setIcon(R.drawable.ic_menu_moreoverflow_normal_holo_light);
		    subMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}catch(Exception e){
		}
		return super.onCreateOptionsMenu(menu);
	}
		
	public Intent getDefaultShareIntent() {
		Intent sendIntent = new Intent();
		try {
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, myAppURL);
			sendIntent.setType("text/plain");
		}catch(Exception e){
		}

	    return sendIntent;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		String appName = "com.uniwic.mediahub";
		switch (item.getItemId()) {
		case 10:
			try {
			    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+appName)));
			} catch (android.content.ActivityNotFoundException anfe) {
			    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+appName)));
			}
			break;
			
		case 15:
			try {
			    startActivity(new Intent(getApplicationContext(),About.class));
			} catch (android.content.ActivityNotFoundException anfe) {
			}
			break;
	
		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void setError() {
		Toast.makeText(getApplicationContext(), "Sorry Videos are not available ",20).show();
	}
	
	public void waveAnimation() {
		hangoutTvOne.setVisibility(View.VISIBLE);
		hangoutTvTwo.setVisibility(View.VISIBLE);
		hangoutTvThree.setVisibility(View.VISIBLE); 
		
		PropertyValuesHolder tvOne_Y = PropertyValuesHolder.ofFloat(hangoutTvOne.TRANSLATION_Y, -40.0f);
		PropertyValuesHolder tvOne_X = PropertyValuesHolder.ofFloat(hangoutTvOne.TRANSLATION_X, 0);
		waveOneAnimator = ObjectAnimator.ofPropertyValuesHolder(hangoutTvOne, tvOne_X, tvOne_Y);
		waveOneAnimator.setRepeatCount(-1);
		waveOneAnimator.setRepeatMode(ValueAnimator.REVERSE);
		waveOneAnimator.setDuration(300);
		waveOneAnimator.start();

		PropertyValuesHolder tvTwo_Y = PropertyValuesHolder.ofFloat(hangoutTvTwo.TRANSLATION_Y, -40.0f);
		PropertyValuesHolder tvTwo_X = PropertyValuesHolder.ofFloat(hangoutTvTwo.TRANSLATION_X, 0);
		waveTwoAnimator = ObjectAnimator.ofPropertyValuesHolder(hangoutTvTwo, tvTwo_X, tvTwo_Y);
		waveTwoAnimator.setRepeatCount(-1);
		waveTwoAnimator.setRepeatMode(ValueAnimator.REVERSE);
		waveTwoAnimator.setDuration(300);
		waveTwoAnimator.setStartDelay(100);
		waveTwoAnimator.start();

		PropertyValuesHolder tvThree_Y = PropertyValuesHolder.ofFloat(hangoutTvThree.TRANSLATION_Y, -40.0f);
		PropertyValuesHolder tvThree_X = PropertyValuesHolder.ofFloat(hangoutTvThree.TRANSLATION_X, 0);
		waveThreeAnimator = ObjectAnimator.ofPropertyValuesHolder(hangoutTvThree, tvThree_X, tvThree_Y);
		waveThreeAnimator.setRepeatCount(-1);
		waveThreeAnimator.setRepeatMode(ValueAnimator.REVERSE);
		waveThreeAnimator.setDuration(300);
		waveThreeAnimator.setStartDelay(200);
		waveThreeAnimator.start();
	}
	
	public void stopAnimator(){
		if (waveOneAnimator != null && waveTwoAnimator != null && waveThreeAnimator != null) {
			waveOneAnimator.cancel();
			waveOneAnimator.removeAllListeners();

			waveTwoAnimator.cancel();
			waveTwoAnimator.removeAllListeners();

			waveThreeAnimator.cancel();
			waveThreeAnimator.removeAllListeners();
			hangoutTvOne.setVisibility(View.GONE);
			hangoutTvTwo.setVisibility(View.GONE);
			hangoutTvThree.setVisibility(View.GONE);
		}
	}
}
