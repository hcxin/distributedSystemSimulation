package com.huaqi.common.util;

import com.google.gson.Gson;
import com.huaqi.common.msg.Base;

public  class MessageUtil {
    private static Gson gson = new Gson();
    public static String getMsg(Base msg) {
        String msgJson = gson.toJson(msg);
        return msgJson;
    }

    public static Base decodeMsgBase(Object msgObj) {
        String msgStr = String.valueOf(msgObj);
        Base msg = gson.fromJson(msgStr, Base.class);
        return msg;
    }
}
