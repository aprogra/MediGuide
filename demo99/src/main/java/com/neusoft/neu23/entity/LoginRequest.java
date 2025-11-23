package com.neusoft.neu23.entity;

public class LoginRequest {
    // 登录请求参数类
    private String username;
    private Integer id;

    // getter和setter方法
    public String getUsername() {
            return username;
        }

    public void setUsername(String username) {
            this.username = username;
        }

    public Integer getId() {
            return id;
        }

    public void setId(Integer id) {
            this.id = id;
        }

}
