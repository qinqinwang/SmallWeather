package com.weather.util;

import java.util.UUID;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class TestUtils {


	
	public static String getFileName(String url) {
		String filename = url.substring(url.lastIndexOf('/') + 1);
		if (filename == null || "".equals(filename.trim())) {
			filename = UUID.randomUUID() + ".apk";
		}
		return filename;
	}

	
	
	public static String getUninatllApkInfo(Context context, String archiveFilePath) {
		String packageNameU ="" ;
		
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath,
				PackageManager.GET_ACTIVITIES);
		
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			packageNameU = appInfo.packageName;
		}
		return packageNameU;
	}
	

}
