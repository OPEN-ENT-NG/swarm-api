# Usage of ENT API

Configuring your "ENT connector" is necessary in order to make your quarkus be able to make API Call to ENT

Make sure you already have structure/users

go to your ENT application -> go to admin console (/admin) -> choose Structure -> Services (application list) -> "Connecteurs" -> "Créer un connecteur"

![connector](/docs/ent-auth-api/assets/connector1.png)

Voici les Scopes à ajouter

```
org.entcore.directory.controllers.StructureController|userList 
org.entcore.directory.controllers.UserController|get 
org.entcore.directory.controllers.UserController|getGroups  
org.entcore.directory.controllers.UserController|myinfos 
org.entcore.directory.controllers.UserController|listUserInStructuresByUAI 
org.entcore.directory.controllers.ClassController|get 
org.entcore.directory.controllers.ClassController|findUsers
org.entcore.directory.controllers.GroupController|getGroup
org.entcore.directory.controllers.DirectoryController|users
org.entcore.directory.controllers.DirectoryController|people
org.entcore.directory.controllers.DirectoryController|classes
org.entcore.directory.controllers.ClassController|findUsers
```

![connector](/docs/ent-auth-api/assets/connector2.png)

Ajouter le client et secret dans `application.properties`

```
app.rest-client.ent-client.username=<nom identifiant>
app.rest-client.ent-client.password=<passwordSecret>
```

la propriété `app.classaafids` contient la liste des classes dans laquelle on souhaite faire le filtre.

```
http://localhost:8090/directory/class/admin/list?structureId=<structureId>
```

pour récupérer l'externalId de la classe
exemple : `3A` correspond à l'externalId `3485$3A`
