package com.nk.streetsnaps.entity;

public class ApiResponse {

    private static final String default_success_code = "0";
    private static final String default_success_msg = "请求成功!";
    private static final String default_success_data = "";

    private static final String default_failure_code = "1";
    private static final String default_failure_msg = "系统异常";
    private static final String default_failure_data = "";

    private String code;
    private String msg;
    private Object data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }


    /******************************************************************/

    public void setSuccessMsg(String msg){
        this.code = default_success_code;
        this.msg = msg;
        this.data = default_success_data;
    }

    public void setSuccessData(Object data){
        this.code = default_success_code;
        this.msg = default_success_msg;
        this.data = data;
    }

    public void setSuccess(){
        this.code = default_success_code;
        this.msg = default_success_msg;
        this.data = default_success_data;
    }

    public void setFailureMsg(String code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public void setFailure(){
        this.code = default_failure_code;
        this.msg = default_failure_msg;
    }
}
