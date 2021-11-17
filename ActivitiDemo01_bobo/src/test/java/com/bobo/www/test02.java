package com.bobo.www;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

/**
 * Activiti进阶篇
 */
public class test02 {
    /**
     * 启动流程实例，并添加businessKey
     */
    @Test
    public void test01() {
        String key = "evection002";

        // 1.获取ProcessEngine对象
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        // 2.获取RuntimeService对象
        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
        // 3.启动流程实例,第二个参数就是设置businessKey，可看源码接口的实现
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, "3033");
        // 4.输出processInstance相关属性
        System.out.println("businessKey =" + processInstance.getBusinessKey());
    }

    /**
     * 全部流程挂起实例与激活
     */
    @Test
    public void test02() {
        String key = "evection";

        // 1.获取ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2.获取RepositoryService对象
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 3.查询流程定义的对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(key)
                .singleResult();
        // 4.获取当前流程定义的状态
        boolean suspended = processDefinition.isSuspended();
        String id = processDefinition.getId();
        // 5.如果挂起就激活，如果激活就挂起
        if (suspended) {
            repositoryService.activateProcessDefinitionById(id,
                    true,//流程实例是否激活
                    null);//激活时间
            System.out.println("流程定义：" + id + ",已激活");
        } else {
            repositoryService.suspendProcessDefinitionById(id,
                    true,//流程实例是否挂起
                    null);//挂起时间
            System.out.println("流程定义：" + id + ",已挂起");
        }
    }

    /**
     * 单个流程实例挂起与激活
     */
    @Test
    public void test03() {
        String key = "evection";

        // 1.获取ProcessEngine对象
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        // 2.获取RuntimeService
        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
        // 3.获取流程实例对象
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId("30001")
                .singleResult();
        // 4.获取相关的状态操作
        boolean suspended = processInstance.isSuspended();
        String id = processInstance.getId();
        if (suspended) {
            runtimeService.activateProcessInstanceById(id);
            System.out.println("流程定义：" + id + "，已激活");
        } else {
            runtimeService.suspendProcessInstanceById(id);
            System.out.println("流程定义：" + id + "，已挂起");
        }
    }
}
