package com.ms.android.opencvdemo;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

/**
 * @author shenjb@ms.com
 * @since 2017/8/29
 */
public class ImageGrayActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gray);

        NDKUtils ndk = new NDKUtils();

        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(
                R.mipmap.pic_test)).getBitmap();
        int w = bitmap.getWidth(), h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int [] resultPixes=ndk.n_gray(pix,w,h);
        Bitmap result = Bitmap.createBitmap(w,h, Bitmap.Config.RGB_565);
        result.setPixels(resultPixes, 0, w, 0, 0,w, h);

        ImageView img = (ImageView)findViewById(R.id.img2);
        img.setImageBitmap(result);

    }
}
