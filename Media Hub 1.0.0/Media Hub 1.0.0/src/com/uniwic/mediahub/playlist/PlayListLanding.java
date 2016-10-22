package com.uniwic.mediahub.playlist;


import java.util.ArrayList;
import java.util.List;

import services.Helper;
import settergetter.Medias;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.ActionMode;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.uniwic.mediahub.InternetCheck;
import com.uniwic.mediahub.MainPagerActivity;
import com.uniwic.mediahub.R;

public class PlayListLanding extends SherlockFragment {
	Helper helper;
	List<Medias> listPlayList;
	List<Medias> listCatPlayList;
	
	ChannelAdapter channelAdapter;
	String imageUrl;
	ActionMode mode;
	
	int selectedPos = -1;
	DisplayImageOptions options;
	GridView playlist_grid;
	Spinner playlist_Category;
	
	private String mContent = "???";
	private static final String KEY_TAB_NUM = "key.tab.num";
	
	int selectedCategory = 0;
	int positionSelected = -1;
	ListView channel_cat_view;
	TextView textView1;
	
	PlaylistTabCategoryAdapter playlistTabCategoryAdapter;
	InternetCheck check;
	
	public static PlayListLanding newInstance(String text) {
        PlayListLanding fragment = new PlayListLanding();
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
		View view = inflater.inflate(R.layout.activity_playlist_landing,container,false);
		
		AdView adView = (AdView) view.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
		
		if(view.findViewById(R.id.mainLayoutPlaylist).getTag().equals("normal")){
			fillPhoneLayouts(view);
		}else{
			fillTabLayouts(view);
		}
		
        return view;
	}

