package com.itheima.mobilesafe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.itheima.mobilesafe.domain.AppInfo;
import com.itheima.mobilesafe.engine.AppInfoProvider;
import com.itheima.mobilesafe.ui.FocusedTextView;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AppManagerActivity extends Activity {
	private TextView tv_title_status;
	private PopupWindow popWindow;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_app_manager);
		tv_avail_rom=(TextView)findViewById(R.id.tv_avail_rom);
		tv_avail_sd=(TextView)findViewById(R.id.tv_avail_sd);
		lv_app_manager=(ListView)findViewById(R.id.lv_app_manager);
		//
		lv_app_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				AppInfo appInfo;
				if(position==0){//第一个小标签,显示用户程序有多少个
					return;
				}else if(position == (userAppInfos.size()+1)){
					return ;
				}else if(position<=userAppInfos.size()){//用户程序
					int newPosition = position-1;
					appInfo = userAppInfos.get(newPosition);
				}else{//系统程序
					int newPosition = position-1-userAppInfos.size()-1;
					appInfo = systemAppInfos.get(newPosition);
				}	
				String packName = appInfo.getPackName();
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.RED);
				tv.setText(packName);
				dismissPopWindow();
				popWindow = new PopupWindow(tv,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
					int x = 60;// view.getLeft();
				int[] location = new int[2];
				view.getLocationInWindow(location);
				popWindow.showAtLocation(parent,Gravity.LEFT|Gravity.TOP,x,location[1]);
			}
		});
		//
		tv_avail_rom.setText("内存可用:"+getAvailRom());//可用内存大小
		tv_avail_sd.setText("SD卡可用:"+getAvailSD());
		tv_title_status=(TextView)findViewById(R.id.tv_title_status);
		lv_app_manager.setOnScrollListener(new OnScrollListener() {
			//滚动事件发生变化时的调用的方法
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			//滚动时调用的方法
			@Override
			public void onScroll(AbsListView view, 
					int firstVisibleItem,//第一个可见的条目
					int visibleItemCount,//可见的条目数量 
					int totalItemCount) {//总条目
				if(userAppInfos==null || systemAppInfos==null )return;
				String tips = "";
				if(firstVisibleItem>=(userAppInfos.size()+1)){//系统程序
					tips = "系统程序:"+systemAppInfos.size()+"个";
				}else{
					tips = "用户程序:"+userAppInfos.size()+"个";
				}
				tv_title_status.setText(tips);
				dismissPopWindow();
			}

		});
		
		ll_loading =(LinearLayout)findViewById(R.id.ll_loading);
		allAppInfos = new ArrayList<AppInfo>();
		fillData();
	}
	private void dismissPopWindow() {
		if(null!=popWindow &&popWindow.isShowing()){
			popWindow.dismiss();
			popWindow=null;
		}
	}
	private List<AppInfo> allAppInfos;
	//用户程序集合
	private List<AppInfo> userAppInfos;
	//系统程序集合
	private List<AppInfo> systemAppInfos;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			ll_loading.setVisibility(View.INVISIBLE);//隐藏
			lv_app_manager.setAdapter(new AppManagerAdapter());
		};
	};
	
	
	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);//显示
		new Thread(){
			public void run() {
				allAppInfos = AppInfoProvider.getAppInfos(getApplicationContext());
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for(AppInfo appinfo:allAppInfos){
					if(appinfo.isUserApp()){
						userAppInfos.add(appinfo);
					}else{
						systemAppInfos.add(appinfo);
					}
				}
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	/**
	 * 获取手机可用内存空间的大小
	 * @return
	 */
	private String getAvailRom(){
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		long size = blockSize*availableBlocks;
		return Formatter.formatFileSize(this, size);
	}
	
	/**
	 * 获取手机可用q外部空间的大小
	 * @return
	 */
	private String getAvailSD(){
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		long size = blockSize*availableBlocks;
		return Formatter.formatFileSize(this, size);
	}
	private TextView tv_avail_rom;
	private TextView tv_avail_sd;
	private ListView lv_app_manager;
	private LinearLayout ll_loading;
	private class AppManagerAdapter extends BaseAdapter{

		@Override
		public int getCount() {
//			return allAppInfos.size();
			return userAppInfos.size()+systemAppInfos.size()+2;//多两个textView小标签
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo appInfo;
			if(position==0){//第一个小标签,显示用户程序有多少个
				TextView tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setText("用户程序:"+userAppInfos.size()+"个");
				return tv;
			}else if(position == (userAppInfos.size()+1)){
				TextView tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setText("系统程序:"+systemAppInfos.size()+"个");
				return tv;
			}else if(position<=userAppInfos.size()){//用户程序
				int newPosition = position-1;
				appInfo = userAppInfos.get(newPosition);
			}else{//系统程序
				int newPosition = position-1-userAppInfos.size()-1;
				appInfo = systemAppInfos.get(newPosition);
			}
			
			View view;
			ViewHolder holder;
			//判断历史convertView是否是RelativeLayout
			if(null!=convertView && convertView instanceof RelativeLayout){
				view = convertView;
				holder = (ViewHolder)view.getTag();
			}else{
				view = View.inflate(getApplicationContext(), R.layout.list_appinfo_item, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView)view.findViewById(R.id.iv_app_icon);
				holder.tv_name=(FocusedTextView)view.findViewById(R.id.tv_app_name);
				holder.tv_location=(TextView)view.findViewById(R.id.tv_app_location);
				view.setTag(holder);
			}
			holder.iv_icon.setImageDrawable(appInfo.getAppIcon());
			holder.tv_name.setText(appInfo.getAppName());
			if(appInfo.isInRom()){
				holder.tv_location.setText("手机内存");
			}else{
				holder.tv_location.setText("外部内存");
			}
			return view;
		}
		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissPopWindow();
	}
	static class ViewHolder{
		FocusedTextView tv_name;
		TextView tv_location;
		ImageView iv_icon;
	}
}
