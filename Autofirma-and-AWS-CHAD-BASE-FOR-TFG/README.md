# tpvv-BoarDalo: Tu Plataforma de Pago Online de Confianza

## 💸 ¿Por Qué tpvv-BoarDalo?

---

tpvv-BoarDalo transforma la manera en que manejas pagos online: seguro, rápido y fácil de integrar.
Ofrecemos una solución confiable que se adapta a cualquier negocio,
brindando una experiencia de pago moderna y fluida tanto para ti como para tus clientes.
¡Con tpvv, llevas tus transacciones al siguiente nivel!

## 💪 Componentes del equipo
 
---

Somos el grupo **G15** de parácticas de la asignatura de **Ingeniería Web** de la **Universidad de Alicante**. Estos son nuestros datos por si necesitáis contactar con nosotros para resolver cualquier duda o problema:

- Cristian Andrés Córdoba Silvestre (**cacs2@alu.ua.es**)
- Daniel Ripoll Sánchez (**drs35@alu.ua.es**)
- Luis Alfonso Jiménez Rodríguez (**lajr1@alu.ua.es**)

## 📦 Explicación de la API

---

La API de tpvv-BoarDalo es una API REST que permite realizar pagos online de forma segura y sencilla.

## 🚀 Levantar el Proyecto en Local

---

### 🔥 Ejecutar la aplicación a partir del application.properties


```sh
./mvnw spring-boot:run
```

### 🔥 Ejecutar la aplicación a partir de los contenedores de docker

1. Para levantar la base de datos de `PostgreSQL` en un contenedor de `Docker`, ejecutar el siguiente comando:

```sh
docker run --name postgres-basevital-develop -e POSTGRES_USER=basevital -e POSTGRES_PASSWORD=basevital -e POSTGRES_DB=basevital -p 6000:5432 -d postgres:13
```

2. Para ejecutar la aplicación para que se conecte a la base de datos de PostgreSQL, ejecutar el siguiente comando:

```sh
mvn spring-boot:run -D spring-boot.run.profiles=postgres
```

3. Para levantar la base de datos de `PostgreSQL` para los TESTS en un contenedor de `Docker`, ejecutar el siguiente comando:

```sh
docker run --name postgres-basevital-test -e POSTGRES_USER=basevital -e POSTGRES_PASSWORD=basevital -e POSTGRES_DB=basevital_test -p 6001:5432 -d postgres:13
```

4. Para ejecutar los tests para que se ejecuten con la base de datos de PostgreSQL, ejecutar el siguiente comando:

```sh
mvn test "-Dspring-boot.run.profiles=postgres"
```


## 🔎 Explicación del funcionamiento de la Integración del Proyecto

---

Al iniciar la aplicación, se crea automáticamente un comercio de prueba.
La `api key` de este comercio es **"mi-api-key-12345"**
Este comercio puede ser utilizado para realizar las pruebas de **integración**.

### 👁️ Estructura de los endpoints

---

- `GET /pago/form?importe=""&idTicket=""`: Este endpoint se encarga de mostrar el formulario de pago.
    - Esta `URL` recibe dos parámetros **obligatorios**: `importe` y `idTicket`.
    - Asimismo, la `API Key` es manejada por este endpoint.
    - Para que el `GET` funcione correctamente, la `API Key` debe de venir incluida en la cabecera `HTTP` (explicado con más detalle abajo).


- `POST ("/pago/realizar")`: Este endpoint se encarga de mostrar el formulario de pago.
    - Este `endpoint` solo maneja como parámetro la `API Key` (nuevamente, la `API Key` debe de venir incluida en la cabecera `HTTP`).

## 🟢 Cómo Probar los Endpoints desde Postman


### 📌 GET /pago/form?importe=""&idTicket=""

---

1. Una vez abierto `Postman`, en el cuadro de texto de la `URL` elegir `GET` e ingresar la siguiente dirección URL (podéis elegir los valores de `importe` y de `idTicket` que queráis):
    - `http://localhost:8123/pago/form?importe=100&idTicket=1`
      ![img.png](public-resources/doc-images/img.png)

