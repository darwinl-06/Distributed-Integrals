# **Integrales Distribuidos 😭**

### Integrantes:

Darwin Lenis Maturana - A00381657

Juan David Patiño - A00381293

Juan Felipe Madrid - A00381242

Luis Pinillos - A00381323

## Introducción

Este proyecto implementa un sistema distribuido para la aproximación de integrales definidas utilizando varios métodos numéricos. El sistema permite a los usuarios ingresar funciones matemáticas y los límites de integración, y luego elige y ejecuta el método de aproximación más adecuado. El procesamiento se puede distribuir en múltiples máquinas para mejorar la eficiencia y la precisión de los cálculos.

### Características Principales

- **Métodos de Aproximación:** Implementación de métodos numéricos como el método del trapecio, el método de Simpson y la regla del punto medio.
- **Procesamiento Distribuido:** Soporte para la ejecución en configuraciones distribuidas de 4, 8 y 12 máquinas, utilizando técnicas de procesamiento SISD  (Single Instruction, Single Data).
- **Patrones de Diseño:** Uso de patrones de diseño como Master - Worker, Observer y Proxy.
- **Interfaz de Usuario:** Entrada de datos a través de una interfaz de línea de comandos que permite al usuario especificar la función y los límites de integración.
- **Análisis de Experimentos:** Herramientas para medir y comparar la aceleración y precisión del sistema en diferentes configuraciones y con varios parámetros.

## Instrucciones de compilación y ejecución

Tenemos 4 componentes: Cliente, Master, Worker y Observer. Para hacer el deploy debemos hacer los siguienes pasos:

- Instanciar el IceBox
- Instanciar el Observer
- Instanciar el Master
- Instanciar el Worker
- Instanciar el Cliente

Instanciamos el IceBox y el Observer en un PC de la sala. Debemos de meterno en la carpeta `iceStomConfig`  en la que encontramos dos archivos de configuracion, `config.icebox` y `config.service`. En estos debemos de poner la las direccion y puertos en los que queremos que corra.

`config.icebox`  

```java
#
# Enable Ice.Admin object
# The IceStorm service has its own endpoints (see config.service).
#
Ice.Admin.Endpoints=tcp -h localhost -p 9996
Ice.Admin.InstanceName=icebox

#
# The IceStorm service. The service is configured using a separate
# configuration file (see config.service).
#
IceBox.Service.IceStorm=IceStormService,37:createIceStorm --Ice.Config=config.service
```

`config.service`  

```java

IceStorm.InstanceName=DemoIceStorm

IceStorm.TopicManager.Endpoints=default -h localhost -p 10000

#
# This property defines the endpoints on which the topic
# publisher objects listen. If you want to federate
# IceStorm instances this must run on a fixed port (or use
# IceGrid).
#
IceStorm.Publish.Endpoints=tcp -h localhost -p 10001:udp -h localhost -p 10001

IceStorm.LMDB.Path=db
```

Al tener estos dos archivos de configuracion correctamente, procedemos a abrir una terminal y acceder al directorio `iceStomConfig`. Una vez aqui ponemos el comando `icebox --Ice.Config=config.icebox` para poner en ejecucion el IceBox y a su vez el Observer.

Ahora pasamos a instanciar el Master, en este tambien debemos de hacer la configuracion en el archivo `config.master`  poniendo la direccion y los puertos del PC en el que queremos que se ejecute.

`config.master`

```java
Ice.ProgramName=Master
Ice.Default.Host=localhost

# Configuration for the MasterInterface object adapter

MasterInterface.Endpoints=tcp -p 9099

TopicManager.Proxy=DemoIceStorm/TopicManager:default -h localhost -p 10000
```

Para ponerlo en ejecucion hacemos `java -jar master.jar` .

Ahora pasamos a instanciar el Worker. Hacemos la configuracion en el archivo `config.worker`  poniendo la direccion y los puertos del PC en el que queremos que se ejecute, la direccion del master y la direccion del PC en el que se esta ejecutando el IceBox.

`config.worker`

```java
#
# The client reads this property to create the reference to the
# "hello" object in the server.
#
Integral.Proxy=MasterIntegral:tcp -p 9099
TopicManager.Proxy=DemoIceStorm/TopicManager:default -h localhost -p 10000
Clock.Subscriber.Endpoints=tcp
#
# Uncomment to use the WebSocket transports instead.
#
#Hello.Proxy=hello:ws -p 10002:udp -p 10000:wss -p 10003

# Only listen on the ZeroTier's LIASOn1 interface by default.
#
Ice.Default.Host=localhost
```

Para ponerlo en ejecucion hacemos `java -jar worker.jar` .

Ahora pasamos a instanciar el Worker. Hacemos la configuracion en el archivo `config.client` poniendo la direccion y los puertos del master y la direccion del PC en el que que se quiere ejecutar para el Callback.

`config.client`

```java
#
# The client reads this property to create the reference to the
# "hello" object in the server.
#
Integral.Proxy=MasterIntegral:tcp -h localhost -p 9099
Callback.Client.Endpoints=default -h localhost

#
# Uncomment to use the WebSocket transports instead.
#

# Only listen on the ZeroTier's LIASOn1 interface by default.
#
Ice.Default.Host=localhost
```

Para ponerlo en ejecucion hacemos `java -jar client.jar` .

WORKERS:
[ 9-11 ], [ 6-7 ] ,  [ 13 ], [ 15 ] , [16-18] , [ 21-22 ]

CLIENT:
[ 4 ]

MASTER / ICEBOX:
[ 3 ]
