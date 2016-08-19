package com.rya.ryamobilesafe.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class SettingItemView extends RelativeLayout {

    private CheckBox cb_ischeck;
    private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.rya.ryamobilesafe";
    private String title;
    private String des;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View.inflate(context, R.layout.view_item_setting, this);

        TextView tv_setting_title = (TextView) this.findViewById(R.id.tv_setting_title);
        TextView tv_setting_des = (TextView) this.findViewById(R.id.tv_setting_des);
        cb_ischeck = (CheckBox) this.findViewById(R.id.cb_ischeck);

        initArrts(attrs);

        tv_setting_title.setText(title);
        tv_setting_des.setText(des);
    }

    private void initArrts(AttributeSet attrs) {
        for(int index = 0; index < attrs.getAttributeCount(); index++) {

            title = attrs.getAttributeValue(NAMESPACE, "item_title");
            des = attrs.getAttributeValue(NAMESPACE, "item_des");
        }
    }

    /**
     * @return 返回当前条目的勾选状态
     */
    public boolean isCheck() {
        return cb_ischeck.isChecked();
    }

    /**
     * @param isCheck   设置勾选状态
     */
    public void setCheck(boolean isCheck) {
        cb_ischeck.setChecked(isCheck);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
