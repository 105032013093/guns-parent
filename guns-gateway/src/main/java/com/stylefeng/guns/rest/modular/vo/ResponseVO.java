package com.stylefeng.guns.rest.modular.vo;

public class ResponseVO<M> {

    // 返回状态[[0|成功;1|业务失败;999|系统异常]
    private String status;
    // 返回错误信息
    private String message;
    // 返回数据实体
    private M date;

    private ResponseVO(){}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public M getDate() {
        return date;
    }

    public void setDate(M date) {
        this.date = date;
    }

    public static<M> ResponseVO  succees(M m){
        ResponseVO responseVO = new ResponseVO();
        responseVO.setDate(m);
        responseVO.setStatus("0");
        return responseVO;
    }

    public static<M> ResponseVO  succees(String msg){
        ResponseVO responseVO = new ResponseVO();
        responseVO.setMessage(msg);
        responseVO.setStatus("0");
        return responseVO;
    }

    // 业务异常
    public static<M> ResponseVO  serviceFail(String msg){
        ResponseVO responseVO = new ResponseVO();
        responseVO.setMessage(msg);
        responseVO.setStatus("1");
        return responseVO;
    }
    // 系统异常
    public static<M> ResponseVO  appFail(String msg){
        ResponseVO responseVO = new ResponseVO();
        responseVO.setDate(msg);
        responseVO.setStatus("999");
        return responseVO;
    }
}
