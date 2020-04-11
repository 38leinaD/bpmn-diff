package de.dplatz.bpmndiff.boundary;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class JSONBConfiguration implements ContextResolver<Jsonb> {

    private Jsonb jsonb;

    public JSONBConfiguration() {
       JsonbConfig config = new JsonbConfig()
               .withFormatting(true)
               .withPropertyVisibilityStrategy(new PropertyVisibilityStrategy() {
                   
                   @Override
                   public boolean isVisible(Method method) {
                       return false;
                   }
                   
                   @Override
                   public boolean isVisible(Field field) {
                       return true;
                   }
               })
               .withDeserializers(new PathSerializer())
               .withSerializers(new PathSerializer());

       jsonb = JsonbBuilder.create(config);
    }

    @Override
    public Jsonb getContext(Class<?> type) {
       return jsonb;
    }

}