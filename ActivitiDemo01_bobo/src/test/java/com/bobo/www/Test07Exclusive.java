package com.bobo.www;

import com.bobo.activiti.pojo.Evection;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test07Exclusive {

    /**
     * 流程部署
     */
    @Test
    public void test01(){
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = engine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("activiti/evection-exclusive.bpmn20.xml")
                .addClasspathResource("activiti/evection-exclusive.png")
                .name("出差申请单-排他网关")
                .deploy();
        System.out.println("流程名称：" + deploy.getName());
        System.out.println("流程定义ID：" + deploy.getId());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void test02(){
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        String key = "evection-exclusive";
        RuntimeService runtimeService = engine.getRuntimeService();
        Evection evection = new Evection();
        evection.setNum(3d);
//        evection.setNum(2d); //验证num=2的情况
        Map<String,Object> map = new HashMap<>();
        map.put("evection",evection);
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey(key,map);
        // 4.输出流程部署的信息
        System.out.println("获取流程实例名称："+processInstance.getName());
        System.out.println("流程定义ID：" + processInstance.getProcessDefinitionId());

    }


    /**
     * 完成任务
     */
    @Test
    public void test03(){
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        //String taskId = "75002";
        String key = "evection-exclusive";
        String userId= "lisi";
        Task task = taskService.createTaskQuery()
//                .taskId()
                .processDefinitionKey(key)
                .taskAssignee(userId)
                .singleResult();

        if(task != null){
            taskService.complete(task.getId());
            System.out.println("成功完成任务！");
        }
    }
}
