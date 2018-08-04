package com.test.samples.avro;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class AvroSampleUsageApp {

    public static void main(String[] args) throws IOException {
        // schema for User
        File schemaFile = new File("src/main/resources/user.schema");
        Schema schema = new Schema.Parser().parse(schemaFile);

        // Create user objects as Generic Record objects
        GenericData.Record user1 = new GenericData.Record(schema);
        user1.put("userId", "user1");
        user1.put("userName", "john");

        GenericData.Record user2 = new GenericData.Record(schema);
        user2.put("userId", "user2");
        user2.put("userName", "steve");

        // where to write the output to
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // avro encode
//        writeToFileAsBinary(schema, user1, user2);
        writeAsJson(out, schema, user1, user2);
    }

    private static void writeAsJson(ByteArrayOutputStream out, Schema schema, GenericData.Record... users) throws IOException {
        DatumWriter writer = new GenericDatumWriter<>(schema);
        JsonEncoder jsonEncoder = EncoderFactory.get().jsonEncoder(schema, out);

        // serialize multiple records
        for (GenericData.Record user : users) {
            writer.write(user, jsonEncoder);
        }
        jsonEncoder.flush(); // ensures the data is written to OutputStream

        // print serialized JSON
        System.out.println(new String(out.toByteArray()));
    }

    private static void writeToFileAsBinary(Schema schema, GenericData.Record... users) throws IOException {
        DatumWriter writer = new GenericDatumWriter<>(schema);
        DataFileWriter<GenericData.Record> dataFileWriter = new DataFileWriter<>(writer);
        dataFileWriter.create(schema, new File("users.avro"));
        for (GenericData.Record user : users) {
            dataFileWriter.append(user);
        }
        dataFileWriter.close();
    }
}