2. En la pestaña de `Headers`, agregar la siguiente clave-valor:
    - En la columna `Key`, poner: `X-API-KEY`
    - En la columna `Value`, poner: `mi-api-key-12345`

![img_1.png](public-resources/doc-images/img_1.png)


3. Ingresados todos estos datos, haced click en `Send`. Si todo está correcto, se mostrará el `HTML` del formulario de pago, el cual tendrá que ser similar al siguiente:

```html
<!DOCTYPE html>
<html>

<head>
    <title>Formulario de Pago</title>
    <meta charset="UTF-8" />
</head>

<body>
<h1>Realizar Pago</h1>
<form action="/pago/realizar" method="post"><input type="hidden" name="_csrf" value="kWlTELFggz0CyUOUUSnTJMoZ9yAozoD4Yl8VR9p7a-zjVKmxqQhhKNJXswwv-SD1aQTnEPgp2hkbq7HVWjt3c-xKUorQYJuC"/>
    <div>
        <label for="importe">Importe:</label>
        <input type="text" id="importe" readonly name="importe" value="100.0" />
    </div>
    <div>
        <label for="ticketExt">ID del Ticket:</label>
        <input type="text" id="ticketExt" readonly name="ticketExt" value="1" />
    </div>
    <div>
        <label for="tarjeta">Número de Tarjeta:</label>
        <input type="text" id="tarjeta" required name="tarjeta" value="" />
    </div>
    <div>
        <button type="submit">Realizar Pago</button>
    </div>
</form>
</body>

</html>
```

![img_2.png](public-resources/doc-images/img_2.png)

### 📌 POST /pago/realizar

---

1. En el cuadro de texto de la `URL`, elegir `POST` e ingresar la siguiente dirección (podéis elegir los valores de importe y de idTicket que queráis):
    - `http://localhost:8123/pago/realizar`

![img_3.png](public-resources/doc-images/img_3.png)

2. En la pestaña de `Headers`, agregar la siguiente clave-valor:
    - En la columna `Key`, poner: `X-API-KEY` en una fila y `Content-Type` en otra.
    - En la columna `Value`, poner: `mi-api-key-12345` en una fila y `application/json` en la otra.

![img_4.png](public-resources/doc-images/img_4.png)

3. En la pestaña `Body`, elegid `raw` y `JSON` e ingresad los siguientes datos (formato `JSON`) (Podéis elegir los valores de `importe`, `ticketExt` y `tarjeta` que queráis):
    ```json
    {
        "importe": 100,
        "ticketExt": 1,
        "tarjeta": "1234567890123456"
    }
    ```
![img_5.png](public-resources/doc-images/img_5.png)

4. Ingresados todos estos datos, haced click en `Send`.
    - Si todo está correcto, se mostrará el siguiente mensaje:
        - Pago procesado correctamente.
    - Si hay algún error, se mostrará el siguiente mensaje:
        - Error: Faltan datos requeridos (`importe`, `ticketExt`, `tarjeta`).

![img_6.png](public-resources/doc-images/img_6.png)

## ❗Nota Final

---

La forma de comprobar que podéis realizar los pagos correctamente con los datos de vuestro propio `comercio` es **exactamente la misma** a la que acabamos de explicar.
Simplemente decidnos los **datos** de vuestro `comercio` y nosotros nos encargaremos de registrar vuestro `comercio` en nuestra base de datos y nosotros os crearemos y os asignaremos una `API Key`, la cual deberéis de almacenar y manejar de forma correcta desde vuestra aplicación.
Una vez establecidos todos estos datos, simplemente seguid **exactamente** los mismos pasos que ya os hemos expuesto arriba.

---

## Eso es todo, 🤑🤑¡A disfrutar de tpvv-BoarDalo! 🤑🤑


 
