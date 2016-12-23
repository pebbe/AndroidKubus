package nl.xs4all.pebbe.kubus;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;

    private float mDensity;

    private long delay = 1000;

    private BlockingQueue queue;

    private boolean running;

    private Thread consumerThread =  new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    DelayObject object = (DelayObject) queue.take();
                    if (running) {
                        mRenderer.setAngleH(mRenderer.getAngleH() - object.dh * TOUCH_SCALE_FACTOR / mDensity);
                        float a = mRenderer.getAngleV() + object.dv * TOUCH_SCALE_FACTOR / mDensity;
                        if (a > 89.99f) {
                            a = 89.99f;
                        } else if (a < -89.99f) {
                            a = -89.99f;
                        }
                        mRenderer.setAngleV(a);
                        requestRender();
                    }
                } catch (InterruptedException e) {
                }
            }
        }
    });

    public MyGLSurfaceView(Context context, Bundle savedInstanceState, float density) {
        super(context);

        mDensity = density;

        queue = new DelayQueue();

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        mRenderer = new MyGLRenderer();
        mRenderer.restoreInstanceState(savedInstanceState);
        mRenderer.setContext(context);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        consumerThread.start();
    }

    public void saveInstanceState(Bundle outState) {
        mRenderer.saveInstanceState(outState);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;
    private boolean mSingle = false;
    private boolean mDouble = false;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        int pointerCount = e.getPointerCount();

        float x0 = e.getX();
        float y0 = e.getY();
        float x1 = 0;
        float y1 = 0;

        if (pointerCount == 2) {
            x1 = e.getX(1);
            y1 = e.getY(1);
        }

        boolean moved = (e.getActionMasked() == MotionEvent.ACTION_MOVE);

        if (pointerCount == 1) {

            if (moved && mSingle) {
                DelayObject object = new DelayObject(x0 - mPreviousX, y0 - mPreviousY, delay);
                try {
                    queue.put(object);
                } catch (Exception ex) {
                }
            }

            mPreviousX = x0;
            mPreviousY = y0;

            mSingle = true;
            mDouble = false;

        } else if (pointerCount == 2) {

            mSingle = false;
            mDouble = true;

        } else {
            mSingle = false;
            mDouble = false;
        }

        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        running = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        queue.clear();
        running = true;
    }
}
