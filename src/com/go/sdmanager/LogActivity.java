package com.go.sdmanager;

import com.go.sdmanager.R;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LogActivity extends Activity
{
	private ListView mListLog;
	DBAdapter dbAdapter;
	private Cursor mCursor;
	private LayoutInflater mInflaterLog;
	private int mCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actionlog);
		mInflaterLog = LayoutInflater.from(this);
		mListLog = (ListView) findViewById(R.id.list_log);

		dbAdapter = new DBAdapter(this);
		dbAdapter.open();
		mCursor = dbAdapter.getAllActions();
		while (mCursor.moveToNext())
		{
			mCount++;
		}
		Log.i("TAG", mCursor.equals(null) + "");

		mListLog.setAdapter(actionAdapter);
	}

	private BaseAdapter actionAdapter = new BaseAdapter()
	{

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{

			ViewHolder holder = null;
			Log.i("TAG", "aaaaaaaz");
			mCursor = dbAdapter.getAllActions();
			if (convertView == null)
			{
				holder = new ViewHolder();

				convertView = mInflaterLog.inflate(R.layout.listlog, null);
				holder.logname = (TextView) convertView.findViewById(R.id.tv_user);
				holder.logaction = (TextView) convertView.findViewById(R.id.tv_action);
				holder.logtime = (TextView) convertView.findViewById(R.id.tv_action_time);
				convertView.setTag(holder);
			} else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			
			int flag = 0;
			while (mCursor.moveToNext())
			{
				if (flag == position)
				{
					Log.i("TAG", "zzzzzzzz");
					holder.logname.setText(mCursor.getString(1));
					holder.logaction.setText(mCursor.getString(2));
					holder.logtime.setText(mCursor.getString(3));
				}
				flag++;
			}
			return convertView;
		}

		@Override
		public long getItemId(int location)
		{
			return location;
		}

		@Override
		public Object getItem(int arg0)
		{
			return null;
		}

		@Override
		public int getCount()
		{
			return mCount;
		}
	};

	private final class ViewHolder
	{
		public TextView logname;
		public TextView logaction;
		public TextView logtime;
	}

	@Override
	protected void onDestroy()
	{

		dbAdapter.close();
		super.onDestroy();
	}

}
