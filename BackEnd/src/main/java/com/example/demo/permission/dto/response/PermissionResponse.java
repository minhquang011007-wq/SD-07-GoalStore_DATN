package com.example.demo.permission.dto.response;

public class PermissionResponse {

    private Integer id;
    private String code;
    private String name;
    private String module;

    public PermissionResponse() {
    }

    public PermissionResponse(Integer id, String code, String name, String module) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.module = module;
    }

    public Integer getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getModule() {
        return module;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setModule(String module) {
        this.module = module;
    }
}