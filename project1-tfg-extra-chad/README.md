## 🚀 Levantar el Proyecto en Local

---

### 🔥 Ejecutar la aplicación a partir del application.properties


```sh
mvn spring-boot:run
```

### 🔥 Ejecutar la aplicación a partir de los contenedores de docker

1. Para levantar la base de datos de `PostgreSQL` en un contenedor de `Docker`, ejecutar el siguiente comando:

```sh
docker run --name postgres-vitalsanity-develop -e POSTGRES_USER=vital -e POSTGRES_PASSWORD=vital -e POSTGRES_DB=vital -p 5058:5432 -d postgres:13
```

2. Para ejecutar la aplicación para que se conecte a la base de datos de PostgreSQL, ejecutar el siguiente comando:

```sh
mvn spring-boot:run -D spring-boot.run.profiles=postgres
```

3. Para levantar la base de datos de `PostgreSQL` para los TESTS en un contenedor de `Docker`, ejecutar el siguiente comando:

```sh
docker run --name postgres-vitalsanity-test -e POSTGRES_USER=vital -e POSTGRES_PASSWORD=vital -e POSTGRES_DB=vital_test -p 5059:5432 -d postgres:13
```

4. Para ejecutar los tests para que se ejecuten con la base de datos de PostgreSQL, ejecutar el siguiente comando:

```sh
mvn test "-Dspring-boot.run.profiles=postgres"
```
