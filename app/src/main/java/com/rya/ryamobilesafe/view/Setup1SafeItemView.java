package com.rya.ryamobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class Setup1SafeItemView extends LinearLayout {

    public static final String NAMESPACE = "http://schemas.android.com/apk/res/com.rya.ryamobilesafe";
    private String item_des;

    public Setup1SafeItemView(Context context) {
        this(context,null);
    }

    public Setup1SafeItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Setup1SafeItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View.inflate(context, R.layout.view_item_safe_setup1, this);

        TextView tv_safe_setup1 = (TextView) this.findViewById(R.id.tv_safe_setup1);

        initAttrs(attrs);

        tv_safe_setup1.setText(item_des);
    }

    private void initAttrs(AttributeSet attrs) {
        item_des = attrs.getAttributeValue(NAMESPACE, "safe_setup1_des");
    }
}
