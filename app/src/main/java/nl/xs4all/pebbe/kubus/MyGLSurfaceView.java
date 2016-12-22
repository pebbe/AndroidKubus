package nl.xs4all.pebbe.kubus;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;

    private float mDensity;

    public MyGLSurfaceView(Context context, Bundle savedInstanceState, float density) {
        super(context);

        mDensity = density;

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        mRenderer = new MyGLRenderer();
        mRenderer.restoreInstanceState(savedInstanceState);
        mRenderer.setContext(context);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
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

                float dx = x0 - mPreviousX;
                float dy = y0 - mPreviousY;
                if (Math.abs(dx) > Math.abs(dy)) {
                    mRenderer.setAngleH(
                            mRenderer.getAngleH() -
                                    dx * TOUCH_SCALE_FACTOR / mDensity);  // = 180.0f / 320

                } else {
                    float a = mRenderer.getAngleV() + dy * TOUCH_SCALE_FACTOR / mDensity;
                    if (a > 89.99f) {
                        a = 89.99f;
                    } else if (a < -89.99f) {
                        a = -89.99f;
                    }
                    mRenderer.setAngleV(a);
                }
                requestRender();
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

}
