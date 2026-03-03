# Postgres SQL
## Requirements
* Install docker.

## run Postgres on docker container
Start Postgres by running the following command in the parent folder of config.

```bash
docker run --name carcassonne-psql \
-e POSTGRES_PASSWORD=mysecretpassword \
-v $(pwd)/config/init.sql:/docker-entrypoint-initdb.d/init.sql \
-d postgres:18-alpine
```

You can terminate the container by pressing ctrl-C


Note on windows you have to give absolute path to the config-directory. 
For example:

```bash
docker run --name carcassonne-psql \
-e POSTGRES_PASSWORD=mysecretpassword \
-v C:\Users\joe\Documents\config\init.sql:/docker-entrypoint-initdb.d/init.sql \
-d postgres:18-alpine
```

You can also make a batch file:
```bash
docker run --name carcassonne-psql \
-e POSTGRES_PASSWORD=mysecretpassword \
-v %cd%\config\init.sql:/docker-entrypoint-initdb.d/init.sql \
-d postgres:18-alpine
```




## Interact with database within the container
To run your own sql queries, you can use the following command.

```bash
docker exec -it carcassonne-psql psql -U postgres
```

