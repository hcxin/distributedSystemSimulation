package com.huaqi.common.msg;

import com.huaqi.common.constant.CMDConst;

import java.io.Serializable;
import java.util.List;

/**
 * 类的功能描述：
 * EAS
 *
 * @ClassName: EAS
 * @Author haichen
 * @Date 2020-07-02 03:00:50
 */
public class EAS extends Base implements Serializable {

    private int serverID;
    private float availableMemory;
    private int availableCPU;
    private int healthyScore;
    private List<AppEngine> appEngineList;
    private String serverStatus;

    public EAS(int cmd) {
        super(cmd);
    }

    private boolean onHeartBeat(){
        if (getCmd() == CMDConst.CMD_HEARTBEAT){
            return true;
        } else {
            return false;
        }

    }
    private boolean makeServerDown(){
        return false;
    }

    public int getServerID() {
        return serverID;
    }

    public void setServerID(int serverID) {
        this.serverID = serverID;
    }

    public float getAvailableMemory() {
        return availableMemory;
    }

    public void setAvailableMemory(float availableMemory) {
        this.availableMemory = availableMemory;
    }

    public int getAvailableCPU() {
        return availableCPU;
    }

    public void setAvailableCPU(int availableCPU) {
        this.availableCPU = availableCPU;
    }

    public int getHealthyScore() {
        return healthyScore;
    }

    public void setHealthyScore(int healthyScore) {
        this.healthyScore = healthyScore;
    }

    public List<AppEngine> getAppEngineList() {
        return appEngineList;
    }

    public void setAppEngineList(List<AppEngine> appEngineList) {
        this.appEngineList = appEngineList;
    }

    public String getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(String serverStatus) {
        this.serverStatus = serverStatus;
    }
}
