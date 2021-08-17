# Reservas API
Microservicio desarrollado en Java 16 con Spring Boot que consiste en 2 endpoints REST.

## Operaciones

### Crear una Reserva
Permite crear una reserva agregando la reserva a una cola y procesandola luego. Luego de ser guardada la reserva se envia un correo.

- Metodo: **POST** 

- URL: http://localhost:4005/reservas/crear-reserva

- Body:

        {
            "fechaIngreso": "2021-08-17",
            "fechaSalida": "2021-08-29",
            "totalDias": 2,
            "numeroPersonas": 3,
            "titularReserva": "Jose Reyes",
            "numeroHabitaciones": 5,
            "numeroMenores": 0,
            "email": "ejemplo@hotmail.com"
        }

- Respuesta:
    - HTTP 202: Reserva agregada a la cola
    - HTTP 400: Petición incorrecta
    - Body: mensaje

### Consultar una reserva
Consulta una reserva creada previamente

- Metodo: **GET**

- URL: http://localhost:4005/reservas/consultar-reserva/{id}

- Respuesta:

        {
            "fechaIngreso": "2021-08-17",
            "fechaSalida": "2021-08-29",
            "totalDias": 2,
            "numeroPersonas": 3,
            "titularReserva": "Jose Reyes",
            "numeroHabitaciones": 5,
            "numeroMenores": 0,
            "email": "ejemplo@hotmail.com"
        }

## Compilación y Ejecución
Los requisitos para poder ejecutar completamente el microservicio son los siguientes:

1. Tener el JDK de Java 16 instalado
2. Tener Docker instalado (Docker o Docker Desktop en Windows)
3. Generar y configurar una llave de aplicación de Gmail para el envio de correo electronico:
    - En gestionar cuenta de Google > Seguridad > Contraseñas de aplicaciones generar una para Gmail
    - Configurar el correo de gmail en el archivo docker-compose.yml en la variable de entorno SMTP.USERNAME
    - Configurar la contraseña de aplicación en el archivo docker-compose.yml en la variable de entorno SMTP.PASSWORD
    - Con el correo configurado se enviaran los correos electronicos.

Luego de haber descargado el codigo ejecutar ```mvn clean package``` para generar el archivo .jar.

Ejecutar el comando ```docker-compose up``` para construir la imagen y ejecutar los dos contenedores: el del microservicio y el de rabbit-mq.

## Arquitectura
La aplicación se compone de un microservicio llamada reservas-api, una cola de mensajes RabbitMQ y una base de datos H2Database. 

Actualmente faltan mas componentes para completar una arquitectura de microservicios como el api-gateway, servicio de descubrimiento, administrador de logs y una centralización de la configuración, sin embargo se cumple con la responsabilidad unica de los microservicios, la atomicidad y la independencia por la contenerización.

