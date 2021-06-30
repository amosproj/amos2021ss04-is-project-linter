########################################################################
####### See README.md in this directory for further instructions #######
########################################################################
FROM openjdk:11.0.11-jdk

# install better process initializer
RUN wget https://github.com/Yelp/dumb-init/releases/download/v1.2.5/dumb-init_1.2.5_amd64.deb
RUN dpkg -i dumb-init_*.deb

# add user, running as sudo is not advised
RUN useradd -ms /bin/bash amos
USER amos
WORKDIR /home/amos

# copy the compiled jar
ARG JAR_FILE=build/libs/server.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["dumb-init", "java","-jar","-Xms1G" ,"-Xmx2G", "app.jar"]
