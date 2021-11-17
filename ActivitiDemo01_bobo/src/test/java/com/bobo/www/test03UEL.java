package com.bobo.www;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用UEL表达式分配任务责任人
 */
public class test03UEL {

    /**
     * 先将新定义的流程部署到Activiti中数据库中
     */
    @Test
    public void test01() {
        // 1.获取ProcessEngine对象
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 2.获取RepositoryService进行部署操作
        RepositoryService service = engine.getRepositoryService();
        // 3.使用RepositoryService进行部署操作
        Deployment deploy = service.createDeployment()
                .addClasspathResource("activiti/evection-uel.bpmn") // 添加bpmn资源
                .addClasspathResource("activiti/diagram-uel.png") // 添加png资源
                .name("出差申请流程-UEL")
                .deploy();// 部署流程
        // 4.输出流程部署的信息
        System.out.println("流程部署的id:" + deploy.getId());
        System.out.println("流程部署的名称：" + deploy.getName());
    }

    /**
     * 创建一个流程实例
     * 给流程定义中的 UEL表达式赋值
     * 会在act_ru_variable表里插入表达式数据
     */
    @Test
    public void test02() {
        // 获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取RuntimeService对象
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 设置 assignee 的取值，
        Map<String, Object> map = new HashMap<>();
        map.put("assignee0", "张三");
        map.put("assignee1", "李四");
        map.put("assignee2", "王五");
        map.put("assignee3", "赵财务");
        // 创建流程实例
        runtimeService.startProcessInstanceByKey("evection-uel", map);
    }
}
