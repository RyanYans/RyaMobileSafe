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
public class SettingItemView extends RelativeLayout {

    private CheckBox cb_ischeck;
    private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.rya.ryamobilesafe";
    private String item_title;
    private String item_des;
    private TextView tv_setting_title;
    private TextView tv_setting_des;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //自定义组合控件 -- 打气筒加载xml的布局(组合控件)
        View.inflate(context, R.layout.view_item_setting, this);

        tv_setting_title = (TextView) this.findViewById(R.id.tv_setting_title);
        tv_setting_des = (TextView) this.findViewById(R.id.tv_setting_des);
        cb_ischeck = (CheckBox) this.findViewById(R.id.cb_ischeck);

        initArrts(attrs);

    }

    private void initArrts(AttributeSet attrs) {
        item_title = attrs.getAttributeValue(NAMESPACE, "item_title");
        item_des = attrs.getAttributeValue(NAMESPACE, "item_des");

        tv_setting_title.setText(item_title);
        tv_setting_des.setText(item_des);
    }

    /**
     * @return 返回当前条目的勾选状态
     */
    public boolean isCheck() {
        return cb_ischeck.isChecked();
    }

    /**
     * @param isCheck 设置勾选状态
     */
    public void setCheck(boolean isCheck) {
        cb_ischeck.setChecked(isCheck);
    }

}
