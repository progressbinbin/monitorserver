package com.msjwsms.monitor;

/**
 * Created by yuantousanfen on 2019/6/24.
 */
public class MonitorStart {
    public static void main(String[] args) {
        try{
            // System信息，从jvm获取
            ComputerInfo.property();
            System.out.println("----------------------------------");
            // cpu信息
            ComputerInfo.cpu();
            System.out.println("----------------------------------");
            // 内存信息
            ComputerInfo.memory();
            System.out.println("----------------------------------");
            // 操作系统信息
            ComputerInfo.os();
            System.out.println("----------------------------------");
            // 用户信息
            ComputerInfo.who();
            System.out.println("----------------------------------");
            // 文件系统信息
            ComputerInfo.file();
            System.out.println("----------------------------------");
            // 网络信息
            ComputerInfo.net();
            System.out.println("----------------------------------");
            // 以太网信息
            ComputerInfo.ethernet();
            System.out.println("----------------------------------");

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
