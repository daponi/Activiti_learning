package com.bobo.www;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.junit.Test;

public class test01 {

    /**
     * 通过默认的方式，生成Activiti的相关的表结构
     */
    @Test
    public void test01() {
        //定义log4j日志文件的位置为当前项目路径
        String rootPath = System.getProperty("user.dir");
        System.setProperty("logPath", rootPath);

        // 使用classpath下的activiti.cfg.xml中的配置来创建 ProcessEngine对象,getDefaultProcessEngine()源码里是写死了配置文件名"activity.cfg.xml"
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        System.out.println(defaultProcessEngine);
    }


    /**
     * 自定义的方式来加载配置文件
     */
    @Test
    public void test02() {
        // 首先创建ProcessEngineConfiguration对象，加载自定义的文件名
        ProcessEngineConfiguration config = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activity.cfg.xml");
        // 通过ProcessEngineConfiguration对象来创建 ProcessEngine对象
        ProcessEngine processEngine = config.buildProcessEngine();
        System.out.println(processEngine);
    }

    @Test
    public void test03(){
        //定义log4j日志文件的位置为当前项目路径
        String rootPath = System.getProperty("user.dir");
        System.setProperty("logPath", rootPath);

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService service = processEngine.getRepositoryService();
        Deployment deploy = service.createDeployment()
                .addClasspathResource("activiti/activitiDemo01.bpmn20.xml")
                .addClasspathResource("activiti/diagram.png")
                .name("出差申请流程")
                .deploy();

        System.out.println("部署的ID:"+deploy.getId());
        System.out.println("部署的name:"+deploy.getName());

    }

}

