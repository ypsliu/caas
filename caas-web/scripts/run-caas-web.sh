#!/bin/sh
nohup java -jar -DAPP_HOME="/rc/app/caas" -Denv="sit" \
 -Dcom.sun.management.jmxremote.port=9010 \
 -Dcom.sun.management.jmxremote.ssl=false \
 -Dcom.sun.management.jmxremote.authenticate=false \
 -XX:MaxMetaspaceSize=1024m -Xmx4096m -Xms4096m -Xmn2g \
 -XX:ParallelGCThreads=4 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC \
 -XX:+PrintGC \
 -XX:+PrintGCDetails \
 -XX:+PrintGCTimeStamps \
 -XX:+PrintGCDateStamps \
 -XX:+PrintGCApplicationConcurrentTime \
 -XX:+PrintGCApplicationStoppedTime \
 -XX:+PrintHeapAtGC \
 -Xloggc:/rc/app/caas/gc.log \
 caas-web-1.0.0-SNAPSHOT.jar >/dev/null 2>&1 &
