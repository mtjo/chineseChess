package com.mtjo.game.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;

import com.mtjo.game.chess.R;
import com.mtjo.game.util.PictureContrast;
import com.mtjo.game.util.RootShellCmd;
import com.mtjo.game.util.ScreenShot;

public class GameService extends Service {
    public static final String TAG = "GameService";
    public RootShellCmd os = new RootShellCmd();
    public GameService() {

        try {
            Thread.currentThread().sleep(2000);//阻断2秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Bitmap bitmap, bitmap1 ,retbitmap ,retbitmap2;
        Bitmap bm = BitmapFactory.decodeFile("/sdcard/2.png");
        Bitmap bm2 = BitmapFactory.decodeFile("/sdcard/1.png");

        ImageView imageView;


        String ret = PictureContrast.similarity(bm,bm2);
        retbitmap = PictureContrast.bitmapMinus(bm,bm2);
        //int [][] map = PictureContrast.map(retbitmap);
        Log.i("ret", "testscreen: "+ret);

        RootShellCmd os = new RootShellCmd();
        os.execString("input tap 168 100");
        try {
            Thread.currentThread().sleep(10000);//阻断2秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        os.execString("input tap 200 400");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() executed");
    }

}
