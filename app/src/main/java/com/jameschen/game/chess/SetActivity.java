package com.jameschen.game.chess;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SetActivity extends Activity {
    public RadioGroup radioGroup;
    public RadioButton red,black,both;
    public byte[] config = new byte[ChessGame.RS_DATA_LEN];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        radioGroup =(RadioGroup)findViewById(R.id.red_or_black);
        red = (RadioButton)findViewById(R.id.radio_red);
        black = (RadioButton)findViewById(R.id.radio_black);
        both = (RadioButton)findViewById(R.id.radio_both);
        radioGroup.setOnCheckedChangeListener(mChangeRadio);
    }

    public void newstart(View view) {
        Intent intent =new Intent();
        intent.putExtra("config",config);
        intent.setClass(SetActivity.this,ChessGame.class);
        startActivity(intent);
    }

    private RadioGroup.OnCheckedChangeListener mChangeRadio = new RadioGroup.OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // TODO Auto-generated method stub
            if (checkedId == red.getId()) {
                // 把mRadio1的内容传到mTextView1
                config[16] = 0;
                Log.i("RadioGroup", "onCheckedChanged: "+red.getText());
            } else if (checkedId == black.getId()) {
                config[16] = 1;
                // 把mRadio2的内容传到mTextView1
                Log.i("RadioGroup", "onCheckedChanged: "+black.getText());
            } else if (checkedId == both.getId()) {
                config[16] = 2;
                // 把mRadio2的内容传到mTextView1
                Log.i("RadioGroup", "onCheckedChanged: "+both.getText());
            }
        }
    };
}
