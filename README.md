# swarm-api

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Requirements
* OpenJDK 21+
* Docker version 20+ (24.0.0 if possible)

### Service requirements

* Springboard
  * Connector ENT to communicate with ENT API
  * potentially need to adjust `app` properties in `application.properties` to match your Springboard configuration

## Configuration settings.xml

Pour pouvoir utiliser le repository privé de CGI learning hub, il faut ajouter le fichier `settings.xml` dans le dossier `.m2` de votre répertoire utilisateur.

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                  http://maven.apache.org/xsd/settings-1.0.0.xsd">                                                                                   <servers>
    <server>
        <id>nexus-snapshots</id>
        <username>${NEXUS_USERNAME}</username>
        <password>${NEXUS_PASSWD}</password>                                                                                                         </server>
    <server>
        <id>nexus-releases</id>
        <username>${NEXUS_USERNAME}</username>
        <password>${NEXUS_PASSWD}</password>
    </server>                                                                                                                                    </servers>
</settings>                                                                                                                                                     
```

Remplacer les variables `${NEXUS_USERNAME}` et `${NEXUS_PASSWD}` par vos identifiants de connexion.

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

Un Springboard local est nécessaire pour utiliser l'API ENT.

Vous pouvez utiliser le Springboard local pour créer un connecteur en suivant les instructions sur le lien suivant :
- [Consume ENT API](docs/ent-auth-api/ent-auth-api.md)

### Provisioning user ENT with keycloak

Simulation utilisateur ENT dans le keycloak ici :

- [Provisioning User ENT](docs/user-provisioning/user-provisioning.md)


## Configuration générale

### Service API ENT

| Propriété                                | Description                                                                                  | Exemple                              |
|------------------------------------------|----------------------------------------------------------------------------------------------|--------------------------------------|
| `%dev.quarkus.rest-client.ent-client.url` | L'URL du service API REST de l'ENT utilisée dans l'environnement de développement.            | `http://localhost:8090`              |
| `app.rest-client.ent-client.username`    | Le nom d'utilisateur pour l'authentification auprès du service API REST.                      | `swarm`                              |
| `app.rest-client.ent-client.password`    | Le mot de passe associé à l'utilisateur pour l'authentification auprès du service API REST.    | `azerty123`                          |

### Variables d'application spécifiques

| Propriété                     | Description                                                                                  | Exemple                       |
|-------------------------------|----------------------------------------------------------------------------------------------|-------------------------------|
| `app.classaafids`              | Liste des identifiants d'une classe spécifique (format séparé par des virgules sans espace). | `bddb0a72-ea8b-4774-8114-d8bc1adc65ac,b3930c71-d0e9-4ddf-b88a-c41bbd649375`             |

### Configuration du serveur de messagerie

| Propriété                           | Description                                                                                      | Exemple                        |
|-------------------------------------|--------------------------------------------------------------------------------------------------|--------------------------------|
| `quarkus.mailer.from`               | L'adresse e-mail utilisée comme expéditeur pour l'envoi des e-mails.                              | `no-reply@swarm.fr` |
| `quarkus.mailer.host`               | L'adresse du serveur SMTP utilisé pour envoyer des e-mails.                                       | `mx.lyceeconnecte.fr`          |
| `quarkus.mailer.port`               | Le port utilisé pour la connexion au serveur SMTP.                                                | `587`                          |
| `quarkus.mailer.tls`                | Indique si TLS est activé (vrai/faux).                                                            | `false`                        |
| `quarkus.mailer.start-tls`          | Définit si le TLS est requis pour la connexion au serveur SMTP.                                   | `REQUIRED`                     |
| `quarkus.mailer.username`           | Le nom d'utilisateur pour l'authentification SMTP.                                                | `no-reply@swarm.fr`            |
| `quarkus.mailer.password`           | Le mot de passe associé à l'utilisateur pour l'authentification SMTP.                             | `password`                     |

### Autres paramètres

| Propriété           | Description                                                                                      | Exemple                                  |
|---------------------|--------------------------------------------------------------------------------------------------|------------------------------------------|
| `host`              | L'URL du service d'hébergement principal de l'application.                                        | `https://swarm.support-ent.fr/`          |
| `mail.domain`       | Le domaine utilisé pour les adresses e-mail dans les communications sortantes.                    | `ng1.support-ent.fr`                     |

### Configuration spécifique à l'environnement

| Propriété                        | Environnement    | Description                                                                                      | Exemple  |
|----------------------------------|------------------|--------------------------------------------------------------------------------------------------|----------|
| `%dev.quarkus.mailer.mock`       | Développement    | Indique si l'envoi de mails est simulé dans l'environnement de développement.                     | `false`  |
| `%test.quarkus.mailer.mock`      | Test             | Indique si l'envoi de mails est simulé dans l'environnement de test.                              | `true`   |

---

### Remarques

* Les propriétés avec le préfixe `%dev` sont utilisées uniquement dans l'environnement de développement.
* Les propriétés avec le préfixe `%test` sont utilisées uniquement dans l'environnement de test.



### Keycloak config

```bash
# access keycloak admin
admin // admin

# authent user
e77e6d72-c7c1-4f51-ba1d-dbaa81abec9d // password

# si vous utiliez d'autres utilisateurs ENT,
# créer votre propre user en prenant l'user id de l'ENT en username

# obsolète 
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


