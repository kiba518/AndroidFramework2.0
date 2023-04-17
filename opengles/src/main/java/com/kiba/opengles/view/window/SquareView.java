package com.kiba.opengles.view.window;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.kiba.opengles.renderer.window.SquareRenderer;
import com.kiba.opengles.utils.OpenGLUtil;
import com.kiba.opengles.view.BaseGLView;
/**
 * @author chends create on 2019/12/10.
 */
public class SquareView extends BaseGLView {

    public SquareView(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        setEGLContextFactory(OpenGLUtil.createFactory());
        setRenderer(new SquareRenderer());
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
