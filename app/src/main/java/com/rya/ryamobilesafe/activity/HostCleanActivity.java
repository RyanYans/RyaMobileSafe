package com.rya.ryamobilesafe.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.rya.ryamobilesafe.R;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class HostCleanActivity extends TabActivity {

    private TabHost mHost;
    private TabWidget mWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_cache_clean);

        mHost = this.getTabHost();
        mWidget = mHost.getTabWidget();

        // 设置tab1 条目1的选项卡View
        View view1 = View.inflate(getApplicationContext(), R.layout.item_tabs, null);
        TextView tv_cache_tab1 = (TextView) view1.findViewById(R.id.tv_cache_tab);
        tv_cache_tab1.setText("缓存清理");
        // 设置tab2 条目2的选项卡View
        View view2 = View.inflate(getApplicationContext(), R.layout.item_tabs, null);
        TextView tv_cache_tab2 = (TextView) view2.findViewById(R.id.tv_cache_tab);
        tv_cache_tab2.setText("SD卡清理");

        //生成选项卡  -- host-> newTabspec-> setIndicator
        TabHost.TabSpec tab1 = mHost.newTabSpec("cache_clean").setIndicator(view1);
        TabHost.TabSpec tab2 = mHost.newTabSpec("sd_cache_clean").setIndicator(view2);

        // 选项卡点击后的跳转操作
        tab1.setContent(new Intent(getApplicationContext(), CacheCleanActivity.class));
        tab2.setContent(new Intent(getApplicationContext(), SDCacheCleanActivity.class));

        //把两个选项卡添加进Host中进行维护
        mHost.addTab(tab1);
        mHost.addTab(tab2);

        //设置样式 --> 选项被选中 or 未选中
        mWidget.getChildAt(0).setBackgroundResource(R.drawable.selector_cache_item);
        mWidget.getChildAt(1).setBackgroundResource(R.drawable.selector_cache_item);
    }
}
