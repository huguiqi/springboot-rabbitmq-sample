#!/usr/bin

echo backup last image to version: $1

IMAGE_ID=`docker images --format '{{.ID}}' registry.cn-shanghai.aliyuncs.com/rios/server-container:latest`
echo imageId is $IMAGE_ID
docker tag $IMAGE_ID registry.cn-shanghai.aliyuncs.com/hz/server-container:$1

echo docker build -t registry.cn-shanghai.aliyuncs.com/rios/server-container:$1 .
docker build --no-cache -t registry.cn-shanghai.aliyuncs.com/hz/server-container:$1 --build-arg APP_ID=rabbitmq=consumers
