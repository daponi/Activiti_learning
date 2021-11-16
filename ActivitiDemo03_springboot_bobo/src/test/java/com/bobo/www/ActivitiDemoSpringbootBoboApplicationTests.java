package com.bobo.www;

import com.bobo.www.utils.SecurityUtil;
import org.activiti.engine.RepositoryService;
import org.activiti.runtime.api.ProcessRuntime;
import org.activiti.runtime.api.TaskRuntime;
import org.activiti.runtime.api.model.ProcessDefinition;
import org.activiti.runtime.api.model.ProcessInstance;
import org.activiti.runtime.api.model.Task;
import org.activiti.runtime.api.model.builders.ProcessPayloadBuilder;
import org.activiti.runtime.api.model.builders.TaskPayloadBuilder;
import org.activiti.runtime.api.query.Page;
import org.activiti.runtime.api.query.Pageable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ActivitiDemoSpringbootBoboApplicationTests {

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private RepositoryService repositoryService;

    @Test
    void contextLoads() {
        System.out.println(taskRuntime);
        System.out.println("成功创建Activiti的数据表！");
        System.out.println("成功自动部署表Process！");
    }

    /**
     * 查询流程的定义
     */
    @Test
    public void test02(){
        securityUtil.logInAs("system");
        Page<ProcessDefinition> processDefinitionPage =
                processRuntime.processDefinitions(Pageable.of(0, 10));
        System.out.println("可用的流程定义数量:" + processDefinitionPage.getTotalItems());
        for (ProcessDefinition processDefinition : processDefinitionPage.getContent()) {
            System.out.println("流程定义：" + processDefinition);
        }
    }


    /**
     * 手动部署流程
     */
    @Test
    public void test03(){
        repositoryService.createDeployment()
                .addClasspathResource("processes/my-evection.bpmn")
                .addClasspathResource("processes/my-evection.png")
                .name("出差申请单")
                .deploy();
        System.out.println("手动部署流程成功！");
    }

    /**
     * 启动流程实例
     */
    @Test
    public void test04(){
        securityUtil.logInAs("system");
        ProcessInstance processInstance = processRuntime.start(ProcessPayloadBuilder
                .start()
                .withProcessDefinitionKey("my-evection")
                .build()
        );
        System.out.println("流程实例id:" + processInstance.getId());
    }

    /**
     * 任务查询、拾取及完成操作
     */
    @Test
    public void test05(){
        securityUtil.logInAs("jack");
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 10));
        if(tasks != null && tasks.getTotalItems() > 0){
            for (Task task : tasks.getContent()) {
                // 拾取任务
                taskRuntime.claim(TaskPayloadBuilder
                        .claim()
                        .withTaskId(task.getId())
                        .build()
                );
                System.out.println("任务：" + task);
                taskRuntime.complete(TaskPayloadBuilder
                        .complete()
                        .withTaskId(task.getId())
                        .build()
                );
            }
        }
        Page<Task> taskPage2 = taskRuntime.tasks(Pageable.of(0,10));
        if(taskPage2 .getTotalItems() > 0){
            System.out.println("任务：" + taskPage2.getContent());
        }
    }

}
