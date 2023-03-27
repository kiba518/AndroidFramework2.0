package com.kiba.framework.dto.user;


import com.kiba.framework.dto.base.BaseCommand;

public class LoginCommand extends BaseCommand {
    public String mobile = "";

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }



}
