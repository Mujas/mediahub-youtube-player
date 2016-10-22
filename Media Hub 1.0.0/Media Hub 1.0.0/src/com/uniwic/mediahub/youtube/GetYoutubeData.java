package com.uniwic.mediahub.youtube;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import settergetter.Category;
import settergetter.Media;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

public class GetYoutubeData {

	ArrayList<Category> categoryList;
	ArrayList<Category> arrayListMedia;
	public static String nextPageCategory = null;
	public static String nextPageUploads = null;
	public static String nextPageSearch = null;

	/*
	 * 
	 * Getting Unlimited Uploads Using next page
	 */

	public List<Category> GetFeedData(String channelName) {
		categoryList = new ArrayList<Category>();

		try {
			for (int k = 0; k < 1; k++) {

				categoryList = new ArrayList<Category>();

				URL url = new URL(channelName);
				URLConnection connection;
				connection = url.openConnection();
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				int responseCode = httpConnection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					InputStream in = httpConnection.getInputStream();
					DocumentBuilderFactory dbf = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document dom = db.parse(in);
					Element docEle = dom.getDocumentElement();
					Element secondElmnt = dom.getDocumentElement();
					NodeList nl = docEle.getElementsByTagName("entry");
					NodeList contentURL = secondElmnt
							.getElementsByTagName("media:player");

					// int j = 0 ;
					if (nl != null && nl.getLength() > 0) {

						nextPageUploads = getnextPageNumber(docEle);

						for (int i = 0; i < nl.getLength(); i++) {

							Element entry = (Element) nl.item(i);
							Element mediaContent = (Element) contentURL.item(i);
							Element title = (Element) entry
									.getElementsByTagName("title").item(0);
							String myTitle = title.getFirstChild()
									.getNodeValue();
							String urlcheck = mediaContent.getAttribute("url");
							String duration = mediaContent
									.getAttribute("duration");
							Category category = new Category();
							category.setCategoryName(myTitle);
							category.setMediaUrl(urlcheck);
							// category.setDuration(Integer.parseInt(duration));
							categoryList.add(category);
							// j = j+3;
						}
					}
				}
			}
		} catch (MalformedURLException e) {
			Log.d("Exception ", e.toString());
		} catch (IOException e) {
			Log.d("Exception ", e.toString());
		} catch (ParserConfigurationException e) {
			Log.d("Exception ", e.toString());
		} catch (SAXException e) {
			Log.d("Exception ", e.toString());
		}

