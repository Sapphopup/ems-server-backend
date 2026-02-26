package com.ecarozzini.springbootchatapp.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModelUser {
    private String loginName;
    private String password;
    private String displayName;
    private String errorMessage;
    @Override
    public String toString(){
        return "Username: "+displayName+" Loginname: "+loginName+" Password: "+password;
    }
}
