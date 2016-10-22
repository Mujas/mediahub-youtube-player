package com.uniwic.mediahub.mychannel;


import java.util.List;

import settergetter.Category;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.ActionMode;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.uniwic.mediahub.InternetCheck;
import com.uniwic.mediahub.R;
import com.uniwic.mediahub.youtube.GetYoutubeData;

public class MyChannelLanding extends SherlockFragment {
	
	
	String imageUrl;
	ActionMode mode;
	
	int selectedPos = -1;
	DisplayImageOptions options;
	GridView playlist_grid;
		
	private String mContent = "???";
	private static final String KEY_TAB_NUM = "key.tab.num";
	
	String playlistCategoryUrl;

	GenreAdapter genreAdapter;
	GetYoutubeData getYoutubeData;
	InternetCheck check;
	List<Category> categoryList;
	String incomingIMAGEPATH="";
	TextView internet;
	View view;
	ProgressBar prgress;
	
	public static MyChannelLanding newInstance(String text) {
        MyChannelLanding fragment = new MyChannelLanding();
        Bundle args = new Bundle();
        args.putString(KEY_TAB_NUM, text);
        fragment.setArguments(null);
        fragment.setArguments(args);
        return fragment;
    }
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContent =  getArguments() != null ? getArguments().getString(KEY_TAB_NUM) : "???";
		initControls();
		initData();
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_mychannel,container,false);
		
		AdView adView = (AdView) view.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
		
		playlist_grid = (GridView) view.findViewById(R.id.gridViewMyChannel);
		prgress = (ProgressBar) view.findViewById(R.id.emptyElement);
		internet = (TextView) view.findViewById(R.id.internet);
		if(check.checkNetworkStatus()){
			
			playlistCategoryUrl = "https://gdata.youtube.com/feeds/api/users/MyDeadManAlive/playlists?&max-results=50&v=2";
			CategoryAsync categoryAsync=new CategoryAsync();
			categoryAsync.execute(playlistCategoryUrl);
			
		}else{
			internet.setVisibility(View.VISIBLE);
		}
		return view;
	}

	private void initControls() {
		
	}

	private void initData() {
		try {
			options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.card_background)
			.showImageForEmptyUri(R.drawable.card_background)
			.showImageOnFail(R.drawable.card_background)
			.cacheInMemory()
			.cacheOnDisc()
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();
			
			getYoutubeData = new GetYoutubeData();
			check = new InternetCheck(getActivity().getBaseContext());
		}catch(Exception e){
		}
	}
	
	
	class CategoryAsync extends AsyncTask<String,Void,List<Category>>{
		
		@Override
		protected void onPreExecute() {
			prgress.setVisibility(View.VISIBLE);
			playlist_grid.setEmptyView(prgress);
			super.onPreExecute();
		}

		@Override
		protected List<Category> doInBackground(String... params) {
			categoryList = getYoutubeData.GetPlaylistCategory(params[0]);
			return categoryList;
		}
		
		@Override
		protected void onPostExecute(List<Category> result) {
			if(result.size() > 0){
				prgress.setVisibility(View.VISIBLE);
				if(categoryList.size() > 0)
				fillGenreListAdapter();
			}else{
				prgress.setVisibility(View.VISIBLE);
			}
			super.onPostExecute(result);
		}
	}
	
		
	class ViewHolder{
		TextView genreTitle,channeTitle,video_count;
		ImageView genreImage,channelImage;
		View view_indicator;
		LinearLayout frame;
	}
	
		
	private void fillGenreListAdapter() {
		genreAdapter=new GenreAdapter(getActivity().getBaseContext());
		playlist_grid.setAdapter(genreAdapter);
	}
		
	class GenreAdapter extends ArrayAdapter{
		Context context;
		ImageLoader imageLoader;
		public GenreAdapter(Context context) {
			super(context, R.layout.item_genre_my_list,categoryList);
			this.context=context;
			imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if(convertView == null){
				holder=new ViewHolder();
				convertView= (LinearLayout)LayoutInflater.from(context).inflate(R.layout.item_genre_my_list,null);
				holder.channeTitle=(TextView)convertView.findViewById(R.id.textView1);
				holder.video_count = (TextView)convertView.findViewById(R.id.textViewCount);
				convertView.setTag(holder);
				
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.channeTitle.setText(categoryList.get(position).getCategoryName());
			holder.video_count.setText(""+categoryList.get(position).getCount());
			
			playlist_grid.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,int pos, long arg3) {
					try{
						
						Intent intent=new Intent(getActivity().getBaseContext(),MyChannelListResult.class);
						Bundle bundle=new Bundle();
						bundle.putString("Name",categoryList.get(pos).getCategoryName());
						bundle.putString("MediaUrl", categoryList.get(pos).getMediaUrl());
						intent.putExtras(bundle);
						startActivity(intent);
						
					}catch(Exception e){
					}
				}
			});
			
			return convertView;
		}
	}
}