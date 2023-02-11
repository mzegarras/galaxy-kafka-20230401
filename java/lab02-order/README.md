## 1: Settings de Intellj
- Settings
  * Build, Execution, Deployment
    * Compiler 
      * Build project automatically => Enabled (devtools reload application)
      * Annotation Processors => Enable annotation processing => Enabled
  * Advanced Settings
    * Allow auto-make to start event if developer application is currently running => Enabled

## 2: Commands kafka
* Delete containers
```
docker-compose down -v
docker rm -f $(docker ps -a -q)
docker volume rm $(docker volume ls -q)

```
* Cluster
```
docker exec --interactive --tty kafka01 bash

kafka-topics --bootstrap-server localhost:9092 \
    --create \
    --topic orders \
    --replication-factor 2 \
    --partitions 3

kafka-topics --bootstrap-server localhost:9092 \
    --describe \
    --topic orders

kafka-console-consumer \
    --bootstrap-server kafka01:9092 \
    --topic orders  \
    --group "consumer01" \
    --from-beginning

kafka-consumer-groups --bootstrap-server localhost:9092 --list
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group myGroup

            
```

## 3: CURL
```
curl --location --request POST 'http://localhost:8080/orders' \
--header 'Content-Type: application/json' \
--data-raw '{
    "order":{
        "id":"1",
        "customerId":"C001",
        "amout":100
    }
}'

curl --location --request POST 'http://localhost:8080/orders' \
--header 'Content-Type: application/json' \
--data-raw '{
    "eventId":9903,
    "order":{
        "id":"1",
        "customerId":"C001",
        "amout":100
    }
}'


curl --location --request PUT 'http://localhost:8080/orders' \
--header 'Content-Type: application/json' \
--data-raw '{
    "eventId": 1,
    "order":{
        "id":"1",
        "customerId":"C001",
        "amout":100
    }
}'

```

## 4: order-consumer - multiple instances
  * Edit configuration
  * Modify options
  * Add VM Options
  ```
  -Dserver.port=8081
  ```

## 5: Postman
* Crear collection: Kafka
* Pre-Request Script
```

var customerId = ["CLI00001", "CLI00002", "CLI00003","CLI00004","CLI00005","CLI00006","CLI00007"]
var randomCustomerId = customerId[_.random(customerId.length - 1)];
pm.environment.set("randomCustomerId",randomCustomerId)

pm.environment.set("orderId", _.random(1, 1000))

pm.environment.set("random_amount", _.random(1, 350))

```
* Request
```
POST http://localhost:8080/orders
{
    "order":{
        "id":"{{orderId}}",
        "customerId":"{{randomCustomerId}}",
        "amout":{{random_amount}}
    }
}

PUT http://localhost:8080/orders
{
    "eventId":"{{orderId}}",
    "order":{
        "id":"{{orderId}}",
        "customerId":"{{randomCustomerId}}",
        "amout":{{random_amount}}
    }
}
```
* Stress
  * Seleccionar el collection "Kafka"
  * Seccionar "..."
  * Clic "Run Collection"

* Mongo Express
* URL: http://localhost:9090/db/ordersdb/orders
* Querys
  ```
  {"customerId": "CLI00001"}
  {"amout": {$gte:200}}
  {"amout": {$lte:3}}
  ```