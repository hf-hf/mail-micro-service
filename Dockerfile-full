FROM registry.cn-hangzhou.aliyuncs.com/flash20/git
FROM registry.cn-hangzhou.aliyuncs.com/kennylee/maven

MAINTAINER hf-hf <553527481@qq.com>

RUN git clone https://github.com/hf-hf/mail-micro-service.git

WORKDIR /app

RUN cd /mail-micro-service && mvn install -Dmaven.test.skip=true \
    && cp target/mail-micro-service.jar /app/app.jar

# 若使用外部配置文件，请将配置都放入config目录，并放开下方的注释
# ADD ./config/ /app

RUN rm -rf /mail-micro-service && rm -rf ~/.m2

ENTRYPOINT exec java -Dfile.encoding=UTF8 \
                     -Duser.timezone=GMT+08 \
                     -jar /app/app.jar
