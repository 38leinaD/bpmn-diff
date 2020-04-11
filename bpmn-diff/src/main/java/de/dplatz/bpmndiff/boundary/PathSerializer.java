package de.dplatz.bpmndiff.boundary;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;

public class PathSerializer implements JsonbSerializer<Path>, JsonbDeserializer<Path> {

    @Override
    public void serialize(Path obj, JsonGenerator generator, SerializationContext ctx) {
        System.out.println("XXX serialize");
       if (obj != null) {
         ctx.serialize(obj.toString(), generator);
       }
    }

    @Override
    public Path deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        System.out.println("XXX deserialize");

        return Paths.get(parser.getString());
    }
}