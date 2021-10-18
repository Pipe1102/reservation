package com.example.backend.enumeration;

import static com.example.backend.constant.Authorities.USER_AUTHORITIES;
import static com.example.backend.constant.Authorities.MANAGER_AUTHORITIES;
import static com.example.backend.constant.Authorities.ADMIN_AUTHORITIES;

public enum Role {

    ROLE_USER(USER_AUTHORITIES),
    ROLE_MANAGER(MANAGER_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES);

    private String[] authorities;

    Role(String... authorities){
        this.authorities = authorities;
    }
    public String[] getAuthorities(){
        return authorities;
    }
}
