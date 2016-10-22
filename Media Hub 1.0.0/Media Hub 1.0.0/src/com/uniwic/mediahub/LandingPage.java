package com.uniwic.mediahub;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import services.Helper;
import settergetter.Medias;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;



public class LandingPage extends SherlockFragment {
	
	Helper helper;
	List<Medias> listCatChannels;
	List<Medias> listChannels;
	
	//For Tabs
	ListView channel_cat_view;
	
	ChannelCategoryAdapter channelCategoryAdapter;
	
	//For Mobile
	GridView channelGridPhone;
	Spinner channelcatSpinner;
	ChannelAdapterPhone channelAdapterPhone;
	
	int selectedCategory = 0;
	String imageUrl;
	TextView textView1;
	int selectedPos = -1;

	DisplayImageOptions options;
	String lPath = null;
	Button cancelButton;
	InternetCheck check;
	
	private String mContent = "???";
	private static final String KEY_TAB_NUM = "key.tab.num";
	

		
	
	public static LandingPage newInstance(String text) {
        LandingPage fragment = new LandingPage();
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
		createDatabase();
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_landing,container, false);
		
		AdView adView = (AdView) view.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
		
		
		if(view.findViewById(R.id.mainLayout).getTag().equals("normal")){
			fillPhoneLayouts(view);
		}else{
			fillTabLayouts(view);
		}

		
        return view;
	}

	private void fillTabLayouts(View view) {
		channelGridPhone = (GridView) view.findViewById(R.id.gridViewHome); 
		channel_cat_view = (ListView) view.findViewById(R.id.listViewCat);
		textView1 = (TextView) view.findViewById(R.id.textView1);
		fillCategoryAdapter();
	}

	private void fillPhoneLayouts(View view) {
		channelGridPhone = (GridView) view.findViewById(R.id.gridViewHome); 
		channelcatSpinner = (Spinner) view.findViewById(R.id.spinnerHome);
		fillCategoryAdapterPhone();
	}

	private void initControls() {
		helper = new Helper(getActivity().getApplicationContext());
		check = new InternetCheck(getActivity().getApplicationContext());
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
	
	private void createDatabase() {
		try {
			helper.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
										/**
										 * 
										 * 
										 * TABLET
										 * 
										 * 
										 ****/
	
	//Fill Main Channel Category List for TABLETS
	private void fillCategoryAdapter() {
		try {
			listCatChannels = new ArrayList<Medias>();
			listCatChannels = helper.getCategory();
			if(listCatChannels.size() > 0) {
				channelCategoryAdapter = new ChannelCategoryAdapter(getActivity().getBaseContext());
				channel_cat_view.setAdapter(channelCategoryAdapter);
				selectedCategory = listCatChannels.get(0).getId();
				textView1.setText(listCatChannels.get(0).getName());
				listChannels = helper.getMedia(selectedCategory);
				if(listChannels.size() > 0) {
					fillGridAdapterPhone();	
				}
			}
		}catch(Exception e){
		}
	}
	
	//Channel Category List Refresh for TABLETS
	void refreshCategory() {
		try{
			channelCategoryAdapter = new ChannelCategoryAdapter(getActivity().getBaseContext());
			channel_cat_view.setAdapter(channelCategoryAdapter);
			channelCategoryAdapter.notifyDataSetChanged();
			channelCategoryAdapter.setNotifyOnChange(true);
		}catch(Exception e){
		}
	}
	
	
	class ViewHolder{
		TextView genreTitle,channeTitle;
		ImageView genreImage,channelImage;
		View view_indicator;
		LinearLayout frame;
	}
	
	class ChannelCategoryAdapter extends ArrayAdapter{
		Context context;
		ImageLoader imageLoader;
		
		public ChannelCategoryAdapter(Context context) {
			super(context, R.layout.item_category_grid,listCatChannels);
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
				
				imageLoader.displayImage("assets://"+listCatChannels.get(position).getDrawable()+MainPagerActivity.image4Theme+".jpg", holder.channelImage, options,new SimpleImageLoadingListener(){
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
					
							listChannels = new ArrayList<Medias>();
							textView1.setText(listCatChannels.get(pos).getName());
							selectedCategory = listCatChannels.get(pos).getId();
							listChannels = helper.getMedia(selectedCategory);
							
							if(listChannels.size() > 0 ) {
								fillGridAdapterPhone();
								
								Medias media = listCatChannels.get(pos);
								listCatChannels.add(0,media);
								listCatChannels.remove(pos+1);
								refreshCategory();
							}
						
					}catch(Exception e){
					}
				}
			});
			
			return convertView;
		}
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
									 * 
									 * 
									 * 
									 * ***/

	//Fill category
	private void fillCategoryAdapterPhone() {
		try {
			listCatChannels = new ArrayList<Medias>();
			listCatChannels = helper.getCategory();
			if(listCatChannels.size() > 0) {
			
				ArrayAdapter adapter = new ArrayAdapter(getActivity().getBaseContext(),android.R.layout.simple_list_item_1,listCatChannels);
				channelcatSpinner.setAdapter(adapter);	
				channelcatSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		        	 public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		        		 selectedCategory = listCatChannels.get(pos).getId();
		        		 listChannels = helper.getMedia(selectedCategory);
		        		 if(listChannels.size() > 0) {
		        				fillGridAdapterPhone();
		        		 }
		        	 }
		        	 
		        	 public void onNothingSelected(AdapterView<?> arg0) {
		        	 }
		         });
			}
		}catch(Exception e){
		}
	}
	
	
	
	
	
	
	
	
									
									/***
									 * 
									 * 
									 * 
									 * 
									 * 
									 * 
									 * COMMAN
									 * 
									 * 
									 * 
									 * 
									 * 
									 * 
									 * **/
	
	//Fill Main Channel Grids
	private void fillGridAdapterPhone() {
		channelAdapterPhone = new ChannelAdapterPhone(getActivity().getBaseContext());
		channelGridPhone.setAdapter(channelAdapterPhone);
	}
		
			
	void refreshMainChannelPhone() {
		channelAdapterPhone.notifyDataSetChanged();
		channelAdapterPhone.setNotifyOnChange(true);
	}
	
	class ChannelAdapterPhone extends ArrayAdapter{
		Context context;
		ImageLoader imageLoader;
		public ChannelAdapterPhone(Context context) {
			super(context, R.layout.item_playlistmain_grid,listChannels);
			this.context=context;
			imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if(convertView == null){
				holder=new ViewHolder();
				convertView= (LinearLayout)LayoutInflater.from(context).inflate(R.layout.item_playlistmain_grid,null);
				holder.channeTitle=(TextView)convertView.findViewById(R.id.textView1);
				holder.channelImage=(ImageView)convertView.findViewById(R.id.imageView1);
				
				convertView.setTag(holder);
				
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.channeTitle.setText(listChannels.get(position).getName());
			//holder.channeTitle.setTypeface(font);
			
			try {
				imageLoader.displayImage("assets://"+listChannels.get(position).getDrawable().trim()+".jpg", holder.channelImage, options,new SimpleImageLoadingListener(){
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
				//holder.channelImage.setBackgroundResource(R.drawable.sony);
			}
			
			channelGridPhone.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int pos, long arg3) {
					selectedPos = pos;
					//refreshMainChannel();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					
				}
			});
			
			
			channelGridPhone.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,int pos, long arg3) {
					try {
						if(check.checkNetworkStatus()) {
							Intent intent=new Intent(getActivity().getBaseContext(),LandingResultActivity.class);
							Bundle bundle=new Bundle();
							bundle.putString("CHANNEL",listChannels.get(pos).getChannelId());
							bundle.putString("CHANNELNAME",listChannels.get(pos).getName());
							bundle.putInt("selectedCategory",selectedCategory);
							
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