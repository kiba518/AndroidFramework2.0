package com.kiba.opengles.view.window;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.kiba.opengles.renderer.window.CubeRenderer;
import com.kiba.opengles.utils.OpenGLUtil;
import com.kiba.opengles.view.BaseGLView;

/**
 * 立方体
 * @author chends create on 2019/12/12.
 */
public class CubeView extends BaseGLView {

    public CubeView(Context context) {
        super(context);
    }

    public CubeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        setEGLContextFactory(OpenGLUtil.createFactory());
        setRenderer(new CubeRenderer());
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
}
