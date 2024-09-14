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
```

![connector](/docs/ent-auth-api/assets/connector2.png)

