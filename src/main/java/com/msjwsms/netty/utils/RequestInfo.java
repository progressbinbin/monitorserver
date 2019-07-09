package com.msjwsms.netty.utils;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by yuantousanfen on 2019/6/26.
 */
public class RequestInfo implements Serializable {
    private String ip;
    private String cname;
    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }


    private HashMap<String,Object> cpuPercMap;
    private HashMap<String,Object> memoryMap;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    public HashMap<String, Object> getCpuPercMap() {
        return cpuPercMap;
    }

    public void setCpuPercMap(HashMap<String, Object> cpuPercMap) {
        this.cpuPercMap = cpuPercMap;
    }

    public HashMap<String, Object> getMemoryMap() {
        return memoryMap;
    }

    public void setMemoryMap(HashMap<String, Object> memoryMap) {
        this.memoryMap = memoryMap;
    }
}
