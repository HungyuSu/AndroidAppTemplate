package com.gocharm.coimotion.apptemplate;

import java.util.Map;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import com.coimotion.csdk.common.COIMCallListener;
import com.coimotion.csdk.common.COIMException;
import com.coimotion.csdk.util.ReqUtil;


import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SpinnerAdapter;

public class MainActivity extends ActionBarActivity {
	private final static String LOG_TAG = "mainActivity";
	private final static String checkTokenURL = "go4art/account/profile";
	
	public DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private LinearLayout mLlvDrawerContent;
	private ListView mLsvDrawerMenu;
	// �O���Q��ܪ������Х�
	private int mCurrentMenuItemPosition = -1;
	private String title = null;
	// ��涵��
	public static final String[] MENU_ITEMS = {
		"���֪�t",  "���@��t",  "�R�Ъ�t", "�ˤl����", "�i������", "���y", "�q�v", "�t�۷|", "�䥦��T"
	};
	public static final String[] catIDs = {
	    "1", "2", "3", "4", "6", "7", "8", "17", "15"
	};
	
	// Adapter
	SpinnerAdapter adapter =
	        ArrayAdapter.createFromResource(this, R.array.actions,
	        android.R.layout.simple_spinner_dropdown_item);

	// Callback
	OnNavigationListener callback = new OnNavigationListener() {

	    String[] items = getResources().getStringArray(R.array.actions); // List items from res

	    @Override
	    public boolean onNavigationItemSelected(int position, long id) {

	        // Do stuff when navigation item is selected

	        Log.d("NavigationItemSelected", items[position]); // Debug

	        return true;

	    }

	};
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
		    // Get extra data included in the Intent
		    Log.i("receiver", "unlock");
		    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		  }
		};
	
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
	};
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("unlock-drawer"));
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drw_layout);
	    // �]�w Drawer ���v�l
	    //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		
		//actionBar.setDisplayHomeAsUpEnabled(true);
		mDrawerToggle = new ActionBarDrawerToggle(
	            this, 
	            mDrawerLayout,    // �� Drawer Toggle ���D���餶���O��
	            R.drawable.ic_launcher, // Drawer �� Icon
	            R.string.open_left_drawer, // Drawer �Q���}�ɪ��y�z
	            R.string.close_left_drawer // Drawer �Q�����ɪ��y�z
	            ) {
	                //�Q���}��n�����Ʊ�
	                @Override
	                public void onDrawerOpened(View drawerView) {
	                    // �N Title �]�w���۩w�q����r
	                    getSupportActionBar().setTitle("�������O");
	                }
	 
	                //�Q���W��n�����Ʊ�
	                @Override
	                public void onDrawerClosed(View drawerView) {
	                    if (mCurrentMenuItemPosition > -1) {
	                    	title = MENU_ITEMS[mCurrentMenuItemPosition];
	                        getSupportActionBar().setTitle(title);
	                        ShowListFrag listFrag = ShowListFrag.newInstance(catIDs[mCurrentMenuItemPosition]);
	        				FragmentTransaction ft  = getSupportFragmentManager().beginTransaction();
	        				ft.replace(R.id.container, listFrag);
	        				ft.addToBackStack(null);
	        				ft.commit();
	                    } else {
	                        // �N Title �]�w�^ APP ���W��
	                        getSupportActionBar().setTitle( "" + ((title == null)? title : R.string.app_name));
	                    }
	                }
	    };
	 
	    mDrawerLayout.setDrawerListener(mDrawerToggle);
	    
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
				.add(R.id.container, new MainFrag()).commit();
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
					//.add(R.id.container, new SigninFrag()).commit();
		}
		setDrawerMenu();
		
		try {
			ReqUtil.initSDK(getApplication());
		} catch (COIMException e1) {
			// TODO Auto-generated catch block
			Log.i(LOG_TAG,"ex: " + e1.getLocalizedMessage());
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ReqUtil.send(checkTokenURL, null, new COIMCallListener() {
			
			@Override
			public void onSuccess(Map<String, Object> results) {
				// TODO Auto-generated method stub
				Log.i(LOG_TAG, "" + results);
				//if (results.get("errCode").equals("0")) {
					JSONObject values = (JSONObject) results.get("value");
					try {
						Log.i(LOG_TAG,"dspname: " + values.getString("dspName"));
						if(!values.getString("dspName").equals("Guest")) {
							
							getSupportFragmentManager().beginTransaction()
							.replace(R.id.container, new SigninFrag()).commit();
							
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				//}
			}
			
			@Override
			public void onProgress(Integer arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFail(HttpResponse arg0, Exception arg1) {
				// TODO Auto-generated method stub
				Log.i(LOG_TAG, "err: " + arg1.getLocalizedMessage());
			}
		});
	}
	
	private void setDrawerMenu() {
	    // �w�q�s�ŧi����Ӫ���G�ﶵ�M�檺 ListView �H�� Drawer���e�� LinearLayou
	    mLsvDrawerMenu = (ListView) findViewById(R.id.lsv_drawer_menu);
	    mLlvDrawerContent = (LinearLayout) findViewById(R.id.llv_left_drawer);
	 
	    // ��M��ﶵ���l����Q�I���ɭn�����ʧ@
	    mLsvDrawerMenu.setOnItemClickListener(new OnItemClickListener() {
	 
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view,
	                int position, long id) {
	            selectMenuItem(position);
	        }
	    });
	    // �]�w�M�檺 Adapter�A�o�̪����ϥ� ArrayAdapter<String>
	    mLsvDrawerMenu.setAdapter(new ArrayAdapter<String>(
	            this,
	            R.layout.drawer_menu_item,  // ��檫�󪺤��� 
	            MENU_ITEMS                  // ��椺�e
	    ));
	}
	
	private void selectMenuItem(int position) {
	    mCurrentMenuItemPosition = position;
	 
	    // �N��檺�l����]�w���Q��ܪ����A
	    mLsvDrawerMenu.setItemChecked(position, true);
	 
	    // ���� Drawer
	    mDrawerLayout.closeDrawer(mLlvDrawerContent);
	}
	
}
