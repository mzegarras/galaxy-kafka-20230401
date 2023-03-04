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
* Create order
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
  ```
* Update order
  ```
  curl --location --request PUT 'http://localhost:8080/orders' \
  --header 'Content-Type: application/json' \
  --data-raw '{
      "eventId": 3,
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
  * Create order
    ```
    POST http://localhost:8080/orders
    {
        "order":{
            "id":"{{orderId}}",
            "customerId":"{{randomCustomerId}}",
            "amout":{{random_amount}}
        }
    }
    ```
  * Update order
    ```
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

## 6: Mongo express
  * URL: http://localhost:9090/db/ordersdb/orders
  * Querys
    ```
    {"customerId": "CLI00001"}
    {"amout": {$gte:200}}
    {"amout": {$lte:3}}
    ```

## 7: Consumer
  ```
  docker exec --interactive --tty kafka01 bash
  
  kafka-console-consumer \
  --bootstrap-server kafka01:9092 \
  --topic orders  \
  --group "consumer01" \
  --from-beginning
  
  kafka-consumer-groups --bootstrap-server localhost:9092 --list
  kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group consumer01
  ```
## 8: Kafka UI
* URL: http://localhost:9091/
* docker-compose.yaml
  ```yaml
    akhq:
      image: tchiotludo/akhq
      environment:
        AKHQ_CONFIGURATION: |
          akhq:
            connections:
              docker-kafka-server:
                properties:
                  bootstrap.servers: "kafka01:9092,kafka02:9092,kafka03:9092"
      ports:
        - 9091:8080
  ```
## 9: Blocking Retry
En la clase KafkaConfig copiar.
  ```java
      @Bean
        public DefaultErrorHandler errorHandler(){

        var fixedBackOff = new FixedBackOff(1000L, 1000);
        var errorHandler = new DefaultErrorHandler(fixedBackOff);

        errorHandler
        .setRetryListeners(((record, ex, deliveryAttempt) -> {
        log.info("Error Record intento Listener, Exception : {} , deliveryAttempt : {} "
        ,ex.getMessage(), deliveryAttempt);
        }));

        errorHandler.addRetryableExceptions(SocketTimeoutException.class);
        errorHandler.addNotRetryableExceptions(NullPointerException.class);

        return  errorHandler;
        }
  ```
### 9.1: Error Handling
* Configurar: 
  * spring.kafka.ack-mode: manual
  * spring.kafka.consumer.auto-offset-reset: latest
  * enable.auto.commit: false

### 9.2:  Crear topics
  ```
  docker-compose down -v
  docker-compose up -d
  docker exec --interactive --tty kafka01 bash
  
  kafka-topics --bootstrap-server localhost:9092 \
  --create \
  --topic orders \
  --replication-factor 2 \
  --partitions 3
  ```
### 9.3:  Generar casos
* Éxito
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
  ```  
* Error
  ```
  curl --location --request PUT 'http://localhost:8080/orders' \
  --header 'Content-Type: application/json' \
  --data-raw '{
      "eventId": 2,
      "order":{
          "id":"666",
          "customerId":"C001",
          "amout":100
      }
  }'
  ```
* Éxito
  ```
  curl --location --request POST 'http://localhost:8080/orders' \
  --header 'Content-Type: application/json' \
  --data-raw '{
      "order":{
          "id":"3",
          "customerId":"C001",
          "amout":100
      }
  }'
  ```  
### 9.4:  Offsets 
* Update offset
```bash
docker exec --interactive --tty kafka01 bash
kafka-consumer-groups --bootstrap-server localhost:9092 --list
kafka-consumer-groups --bootstrap-server localhost:9092 --group consumer02 --describe
```
* Mover offset
```bash
kafka-consumer-groups --bootstrap-server localhost:9092 --group consumer02 --reset-offsets --shift-by 1 --topic orders --execute
kafka-consumer-groups --bootstrap-server localhost:9092 --group consumer02 --reset-offsets --shift-by -1 --topic orders --execute
kafka-consumer-groups --bootstrap-server localhost:9092 --group consumer02 --reset-offsets --shift-by 2 --topic orders --execute
```
* Mover earliest
```bash
kafka-consumer-groups --bootstrap-server localhost:9092 --group consumer02 --reset-offsets --to-earliest --topic orders --execute
```
* Mover latest
```bash
kafka-consumer-groups --bootstrap-server localhost:9092 --group consumer02 --reset-offsets --to-latest --topic orders --execute
```
## 10: Non-Blocking Retry
  ```
  docker exec --interactive --tty kafka01 bash
  
  kafka-topics --bootstrap-server localhost:9092 \
  --create \
  --topic orders \
  --replication-factor 2 \
  --partitions 3
  
  kafka-topics --bootstrap-server localhost:9092 \
  --create \
  --topic orders-retry-0 \
  --replication-factor 2 \
  --partitions 1
  
  kafka-topics --bootstrap-server localhost:9092 \
  --create \
  --topic orders-retry-1 \
  --replication-factor 2 \
  --partitions 1
  
  kafka-topics --bootstrap-server localhost:9092 \
  --create \
  --topic orders-retry-2 \
  --replication-factor 2 \
  --partitions 1
  
  kafka-topics --bootstrap-server localhost:9092 \
  --create \
  --topic orders-dlt \
  --replication-factor 2 \
  --partitions 1
  ```
### 10.1: Add Retryable
* OrderConsumerManual
```java
@RetryableTopic(
        attempts = "4",
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
        backoff = @Backoff(delay = 10000, multiplier = 5.0),
        //backoff = @Backoff(delay = 5000),
        autoCreateTopics = "false",
        exclude = {SerializationException.class, DeserializationException.class}

)
```
* After 5s ~ Delay = 5000
* After 25s ~ Delay = 5000 * 5 = 25000
* After 125s ~ Delay = 25000 * 5 = 125000 with Max Delay of 200000
### 10.2: Add Retryable
```java
    @DltHandler
    public void handleDlt(OrderEvent message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("*****Message: {} handled by dlq topic: {}", message, topic);
    }
```
