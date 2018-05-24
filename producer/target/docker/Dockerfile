FROM java
ADD 0/exampleproducer-assembly-0.1-SNAPSHOT.jar /app/exampleproducer-assembly-0.1-SNAPSHOT.jar
COPY 1/dockerscripts /app/
RUN ["chmod", "+x", "\/app\/entrypoint.sh"]
RUN ["chmod", "+x", "\/app\/wait-for-it.sh"]
ENTRYPOINT ["\/app\/entrypoint.sh"]
CMD ["exampleproducer", "0.1-SNAPSHOT"]