	private void initControls() {
		helper = new Helper(getActivity().getApplicationContext());
		check = new InternetCheck(getActivity().getApplicationContext());
		listPlayList = new ArrayList<Medias>();
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
		}catch(Exception e){
		}
	}
	
	private void fillTabLayouts(View view) {
		playlist_grid = (GridView) view.findViewById(R.id.gridViewPlaylist);
		channel_cat_view = (ListView) view.findViewById(R.id.listViewCat);
		textView1 = (TextView) view.findViewById(R.id.textView1);
		fillCategoryTabAdapter();
	}

	private void fillPhoneLayouts(View view) {
		playlist_grid = (GridView) view.findViewById(R.id.gridViewPlaylist);
		playlist_Category = (Spinner) view.findViewById(R.id.spinner1);
		fillCategoryPhoneAdapter();
	}
	
	
	
										/**
										 * 
										 * 
										 * 
										 * 
										 * 
										 * 
										 * PHONE 
										 * 
										 * 
										 * 
										 * 
										 * 
										 * **/
	
	
	
	
	//Fill Playlist category Spinner
	
	private void fillCategoryPhoneAdapter() {
		try {
			listCatPlayList = new ArrayList<Medias>();
			listCatPlayList = helper.getPlaylistCategory();
			if(listCatPlayList.size() > 0) {
			
				ArrayAdapter adapter = new ArrayAdapter(getActivity().getBaseContext(),android.R.layout.simple_list_item_1,listCatPlayList);
				playlist_Category.setAdapter(adapter);	
				playlist_Category.setOnItemSelectedListener(new OnItemSelectedListener() {
		        	 public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		        		 listPlayList = new ArrayList<Medias>();
		        		 selectedCategory = listCatPlayList.get(pos).getId();
		        		 Log.d("selectedCategory***",""+selectedCategory);
		        		 listPlayList = helper.getPlayList(selectedCategory);
		        		 Log.d("listPlayList***",""+listPlayList.size());
		        		 if(listPlayList.size() > 0 ) {
		        			 fillGridAdapter();
		        		 }else{
		        			 listPlayList.clear();
		        			 fillGridAdapter();
		        		 }
		        	 }
		        	 public void onNothingSelected(AdapterView<?> arg0) {
		        	 }

		         });
			}
		}catch(Exception e){
		}
	}
	
											/**
											 * 
											 * 
											 * 
											 * 
											 * 
											 * 
											 * TABLET
											 * 
											 * 
											 * 
											 * 
											 * 
											 * **/
	
	//Fill Main Channel Category ListView for TABLETS
	private void fillCategoryTabAdapter() {
		try {
			listCatPlayList = new ArrayList<Medias>();
			listCatPlayList = helper.getPlaylistCategory();
			if(listCatPlayList.size() > 0) {
				playlistTabCategoryAdapter = new PlaylistTabCategoryAdapter(getActivity().getBaseContext());
				channel_cat_view.setAdapter(playlistTabCategoryAdapter);
				selectedCategory = listCatPlayList.get(0).getId();
				textView1.setText(listCatPlayList.get(0).getName());
				listPlayList = helper.getPlayList(selectedCategory);
				if(listPlayList.size() > 0) {
					fillGridAdapter();
				}
			}
		}catch(Exception e){
		}
	}
	
	//Channel Category List Refresh for TABLETS
	
	void refreshCategory() {
		try{
			playlistTabCategoryAdapter = new PlaylistTabCategoryAdapter(getActivity().getBaseContext());
			channel_cat_view.setAdapter(playlistTabCategoryAdapter);
			playlistTabCategoryAdapter.notifyDataSetChanged();
			playlistTabCategoryAdapter.setNotifyOnChange(true);
		}catch(Exception e){
		}
	}
		
		
	class ViewHolder{
		TextView genreTitle,channeTitle;
		ImageView genreImage,channelImage;
		View view_indicator;
		LinearLayout frame;
	}
	
	
	//Adapter for the Listview Tablet category
	
	class PlaylistTabCategoryAdapter extends ArrayAdapter{
		Context context;
		ImageLoader imageLoader;
		
		public PlaylistTabCategoryAdapter(Context context) {
			super(context, R.layout.item_category_grid,listCatPlayList);
			this.context=context;
			imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if(convertView == null) {
				
				holder=new ViewHolder();
				convertView= (LinearLayout)LayoutInflater.from(context).inflate(R.layout.item_category_grid,null);
				holder.frame=(LinearLayout)convertView.findViewById(R.id.frame);
				holder.channelImage=(ImageView)convertView.findViewById(R.id.imageView1);
				holder.view_indicator=(View)convertView.findViewById(R.id.view_indicator);
				convertView.setTag(holder);
			
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			try {
		
				imageLoader.displayImage("assets://"+listCatPlayList.get(position).getDrawable()+MainPagerActivity.image4Theme+".jpg", holder.channelImage, options,new SimpleImageLoadingListener(){
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
							Animation anim = AnimationUtils.loadAnimation(getActivity().getBaseContext(),R.anim.fade_in);
							holder.channelImage.setAnimation(anim);
					        anim.start();
					        super.onLoadingComplete(imageUri, view, loadedImage);
					}
				});
			}catch(Exception e){
			}
			
			channel_cat_view.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,int pos, long arg3) {
					try {
					
						listPlayList = new ArrayList<Medias>();
						textView1.setText(listCatPlayList.get(pos).getName());
						selectedCategory = listCatPlayList.get(pos).getId();
						listPlayList = helper.getPlayList(selectedCategory);
							
						if(listPlayList.size() > 0 ) {
							fillGridAdapter();
								
							Medias media = listCatPlayList.get(pos);
							listCatPlayList.add(0,media);
							listCatPlayList.remove(pos+1);
							refreshCategory();
						}
						
					}catch(Exception e){
					}
				}
			});
			
			return convertView;
		}
	}
	
	
										/***
										 * 
										 * 
										 * 
										 * 
										 * 
										 * 
										 * 
										 * COMMAN GRIDVIEW
										 * 
										 * 
										 * 
										 * 
										 * 
										 * **/
	
	//Fill Main Channel Grids
	private void fillGridAdapter() {
		channelAdapter=new ChannelAdapter(getActivity().getBaseContext());
		playlist_grid.setAdapter(channelAdapter);
	}
	
		
	void refreshMainChannel() {
		channelAdapter.notifyDataSetChanged();
		channelAdapter.setNotifyOnChange(true);
	}
		
	class ChannelAdapter extends ArrayAdapter{
		Context context;
		ImageLoader imageLoader;
		public ChannelAdapter(Context context) {
			super(context, R.layout.item_playlistmain_grid,listPlayList);
			this.context=context;
			imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if(convertView == null) {
				holder=new ViewHolder();
				convertView= (LinearLayout)LayoutInflater.from(context).inflate(R.layout.item_playlistmain_grid,null);
				holder.channeTitle=(TextView)convertView.findViewById(R.id.textView1);
				holder.channelImage=(ImageView)convertView.findViewById(R.id.imageView1);
				
				convertView.setTag(holder);
				
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.channeTitle.setText(listPlayList.get(position).getName());
			
			try {
				imageLoader.displayImage("assets://"+listPlayList.get(position).getDrawable().trim()+".jpg", holder.channelImage, options,new SimpleImageLoadingListener(){
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
							Animation anim = AnimationUtils.loadAnimation(getActivity().getBaseContext(),R.anim.fade_in);
							holder.channelImage.setAnimation(anim);
					        anim.start();
						super.onLoadingComplete(imageUri, view, loadedImage);
					}
				});
			}catch(Exception e){
			}
			
			playlist_grid.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,int pos, long arg3) {
					try {
						if(check.checkNetworkStatus()) {
							String IMAGEPATH = listPlayList.get(pos).getDrawable().trim()+".jpg";
							Intent intent=new Intent(getActivity().getBaseContext(),PlayListResult.class);
							Bundle bundle=new Bundle();
							bundle.putString("CHANNEL",listPlayList.get(pos).getChannelId());
							bundle.putString("CHANNELNAME",listPlayList.get(pos).getName());
							bundle.putString("IMAGEPATH", IMAGEPATH);
							intent.putExtras(bundle);
							startActivity(intent);
						}else{
							Toast.makeText(context, "Please check the internet conectivity", 20).show();
						}
					}catch(Exception e){
					}
				}
			});
			
			return convertView;
		}
	}
}