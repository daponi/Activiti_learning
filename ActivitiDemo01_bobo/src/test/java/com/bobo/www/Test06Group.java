package com.bobo.www;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.List;

public class Test06Group {

    /**
     * 流程部署
     */
    @Test
    public void test01(){
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = engine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("activiti/evection1.bpmn")
                .addClasspathResource("activiti/evection1.png")
                .name("出差申请单-组任务")
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
        RuntimeService runtimeService = engine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("evection1");
        // 4.输出流程部署的信息
        System.out.println("获取流程实例名称："+processInstance.getName());
        System.out.println("流程定义ID：" + processInstance.getProcessDefinitionId());
    }

    /**
     * 查询组任务
     */
    @Test
    public void test03(){
        String key = "evection1";
        String candidateUser = "lisi";
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey(key)
                .taskCandidateUser(candidateUser)
//                .taskCandidateOrAssigned(candidateUser)
//                .taskAssignee(candidateUser)
                .list();
        for (Task task : list) {
            System.out.println("流程实例Id：" + task.getProcessInstanceId());
            System.out.println("任务ID：" + task.getId());
            System.out.println("负责人:" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }

    /**
     * 候选人 拾取任务
     */
    @Test
    public void test04(){
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        String taskId = "65002";
        // 候选人
        String userId = "lisi";
        // 拾取任务
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskCandidateUser(userId) // 根据候选人查询
                .singleResult();
        if(task != null){
            // 可以拾取任务
            taskService.claim(taskId,userId);
            System.out.println("拾取成功");
        }
    }

    /**
     * 完成个人任务
     */
    @Test
    public void test05(){
        String  taskId = "62505";
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        taskService.complete(taskId);
        System.out.println("完成任务：" + taskId);
    }

    /**
     * 归还任务
     */
    @Test
    public void test06(){
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        String taskId = "65002";
        String userId= "lisi";
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskAssignee(userId)
                .singleResult();
        if(task != null){
            // 如果设置为null，归还组任务，任务没有负责人
            taskService.setAssignee(taskId,null);
            System.out.println("成功归还任务");
        }
    }

    /**
     * 任务交接
     */
    @Test
    public void test07(){
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        String taskId = "65002";
        String userId= "zhangsan";
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskAssignee(userId)
                .singleResult();
        if(task != null){
            // 任务交接，设置该任务的新的负责人,
            taskService.setAssignee(taskId,"赵六");
            System.out.println("成功交接任务");

        }
    }
}
