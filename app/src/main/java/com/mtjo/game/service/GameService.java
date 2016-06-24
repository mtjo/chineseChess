package com.mtjo.game.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.mtjo.game.chess.Position;
import com.mtjo.game.chess.Search;
import com.mtjo.game.util.PictureContrast;
import com.mtjo.game.util.RootShellCmd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class GameService extends Service {
    public static final String TAG = "GameService";
    public int screen = 0;
    public RootShellCmd os = new RootShellCmd();
    private double semblance;
    private final String filePath="/sdcard/";
    private final String  fileName="tmp_chress_";



    private static final int PHASE_LOADING = 0;
    private static final int PHASE_WAITING = 1;
    private static final int PHASE_THINKING = 2;
    private static final int PHASE_EXITTING = 3;

    private static final int COMPUTER_BLACK = 0;
    private static final int COMPUTER_RED = 1;
    private static final int COMPUTER_NONE = 2;

    private static final int RESP_HUMAN_SINGLE = -2;
    private static final int RESP_HUMAN_BOTH = -1;
    private static final int RESP_CLICK = 0;
    private static final int RESP_ILLEGAL = 1;
    private static final int RESP_MOVE = 2;
    private static final int RESP_MOVE2 = 3;
    private static final int RESP_CAPTURE = 4;
    private static final int RESP_CAPTURE2 = 5;
    private static final int RESP_CHECK = 6;
    private static final int RESP_CHECK2 = 7;
    private static final int RESP_WIN = 8;
    private static final int RESP_DRAW = 9;
    private static final int RESP_LOSS = 10;

    private Bitmap imgBackground, imgXQWLight/*,imgThinking*/;
    private static final String[] IMAGE_NAME = { null, null, null, null, null,
            null, null, null, "rk", "ra", "rb", "rn", "rr", "rc", "rp", null,
            "bk", "ba", "bb", "bn", "br", "bc", "bp", null, };
    private int widthBackground, heightBackground;

    static final int RS_DATA_LEN = 512;

    byte[] rsData = new byte[RS_DATA_LEN];

    byte[] retractData = new byte[RS_DATA_LEN];

    private Position pos = new Position();
    private Search search = new Search(pos, 12);
    private String message;
    private int cursorX, cursorY;
    private int sqSelected, mvLast;
    // Assume FullScreenMode = false
    private int normalWidth = 720;
    private int normalHeight = 1280;

    volatile int phase = PHASE_LOADING;

    private boolean init = false;
    private Bitmap imgBoard, imgSelected, imgSelected2, imgCursor, imgCursor2;
    private Bitmap[] imgPieces = new Bitmap[24];
    private int squareSize, width, height, left, right, top, bottom ;
    private Context context;
    private Paint paint = new Paint();

    int moveMode, level;


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                //todo something....
                String screenshot = "screencap -p "+filePath+fileName + screen + ".png";
                os.execString(screenshot);
                Log.i(TAG, "handleMessage:shell run " + screenshot);
                try {
                    //Thread.sleep(2000);
                    Thread.currentThread().sleep(5000);//阻断
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (screen >= 1) {
                    try{
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
                    }catch (Exception e) {
                        Log.i(TAG, "handleMessage: "+e);
                    }
                    clickSquare();


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

    private void clickSquare() {

        int sq = Position.COORD_XY(cursorX + Position.FILE_LEFT, cursorY
                + Position.RANK_TOP);
        Log.i(TAG, "clickSquare:sq: "+sq);
        if (moveMode == COMPUTER_RED) {
            sq = Position.SQUARE_FLIP(sq);
        }
        int pc = pos.squares[sq];
        if ((pc & Position.SIDE_TAG(pos.sdPlayer)) != 0) {

            mvLast = 0;
            sqSelected = sq;
        } else {
            if (sqSelected > 0 && addMove (Position.MOVE(sqSelected, sq)) && !responseMove()) {
                rsData[0] = 0;
                phase = PHASE_EXITTING;
            }
        }
		RootShellCmd os = new RootShellCmd();
		os.execString("input tap "+cursorX+" "+cursorY);
        if(getResult()){
            Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
        }
    }



    /** Player Move Result */
    private boolean getResult() {
        return getResult(moveMode == COMPUTER_NONE ? RESP_HUMAN_BOTH
                : RESP_HUMAN_SINGLE);
    }

    /** Computer Move Result */
    private boolean getResult(int response) {
        if (pos.isMate()) {
            message = (response < 0 ? "祝贺你取得胜利！" : "请再接再厉！");
            return true;
        }
        int vlRep = pos.repStatus(3);
        if (vlRep > 0) {
            vlRep = (response < 0 ? pos.repValue(vlRep) : -pos.repValue(vlRep));
            message = (vlRep > Position.WIN_VALUE ? "长打作负，请不要气馁！"
                    : vlRep < -Position.WIN_VALUE ? "电脑长打作负，祝贺你取得胜利！"
                    : "双方不变作和，辛苦了！");
            return true;
        }
        if (pos.moveNum > 100) {
            message = "超过自然限着作和，辛苦了！";
            return true;
        }
        if (response != RESP_HUMAN_SINGLE) {
            if (response >= 0) {
            }
            // Backup Retract Status
            System.arraycopy(rsData, 0, retractData, 0, RS_DATA_LEN);
            // Backup Record-Score Data
            rsData[0] = (byte) (pos.sdPlayer + 1);
            System.arraycopy(pos.squares, 0, rsData, 256, 256);
        }
        return false;
    }

    void load(byte rsData[], final int handicap, final int moveMode, int level) {

        this.moveMode = moveMode;
        this.level = level;
        this.rsData = rsData;
        cursorX = cursorY = 7;
        sqSelected = mvLast = 0;

        System.out.println(Arrays.toString(rsData));

        if (rsData[0] == 0) {
            pos.fromFen(Position.STARTUP_FEN[handicap]);
        } else {
            // Restore Record-Score Data
            pos.clearBoard();
            for (int sq = 0; sq < 256; sq++) {
                int pc = rsData[sq + 256];
                if (pc > 0) {
                    pos.addPiece(sq, pc);
                }
            }
            if (rsData[0] == 2) {
                pos.changeSide();
            }
            pos.setIrrev();
        }
        // Backup Retract Status
        System.arraycopy(rsData, 0, retractData, 0, RS_DATA_LEN);
        // Call "responseMove()" if Computer Moves First
        phase = PHASE_LOADING;
        /*if (pos.sdPlayer == 0 ? moveMode == COMPUTER_RED
                : moveMode == COMPUTER_BLACK) new Thread() {
            public void run() {
                while (phase == PHASE_LOADING) {
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        // Ignored
                    }
                }
                responseMove();
            }
        }.start();*/
    }

    boolean responseMove() {
        if (getResult()) {
            return false;
        }
        if (moveMode == COMPUTER_NONE) {
            return true;
        }
        phase = PHASE_THINKING;
        if (moveMode != COMPUTER_RED) {
          //  invalidate();
        }

        mvLast = search.searchMain(1000 << (level << 1));
        pos.makeMove(mvLast);
        int response = pos.inCheck() ? RESP_CHECK2
                : pos.captured() ? RESP_CAPTURE2 : RESP_MOVE2;
        if (pos.captured()) {
            pos.setIrrev();
        }
        phase = PHASE_WAITING;
        if (moveMode != COMPUTER_RED) {
          //  invalidate();
        }

        return !getResult(response);
    }
    private boolean addMove(int mv) {
        if (pos.legalMove(mv)) {
            if (pos.makeMove(mv)) {
                if (pos.captured()) {
                    pos.setIrrev();
                }
                sqSelected = 0;
                mvLast = mv;
                return true;
            }
        }
        return false;
    }

}
