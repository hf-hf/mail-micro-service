#!/bin/bash
# 重启
echo "restart mail-micro-service"

#export JAVA_HOME=/bak/java/jdk1.8.0_131
#echo ${JAVA_HOME}
dir_base=/app/mail-micro-service
cd ${dir_base}
echo 'deploying...'
jar_name='mail-micro-service.jar'
pid=`ps -ef | grep mail-micro-service | grep "java" | awk '{print $2}'`
if [ -n "$pid" ]
then
   kill -9 $pid
fi

echo ${dir_base}/${jar_name}
nohup ${JAVA_HOME}/bin/java -jar -Xms256m -Xmx1024m ${dir_base}/${jar_name} >/dev/null 2>&1 &
sleep 2
echo 'ok!'
