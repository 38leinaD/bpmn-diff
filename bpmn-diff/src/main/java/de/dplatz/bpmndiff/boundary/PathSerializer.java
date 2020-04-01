package de.dplatz.bpmndiff.boundary;
import java.nio.file.Path;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

public class PathSerializer implements JsonbSerializer<Path> {

    @Override
    public void serialize(Path obj, JsonGenerator generator, SerializationContext ctx) {
       if (obj != null) {
         ctx.serialize(obj.toString(), generator);
       }
    }
}