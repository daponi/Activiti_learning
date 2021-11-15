package com.bobo.www;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * Activiti基础篇
 */
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

    /**
     * 实现文件的单个部署
     */
    @Test
    public void test03(){
        //定义log4j日志文件的位置为当前项目路径
        String rootPath = System.getProperty("user.dir");
        System.setProperty("logPath", rootPath);

        // 1.获取ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2.获取RepositoryService进行部署操作
        RepositoryService service = processEngine.getRepositoryService();
        // 3.使用RepositoryService进行部署操作Deployment deploy = service.createDeployment()
        Deployment deploy = service.createDeployment()
                .addClasspathResource("activiti/Demo01.bpmn20.xml")// 添加bpmn资源
                .addClasspathResource("activiti/diagram-02.png") // 添加png资源
                .name("出差申请流程002")// 添加流程名字
                .key("evection002")
                .deploy();// 部署流程

        System.out.println("部署的ID:"+deploy.getId());
        System.out.println("部署的name:"+deploy.getName());
        System.out.println("部署的key:"+deploy.getKey());

    }

    /**
     * 通过一个zip文件来部署操作
     */
    @Test
    public void test04(){
        // 定义zip文件的输入流inputStream
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("activiti/activiti.zip");
        // 对 inputStream 做装饰
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        // 1.获取ProcessEngine对象
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        // 2.获取RepositoryService进行部署操作
        RepositoryService service = defaultProcessEngine.getRepositoryService();
        // 3.使用RepositoryService进行部署zip操作
        Deployment deploy = service.createDeployment()
                .addZipInputStream(zipInputStream)
                .name("出差申请流程2")
                .deploy();
        // 4.输出流程部署的信息
        System.out.println("部署的ID:"+deploy.getId());
        System.out.println("部署的name:"+deploy.getName());

    }

    /**
     * 启动一个流程实例
     */
    @Test
    public void test05(){
        // 1.创建ProcessEngine对象
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        // 2.获取RuntimeService对象
        RuntimeService runtimeService = engine.getRuntimeService();
        // 3.根据流程定义的id启动流程String id= "evection";
        String key ="evection";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key);
        // 4.输出相关的流程实例信息
        System.out.println("流程定义的ID：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例的ID：" + processInstance.getId());
        System.out.println("当前活动的ID：" + processInstance.getActivityId());

    }

    /**
     * 任务/节点查询
     */
    @Test
    public void test06(){
        String assignee = "zhangsan";
        String key = "evection";
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey(key)//定义的key
                .taskAssignee(assignee)//任务处理人
                .list();//返回全部结果
        for (Task task : list) {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println(" 任 务 id:" + task.getId());
            System.out.println(" 任 务 负 责 人 ：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }

    /**
     * 流程任务处理
     */
    @Test
    public void test07(){
        String assignee = "lisi";
        String key = "evection";

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        //先查出任务
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(key)//定义的key
                .taskAssignee(assignee)//任务处理人
                .singleResult();//返回单个结果
        //根据任务id完成任务
        taskService.complete(task.getId());
        System.out.println("执行成功！");
    }

    /**
     * 查询流程的定义
     */
    @Test
    public void test08(){
        String key = "evection";

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取资源管理类
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 获取一个 ProcessDefinitionQuery对象 用来查询操作
        ProcessDefinitionQuery definitionQuery = repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinition> list = definitionQuery.processDefinitionKey(key)
                .orderByProcessDefinitionVersion()//按照版本排序
                .desc()//降序排序
                .list();

        //输出流程定义的相关信息
        for (ProcessDefinition processDefinition : list) {
            System.out.println("流程定义的ID：" + processDefinition.getId());
            System.out.println("流程定义的name：" + processDefinition.getName());
            System.out.println("流程定义的key:" + processDefinition.getKey());
            System.out.println("流程定义的version:" + processDefinition.getVersion());
            System.out.println("流程部署的id:" + processDefinition.getDeploymentId());
        }
    }

    /**
     * 删除流程
     */
    @Test
    public void test09(){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
        // 非级联删除流程定义，如果该流程定义已经有了流程实例启动则删除时报错
        repositoryService.deleteDeployment("5001");
        //第二个参数设置为TRUE时进行级联删除流程定义，即使流程有实例启动，也可以删除，设置为false 非级联删除操作。
        //repositoryService.deleteDeployment("12501",true)
        System.out.println("成功删除流程");
    }
    /**
     * 流程资源的下载
     * 此处发现问题，
     * 1.需要以bpmn或者bpmn20.xml结尾的资源内容才会去部署流程 【解决】
     * 2.我部署后act_re_procdef表插入的数据里DGRM_RESOURCE_NAME_字段没有插入.png【解决，重启后过几天自然好了？时好时坏】
     */
    @Test
    public void test10() throws IOException {
        String key = "evection";
        // 1.得到ProcessEngine对象
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        // 2.获取RepositoryService对象
        RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
        // 3.得到查询器
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(key)
                .singleResult();
        // 4.获取流程部署的id
        String deploymentId = processDefinition.getDeploymentId();
        // 5.通过repositoryService对象的相关方法 来获取图片信息和bpmn信息
        // png图片的流
        InputStream pngInput = repositoryService.getResourceAsStream(deploymentId,processDefinition.getDiagramResourceName());
        //bpmn文件的流
        InputStream bpmnInput = repositoryService.getResourceAsStream(deploymentId, processDefinition.getResourceName());
        // 6.文件的保存  ./相当于System.getProperty("user.dir")， ../相当于System.getProperty("user.dir")的上层
        File filePng = new File("./logs/evection.png");
        File fileBpmn = new File(System.getProperty("user.dir") +"/logs/evection.bpmn20.xml");
        //建立输出流
        FileOutputStream pngOutput = new FileOutputStream(filePng);
        FileOutputStream bpmnOutput = new FileOutputStream(fileBpmn);
        //将输入流复制到输出流
        IOUtils.copy(pngInput,pngOutput);
        IOUtils.copy(bpmnInput,bpmnOutput);

        //7.关闭流
        pngInput.close();
        pngOutput.close();
        bpmnInput.close();
        bpmnOutput.close();
    }
    /**
     * 流程历史信息查看
     */
    @Test
    public void test11(){
        String key = "evection";

        // 1.得到ProcessEngine对象
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取流程部署的id: evection:2:2504，可自己看数据库数据
        RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(key)
                .singleResult();
        String id = processDefinition.getId();

        // 2.获取HistoryService对象
        HistoryService historyService = defaultProcessEngine.getHistoryService();
        // 获取 act_hi_actinst 表的查询对象
        HistoricActivityInstanceQuery instanceQuery = historyService.createHistoricActivityInstanceQuery();
        instanceQuery.processDefinitionId(id);
        instanceQuery.orderByHistoricActivityInstanceStartTime()
                .desc();

        //3.打印输出结果
        List<HistoricActivityInstance> list = instanceQuery.list();
        for (HistoricActivityInstance hist : list) {

            System.out.println("节点名称："+hist.getActivityName());
            System.out.println("节点类型："+hist.getActivityType());
            System.out.println("节点处理人："+hist.getAssignee());
            System.out.println("流程定义ID："+hist.getProcessDefinitionId());
            System.out.println("流程实例ID："+hist.getProcessInstanceId());
            System.out.println("______________________________");
        }

    }
}

