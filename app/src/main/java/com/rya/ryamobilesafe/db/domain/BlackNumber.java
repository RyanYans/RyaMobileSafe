package com.rya.ryamobilesafe.db.domain;

/**
 * Created by Rya32 on 广东石油化工学院.
 * Version 1.0
 */
public class BlackNumber {
    private String phone;
    private String state;

    @Override
    public String toString() {
        return "BlackNumber{" +
                "phone='" + phone + '\'' +
                ", state='" + state + '\'' +
                '}';
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
