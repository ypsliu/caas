#!/bin/sh
nohup java -jar -DAPP_HOME="/rc/app/caas-loadtest" \
 -XX:MaxMetaspaceSize=1024m -Xmx4096m -Xms4096m \
 -XX:ParallelGCThreads=4 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC \
 -XX:+PrintGC \
 -XX:+PrintGCDetails \
 -XX:+PrintGCTimeStamps \
 -XX:+PrintGCApplicationConcurrentTime \
 -XX:+PrintGCApplicationStoppedTime \
 -XX:+PrintHeapAtGC \
 -Xloggc:/rc/app/caas-loadtest/gc.log \
 caas-loadtest-1.0.0-SNAPSHOT.jar >/dev/null 2>&1 &
