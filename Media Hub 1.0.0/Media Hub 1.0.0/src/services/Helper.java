package services;

import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import settergetter.Medias;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import android.util.Log;

public class Helper extends SQLiteOpenHelper {

	// database variables.
	private static String DB_PATH = "/data/data/com.uniwic.mediahub/databases/";
	private static String DB_NAME = "fun_tv.sqlite";
	private SQLiteDatabase myDataBase;
	private final Context myContext;
	private String TAG = "Helper";
	Cursor cursorGetData;
	SQLiteDatabase checkDB = null;

	public Helper(Context context) {
		super(context, DB_NAME, null, 4);
		this.myContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
		if(newVersion > oldVersion) {
			myContext.deleteDatabase("fun_tv.sqlite");
			copyDataBase();
		}
		} catch (Exception e) {
		}
	}

	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();
		if (!dbExist) {
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
	}

	private boolean checkDataBase() {

		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,SQLiteDatabase.OPEN_READWRITE);
		} catch (SQLiteException e) {
			Log.e(TAG, "Error is" + e.toString());
		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	private void copyDataBase() throws IOException {
		InputStream myInput = myContext.getAssets().open(DB_NAME);
		String outFileName = DB_PATH + DB_NAME;
		OutputStream myOutput = new FileOutputStream(outFileName);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public void openDataBase() throws SQLException {
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);
	}

	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();
		super.close();
	}

	private Cursor getData(String sql) {
		openDataBase();
		cursorGetData = getReadableDatabase().rawQuery(sql, null);
		return cursorGetData;
	}

	private long insertData(String tableName, ContentValues values) {
		openDataBase();
		return myDataBase.insert(tableName, null, values);

	}

	private int updateData(String tableName, ContentValues values,
			String condition) {
		openDataBase();
		return myDataBase.update(tableName, values, condition, null);
	}

	private int deleteData(String tableName, String condition) {
		
		return myDataBase.delete(tableName, condition, null);
	}
	
	
								/**
								 * 
								 * Retrieving 
								 * 
								 * 1 
								 * 
								 * 
								 * **/
	

	public ArrayList<Medias> getCategory() {
		ArrayList<Medias> categoryList = new ArrayList<Medias>();
		Cursor cursor = null;
		try {
			cursor = getData("select * from media_category");
			int size = cursor.getCount();
			cursor.moveToFirst();
			int mc_id = cursor.getColumnIndex("mc_id");
			int mc_name = cursor.getColumnIndex("mc_name");
			int mc_drawable = cursor.getColumnIndex("mc_drawable");
			
			for (int i = 0; i < cursor.getCount(); i++) {
				Medias category=new Medias();
				category.setId(cursor.getInt(mc_id));
				category.setName(cursor.getString(mc_name));
				category.setDrawable(cursor.getString(mc_drawable));
				
				categoryList.add(category);
				cursor.moveToNext();
			}
		} catch (Exception ex) {
		}finally{
			cursor.close();
			cursorGetData.close();
			myDataBase.close();
		}
		return categoryList;
	}

	
	public List<Medias> getMedia(int categoryID) {
		List<Medias> candidatename = new ArrayList<Medias>();
		Cursor cursor = null;
		try {
			cursor = getData("select * from media_details WHERE mc_id="+categoryID);
			int size = cursor.getCount();
			cursor.moveToFirst();
			
			
			int md_name = cursor.getColumnIndex("md_name");
			int md_drawable = cursor.getColumnIndex("md_drawable");
			int channel_id = cursor.getColumnIndex("channel_id");
			
			for (int i = 0; i < cursor.getCount(); i++) {
				Medias category=new Medias();
				category.setName(cursor.getString(md_name));
				category.setDrawable(cursor.getString(md_drawable));
				category.setChannelId(cursor.getString(channel_id));
				
				candidatename.add(category);
				cursor.moveToNext();
			}
			
		} catch (Exception ex) {

		}finally{
			cursor.close();
			cursorGetData.close();
			myDataBase.close();

		}
		
		return candidatename;
	}

	
									/**
									 * 
									 * Getting Playlist Category
									 * 
									 * ***/
	
	public List<Medias> getPlaylistCategory() {
		ArrayList<Medias> categoryList = new ArrayList<Medias>();
		Cursor cursor = null;
		try {
			cursor = getData("select * from playlist_category");
			int size = cursor.getCount();
			cursor.moveToFirst();
			int pc_id = cursor.getColumnIndex("pc_id");
			int pc_name = cursor.getColumnIndex("pc_name");
			int pc_drawable = cursor.getColumnIndex("pc_drawable");
			
			for (int i = 0; i < cursor.getCount(); i++) {
				Medias category=new Medias();
				category.setId(cursor.getInt(pc_id));
				category.setName(cursor.getString(pc_name));
				category.setDrawable(cursor.getString(pc_drawable));
				
				categoryList.add(category);
				cursor.moveToNext();
			}
		} catch (Exception ex) {
		}finally{
			cursor.close();
			cursorGetData.close();
			myDataBase.close();
		}
		return categoryList;
	}
	
	public List<Medias> getPlayList(int categoryID) {
		ArrayList<Medias> playList = new ArrayList<Medias>();
		Cursor cursor = null;
		try {
			cursor = getData("select * from playlist_details WHERE pc_id="+categoryID);
			int size = cursor.getCount();
			cursor.moveToFirst();
			int pd_id = cursor.getColumnIndex("pd_id");
			int pd_name = cursor.getColumnIndex("pd_name");
			int playlist_id = cursor.getColumnIndex("playlist_id");
			int playlist_imgurl = cursor.getColumnIndex("playlist_imgurl");
			
			for (int i = 0; i < cursor.getCount(); i++) {
				Medias category=new Medias();
				category.setId(cursor.getInt(pd_id));
				category.setName(cursor.getString(pd_name));
				category.setChannelId(cursor.getString(playlist_id));
				category.setDrawable(cursor.getString(playlist_imgurl));
				
				playList.add(category);
				cursor.moveToNext();
			}
		} catch (Exception ex) {
		}finally{
			cursor.close();
			cursorGetData.close();
			myDataBase.close();
		}
		return playList;
	}

									
	
}
