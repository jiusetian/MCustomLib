package com.lib_ui.widget;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by LXR ON 2018/9/21.
 * 1.
 */
public class PieView extends View{

    //画笔
    private Paint paint=new Paint();
    

    public PieView(Context context){
        this(context,null);
    }

    public PieView(Context context, AttributeSet attrs){
        super(context,attrs);
    }


}
