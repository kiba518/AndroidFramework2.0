package com.kiba.opengles.view.window;

import android.content.Context;

import com.kiba.opengles.renderer.window.TriangleMatrixRenderer;
import com.kiba.opengles.utils.OpenGLUtil;
import com.kiba.opengles.view.BaseGLView;


/**
 * @author chends create on 2019/12/10.
 */
public class TriangleMatrixView extends BaseGLView {

    public TriangleMatrixView(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        setEGLContextFactory(OpenGLUtil.createFactory());
        setRenderer(new TriangleMatrixRenderer());
        //setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
}
