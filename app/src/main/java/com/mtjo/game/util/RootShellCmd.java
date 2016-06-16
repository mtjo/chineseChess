package com.mtjo.game.util;

import java.io.DataOutputStream;
import java.io.OutputStream;

/**
 * Created by mtjo on 16-6-16.
 */
public class RootShellCmd {
    private OutputStream os;

    /**
     * 执行shell指令 * * @param cmd * 指令
     */



    public final void exec(String cmd) {
        try {
            if (os == null) {
                os = Runtime.getRuntime().exec("su").getOutputStream();
            }
            os.write(cmd.getBytes());
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 后台模拟全局按键 * * @param keyCode * 键值
     */
    public final void simulateKey(int keyCode) {
        exec("input keyevent " + keyCode + "\n");
    }
    public final void simulateTouch(int x, int y) {

        exec("sendevent /dev/input/event0 3 0 "+x+"\n");
        exec("sendevent /dev/input/event0 3 1 "+y+"\n");
        exec("sendevent /dev/input/event0 1 330 1\n");
        exec("sendevent /dev/input/event0 0 0 0\n");
        exec("sendevent /dev/input/event0 1 330 0\n");
        exec("sendevent /dev/input/event0 0 0 0\n");

        exec("sendevent /dev/input/event0 \n");
    }
    public final void execString(String string) {
        exec(string+"\n");
    }

}
