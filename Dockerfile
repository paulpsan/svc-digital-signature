FROM azul/zulu-openjdk:11.0.10
RUN apt update && apt install -y telnet iputils-ping vim

VOLUME /tmp
RUN mkdir -p /opt/middleware/logs
VOLUME /opt/middleware/logs
ARG DEPENDENCY=./target/dependency-docker
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

ENV EUREKA_URI "http://svc-discovery:8761/eureka"
ENV HOSTNAME "svc-signature"
ENV SPRING_PROFILE "local"
ENV CONFIG_URI "http://localhost:8890"
ENV CONFIG_USERNAME "pocketbank"
ENV CONFIG_PASSWORD "eHzSqPnMp3CWpzxB"

RUN apt update && apt install -y tzdata

ENV TZ=America/La_Paz
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY ./ssl/ssl_public_star_grupofortaleza.com.bo_2020.crt $JAVA_HOME/lib/security/
RUN keytool -import -noprompt -alias star_gfo_2020 -file $JAVA_HOME/lib/security/ssl_public_star_grupofortaleza.com.bo_2020.crt -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit

COPY ./ssl/ssl_public_star_grupofortaleza.com.bo_2021.crt $JAVA_HOME/lib/security/
RUN keytool -import -noprompt -alias star_gfo_2021 -file $JAVA_HOME/lib/security/ssl_public_star_grupofortaleza.com.bo_2021.crt -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit

COPY ./ssl/ssl_public_star_grupofortaleza.com.bo_2022.crt $JAVA_HOME/lib/security/
RUN keytool -import -noprompt -alias star_gfo_2022 -file $JAVA_HOME/lib/security/ssl_public_star_grupofortaleza.com.bo_2022.crt -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit

RUN keytool -v -list -keystore /usr/lib/jvm/zulu11-ca-amd64/lib/security/cacerts -alias star_gfo_2022

ENTRYPOINT ["java",                                                 \
            "-noverify",                                            \
            "-XX:MaxRAMPercentage=90.0",                            \
            "-XX:InitialRAMPercentage=80.0",                        \
            "-Dspring.profiles.active=${SPRING_PROFILE}",           \
            "-Dspring.jmx.enabled=false",                           \
            "-cp",                                                  \
            "app:app/lib/*",                                        \
            "com.fortaleza.svc.firmadigital.MsFirmaDigitalApplication"]
