FROM openjdk:8-jre-alpine
VOLUME /tmp
ADD target/*.war app.war
ENV JAVA_OPTS "-Xmx192m -Xms192m -Djava.security.egd=file:///dev/./urandom -XX:+HeapDumpOnOutOfMemoryError "
ENV TZ Africa/Johannesburg
ENV dynamic_port 5400
# NOT USE YET -XX:+UseG1GC
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app.war --server.port=${dynamic_port}" ]