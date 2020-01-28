FROM wipp/pyramid-building:1.1.2
LABEL maintainer="National Institute of Standards and Technology"

ARG EXEC_DIR="/opt/executables"
ARG DATA_DIR="/data"

# Create folders
RUN mkdir -p ${EXEC_DIR} \
    && mkdir -p ${DATA_DIR}/inputs \
    && mkdir ${DATA_DIR}/outputs
    
# Install java 8 jdk
RUN apt-get update \
    && apt-get install -y openjdk-8-jdk \
    && update-alternatives --set java /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java

# Copy wipp-pyramid-plugin JAR
COPY target/wipp-pyramid-plugin*.jar ${EXEC_DIR}/wipp-pyramid-plugin.jar

# Set working directory
WORKDIR ${EXEC_DIR}

# Default command. Additional arguments are provided through the command line
ENTRYPOINT ["/usr/bin/java", "-jar", "wipp-pyramid-plugin.jar"]