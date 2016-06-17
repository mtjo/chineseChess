package com.mtjo.game.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.mtjo.game.util.PictureContrast;
import com.mtjo.game.util.RootShellCmd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class GameService extends Service {
    public static final String TAG = "GameService";
    public int screen = 0;
    public RootShellCmd os = new RootShellCmd();
    private double semblance;
    private final String filePath="/sdcard/";
    private final String  fileName="tmp_chress_";
    private int squareSize, width, height, left, right, top, bottom ;


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                //todo something....
                String screenshot = "screencap -p "+filePath+fileName + screen + ".png";
                os.execString(screenshot);
                Log.i(TAG, "handleMessage:shell run " + screenshot);
                try {
                    Thread.currentThread().sleep(2000);//阻断
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (screen >= 1) {
                    semblance = 0.0;
                    Bitmap bm = BitmapFactory.decodeFile(filePath+fileName + screen + ".png");
                    Bitmap bm2 = BitmapFactory.decodeFile(filePath+fileName + (screen - 1) + ".png");
                    Bitmap bitmapMinus = PictureContrast.bitmapMinus(bm, bm2);

                    Log.e(TAG, "保存图片");
                    File f = new File("/sdcard/", screen + "_" + screen + ".png");
                    if (f.exists()) {
                        f.delete();
                    }
                    try {
                        FileOutputStream out = new FileOutputStream(f);
                        bitmapMinus.compress(Bitmap.CompressFormat.PNG, 90, out);
                        out.flush();
                        out.close();
                        Log.i(TAG, "已经保存");
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                screen++;
            }
        }
    };


    private Timer timer = new Timer(true);

    //任务
    private TimerTask task = new TimerTask() {
        public void run() {
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
        }
    };

    private void init(){
        width = 720;
        height = 1280;
        squareSize = Math.min(width / 9, height / 10);
    }
    public GameService() {
        init();


        //启动定时器
        timer.schedule(task, 0, 10 * 1000);
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
