package com.rya.ryamobilesafe.db.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class ProcessInfo {
    private String name;
    private String packageName;
    private Drawable icon;
    private long memorySize;
    private boolean isSystem;
    private boolean ischeck = false;

    public boolean ischeck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

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

    public long getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(long memorySize) {
        this.memorySize = memorySize;
    }

    public boolean getSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }
}
