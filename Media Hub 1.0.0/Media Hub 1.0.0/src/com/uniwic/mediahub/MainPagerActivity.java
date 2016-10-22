package com.uniwic.mediahub;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.actionbarsherlock.widget.ShareActionProvider.OnShareTargetSelectedListener;
import com.startapp.android.publish.StartAppAd;
import com.uniwic.mediahub.mychannel.MyChannelLanding;
import com.uniwic.mediahub.playlist.PlayListLanding;
import com.uniwic.mediahub.theme.Theme;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class MainPagerActivity extends SherlockFragmentActivity {
	
	private static final String[] CONTENT = new String[] { "UPLOADS", "PLAYLIST", "MY CHANNEL"};
	
	TestFragmentAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;
    
    Dialog dialog;
	RadioButton greenRadio,blackRadio;
	public static String image4Theme = "_g";
	
	//OverFlow
	private Menu mainMenu;
	private SubMenu subMenu;
	
	String myAppURL = "http://play.google.com/store/apps/details?id=com.uniwic.mediahub";
	StartAppAd startAppAd = new StartAppAd(this);
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.onActivityCreateSetTheme(this);
        
        StartAppAd.init(this, "110026263", "209646634");
                   
        setContentView(R.layout.simple_tabs);
        
        startAppAd.showAd(); 
        startAppAd.loadAd(); 
        
        mPager = (ViewPager)findViewById(R.id.pager);
        mAdapter = new TestFragmentAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);

        mIndicator = (TitlePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        mPager.setOffscreenPageLimit(3);
    }
	
	class TestFragmentAdapter extends FragmentStatePagerAdapter {	    
	    private int mCount = CONTENT.length;
	    Fragment frag = new Fragment();
	    
	    public TestFragmentAdapter(FragmentManager fm) {
	        super(fm);
	    }

	    @Override
	    public Fragment getItem(int position) {
	    	
	    	switch (position) {
			case 0:
				
				frag = LandingPage.newInstance(String.valueOf(position)); 
				break;
			case 1:
				
				frag = PlayListLanding.newInstance(String.valueOf(position)); 
				break;
			case 2:
				frag = MyChannelLanding.newInstance(String.valueOf(position)); 
			break;

			default:
				break;
			}
			return frag;
	    	 
	    }

	    @Override
	    public int getCount() {
	        return mCount;
	    }
	    
	    @Override
	    public CharSequence getPageTitle(int position) {
	    	return CONTENT[position];
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
	              
	                return true;
	            }
	     
	            public boolean onQueryTextSubmit(String query) {
	            	
	            	Intent intent=new Intent(getApplicationContext(),SearchActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("CHANNEL",query);
					intent.putExtras(bundle);
					startActivity(intent);
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
		//String appName = "com.zps.eztube";
		
		try {
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
		
			case 20:
				try {
					
					dialog = new Dialog(this,R.style.TransparentTHEME);
					dialog.setTitle("Select Theme");
					dialog.setContentView(R.layout.theme_dialog);
					
					greenRadio = (RadioButton) dialog.findViewById(R.id.radioButtonWhite);
					blackRadio = (RadioButton) dialog.findViewById(R.id.radioButtonBlack);
					dialog.show();
					
					if(image4Theme.equalsIgnoreCase("_g")){
						greenRadio.setChecked(true);
						blackRadio.setChecked(false);
					}else{
						blackRadio.setChecked(true);
						greenRadio.setChecked(false);
					}
					
					greenRadio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							if (isChecked) {
								image4Theme = "_g";
								Theme.changeToTheme(MainPagerActivity.this,Theme.THEME_WHITE);
								dialog.dismiss();
							}
						}
					});
					
					blackRadio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							if (isChecked){
								image4Theme = "";
								Theme.changeToTheme(MainPagerActivity.this,Theme.THEME_BLACK);
								dialog.dismiss();
							}
						}
					});
					
				}catch (Exception e) {
			}
		
			default:
				break;
			}
		}catch(Exception e){
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		startAppAd.onPause();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		startAppAd.onResume();
		super.onResume();
	}
	
	@Override
	public void onBackPressed() {
		
		startAppAd.onBackPressed();
		super.onBackPressed();
	}
}