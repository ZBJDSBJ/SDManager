package com.go.sdmanager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{
	private TextView mSdPath;
	private Button mToRoot;
	private Button mToFront;
	private Button mMakeFile;
	private Button mShowLog;
	private Button mDownLoader;
	private ListView mListFilename;
	private LinearLayout mLinerBtn;
	private Button mMove;
	private Button mUpMove;

	String mBackPath;
	private String mRootPath = "/mnt/sdcard";
	private File[] mFiles = null;
	private String mStrPath;
	private static int mCount = 0;
	MyFileAction fileAction;
	private Cursor mCursor;
	DBAdapter mDbAdapter;
	// private long mCountAction = 0;
	ArrayList<HashMap<String, Object>> listItems = new ArrayList<HashMap<String, Object>>();

	 MyAdapter mMyAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initView();

		mDbAdapter = new DBAdapter(MainActivity.this);

		SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE | MODE_MULTI_PROCESS);
		mBackPath = preferences.getString("mstrPath", mStrPath);
		if (mBackPath != null)
		{
			getFilesDir(mBackPath);
		} else
		{
			getFilesDir(mRootPath);

			// mMyAdapter = new MyAdapter(MainActivity.this, mFiles);
			// mListFilename.setAdapter(mMyAdapter);
		}

		onclicks();
	}

	// 初始化
	private void initView()
	{
		mSdPath = (TextView) findViewById(R.id.tv_sd_path);
		mToRoot = (Button) findViewById(R.id.btn_root);
		mToFront = (Button) findViewById(R.id.btn_front);
		mMakeFile = (Button) findViewById(R.id.btn_newfile);
		mShowLog = (Button) findViewById(R.id.btn_log);
		mListFilename = (ListView) findViewById(R.id.lv_filename);
		mLinerBtn = (LinearLayout) findViewById(R.id.Liner_btn);
		mMove = (Button) findViewById(R.id.btn_move);
		mUpMove = (Button) findViewById(R.id.btn_up_move);
		mDownLoader = (Button) findViewById(R.id.btn_downloader);
	}

	// 显示文件
	public void getFilesDir(String filePath)
	{
		mSdPath.setText(filePath);
		mStrPath = filePath;

		File file = new File(filePath);
		mFiles = file.listFiles();

		Arrays.sort(mFiles, new Comparator<File>()
		{
			@Override
			public int compare(File lhs, File rhs)
			{
				return lhs.getName().compareTo(rhs.getName());
			}
		});

		// mMyAdapter.notifyDataSetChanged();
		mListFilename.setAdapter(new MyAdapter(MainActivity.this, mFiles));
	}

	// 对数据库的操作
	private void MyDBAdapterAction(String action)
	{
		long systemTimeMillis = System.currentTimeMillis();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
		String systemTime = dateFormat.format(systemTimeMillis);

		mDbAdapter.open();
		mCursor = mDbAdapter.getAllActions();
		Log.i("TAG", mCursor.getCount() + "");
		if (mCursor.getCount() >= 30)
		{
			if (mCursor.moveToFirst())
			{
				mDbAdapter.deletelAction(mCursor.getString(0));
			}
		}

		mDbAdapter.insertAction("user", action, systemTime);
		mDbAdapter.close();
	}

	private void onclicks()
	{
		// 下载
		mDownLoader.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				Intent intent = new Intent(MainActivity.this, DownloadListActivity.class);
				startActivity(intent);
			}
		});

		// 显示日志
		mShowLog.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				Intent intent = new Intent(MainActivity.this, LogActivity.class);
				startActivity(intent);
			}
		});

		// 打开根目录
		mToRoot.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				MyDBAdapterAction("打开根目录");

				if (!mStrPath.equals(mRootPath))
				{
					getFilesDir(mRootPath);
				} else if (mStrPath.equals(mRootPath))
				{
					Toast.makeText(MainActivity.this, "已经是根目录", Toast.LENGTH_LONG).show();
				}
			}
		});

		// 打开上一目录
		mToFront.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				MyDBAdapterAction("打开上一目录");

				if (!mStrPath.equals(mRootPath))
				{
					File file = new File(mStrPath);
					File upPath = file.getParentFile();

					if ((upPath.toString()).equals(mRootPath))
					{
						getFilesDir(upPath.toString());
					} else
					{
						mStrPath = upPath.getAbsolutePath();
						getFilesDir(mStrPath);
					}
				} else
				{
					Toast.makeText(MainActivity.this, "已经是根目录", Toast.LENGTH_LONG).show();
				}
			}
		});

		// 新建文件夹
		mMakeFile.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				File file = new File(mStrPath);
				makeDir(file);
			}
		});

		// 点击打开文件
		mListFilename.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int location, long arg3)
			{
				File file = mFiles[location];
				if (file.isDirectory())
				{
					getFilesDir(file.getPath());
					MyDBAdapterAction("打开文件" + file.getPath());

					// Log.i("TAG", mstrPath);
				} else
				{
					openFile(file);
					MyDBAdapterAction("打开文件" + file);
				}
			}
		});

		// 长按监听弹出dialog对话框
		mListFilename.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int location, long arg3)
			{
				final File file = mFiles[location];
				Log.i("TAG", mStrPath + "  mstrPath");

				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("选项");
				builder.setItems(new String[]
				{ "重命名", "复制", "移动", "删除" }, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						switch (which)
						{
						case 0:

							break;
						case 1:
							mLinerBtn.setVisibility(View.VISIBLE);
							filecopy(file);
							break;

						case 2:
							mLinerBtn.setVisibility(View.VISIBLE);
							moveFile(file);
							break;
						case 3:
							deleteDir(file);
							break;

						default:
							break;
						}
					}
				});
				builder.show();
				return true;
			}
		});

		mUpMove.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				if (!mStrPath.equals(mRootPath))
				{
					File file = new File(mStrPath);
					File upPath = file.getParentFile();

					if ((upPath.toString()).equals(mRootPath))
					{
						Toast.makeText(MainActivity.this, "已经是根目录", Toast.LENGTH_LONG).show();
						getFilesDir(upPath.toString());
					} else
					{
						mStrPath = upPath.getAbsolutePath();
						getFilesDir(mStrPath);
						Log.i("TAG", mStrPath + "  mstrPath");
					}
					Log.i("TAG", upPath.toString() + "  up");
				} else
				{
					Toast.makeText(MainActivity.this, "已经是根目录", Toast.LENGTH_LONG).show();
				}
			}
		});

	}

	// 复制文件夹
	private void filecopy(final File file)
	{
		fileAction = new MyFileAction();

		mMove.setText("复制到这个目录");
		mMove.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				File plasPath = new File(mStrPath + "/" + file.getName());

				if (plasPath.exists() && !file.getPath().equals(plasPath.getPath()))
				{
					Toast.makeText(MainActivity.this, "文件名重复，请重新操作", Toast.LENGTH_LONG).show();
					mLinerBtn.setVisibility(View.INVISIBLE);
				} else if (file.getPath().equals(plasPath.getPath()))
				{
					Toast.makeText(MainActivity.this, "不能复制到本目录", Toast.LENGTH_LONG).show();
				} else
				{
					fileAction.copyDir(file, plasPath);
					getFilesDir(mStrPath);
					MyDBAdapterAction("复制文件" + file + "到" + mStrPath);
				}
				mLinerBtn.setVisibility(View.INVISIBLE);
			}
		});
	}

	// 移动文件
	private void moveFile(final File file)
	{
		fileAction = new MyFileAction();
		mMove.setText("移动到这个目录");

		mMove.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				File plasPath = new File(mStrPath + "/" + file.getName());

				if (plasPath.exists() && !file.getPath().equals(plasPath.getPath()))
				{
					Toast.makeText(MainActivity.this, "文件名重复，请重新操作", Toast.LENGTH_LONG).show();
					mLinerBtn.setVisibility(View.INVISIBLE);
				} else if (file.getPath().equals(plasPath.getPath()))
				{
					Toast.makeText(MainActivity.this, "不能移动到本目录", Toast.LENGTH_LONG).show();
					mLinerBtn.setVisibility(View.INVISIBLE);
				} else
				{
					if (file.isDirectory())
					{
						String[] cutFile = file.getPath().split("/");
						String[] cutPlasPath = plasPath.getPath().split("/");
						String eCutFile = file.getPath().substring(0, 8) + cutFile[2];
						String pCutPlasPath = plasPath.getPath().substring(0, 8) + cutPlasPath[2];
						/*
						 * 判断是否在同一个文件夹中进行文件夹移动， 如果是将使用中间文件夹作为过度，文件夹移动，否则将出现错误
						 */
						if (eCutFile.equals(pCutPlasPath))
						{
							int fl = file.getPath().length();
							int pl = plasPath.getPath().length();
							// 新建中间文件，存放要移动的文件夹
							File newplasPath = new File("/mnt/sdcard/linshidewenjianjiawode");
							newplasPath.mkdir();
							File nPlasPath = new File(newplasPath.getPath() + "/" + file.getName());
							// 把要移动的文件夹复制到中间文件夹中
							fileAction.copyDir(file, nPlasPath);
							// 从中间文件夹中复制目标文件夹到所指定路径，到此完成了目标文件夹复制到指定路径中
							fileAction.copyDir(nPlasPath, plasPath);
							// 删除中间文件夹
							fileAction.deleteDir(newplasPath);
							/*
							 * 如果源文件夹路径比指定路径短，也就是说此时的源文件夹路径是指定路径的父路径
							 * 此时删除源文件夹，将连移动文件夹一同删除
							 */
							if (fl < pl)
							{
								// 删除源文件夹
								fileAction.deleteDir(file);
								Log.i("TAG", "daozhilile ");
							}
							getFilesDir(mStrPath);
							Log.i("TAG", fl + mStrPath + "daozhilile " + pl);
						} else
						{
							fileAction.copyDir(file, plasPath);
							getFilesDir(mStrPath);
							MyDBAdapterAction("移动文件" + file + "到" + mStrPath);
						}
					} else
					{
						fileAction.copyDir(file, plasPath);
						file.delete();
						getFilesDir(plasPath.getParent());
						MyDBAdapterAction("移动文件" + file + "到" + plasPath.getParent());
					}
					mLinerBtn.setVisibility(View.INVISIBLE);
				}
			}
		});

	}

	// 删除文件夹
	private void deleteDir(final File file)
	{
		fileAction = new MyFileAction();

		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("警告！");
		builder.setMessage("是否确定删除？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				File delfile = file;

				if (file.isDirectory())
				{
					fileAction.deleteDir(delfile);
					MyDBAdapterAction("删除文件夹" + delfile);
				} else
				{
					file.delete();
					MyDBAdapterAction("删除文件夹" + file);

				}
				getFilesDir(mStrPath);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
			}
		});
		builder.show();
	}

	// 创建文件夹
	private void makeDir(File file)
	{
		String dirname = String.valueOf("新建文件夹" + mCount);
		String dirPath = file.getPath().toString() + "/" + dirname;
		File dirFile = new File(dirPath);
		if (dirFile.exists())
		{
			mCount = mCount + 1;
			makeDir(file);
		} else
		{
			mCount = 0;
			dirFile.mkdirs();
			MyDBAdapterAction("新建文件夹：" + dirname);
			getFilesDir(mStrPath);
		}
	}

	// 打开文件
	protected void openFile(File file)
	{
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type = getMIMEType(file);
		intent.setDataAndType(Uri.fromFile(file), type);
		startActivity(intent);
	}

	private String getMIMEType(File file)
	{
		String type = "";
		String fName = file.getName();
		String endName = (String) fName.subSequence(fName.lastIndexOf(".") + 1, fName.length());
		if (!endName.equals("apk"))
		{
			if (endName.equals("mp3") || endName.equals("m4a") || endName.equals("mid") || endName.equals("xmf") || endName.equals("ogg") || endName.equals("wav"))
			{
				type = "audio";
			} else if (endName.equals("3gp") || endName.equals("mp4"))
			{
				type = "video";
			} else if (endName.equals("jpg") || endName.equals("gif") || endName.equals("png") || endName.equals("jpeng") || endName.equals("bmp"))
			{
				type = "image";
			} else
			{
				type = "*";
			}
			type += "/*";
		} else
		{
			type = "application/vnd.android.package-archive";
		}

		return type;
	}

	@Override
	protected void onPause()
	{
		SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE | MODE_MULTI_PROCESS);
		Editor editor = preferences.edit();
		editor.putString("mstrPath", mStrPath);
		editor.commit();

		super.onPause();
	}

}
