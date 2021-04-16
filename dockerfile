FROM java:8
VOLUME /tmp 
ADD wechat-0.0.1-SNAPSHOT.jar app.jar 
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Duser.timezone=GMT+8","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]