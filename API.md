# API Java #

En caso de necesitar cargar una gran cantidad de datos de una manera automática a la red social, es posible conectarse mediante una API de java.

Esta API es bastante limitada, la idea es que el usuario avanzado pueda utilizarla para crear un mini programa que se encargue de cargar la red social y posteriormente ser manipulada con la interfaz de SocialNetworkManager.

# Métodos existentes #

> Mayor información en el javadoc: http://socialnetworks.b9.cl/javadoc/cl/b9/socialNetwork/api/SocialNetwork.html

  * Crear una red social
  * Crear una familia de actores
  * Crear un actor de una determinada familia
  * Recuperar un actor desde una red social
  * Guardar la red social a un archivo [SNM](SNM.md) para poder ser leido por SocialNetworkManager

# Ejemplo de Uso #

```

public static void main(String[] args){
   SocialNetwork manager = cl.b9.socialNetwork.api.SocialNetwork();
   SNActorFamily persona = manager.createActorFamily("persona");
   SNActor pedro = manager.createActor(persona, "pedro");
   SNActor juan = manager.createActor(persona, "juan");
   SNActor diego = manager.createActor(persona, "diego");
   SNActor luis = manager.createActor(persona, "luis");
   SNActor carlos = manager.createActor(persona, "carlos");
   manager.createRelation("reporta a",juan, "subordinado", diego, "jefe");
   manager.createRelation("reporta a",pedro, "subordinado", diego, "jefe");
   manager.createRelation("reporta a",diego, "subordinado", luis, "jefe");
   manager.createRelation("reporta a",carlos, "subordinado", luis, "jefe");
   manager.save("red1.snm");
}


```


# Publicaciones DCC #

> En http://code.google.com/p/socialnetworkmanager/source/browse/trunk/src/cl/b9/socialNetwork/tools/PublicacionesDCC.java se puede ver un ejemplo mucho más completo de como utilizar esta api la carga de datos de manera automática. En ese caso se muestra la carga de una red social de todas las publicaciones del departamento de ciencias de la computación de la universidad de chile.