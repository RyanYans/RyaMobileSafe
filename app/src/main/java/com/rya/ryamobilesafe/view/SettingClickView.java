package com.rya.ryamobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 * 自定义组合控件
 */
public class SettingClickView extends RelativeLayout {

    private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.rya.ryamobilesafe";
    private TextView tv_setting_title;
    private TextView tv_setting_des;


    public SettingClickView(Context context) {
        this(context, null);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //自定义组合控件 -- 打气筒加载xml的布局(组合控件)
        View.inflate(context, R.layout.view_click_setting, this);

        tv_setting_title = (TextView) this.findViewById(R.id.tv_setting_title);
        tv_setting_des = (TextView) this.findViewById(R.id.tv_setting_des);

        initArrts(attrs);

    }

    private void initArrts(AttributeSet attrs) {
        String item_title = attrs.getAttributeValue(NAMESPACE, "item_title");
        tv_setting_title.setText(item_title);
    }

    public void setDesc(String desc) {
        tv_setting_des.setText(desc);
    }


}
