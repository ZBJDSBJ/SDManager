package com.go.download;

/**
 * 自定义的一个记载下载器详细信息的类
 */
public class LoadInfo
{
	public int mFileSize;// 文件大小
	private int mComplete;// 完成度
	private String mUrlstring;// 下载器标识

	public LoadInfo(int fileSize, int complete, String urlstring)
	{
		this.mFileSize = fileSize;
		this.mComplete = complete;
		this.mUrlstring = urlstring;
	}

	public LoadInfo()
	{
	}

	public int getFileSize()
	{
		return mFileSize;
	}

	public void setFileSize(int fileSize)
	{
		this.mFileSize = fileSize;
	}

	public int getComplete()
	{
		return mComplete;
	}

	public void setComplete(int complete)
	{
		this.mComplete = complete;
	}

	public String getUrlstring()
	{
		return mUrlstring;
	}

	public void setUrlstring(String urlstring)
	{
		this.mUrlstring = urlstring;
	}

	@Override
	public String toString()
	{
		return "LoadInfo [fileSize=" + mFileSize + ", complete=" + mComplete + ", urlstring=" + mUrlstring + "]";
	}
}