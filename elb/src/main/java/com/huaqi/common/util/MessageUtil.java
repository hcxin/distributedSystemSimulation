package com.huaqi.common.util;

import com.google.gson.Gson;
import com.huaqi.common.msg.Base;
import com.huaqi.common.msg.EAS;

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
    public static EAS decodeMsgEAS(Object msgObj) {
        String msgStr = String.valueOf(msgObj);
        EAS msg = gson.fromJson(msgStr, EAS.class);
        return msg;
    }
    public static Gson getGson() {
        return gson;
    }
}
