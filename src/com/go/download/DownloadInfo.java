package com.go.download;

/**
 * 创建一个下载信息的实体类
 */
public class DownloadInfo
{
	private int mThreadId; // 下载器id
	private int mStartPos; // 开始点
	private int mEndPos; // 结束点
	private int mCompeleteSize; // 完成度
	private String url; // 下载器网络标识

	public DownloadInfo(int threadId, int startPos, int endPos, int compeleteSize, String url)
	{
		this.mThreadId = threadId;
		this.mStartPos = startPos;
		this.mEndPos = endPos;
		this.mCompeleteSize = compeleteSize;
		this.url = url;
	}

	public DownloadInfo()
	{
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public int getThreadId()
	{
		return mThreadId;
	}

	public void setThreadId(int threadId)
	{
		this.mThreadId = threadId;
	}

	public int getStartPos()
	{
		return mStartPos;
	}

	public void setStartPos(int startPos)
	{
		this.mStartPos = startPos;
	}

	public int getEndPos()
	{
		return mEndPos;
	}

	public void setEndPos(int endPos)
	{
		this.mEndPos = endPos;
	}

	public int getCompeleteSize()
	{
		return mCompeleteSize;
	}

	public void setCompeleteSize(int compeleteSize)
	{
		this.mCompeleteSize = compeleteSize;
	}

	@Override
	public String toString()
	{
		return "DownloadInfo [threadId=" + mThreadId + ", startPos=" + mStartPos + ", endPos=" + mEndPos + ", compeleteSize=" + mCompeleteSize + "]";
	}
}