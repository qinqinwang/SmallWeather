package com.weather.util;

import java.io.File;
import java.io.IOException;

import android.os.Environment;

public class SDPermission {


	public static boolean checkFsWritable() {
        // Create a temporary file to see whether a volume is really writeable.
        // It's important not to put it in the root directory which may have a
        // limit on the number of files.
        String directoryName = Environment.getExternalStorageDirectory().toString() + "/DCIM";
        File directory = new File(directoryName); // 在SD卡上创建DCIM目录
        if (!directory.isDirectory()) {   // 测试创建目录
            if (!directory.mkdirs()) {
                return false;
            }
        }
        File f = new File(directoryName, ".probe");
        try {
            // Remove stale file if any
            if (f.exists()) {
                f.delete();
            }
            if (!f.createNewFile()) {     //测试创建文件
                return false;
            }
            f.delete();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

}
