FROM registry.cn-hangzhou.aliyuncs.com/kennylee/maven

MAINTAINER hf-hf <553527481@qq.com>

WORKDIR /app

ADD . /tmp

RUN cd /tmp && mvn install -Dmaven.test.skip=true \
	&& mv target/mail-micro-service.jar /app/app.jar

# 若使用外部配置文件，请将配置都放入config目录，并放开下方的注释
# ADD ./config/ /app/

RUN rm -rf /tmp && rm -rf ~/.m2

RUN mkdir -p /app/tmpdir

ENTRYPOINT exec java -Dfile.encoding=UTF8 \
                     -Duser.timezone=GMT+08 \
                     -Djava.io.tmpdir=/app/tmpdir \
                     -jar /app/app.jar
