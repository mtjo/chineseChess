package com.mtjo.game.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.mtjo.game.chess.R;
import com.mtjo.game.util.PictureContrast;
import com.mtjo.game.util.RootShellCmd;
import com.mtjo.game.util.ScreenShot;

import java.util.Timer;
import java.util.TimerTask;

public class GameService extends Service {
    public static final String TAG = "GameService";
    public int screen = 0;
    public RootShellCmd os = new RootShellCmd();


    private Handler handler  = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                //todo something....
                String screenshot = "shell screencap -p /sdcard/screenshot"+screen+".png";
                screen++;
                Bitmap bitmap, bitmap1 ,retbitmap ,retbitmap2;
                Bitmap bm = BitmapFactory.decodeFile("/sdcard/2.png");
                Bitmap bm2 = BitmapFactory.decodeFile("/sdcard/1.png");

                ImageView imageView;


                String ret = PictureContrast.similarity(bm,bm2);
                retbitmap = PictureContrast.bitmapMinus(bm,bm2);
                //int [][] map = PictureContrast.map(retbitmap);
                Log.i("ret", "testscreen: "+ret);

                RootShellCmd os = new RootShellCmd();
                os.execString(screenshot);
                //os.execString("input tap 168 100");
                //os.execString("input tap 200 400");
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
    public GameService() {


//启动定时器
        timer.schedule(task, 0, 10*1000);


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
