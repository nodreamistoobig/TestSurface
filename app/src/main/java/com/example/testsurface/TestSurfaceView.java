package com.example.testsurface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.net.CookieHandler;

public class TestSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    DrawThread drawThread;
    Paint paint = new Paint();

    public TestSurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }


    public class DrawThread extends Thread {
        private SurfaceHolder surfaceHolder;
        float x = 0,y = 0;
        float radius = 0;
        boolean drawing = false;

        private volatile boolean running = true;//флаг для остановки потока
        {
            paint.setColor(Color.BLUE);
        }

        public DrawThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }

        public void requestStop() {
            running = false;
        }

        public void setPoints(float x, float y){
            this.x=x;
            this.y=y;
            radius=0;
            drawing = true;
        }

        @Override
        public void run() {
            super.run();
            Paint circle = new Paint();
            circle.setColor(Color.YELLOW);
            while (running) {
                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    try {
                        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
                        if (drawing){
                            radius+=5;
                            canvas.drawCircle(x,y,radius,circle);
                            Thread.sleep(1000);

                        }

                    } catch (InterruptedException e) {
                    } finally {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        drawThread.setPoints((int)event.getX(),(int)event.getY());
        return false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new DrawThread(getHolder());
        drawThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        drawThread.requestStop();
        boolean retry = true;
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
                //
            }
        }
    }

}
