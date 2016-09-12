package com.rya.ryamobilesafe.db.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class AppInfo {
    private String name;
    private String packageName;
    private Drawable icon;
    private boolean isSystem;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(boolean system) {
        isSystem = system;
    }
}
