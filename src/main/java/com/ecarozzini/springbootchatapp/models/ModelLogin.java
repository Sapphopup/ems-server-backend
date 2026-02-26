package com.ecarozzini.springbootchatapp.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModelLogin {
    private String loginName;
    private String password;
    @Override
    public String toString(){
        return "Loginname: "+loginName+"; Password: "+password+";";
    }
}