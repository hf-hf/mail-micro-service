# mail-micro-service

<p align="left">
    <a>
    	<img src="https://img.shields.io/badge/JDK-1.8+-brightgreen.svg" >
    	<img src="https://img.shields.io/badge/SpringBoot-2.1.0-green.svg" >
    </a>
</p>

## 项目介绍
`mail-micro-service`是基于SpringBoot、JavaMail实现的邮件微服务系统，支持以轮询、加权轮询方式负载多邮箱配置，提供邮件发送API。

## 开发需求
由于免费邮箱存在邮件发送数量和频率的上限，若发送频繁，可能会被邮件服务商判定为垃圾邮件（554 DT:SPM），因此采用多个免费邮箱轮询发送。

## 软件环境
- JDK1.8+
- Maven3.0+

## 配置说明

### application-dev.properties
```
# 轮询方式：normal（轮询）、weighted（加权轮询）
mail.roundrobin.type=weighted
# 附件临时目录，linux下需要修改路径
file.folder=D://maindisk/temp
```

### mailX.properties（X为从0开始的正整数，多个实例需顺序配置，中间不允许跳过）
```
# 邮箱host，这里以163为例
mail.smtp.host=smtp.163.com
mail.smtp.socketFactory.class=javax.net.ssl.SSLSocketFactory
# 某些云服务商因安全问题，不允许通过stmp 25端口发送邮件，因此使用465端口，采用SSL协议加密传输邮件
mail.smtp.socketFactory.port=465
# 进行身份验证
mail.smtp.auth=true
# 账号
mail.username=XXXX
# 授权码
mail.password=XXXX
# 标识
mail.id=mail0
# 启用标志
mail.isAvailable=true
# 加权轮询权重
mail.weight=2
# debug模式
mail.debug=false
```

系统会优先读取jar包同级目录的配置文件（自定义的mailX.properties也是），若没找到则会使用classpath下配置，推荐使用外部配置文件部署。

## 安装教程
安装JDK，配置Maven环境，Clone代码，配置邮箱账号授权码，编译打包工程，运行jar包。

```
mvn clean install -Dmaven.test.skip=true
java -jar target/mail-micro-service.jar
```

推送授权码并非邮箱密码，163/126邮箱获取方式请[点我查看](http://help.163.com/10/0312/13/61J0LI3200752CLQ.html) ，QQ邮箱请[点我查看](https://jingyan.baidu.com/article/90895e0f2af42664ec6b0b14.html)，邮箱必须开启POP3/SMTP服务。

## 使用说明
发送无附件邮件
POST http://127.0.0.1:12345/api/v0.0.1/mail/send?to=xxxxx@qq.com&title=我是没有附件的主题&content=我是没有附件的内容

多收件人，请使用;分隔收件人邮箱，更多使用详情见[MailLocalhostTest.java](/src/test/java/top/mail/MailLocalhostTest.java)

## 测试截图
![demo](/images/demo.gif)

## 源码托管
- [https://gitee.com/hf-hf/mail-micro-service](https://gitee.com/hf-hf/mail-micro-service)
- [https://github.com/hf-hf/mail-micro-service](https://github.com/hf-hf/mail-micro-service)

## 更新记录
- v0.0.1 以轮询、加权轮询方式负载多邮箱配置，提供邮件发送API
- v0.0.2 记录邮件发送日志，并提供查询页面
- v0.0.3 添加Dockerfile

## v0.0.2更新
配置日志切面，使用h2database存储发送日志，默认开启h2 console，可查看数据库信息，数据文件存储于同级目录/h2/mms下，可修改使用h2内存模式或者其他数据库配置，http://127.0.0.1:12345/api/v0.0.1/h2-console/

提供发送记录查询页面，可查看历史发送记录，http://127.0.0.1:12345/api/v0.0.1/

![v0.0.2_1](/images/v0.0.2_1.png)

## v0.0.3更新
添加Dockerfile，提供Docker部署方案。

手动拉取代码安装。
```
git clone https://github.com/hf-hf/mail-micro-service.git
cd mail-micro-service
./docker/docker-build.sh
./docker/docker-run.sh
```

快捷安装，拷贝Dockerfile-full，重名为Dockerfile。
```
docker build -t mail-micro-service ./
docker run --name mail-micro-service -e JVMXMX=1G -v /app/app-log:/app/app-log -v /app/h2:/app/h2 -p 12345:12345 -d mail-micro-service
```

若要使用外部配置文件，请将配置文件放置在Dockerfile同级目录的config文件夹下，并放开Dockerfile中的注释行。