		return categoryList;
	}

	private String getnextPageNumber(Element eElement) {

		String returnVal = null;
		try {
			boolean got = false;

			Node eNode;
			int NumOFItem = eElement.getElementsByTagName("link").getLength();
			for (int y = 5; y < 15; y++) {
				if (got) {
					break;
				}
				eNode = eElement.getElementsByTagName("link").item(y);
				NamedNodeMap attributes = eNode.getAttributes();
				for (int g = 0; g < attributes.getLength(); g++) {
					Attr attribute = (Attr) attributes.item(g);
					if (attribute.getNodeName().equals("rel")
							&& attribute.getNodeValue().equals("next")) {
						try {
							returnVal = eNode.getAttributes()
									.getNamedItem("href").getNodeValue();
							got = true;
							break;
						} catch (Exception e) {
							returnVal = null;
						}
					}
				}
			}
		} catch (Exception e) {
		}

		return returnVal;
	}

	/**
	 * 
	 * **Search Data
	 * 
	 * @param channelName
	 * @return
	 */

	public List<Category> GetSearchData(String channelName) {
		categoryList = new ArrayList<Category>();
		try {

			categoryList = new ArrayList<Category>();
			// String
			// featuredFeed="https://gdata.youtube.com/feeds/api/users/"+channelName+"/playlists?v=2".trim();
			// String featuredFeed =
			// "https://gdata.youtube.com/feeds/api/users/MyDeadmanAlive/subscriptions?v=2";
			/*
			 * channelName = channelName.replace(" ", "+"); String featuredFeed
			 * = "https://gdata.youtube.com/feeds/api/videos?q=" + channelName +
			 * "&orderby=published&start-index=1&max-results=50&v=2";
			 */

			URL url = new URL(channelName);
			URLConnection connection;
			connection = url.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			int responseCode = httpConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream in = httpConnection.getInputStream();
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document dom = db.parse(in);
				Element docEle = dom.getDocumentElement();
				Element secondElmnt = dom.getDocumentElement();
				NodeList nl = docEle.getElementsByTagName("entry");
				NodeList contentURL = secondElmnt
						.getElementsByTagName("media:player");
				// int j = 0 ;
				if (nl != null && nl.getLength() > 0) {

					nextPageSearch = getnextPageNumber(docEle);

					for (int i = 0; i < nl.getLength(); i++) {
						Element entry = (Element) nl.item(i);
						Element mediaContent = (Element) contentURL.item(i);
						Element title = (Element) entry.getElementsByTagName(
								"title").item(0);
						String myTitle = title.getFirstChild().getNodeValue();
						String urlcheck = mediaContent.getAttribute("url");
						String duration = mediaContent.getAttribute("duration");
						Category category = new Category();
						category.setCategoryName(myTitle);
						category.setMediaUrl(urlcheck);
						// category.setDuration(Integer.parseInt(duration));
						categoryList.add(category);
					}
				}
			}

		} catch (MalformedURLException e) {
			Log.d("Exception ", e.toString());
		} catch (IOException e) {
			Log.d("Exception ", e.toString());
		} catch (ParserConfigurationException e) {
			Log.d("Exception ", e.toString());
		} catch (SAXException e) {
			Log.d("Exception ", e.toString());
		}
		return categoryList;
	}

	/**
	 * 
	 * 
	 * PLAYLIST CATEGORY Getting Unlimited Playlist category Using next page
	 * 
	 * 
	 */

	public List<Category> GetPlaylistCategory(String channelName) {
		categoryList = new ArrayList<Category>();

		try {
			for (int k = 0; k < 1; k++) {

				URL url = new URL(channelName);
				URLConnection connection;
				connection = url.openConnection();
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				int responseCode = httpConnection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {

					InputStream in = httpConnection.getInputStream();
					DocumentBuilderFactory dbf = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document dom = db.parse(in);
					Element docEle = dom.getDocumentElement();
					Element secondElmnt = dom.getDocumentElement();
					NodeList nl = docEle.getElementsByTagName("entry");
					NodeList contentURL = secondElmnt
							.getElementsByTagName("content");

					int size = nl.getLength();

					if (nl != null && size > 0) {

						nextPageCategory = getnextPageNumber(docEle);

						for (int i = 0; i < nl.getLength(); i++) {
							Element entry = (Element) nl.item(i);
							Element mediaContent = (Element) contentURL.item(i);
							Element title = (Element) entry
									.getElementsByTagName("title").item(0);
							Element yt_count = (Element) entry
									.getElementsByTagName("yt:countHint").item(
											0);
							String myTitle = title.getFirstChild()
									.getNodeValue();
							String urlcheck = mediaContent.getAttribute("src");
							String count = yt_count.getFirstChild()
									.getNodeValue();
							Category category = new Category();
							category.setCategoryName(myTitle);
							category.setMediaUrl(urlcheck);
							category.setCount(Integer.parseInt(count));
							categoryList.add(category);
						}
					}
				}
			}
		} catch (MalformedURLException e) {
			Log.d("Exception ", e.toString());
		} catch (IOException e) {
			Log.d("Exception ", e.toString());
		} catch (ParserConfigurationException e) {
			Log.d("Exception ", e.toString());
		} catch (SAXException e) {
			Log.d("Exception ", e.toString());
		}

		return categoryList;
	}

	/***
	 * 
	 * 
	 * PLAYLIST DATA Only 50 videos is coming
	 * To get Age Restricted Videos
	 * featuredFeed = featuredFeed + "&max-results=50&v=2&prettyprint=true&safeSearch=none";
	 * 
	 * ***/

	public List<Category> GetPlaylistData(String featuredFeed) {
		try {
			for (int k = 0; k < 1; k++) {
				arrayListMedia = new ArrayList<Category>();

				if (featuredFeed.length() > 0) {
					featuredFeed = featuredFeed.substring(0,featuredFeed.length() - 3);
					featuredFeed = featuredFeed + "&max-results=50&v=2";
				}

				URL url = new URL(featuredFeed);
				URLConnection connection;
				connection = url.openConnection();
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				int responseCode = httpConnection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					InputStream in = httpConnection.getInputStream();
					DocumentBuilderFactory dbf = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document dom = db.parse(in);
					Element docEle = dom.getDocumentElement();
					Element secondElmnt = dom.getDocumentElement();
					Element videoElmnt = dom.getDocumentElement();

					NodeList nl = docEle.getElementsByTagName("entry");
					NodeList contentURL = secondElmnt
							.getElementsByTagName("content");
					if (nl != null && nl.getLength() > 0) {
						for (int i = 0; i < nl.getLength(); i++) {
							Element entry = (Element) nl.item(i);

							Element title = (Element) entry
									.getElementsByTagName("title").item(0);
							Element videoID = (Element) entry
									.getElementsByTagName("yt:videoid").item(0);
							String myTitle = title.getFirstChild()
									.getNodeValue();
							String myvideoID = videoID.getFirstChild()
									.getNodeValue();
							Category media = new Category();
							media.setCategoryName(myTitle);
							media.setMediaUrl(myvideoID);
							arrayListMedia.add(media);

						}
					}
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

		return arrayListMedia;
	}
}
