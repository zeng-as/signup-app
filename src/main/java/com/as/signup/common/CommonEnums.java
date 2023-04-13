package com.as.signup.common;

public final class CommonEnums {
    public static enum ResCode {
        OK("200", "请求成功"),
        EX_PARAM("1001", "参数不完整"),
        EX_UPLOAD("1002", "文件上传失败"),
        EX_SIGNUP("1003", "报名失败"),
        EX_SYSTEM("9999", "系统异常");

        private final String code;
        private final String desc;

        public String getCode() {
            return this.code;
        }

        public String getDesc() {
            return this.desc;
        }

        private ResCode(final String code, final String desc) {
            this.code = code;
            this.desc = desc;
        }
    }
}
