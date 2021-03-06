package com.gocharm.coimotion.apptemplate;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.coimotion.csdk.common.COIMCallListener;
import com.coimotion.csdk.common.COIMException;
import com.coimotion.csdk.util.ReqUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v7.app.ActionBarActivity;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MapActivity extends ActionBarActivity {
	private static final String LOG_TAG = "mapAct";
	private static String tsIDs;
	private static LinearLayout descView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			ReqUtil.initSDK(getApplication());
		} catch (COIMException e) {
		} catch (Exception e) {
		}
		
		double lat;
		double lng;
		String placeName;
		
		setContentView(R.layout.activity_map);
		descView = (LinearLayout) findViewById(R.id.descView);
		
		android.support.v7.app.ActionBar ab = getSupportActionBar();
		ab.setTitle("活動地點");
		
		lat = Double.parseDouble(getIntent().getExtras().getString("lat"));
		lng = Double.parseDouble(getIntent().getExtras().getString("lng"));
		placeName = getIntent().getExtras().getString("here");
		ab.setSubtitle(placeName);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.map, myMapFragment.newInstance(new LatLng(lat, lng), placeName)).commit();
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem route = menu.add(0,111,0,"路線列表");
		if(android.os.Build.VERSION.SDK_INT > 10){
			route.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		Log.i(LOG_TAG, "tsIDs: " + tsIDs);
		if (id == 111) {
			Intent intent = new Intent();
			intent.setClass(this, RouteListActivity.class);
			intent.putExtra("tsIDs", tsIDs);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public static class myMapFragment extends SupportMapFragment {
		private static final String LOG_TAG = "mapFragment";
	    private GoogleMap mapView;
	    private Map<String, String> stopList;
	    public static LatLng destination;
	    public static String place;
	    
	    public static myMapFragment newInstance(LatLng pos, String placeName){
	    	myMapFragment frag = new myMapFragment();
	    	destination = pos;
	    	place = placeName;
			return  frag;
		}
	    
	    public myMapFragment() {
	    	Log.i(LOG_TAG, "fragment constructed");
	    	
	    }
	    
	    @Override
	    public void onCreate(Bundle arg0) {
	    	Log.i(LOG_TAG, "fragment on create");
	        super.onCreate(arg0);
	    }

	    @Override
	    public View onCreateView(LayoutInflater mInflater, ViewGroup arg1, Bundle arg2) {
	    	Log.i(LOG_TAG, "fragment on create view");
	        View view = super.onCreateView(mInflater, arg1, arg2);
	        descView.bringToFront();
	        return view;
	    }

	    @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);
	        Log.i(LOG_TAG, "fragment on activity create");
	        stopList = new HashMap<String, String>();
	        Map<String, Object> mapParam = new HashMap<String, Object>();
	        mapParam.put("lat", "" + destination.latitude);
	        mapParam.put("lng", "" + destination.longitude);
	        
	        ReqUtil.send("twCtBus/busStop/search", mapParam, new COIMCallListener() {
				
				@Override
				public void onSuccess(Map<String, Object> result) {
					JSONObject value = (JSONObject) result.get("value");
					try {
						JSONArray stops = value.getJSONArray("list");
						Log.i(LOG_TAG, "success: " + stops);
						tsIDs = "[";
						for (int i=0; i<stops.length(); i++) {
							Map<String, String> item = new HashMap<String, String>();
							if(i>0) {
								tsIDs += ",";
							}
							tsIDs += stops.getJSONObject(i).getString("tsID");
							//mark stop i
							String title = stops.getJSONObject(i).getString("stName");
							double sLat = stops.getJSONObject(i).getDouble("latitude");
							double sLng = stops.getJSONObject(i).getDouble("longitude");
							Marker stop= mapView.addMarker(new MarkerOptions()
											.position(new LatLng(sLat, sLng))
											.title(title)
											.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
							//save to array
							item.put("title", title);
							stopList.put(stop.getId(), stops.getJSONObject(i).getString("tsID"));
						}
						tsIDs += "]";
						descView.bringToFront();
					} catch (JSONException e) {
					}
				}
								
				@Override
				public void onFail(HttpResponse response, Exception ex) {
					Log.i(LOG_TAG, "ex: " + ex.getLocalizedMessage());
				}
			});
	        
	        mapView = getMap();
	        mapView.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 16));
	        final Marker here= mapView.addMarker(new MarkerOptions().position(destination).title(place));
	        mapView.setOnMarkerClickListener(new OnMarkerClickListener() {
				
				@Override
				public boolean onMarkerClick(final Marker marker) {
					Log.i(LOG_TAG, "marker: " + marker.getTitle());
					Log.i(LOG_TAG, "here: " + here.getTitle());
					if(marker.getTitle().equals(here.getTitle())) {
						Log.i(LOG_TAG, "click here~~");
					}
					else {
						String tsID = stopList.get(marker.getId());
						ReqUtil.send("twCtBus/busStop/routes/" + tsID, null, new COIMCallListener() {
							
							@Override
							public void onSuccess(Map<String, Object> result) {
								try {
									JSONObject value = new JSONObject(result).getJSONObject("value");
									JSONArray list = value.getJSONArray("list");
									String tmp = "";
									for (int i = 0; i< list.length(); i++) {
										if(i > 0) 
											tmp += ", ";
										tmp += list.getJSONObject(i).getString("rtName");
									}
									marker.setSnippet(tmp);
									Log.i(LOG_TAG, "routes: " + tmp);
								} catch (JSONException e) {
									e.printStackTrace();
								}
								marker.showInfoWindow();
							}
							
							@Override
							public void onProgress(Integer progress) {
							}
							
							@Override
							public void onFail(HttpResponse response, Exception ex) {
								Log.i(LOG_TAG, "ex: " + ex.getLocalizedMessage());
							}
						});
					}
					return false;
				}
			});	        
	    }
	}

}
