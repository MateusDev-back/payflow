FROM maven:3.8.3-openjdk-17

ENV PROJECT_HOME /usr/src/payflow
ENV JAR_NAME payflow-1.0.0.jar

RUN mkdir -p $PROJECT_HOME
WORKDIR $PROJECT_HOME

COPY . .

RUN mvn clean package -DskipTests

RUN mv $PROJECT_HOME/target/$JAR_NAME $PROJECT_HOME/

# Define o comando de entrada para rodar a aplicação
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "payflow-1.0.0.jar"]
