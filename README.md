# spring-boot-activeMQ

1. 下载activeMQ

[官方地址](http://activemq.apache.org/index.html)

找到对应环境的版本，下载下来后进行安装

2. 安装

由于我是迫切想用它，所以用docker直接省了安装这一步。

安装完成后，访问：

http://127.0.0.1:32771

由于我用的是docker,端口号32771其实对应的是8161

[参考官方](http://activemq.apache.org/getting-started.html#GettingStarted-Introduction)

3. 集成springboot

pom.xml:

    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>
    
        <groupId>com.example</groupId>
        <artifactId>activeMQ</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <packaging>war</packaging>
    
        <name>activeMQ</name>
        <description>Demo project for Spring Boot</description>
    
        <parent>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
            <version>1.5.6.RELEASE</version>
            <relativePath/> <!-- lookup parent from repository -->
        </parent>
    
        <properties>
            <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
            <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
            <java.version>1.8</java.version>
        </properties>
    
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
            </dependency>
    
            <dependency>
                <groupId>org.mybatis.spring.boot</groupId>
                <artifactId>mybatis-spring-boot-starter</artifactId>
                <version>1.3.0</version>
            </dependency>
    
    
            <!--<dependency>-->
                <!--<groupId>org.apache.activemq</groupId>-->
                <!--<artifactId>activemq-pool</artifactId>-->
                 <!--<version>5.14.3</version>-->
            <!--</dependency>-->
    
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-activemq</artifactId>
            </dependency>
    
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <optional>true</optional>
            </dependency>
    
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-test</artifactId>
                <scope>test</scope>
            </dependency>
    
    
        </dependencies>
    
        <build>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                </plugin>
            </plugins>
        </build>
    
        <repositories>
            <repository>
                <id>alimaven</id>
                <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
            </repository>
            <repository>
                <id>spring-snapshots</id>
                <url>http://repo.spring.io/snapshot</url>
                <snapshots><enabled>true</enabled></snapshots>
            </repository>
            <repository>
                <id>spring-milestones</id>
                <url>http://repo.spring.io/milestone</url>
            </repository>
        </repositories>
    
    </project>



4. 写一个最简单的场景demo---生产者消费者


生产者：

    package com.example.demo.service;
    
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.jms.annotation.JmsListener;
    import org.springframework.jms.core.JmsMessagingTemplate;
    import org.springframework.stereotype.Service;
    
    import javax.jms.Destination;
    
    
    /**
     * Created by sam on 2018/3/27.
     */
    
    @Service
    public class Producer {
    
        @Autowired // 也可以注入JmsTemplate，JmsMessagingTemplate对JmsTemplate进行了封装
        private JmsMessagingTemplate jmsTemplate;
        // 发送消息，destination是发送到的队列，message是待发送的消息
        public void sendMessage(Destination destination, final String message){
            jmsTemplate.convertAndSend(destination, message);
        }
    
        @JmsListener(destination = "out.queue")
        public void consumerMessage(String txt){
            System.out.println("从consumer2回复的报文是："+txt);
        }
    
    
    }



消费者：


    package com.example.demo.service;
    
    import org.springframework.jms.annotation.JmsListener;
    import org.springframework.stereotype.Component;
    
    /**
     * Created by sam on 2018/3/27.
     */
    @Component
    public class Consumer {
    
        // 使用JmsListener配置消费者监听的队列，其中text是接收到的消息
        @JmsListener(destination = "mytest.queue")
        public void receiveQueue(String text) {
            System.out.println("消费者1消费:"+text);
        }
    }


Consumer2:

    package com.example.demo.service;
    
    import org.springframework.jms.annotation.JmsListener;
    import org.springframework.messaging.handler.annotation.SendTo;
    import org.springframework.stereotype.Component;
    
    /**
     * Created by sam on 2018/3/27.
     */
    @Component
    public class Consumer2 {
    
        @JmsListener(destination = "mytest.queue")
        @SendTo("out.queue")
        public String receiveQueue(String text) {
            System.out.println("消费者2收到:"+text);
            return "儿子收到"+text;
        }
    }


解决了共享资源并发的问题。。。。。


[参考文章](https://www.jianshu.com/p/ecdc6eab554c)




