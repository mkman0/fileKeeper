package main.java.org.example;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class KeeperSerializer implements JsonSerializer<Keeper> {

    @Override
    public JsonElement serialize(Keeper src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonKeeper = new JsonObject();

        jsonKeeper.addProperty("path", src.getPath());
        jsonKeeper.addProperty("keptFiles", src.getKeptFiles());
        jsonKeeper.addProperty("mode", src.getMode().toString());
        jsonKeeper.addProperty("sort", src.getSort().toString());

        return jsonKeeper;
    }
}
