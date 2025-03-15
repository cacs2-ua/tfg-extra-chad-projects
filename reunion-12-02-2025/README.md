```sh
docker run --name postgres-basevital-develop -e POSTGRES_USER=basevital -e POSTGRES_PASSWORD=basevital -e POSTGRES_DB=basevital -p 6000:5432 -d postgres:13
```

```sh
docker run --name postgres-basevital-test -e POSTGRES_USER=basevital -e POSTGRES_PASSWORD=basevital -e POSTGRES_DB=basevital_test -p 6001:5432 -d postgres:13
```