FROM openjdk:11.0.11-jdk

RUN wget https://github.com/Yelp/dumb-init/releases/download/v1.2.5/dumb-init_1.2.5_amd64.deb
RUN dpkg -i dumb-init_*.deb

RUN useradd -ms /bin/bash amos
USER amos
WORKDIR /home/amos

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["dumb-init", "java","-jar","app.jar"]
