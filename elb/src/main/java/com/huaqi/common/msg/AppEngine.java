package com.huaqi.common.msg;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class AppEngine implements Serializable {
    private String server;
    private String instanceID;
    private String port;
    private String ip;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getInstanceID() {
        return instanceID;
    }

    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
