#####################################
# BASE IMAGE FOR BUILDING THE PROJECT
#####################################
FROM gradle:7.4.2-jdk17 AS dependencies

WORKDIR /home/gradle/usvision

RUN mkdir app-analyses app-model app-persistence app-reports app-web \
    && chown gradle:gradle . -R

USER gradle

CMD gradle build --continuous

# COPY ALL PROJECTS build.gradle.kts FILES TO THEIR DESTINATIONS
COPY --chown=gradle:gradle  settings.gradle.kts                                                     ./
COPY --chown=gradle:gradle  build.gradle.kts                    gradle.properties                   ./
COPY --chown=gradle:gradle  app-analyses/build.gradle.kts       app-analyses/gradle.properties      ./app-analyses/
COPY --chown=gradle:gradle  app-model/build.gradle.kts          app-model/gradle.properties         ./app-model/
COPY --chown=gradle:gradle  app-persistence/build.gradle.kts    app-persistence/gradle.properties   ./app-persistence/
COPY --chown=gradle:gradle  app-reports/build.gradle.kts        app-reports/gradle.properties       ./app-reports/
COPY --chown=gradle:gradle  app-web/build.gradle.kts            app-web/gradle.properties           ./app-web/


# PRE-INSTALL JUST THE DEPENDENCIES -- THIS SHALL SPEEDUP FUTURE BUILDS
RUN gradle clean build

# COPY THE REST OF THE CODE
COPY --chown=gradle:gradle . ./





###########################
# BUILDER IMAGE
###########################
FROM dependencies AS builder

# BUILD THE FAT JAR
RUN gradle :app-web:shadowJar





############################
# RUNNER IMAGE
############################
FROM eclipse-temurin:17-jdk AS web

WORKDIR /usvision

ENV USVISION_HTTP_PORT=8080

EXPOSE ${USVISION_HTTP_PORT}

COPY --from=builder /home/gradle/usvision/app-web/build/libs/*-all.jar ./usvision-web.jar

ENTRYPOINT ["java", "-jar", "/usvision/usvision-web.jar"]