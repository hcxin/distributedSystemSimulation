package com.huaqi.elb.lb;

import com.huaqi.common.msg.AppEngine;

public interface LoadBalance {
    AppEngine getAppEngineServer();
}
