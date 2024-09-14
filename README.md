# swarm-api

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Requirements
* OpenJDK 21+
* Docker version 20+ (24.0.0 if possible)

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/
> Swagger at http://localhost:8080/api/q/swagger-ui/ (see Authenticate with Swagger)

### Authenticate with Swagger

To try the api in swagger you must click on authorize and go to SecurityScheme (OAuth2 ,implicit) with client id only .
Enter the client id "console". Login with a user. Close the window and you are connected.

If you want to log out you must clear the cookies and do :
```shell script
./mvnw clean compile quarkus:dev
```

### Use ENT API

- [Consume ENT API](docs/ent-auth-api/ent-auth-api.md)

### Provisioning user ENT with keycloak

- [Provisioning User ENT](docs/user-provisioning/user-provisioning.md)


### Keycloak config

```bash
# access keycloak admin
admin // admin

# authent user
user.dev // password
```

## Add Debug for Java with vscode

create a folder `.vscode` and inside this folder `launch.json`

You must add 

```bash
# .vscode/launch.json
{
    // Use IntelliSense to learn about possible attributes.
    // Hover to view descriptions of existing attributes.
    // For more information, visit: https://go.microsoft.com/fwlink/?linkid=830387
    "version": "0.2.0",
    "configurations": [     
        {
            "type": "java",
            "name": "Remote Debug (Attach Quarkus)",
            "request": "attach",
            "hostName": "localhost",
            "port": 5005
        },
        {
            "type": "java",
            "name": "Launch Current File",
            "request": "launch",
            "mainClass": "${file}"
        },
    ]
}
# from vscode UI : Run -> Start Debugging
```

## Debug HTTP Rest 

To debug HTTP requests from clients add the following to the application.properties file : 

```shell script
quarkus.rest-client.logging.scope=request-response
quarkus.rest-client.logging.body-limit=1024
quarkus.log.category."request.tracer".level=DEBUG
quarkus.log.category."response.tracer".level=DEBUG
quarkus.log.category."org.apache.http".level=DEBUG
quarkus.log.category."org.jboss.resteasy.reactive.client.logging".level=DEBUG
```


## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true -Dquarkus.native.additional-build-args=-J-Xmx6g,-march=compatibility
```

You can then execute your native executable with: `./target/swarm-api-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
