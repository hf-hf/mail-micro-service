# 当某行代码出错时，继续往下
#!/bin/bash

docker ps -a | grep mail-micro-service | awk '{print $1}' | xargs docker kill
docker ps -a | grep mail-micro-service | awk '{print $1}' | xargs docker rm