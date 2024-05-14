package org.chc.ezim.entity.enums;


public enum ResponseCodeEnum {
    CODE_200(200, "请求成功"),
    CODE_401(401, "身份验证已过期，请重新登陆。"),
    CODE_403(403, "禁止访问资源"),
    CODE_404(404, "请求地址不存在"),
    CODE_500(500, "服务器错误，请联系管理员"),
    CODE_600(600, "请求参数错误"),
    CODE_601(601, "信息已经存在");

    private final Integer code;

    private final String msg;

    ResponseCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
