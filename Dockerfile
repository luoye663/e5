# 基于 Maven 和 Java 17 镜像构建 Docker 镜像
FROM maven:3.8.3-openjdk-17 AS build
# 指定工作目录
WORKDIR /app
# 将项目文件复制到容器中
COPY . .
# 运行 Maven 命令构建项目并打包为 JAR 文件
RUN ls
RUN mvn clean package -DskipTests

# # 使用 Java 17 的镜像作为基础镜像 
FROM docker.io/library/openjdk:17-jdk-slim
# 将 JAR 文件复制到容器中 
COPY --from=build /app/target/*.jar e5-backend.jar
# 暴露 Spring Boot 应用程序使用的端口 EXPOSE 8080 

# 在容器中运行应用程序 
ENTRYPOINT ["java", "-jar", "/e5-backend.jar"]

