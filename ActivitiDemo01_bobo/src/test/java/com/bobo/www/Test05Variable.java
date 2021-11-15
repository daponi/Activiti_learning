package com.bobo.www;

import com.bobo.activiti.pojo.Evection;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;

public class Test05Variable {

    /**
     * 实现文件的单个部署
     */
    @Test
    public void test01() {
        //定义log4j日志文件的位置为当前项目路径
        String rootPath = System.getProperty("user.dir");
        System.setProperty("logPath", rootPath);

        // 1.获取ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2.获取RepositoryService进行部署操作
        RepositoryService service = processEngine.getRepositoryService();
        // 3.使用RepositoryService进行部署操作Deployment deploy = service.createDeployment()
        Deployment deploy = service.createDeployment()
                .addClasspathResource("activiti/evection-variable.bpmn20.xml")// 添加bpmn资源
                .addClasspathResource("activiti/evection-variable.png") // 添加png资源
                .name("出差申请流程-流程变量")// 添加流程名字
                .deploy();// 部署流程

        System.out.println("部署的ID:" + deploy.getId());
        System.out.println("部署的name:" + deploy.getName());
        System.out.println("部署的key:" + deploy.getKey());

    }


    /**
     * 启动流程实例，设置流程变量
     */
    @Test
    public void test02() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 流程定义key
        String key = "evection-variable";
        HashMap<String, Object> variable = new HashMap<>();
        // 创建出差对象 POJO
        Evection evection = new Evection();
        // 设置出差天数
//        evection.setNum(4d);
        evection.setNum(2d);
        // 定义流程变量到集合中
        variable.put("evection", evection);
        // 设置assignee的取值
        variable.put("assignee0", "张三1");
        variable.put("assignee1", "李四1");
        variable.put("assignee2", "王五1");
        variable.put("assignee3", "赵财务1");
        //启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, variable);
        // 输出信息
        System.out.println("获取流程实例名称：" + processInstance.getName());
        System.out.println("流程定义的ID：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例的ID：" + processInstance.getId());
        System.out.println("当前活动的ID：" + processInstance.getActivityId());
    }

    /**
     * 完成任务
     */
    @Test
    public void test03() {
        String key = "evection-variable";
        String assignee = "李四1";
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processDefinitionKey(key)
                .taskAssignee(assignee)
                .singleResult();
        if (task != null) {
            taskService.complete(task.getId());
            System.out.println(task.getAssignee() + "执行" + task.getName() + "完成！");
        }
    }

    /**
     * 启动流程实例，任务办理时设置流程变量
     */
    @Test
    public void test04() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 流程定义key
        String key = "evection-variable";
        HashMap<String, Object> variable = new HashMap<>();
        // 设置assignee的取值,POJO放在完成任务时设置
        variable.put("assignee0", "张三1");
        variable.put("assignee1", "李四1");
        variable.put("assignee2", "王五1");
        variable.put("assignee3", "赵财务1");
        //启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, variable);
        // 输出信息
        System.out.println("获取流程实例名称：" + processInstance.getName());
        System.out.println("流程定义的ID：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例的ID：" + processInstance.getId());
        System.out.println("当前活动的ID：" + processInstance.getActivityId());
    }

    /**
     * 完成任务时设置流程变量
     */
    @Test
    public void test05() {
        String key = "evection-variable";
        String assignee = "李四1";
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        // 创建出差对象 POJO
        Evection evection = new Evection();
        HashMap<String, Object> variable = new HashMap<>();
        // 设置出差天数
        evection.setNum(2d);// evection.setNum(4d);
        // 定义流程变量到集合中
        variable.put("evection", evection);
        Task task = taskService.createTaskQuery().processDefinitionKey(key)
                .taskAssignee(assignee)
                .singleResult();
        if (task != null) {
            //完成任务时，设置流程变量
            taskService.complete(task.getId(), variable);
            System.out.println(task.getAssignee() + "执行" + task.getName() + "完成！");
        }
    }

    /**
     * 当流程实例已运行且还没使用流程变量时，可以往里面插入一个流程变量
     */
    @Test
    public void setGlobalVariableByExecutionId() {
        // 当前流程实例执行 id，通常设置为当前执行的流程实例
        String executionId = "2601";
        // 获取processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 创建出差pojo对象
        Evection evection = new Evection();
        // 设置天数
        evection.setNum(3d);
        // 通过流程实例 id设置流程变量
        runtimeService.setVariable(executionId, "evection", evection);
        // 一次设置多个值
        // runtimeService.setVariables(executionId, variables)
    }

    /**
     * 当一个task已运行且还没使用流程变量时，可以往里面插入一个流程变量
     */
    @Test
    public void setGlobalVariableByTaskId() {
        //当前待办任务id
        String taskId = "1404";
        // 获取processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        Evection evection = new Evection();
        evection.setNum(3);
        //通过任务设置流程变量
        taskService.setVariable(taskId, "evection", evection);
//        taskService.setVariables(taskId, "evection", hash);

        //一次设置多个值
        //taskService.setVariables(taskId, variables)
    }
}
