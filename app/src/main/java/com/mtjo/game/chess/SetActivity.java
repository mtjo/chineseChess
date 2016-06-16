package com.mtjo.game.chess;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.mtjo.game.service.GameService;
import com.mtjo.game.util.PictureContrast;
import com.mtjo.game.util.RootShellCmd;
import com.mtjo.game.util.ScreenShot;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


public class SetActivity extends Activity {
    public RadioGroup player,level;
    public RadioButton red, black, both, beginner, amateur, expert;
    public byte[] config = new byte[ChessGame.RS_DATA_LEN];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        player =(RadioGroup)findViewById(R.id.radiogroup_player);
        red = (RadioButton)findViewById(R.id.radio_player_red);
        black = (RadioButton)findViewById(R.id.radio_player_black);
        both = (RadioButton)findViewById(R.id.radio_player_both);
        player.setOnCheckedChangeListener(playermChangeRadio);

        level = (RadioGroup)findViewById(R.id.radiogroup_level);
        beginner = (RadioButton)findViewById(R.id.radio_level_beginner);
        amateur = (RadioButton)findViewById(R.id.radio_level_amateur);
        expert = (RadioButton)findViewById(R.id.radio_level_expert);
        level.setOnCheckedChangeListener(levelmChangeRadio);
    }

    public void newstart(View view) {
        Intent intent =new Intent();
        intent.putExtra("config",config);
        intent.setClass(SetActivity.this,ChessGame.class);
        startActivity(intent);
    }

    private RadioGroup.OnCheckedChangeListener playermChangeRadio = new RadioGroup.OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // TODO Auto-generated method stub
            if (checkedId == red.getId()) {
                config[16] = 0;
                Log.i("RadioGroup", "onCheckedChanged: "+red.getText());
                level.setVisibility(View.VISIBLE);
            } else if (checkedId == black.getId()) {
                config[16] = 1;
                level.setVisibility(View.VISIBLE);
                Log.i("RadioGroup", "onCheckedChanged: "+black.getText());
            } else if (checkedId == both.getId()) {
                level.setVisibility(View.GONE);
                config[16] = 2;
                Log.i("RadioGroup", "onCheckedChanged: "+both.getText());
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener levelmChangeRadio = new RadioGroup.OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // TODO Auto-generated method stub
            if (checkedId == beginner.getId()) {
                // 把mRadio1的内容传到mTextView1
                config[17] = 0;
                Log.i("RadioGroup", "onCheckedChanged: "+beginner.getText());
            } else if (checkedId == amateur.getId()) {
                config[17] = 1;
                // 把mRadio2的内容传到mTextView1
                Log.i("RadioGroup", "onCheckedChanged: "+amateur.getText());
            } else if (checkedId == expert.getId()) {
                config[17] = 2;
                // 把mRadio2的内容传到mTextView1
                Log.i("RadioGroup", "onCheckedChanged: "+expert.getText());
            }
        }
    };

    public void testscreen(View view) {
        Bitmap bitmap, bitmap1 ,retbitmap ,retbitmap2;

        bitmap=ScreenShot.takeScreenShot(SetActivity.this);


        Bitmap bm = BitmapFactory.decodeFile("/sdcard/2.png");
        Bitmap bm2 = BitmapFactory.decodeFile("/sdcard/1.png");

        ImageView imageView;

        /*try {
            Thread.currentThread().sleep(2000);//阻断2秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        bitmap1=ScreenShot.takeScreenShot(SetActivity.this);
        String ret = PictureContrast.similarity(bm,bm2);
        retbitmap = PictureContrast.bitmapMinus(bm,bm2);
        //int [][] map = PictureContrast.map(retbitmap);
        Log.i("ret", "testscreen: "+ret);
        imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setImageBitmap(retbitmap);
        /*for (int a = 0 ; a<10; a++)
            System.out.println( Arrays.toString(map[a]));*/
//        execShellCmd("getevent -p");
//        execShellCmd("sendevent /dev/input/event0 1 158 1");
//        execShellCmd("sendevent /dev/input/event0 1 158 0");
//        execShellCmd("input keyevent 4");//home
//        execShellCmd("input text  'helloworld!' ");
//        execShellCmd("input tap 168 252");
//        execShellCmd("input swipe 100 250 200 280");
        RootShellCmd os = new RootShellCmd();
        os.execString("input tap 168 252");
        //os.simulateTouch(200,200);
        //os.execString("input swipe 100 250 200 280");
        //os.simulateKey(4);

    }


    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            Bitmap bitmap ;
            bitmap= null;
            ImageView imageView;
            imageView = (ImageView)findViewById(R.id.imageView);
            imageView.setImageBitmap(bitmap);

            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void execShellCmd(String cmd) {

        try {
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(
                    outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    public void startService (View view) {
        Intent startIntent = new Intent(this, GameService.class);
        startService(startIntent);

    }
    public void stopService (View view) {
        Intent stopIntent = new Intent(this, GameService.class);
        stopService(stopIntent);

    }
}
