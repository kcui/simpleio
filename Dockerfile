FROM java:8-jre
COPY target/simpleio-1.0-SNAPSHOT.jar /data/
EXPOSE 8080 8081
WORKDIR /data/
CMD java -jar simpleio-1.0-SNAPSHOT.jar server