package com.gocharm.coimotion.apptemplate;

import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity {
	private final static String LOG_TAG = "mainActivity";
	
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
	/*
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
	*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
	        				//ft.addToBackStack(null);
	        				ft.commit();
	                    } else {
	                        
	                        getSupportActionBar().setTitle(title);
	                    }
	                }
	    };
	 
	    mDrawerLayout.setDrawerListener(mDrawerToggle);
	    
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
			String catID = extras.getString("catID");
			for (int i = 0; i < 9; i++) {
				
				if (catIDs[i].equals(catID)) {
					Log.i(LOG_TAG, "equals " + MENU_ITEMS[i]);
					title = MENU_ITEMS[i];
					getSupportActionBar().setTitle(MENU_ITEMS[i]);
				}
			}
			ShowListFrag slFrag = ShowListFrag.newInstance(catID);
			getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, slFrag).commit();
		}
		setDrawerMenu();
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
