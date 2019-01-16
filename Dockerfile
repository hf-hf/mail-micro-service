FROM registry.cn-shenzhen.aliyuncs.com/xuu/java:8u191
FROM maven:3

MAINTAINER hf-hf <553527481@qq.com>

WORKDIR /app

ADD . /tmp

RUN cd /tmp && mvn install -Dmaven.test.skip=true \
	&& mv target/mail-micro-service.jar /app/app.jar

# 若使用外部配置文件，请将配置都放入conf目录，并放开下方的注释
# ADD ./conf/ /app/

RUN rm -rf /tmp/* && rm -rf ~/.m2

ENTRYPOINT exec java -Dfile.encoding=UTF8 \
                     -Duser.timezone=GMT+08 \
                     -jar /app/app.jar
