package com.mtjo.game.util;

import java.text.DecimalFormat;
import java.util.Arrays;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Created by mtjo on 16-6-15.
 */





/**
 * Android图片对比(像素精准对比),速度较慢建议用多线程获取
 * @author xupp
 * @createData 2013-7-18
 */

public class PictureContrast {
    private static int t = 0;
    private static int f = 0;
    private Bitmap last_bamp;
    public static double similarity (Bitmap bm_one,Bitmap bm_two) {
        //保存图片所有像素个数的数组，图片宽×高
        int[] pixels_one = new int[bm_one.getWidth()*bm_one.getHeight()];
        int[] pixels_two = new int[bm_two.getWidth()*bm_two.getHeight()];
        //获取每个像素的RGB值
        bm_one.getPixels(pixels_one,0,bm_one.getWidth(),0,0,bm_one.getWidth(),bm_one.getHeight());
        bm_two.getPixels(pixels_two,0,bm_two.getWidth(),0,0,bm_two.getWidth(),bm_two.getHeight());
        //如果图片一个像素大于图片2的像素，就用像素少的作为循环条件。避免报错
        if (pixels_one.length >= pixels_two.length) {
            //对每一个像素的RGB值进行比较
            for(int i = 0; i < pixels_two.length; i++){
                int clr_one = pixels_one[i];
                int clr_two = pixels_two[i];
                //RGB值一样就加一（以便算百分比）
                if (clr_one == clr_two) {
                    t++;
                }else {
                    f++;
                }
            }
        }else {
            for(int i = 0; i < pixels_one.length; i++){
                int clr_one = pixels_one[i];
                int clr_two = pixels_two[i];
                if (clr_one == clr_two) {
                    t++;
                }else {
                    f++;
                }
            }

        }

        return myPercent(t,t+f);

    }
    /**
     * 百分比的计算
     * @author xupp
     * @param y(母子)
     * @param z（分子）
     * @return 百分比（保留小数点后两位）
     */
    public static double myPercent(int y,int z)
    {
        String baifenbi="";//接受百分比的值
        double baiy=y*1.0;
        double baiz=z*1.0;
        double fen=baiy/baiz;
        DecimalFormat df1 = new DecimalFormat("00.00%"); //##.00%   百分比格式，后面不足2位的用0补齐
        baifenbi= df1.format(fen);
        double semblance = baiy/baiz;
        return semblance;
    }

    //图片相减
    public static Bitmap bitmapMinus (Bitmap bm_one,Bitmap bm_two) {

        //保存图片所有像素个数的数组，图片宽×高
        bm_one = ImageCrop(bm_one);
        bm_two = ImageCrop(bm_two);




        int[] pixels_one = new int[bm_one.getWidth()*bm_one.getHeight()];
        int[] pixels_two = new int[bm_two.getWidth()*bm_two.getHeight()];
        int[] pixels_minus=new int[bm_one.getWidth()*bm_one.getHeight()];
        int picw=bm_one.getWidth();
        int pich=bm_one.getHeight();
        bm_one.getPixels(pixels_one,0,bm_one.getWidth(),0,0,bm_one.getWidth(),bm_one.getHeight());
        bm_two.getPixels(pixels_two,0,bm_two.getWidth(),0,0,bm_two.getWidth(),bm_two.getHeight());
        int[][] map  = new int[10][9];

        for (int y = 0; y < pich; y++) {
            //对每一个像素的RGB值进行比较
             for (int x = 0; x < picw; x++) {

                int index = y * picw + x;
                int clr_one = pixels_one[index];
                int clr_two = pixels_two[index];
                int r = ((pixels_minus[index] >> 16) & 0xff) | 0xff;
                int g = ((pixels_minus[index] >> 8) & 0xff) | 0xff;
                int b = (pixels_minus[index] & 0xff) | 0xff;


                if (clr_one != clr_two) {
                    pixels_minus[index] = 0xff000000 | (r << 16) | (g << 8) | b;
                } else {
                    pixels_minus[index] = clr_one - clr_two;
                }

                 //边框显示
                /*if (x % (picw/ 9) == 0 || y % (pich / 10) == 0 ) {
                    pixels_minus[index] = 0xff000000 | (r << 16) | (g << 8) | b;
                }*/

            }

        }
        Bitmap newbitmap = Bitmap.createBitmap(picw, pich, Bitmap.Config.ARGB_8888);
        newbitmap.setPixels(pixels_minus, 0, picw, 0, 0, picw, pich);

        return  newbitmap;

    }

    /**
     * 裁剪棋盘大小
     *MTJO
     * */
    public static Bitmap ImageCrop(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();


        int newh = w*10/9;// 裁切后所取的正方形区域边长

        int retX =  0;//基于原图，取正方形左上角x坐标
        int retY = (h - newh) / 2;

        //下面这句是关键
        return Bitmap.createBitmap(bitmap, retX, retY, w, newh, null, false);
    }

    public static int [][] map (Bitmap bitmap) {

        //保存图片所有像素个数的数组，图片宽×高
        int[] pixels_one = new int[bitmap.getWidth()*bitmap.getHeight()];
        int[] pixels_minus=new int[bitmap.getWidth()*bitmap.getHeight()];

        int picw=bitmap.getWidth();
        int pich=bitmap.getHeight();
        int onew = picw/9;
        int oneh = pich/10;
        int[][] map  = new int[10][9];
        bitmap.getPixels(pixels_one,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());

        for (int row = 1; row <= 10; row++) {
            for (int column = 1; column <=9; column++) {
                for (int y = 0; y < pich; y++) {
                    //对每一个像素的RGB值进行比较
                    for (int x = 0; x < picw; x++) {
                        int index = y * picw + x;
                        int clr_one = pixels_one[index];
                        int r = ((pixels_minus[index] >> 16) & 0xff) | 0xff;
                        int g = ((pixels_minus[index] >> 8) & 0xff) | 0xff;
                        int b = (pixels_minus[index] & 0xff) | 0xff;
                        if (rangeInDefined(x, onew*column, onew*(column+1))&&
                                rangeInDefined(y, oneh*row, oneh*(row+1))&&
                                        clr_one != 0) {
                            pixels_minus[index] = 0xff000000 | (r << 16) | (g << 8) | b;
                            map[row][column]++;


                        }

                        if (clr_one != 0) {
                            pixels_minus[index] = 0xff000000 | (r << 16) | (g << 8) | b;
                        } else {
                            pixels_minus[index] = clr_one;
                        }


                    }

                }

            }
        }
        /*for (int a = 0 ; a<10; a++)
        System.out.println( Arrays.toString(map[a]));
        Bitmap newbitmap = Bitmap.createBitmap(picw, pich, Bitmap.Config.ARGB_8888);
        newbitmap.setPixels(pixels_minus, 0, picw, 0, 0, picw, pich);*/

        return  map;

    }
    public static boolean rangeInDefined(int current, int min, int max)
    {
        return Math.max(min, current) == Math.min(current, max);
    }
}