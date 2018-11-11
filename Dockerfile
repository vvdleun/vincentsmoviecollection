FROM adoptopenjdk/openjdk8-openj9:latest
RUN mkdir /opt/vincentsmoviescollection
COPY ./build/libs/vincentsmoviescollection-all.jar /opt/vincentsmoviescollection/
COPY ./config.json /opt/vincentsmoviescollection/
CMD ["java", "-jar", "/opt/vincentsmoviescollection/vincentsmoviescollection-all.jar", "--config", "/opt/vincentsmoviescollection/config.json"]
