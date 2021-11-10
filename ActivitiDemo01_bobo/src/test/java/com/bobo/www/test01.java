package com.bobo.www;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.junit.Test;

public class test01 {
    /**
     * 生成Activiti的相关的表结构
     */
    @Test
    public void test01() {
        //定义日志文件的位置为当前项目路径
        String rootPath = System.getProperty("user.dir");
        System.setProperty("logPath", rootPath);

        // 使用classpath下的activiti.cfg.xml中的配置来创建 ProcessEngine对象
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        System.out.println(defaultProcessEngine);
    }



    @Test
    public void test02() {
        int i = 6;

        if (i > 3) {
            System.err.println("i>3");
        } else if (i > 5) {

            System.err.println("i>5");
        }
    }

}

