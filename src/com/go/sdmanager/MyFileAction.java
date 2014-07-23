package com.go.sdmanager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyFileAction
{
	// 文件复制
	public void copyFile(File file, File plasPath)
	{
		try
		{
			FileInputStream fileInput = new FileInputStream(file);
			BufferedInputStream inBuff = new BufferedInputStream(fileInput);
			FileOutputStream fileOutput = new FileOutputStream(plasPath);
			BufferedOutputStream outBuff = new BufferedOutputStream(fileOutput);
			byte[] b = new byte[1025 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1)
			{
				outBuff.write(b, 0, len);
			}
			outBuff.flush();
			inBuff.close();
			outBuff.close();
			fileOutput.close();
			fileInput.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	// 文件夹复制，包括文件夹里面的文件复制
	public void copyDir(File file, File plasPath)
	{
		plasPath.mkdir();
		File[] f = file.listFiles();
		for (File newFile : f)
		{
			if (newFile.isDirectory())
			{
				File files = new File(file.getPath() + "/" + newFile.getName());
				File plasPaths = new File(plasPath.getPath() + "/" + newFile.getName());
				copyDir(files, plasPaths);
			} else
			{
				String newPath = plasPath.getPath() + "/" + newFile.getName();
				File newPlasFile = new File(newPath);
				copyFile(newFile, newPlasFile);
			}
		}

	}

	// 文件夹删除，包括文件夹里面的文件
	public void deleteDir(File delFile)
	{
		File[] f = delFile.listFiles();// 取得文件夹里面的路径
		File upFile = delFile;
		
		if (f.length == 0 && upFile.getParent().equals("/mnt/sdcard"))
		{
			delFile.delete();
		} else if (f.length == 0 && !upFile.getParent().equals("/mnt/sdcard"))
		{
			delFile.delete();
		} else
		{
			for (File nFile : f)
			{
				if (nFile.isDirectory())
				{
					deleteDir(nFile);
				} else
				{
					nFile.delete();
				}
			}
			delFile.delete();
		}
		delFile.delete();
	}

}
