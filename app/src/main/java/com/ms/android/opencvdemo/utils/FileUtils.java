package com.ms.android.opencvdemo.utils;

import android.content.Context;

import com.ms.android.opencvdemo.NDKUtils;
import com.ms.android.opencvdemo.R;

import java.io.File;
import java.io.FileOutputStream;

import java.io.InputStream;

/**
 * @author shenjb@ms.com
 * @since 2017/8/29
 */
public class FileUtils {

    public  static String getFaceFile(Context context){
        String strFile=null;
        InputStream is=null;
        FileOutputStream os=null;
        try {
            File mCascadeFile = new File("/storage/emulated/0/DCIM/", "haarcascade_frontalface_alt.xml");
            if (!mCascadeFile.exists()){
                // 加载人脸检测模式文件
                is = context.getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
                os = new FileOutputStream(mCascadeFile);
                byte[] buffer = new byte[4096];
                int byteesRead;
                while ((byteesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, byteesRead);
                }

            }
            strFile=mCascadeFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            strFile=null;
        }finally {
            try {
                if (is!=null){
                    is.close();
                }
                if (is!=null){
                    os.close();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return strFile;
    }
}
