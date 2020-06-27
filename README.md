# spring-boot-rabbitMQ

官网：https://www.rabbitmq.com

rabbitmq中文文档：
    
    https://rabbitmq.mr-ping.com/




1. 安装

由于我是迫切想用它，所以用docker直接省了安装这一步。

用的最新的官方镜像：
    
    bitnami/rabbitmq:latest
    

rabbitMQ版本：
        
        3.8.2


启动成功后，正常情况下会有四个端口号：

* 4369 (epmd) 
* 25672 (Erlang distribution)
* 5672, 5671 (启用了 或者 未启用 TLS 的 AMQP 0-9-1)
* 15672 (如果管理插件被启用)

安装完成后，访问：

http://127.0.0.1:15672



2. 新增用户

官网资料上说rabbitMQ有一个默认的guest/guest账号，但是我拉的这个镜像里是没有的。


我们看一下有没有用户：



进入docker:
        
        docker exec -it rabbitmq bash
        
        
查看是否有用户：

        rabbitmqctl  list_users
        
返回：
        
        
好像看到有个tags和administor 用户，但其实不是用户列表。
        
        
新增一个用户：
        
        rabbitmqctl add_user sam 123456
        
将这个用户提为管理员：
        
        set_user_tags sam administrator

        



3. 集成springboot

pom.xml:

    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>
    
        <groupId>com.example</groupId>
        <artifactId>rabbitMQ</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <packaging>war</packaging>
    
        <name>rabbitMQ</name>
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
                <!--<groupId>org.apache.rabbitMQ</groupId>-->
                <!--<artifactId>rabbitMQ-pool</artifactId>-->
                 <!--<version>5.14.3</version>-->
            <!--</dependency>-->
    
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-amqp</artifactId>
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



4. rabbitMQ知识地图

![知识地图](.README_images/79ef4fa3.png)

[下载地图](.README_images/RabbitMQ.xmind)

5. 什么是死信队列

死信队列: DLX，dead-letter-exchange
其实就是在一般的队列上面设置dlx属性。
利用 dlx，当消息在一个队列中变成死信 (dead message) 之后，它能被重新 publish 到另一个 exchange，这个 exchange 就是 dlx


6. 什么情况会变成死信队列

消息变成死信的原因有:
    
    1.消息被拒绝 (basic.reject / basic.nack) 并且 reQueue=false

    2.消息 TTL 过期

    3.队列达到最大长度了


7. 写一个死信队列的场景，来实现请求第三方平台接口失败时，延时重试
厂里需要能够支持10万+的请求，要求有重试机制，时间两天，请记住2020年6月25号，这个端午假期，说是有三倍工资，拭目以待。

基本已经定了使用rabbitMQ实现负载分发，但是我们需要调用第三方平台的接口，而第三分平台接口貌似只支持每秒钟100次调用，超过这个频率了就会失败，或者报错。
要求：
    1. 消费者在调用第三方平台接口如果报错或者失败的情况下，要将失败的消息存起来，5秒后重试，同一消息重试不能超过3次。
    2. 多消费者不能重复消费
    
以下是基本的流程：
    
![基本流程](.README_images/54fe9b2c.png)    
    


8. MQ的交换机与队列配置如下：

![配置](.README_images/f25283ed.png)




9. IM在MQ的这个配置中的请求流程

![](.README_images/2c949d67.png)



[参考文章](https://www.cnblogs.com/mfrank/p/11184929.html)




