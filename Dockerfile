FROM wipp/pyramid-building:1.1.3
LABEL maintainer="National Institute of Standards and Technology"

# Create folders
RUN mkdir -p /opt/executables \
    && mkdir -p ${DATA_DIR}/inputs \
    && mkdir -p ${DATA_DIR}/outputs
    
# Install java 8 jdk
RUN apt-get update \
    && apt-get install -y openjdk-8-jdk \
    && update-alternatives --set java /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java

# Copy wipp-pyramid-plugin JAR
COPY target/wipp-pyramid-plugin*.jar /opt/executables/wipp-pyramid-plugin.jar

# Default command. Additional arguments are provided through the command line
ENTRYPOINT ["/usr/bin/java", "-jar", "/opt/executables/wipp-pyramid-plugin.jar"]