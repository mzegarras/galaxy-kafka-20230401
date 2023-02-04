# Iniciar Kafka

<details>
<summary>Mac/Windows/Linux</summary>
<p>

## Iniciar Zookeeper y Kafka Broker

- Iniciar Zookeeper.

```
./zookeeper-server-start.sh ../config/zookeeper.properties
```

- Iniciar Kafka broker 1.

```
./kafka-server-start.sh ../config/server01.properties
```

- Iniciar Kafka broker 2.

```
./kafka-server-start.sh ../config/server02.properties
```

- Iniciar Kafka broker "n".

```
./kafka-server-start.sh ../config/servern.properties
```

</p>
</details>

<details>
<summary>Docker</summary>
<p>

## Iniciar Zookeeper y Kafka Broker

- Limpiar containers.

```
docker volume rm $(docker volume ls -qf dangling=true)
```

- Iniciar single broker
```
docker-compose -f ./resources/docker-compose-single.yaml up -d
```

- Listar servicios
```
docker-compose -f ./resources/docker-compose-single.yaml ps
```

- Detener single broker
```
docker-compose -f ./resources/docker-compose-single.yaml down -v
```

- Iniciar cluster kafka
```
docker-compose -f ./resources/docker-compose-cluster.yaml up -d
```

- Listar servicios
```
docker-compose -f ./resources/docker-compose-cluster.yaml ps
```

- Detener cluster kafka
```
docker-compose -f ./resources/docker-compose-cluster.yaml down -v
```


</p>
</details>


## ¿Cómo crear un "topic"?

```
docker exec --interactive --tty kafka01 bash

kafka-topics --bootstrap-server localhost:9092 --list

kafka-topics --bootstrap-server localhost:9092 \
    --create \
    --topic weblog

kafka-topics --bootstrap-server localhost:9092 \
    --create \
    --topic test-topic \
    --replication-factor 1 \
    --partitions 4

kafka-topics --bootstrap-server localhost:9092 \
    --describe \
    --topic test-topic

```

## ¿Cómo crear un Producer como consola?

### Sin Key

```
docker exec --interactive --tty kafka01 bash

kafka-console-producer --bootstrap-server localhost:9092 --topic weblog
```

### Con Key
```
docker exec --interactive --tty kafka01 bash

kafka-console-producer  --bootstrap-server localhost:9092 --topic weblog --property "key.separator=:" --property "parse.key=true"

```


## ¿Cómo crear un Consumer como consola?
```
docker exec --interactive --tty kafka01 bash

kafka-console-consumer \
    --bootstrap-server kafka01:9092 \
    --topic weblog  \
    --from-beginning \
    --property print.headers=true \
    --property print.timestamp=true

kafka-console-consumer \
    --bootstrap-server kafka01:9092 \
    --topic weblog  \
    --group "consumer01" \
    --from-beginning

```

## Listar consumer groups 

```
kafka-consumer-groups --bootstrap-server localhost:9092 --list

kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group galaxy-application
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group galaxy-application-01

```