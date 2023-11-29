FROM airdock/oracle-jdk:1.8
LABEL maintainer "huguiqi@hhotel.com"
# ascdc/jdk8
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

RUN  head -n 1 /etc/issue
RUN apt-get update
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo "Asia/Shanghai" >/etc/timezone && apt-get install -y tzdata \
&& dpkg-reconfigure -f noninteractive tzdata

#COPY ./libs/*  $JAVA_HOME/jre/lib/security/
RUN echo "JAVA_HOME:$JAVA_HOME"
RUN mkdir -p /data /workspace /data/count /data/logs
VOLUME ["/data","/workspace"]
WORKDIR /workspace
COPY ./app.jar /workspace
RUN pwd
ARG JAVA_PARAMS
RUN echo $APP_ID

ENV APPID=rabbitmq-product \
APP_PORT=80 \
ENV_TYPE=Test \
JVM=$JAVA_PARAM \
SPRING=""

ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -jar -Dserver.port=$APP_PORT app.jar $JVM
CMD ["-Dspring.profiles.active=$ENV_TYPE"]

EXPOSE 80
