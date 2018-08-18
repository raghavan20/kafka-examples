# Kakfa examples


## Producers and consumers - basic usage

`ProducerMain` produces simple records with key of type `String` and value of type `Long`. `ConsumerMain` and 
`ConsumerMainAssignmentMain` consume records. They demonstrate how to  automatically receive partition assignments 
vs manually assigning partitions and controlling from which offset to read from.

## `orders` example - `kafka` with `avro`

Run `OrderProducerMain` to produce order records and run `OrderConsumerMain` to consume order records.

**Dependencies**

-   zookeeper
-   kafka
-   schema-registry

The examples are tested with `confluent-5.0.0` downloadable from Confluent. All services could be started from the 
downloaded artifact. Alternatively used `docker-compose` to bring up Kafka and Zookeeper and use downloaded 
Confluent artifact to bring up `schema-registry`
 
[Download from Confluent Open Source](https://www.confluent.io/download) 


## Avro

Avro is the default choice to associate schema to topics. The examples in this project demonstrate how to serialize
a record / logical type to JSON or binary format using Avro encoders.

**Sample use of Avro by itself**

`AvroSampleUsageApp` demonstrates how can be used to generate serialized form of types either in JSON or binary form. 
[Documentation](https://avro.apache.org/docs/1.8.2/gettingstartedjava.html#Serializing+and+deserializing+without+code+generation)

This example does not use Java model classes for User and Order. If using model classes, code generation is required 
to serialize and deseralize with Avro. [Documentation](https://avro.apache.org/docs/1.8.2/gettingstartedjava.html#Serializing+and+deserializing+with+code+generation)

** Dependencies **

-    Maven dependency: `org.apache.avro:avro:1.8.2`

## TODO

- basic streaming example: count
- stream example: sum
- stream example: joins
- stream with Avro
- stream with SchemaRegistry
