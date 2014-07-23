package com.go.sdmanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadActivity extends Activity
{
	private ProgressBar mProgressBarOne;
	private ProgressBar mProgressBarTwo;
	private ProgressBar mProgressBarThree;
	private Button mDownLoadOne;
	private Button mDownLoadTwo;
	private Button mDownLoadThree;
	private TextView mPercentOne;

	private String root = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	private String downloadFile = "http://gongxue.cn/yingyinkuaiche/UploadFiles_9323/201008/2010082909434077.mp3";
	private String downloadFile1 = "http://gongxue.cn/yingyinkuaiche/UploadFiles_9323/201008/2010082909434077.mp3";

	// 固定存放下载的文件的路径：SD卡目录下
	private static final String SD_PATH = "/mnt/sdcard/";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.downloader);

		initView();

		onClicks();
	}

	private void initView()
	{
		mProgressBarOne = (ProgressBar) findViewById(R.id.progressBar1);
		mProgressBarTwo = (ProgressBar) findViewById(R.id.progressBar2);
		mProgressBarThree = (ProgressBar) findViewById(R.id.progressBar3);
		mDownLoadOne = (Button) findViewById(R.id.btn_download_one);
		mDownLoadTwo = (Button) findViewById(R.id.btn_download_two);
		mDownLoadThree = (Button) findViewById(R.id.btn_download_three);
		mPercentOne = (TextView) findViewById(R.id.tv_percent_one);
	}

	private void onClicks()
	{
		mDownLoadOne.setOnClickListener(listener);
		mDownLoadTwo.setOnClickListener(listener);
		mDownLoadThree.setOnClickListener(listener);
	}

	OnClickListener listener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.btn_download_one:
				if (mDownLoadOne.getText().equals("下载"))
				{
					download(downloadFile, root, mProgressBarOne, mPercentOne);
					mDownLoadOne.setText("暂停");
				} else if (mDownLoadOne.getText().equals("暂停"))
				{
					mDownLoadOne.setText("下载");
				}

				break;
			case R.id.btn_download_two:
				mDownLoadTwo.setText("暂停");

				break;
			case R.id.btn_download_three:
				mDownLoadThree.setText("暂停");
				break;
			default:
				break;
			}
		}
	};

	// 下载函数
	private void download(String url, String targetPath, ProgressBar pb, TextView tv)
	{
		DownloadThread dt = new DownloadThread(url, targetPath, pb, tv);

		dt.start();
	}

	// 下载线程
	public class DownloadThread extends Thread
	{
		private String url = "";
		private String targetPath = "";

		private int hasDownload = 0;

		private int len = -1;
		private byte buffer[] = new byte[4 * 1024];
		private int size = 0;
		private int rate = 0;

		private MyHandler myHandler = null;
		private Message msg = null;

		private ProgressBar pb = null;
		private TextView tv = null;

		public DownloadThread(String url, String targetPath, ProgressBar pb, TextView tv)
		{
			this.url = url;
			this.targetPath = targetPath;

			this.pb = pb;
			this.tv = tv;

			myHandler = new MyHandler(this.pb, this.tv);
		}

		public void run()
		{
			String targetFileName = this.targetPath + this.url.substring(this.url.lastIndexOf("/") + 1, this.url.length());
			File downloadFile = new File(targetFileName);

			if (!downloadFile.exists())
			{
				try
				{
					downloadFile.createNewFile();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}

			try
			{
				URL fileUrl = new URL(this.url);
				HttpURLConnection conn = (HttpURLConnection) fileUrl.openConnection();

				// 获取文件大小
				size = conn.getContentLength();

				InputStream is = conn.getInputStream();

				OutputStream os = new FileOutputStream(targetFileName);

				while ((len = is.read(buffer)) != -1)
				{
					os.write(buffer);

					hasDownload += len;

					rate = (hasDownload * 100 / size);

					msg = new Message();

					msg.arg1 = rate;

					myHandler.sendMessage(msg);

					System.out.println(rate + "%");
				}
			} catch (MalformedURLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}

		}

	}

	// 自定义一个Handler类，处理线程消息
	public class MyHandler extends Handler
	{
		private ProgressBar progressBar;
		private TextView textView;

		// 通过构造函数来确定给哪个ProgressBar刷新
		public MyHandler(ProgressBar progressBar, TextView textView)
		{
			this.progressBar = progressBar;
			this.textView = textView;
		}

		@Override
		public void handleMessage(Message msg)
		{
			if (!Thread.currentThread().isInterrupted())
			{
				this.progressBar.setProgress(msg.arg1);
				this.textView.setText(msg.arg1 + "%");
			}
			super.handleMessage(msg);
		}
	};

}
