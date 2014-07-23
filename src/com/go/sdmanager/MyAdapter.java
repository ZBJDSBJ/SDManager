package com.go.sdmanager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.go.sdmanager.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter
{
	private LayoutInflater inflater;
	private File[] items;

	public MyAdapter(Context context, File[] items)
	{
		this.items = items;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount()
	{
		if(items == null) {
			return 0;
		}
		return items.length;
	}

	@Override
	public Object getItem(int location)
	{
		return items[location];
	}

	@Override
	public long getItemId(int location)
	{
		return location;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder = null;

		if (convertView == null)
		{
			holder = new ViewHolder();

			convertView = inflater.inflate(R.layout.listfile, null);
			holder.fileico = (ImageView) convertView.findViewById(R.id.iv_file_ico);
			holder.filename = (TextView) convertView.findViewById(R.id.tv_detail_filename);
			holder.filetime = (TextView) convertView.findViewById(R.id.tv_filetime);
			holder.filesize = (TextView)convertView.findViewById(R.id.tv_filesize);
			convertView.setTag(holder);
		} else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		File file = new File(items[position].getPath());
		holder.filename.setText(items[position].getName());

		if (file.isDirectory())
		{
			holder.fileico.setImageResource(R.drawable.folder);
		} else
		{
			holder.fileico.setImageResource(R.drawable.file);
			Date date = new Date(file.lastModified());
			SimpleDateFormat format = new SimpleDateFormat("yy年MM月dd日 hh:mm:ss   ");
			String str = format.format(date);
			holder.filetime.setText(str);
			holder.filesize.setText(items[position].length()/1024+"KB");
		}
		return convertView;
	}

	private final class ViewHolder
	{
		public ImageView fileico;
		public TextView filename;
		public TextView filetime;
		public TextView filesize;
	}
}
