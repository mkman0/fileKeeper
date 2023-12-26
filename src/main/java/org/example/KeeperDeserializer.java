package main.java.org.example;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.nio.file.InvalidPathException;

public class KeeperDeserializer implements JsonDeserializer<Keeper> {
    @Override
    public Keeper deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String path = jsonObject.get("path").getAsString();
        int keptFiles = jsonObject.get("keptFiles").getAsInt();
        Keeper.Mode mode = Keeper.Mode.valueOf(jsonObject.get("mode").getAsString().toUpperCase());
        Keeper.Sort sort = Keeper.Sort.valueOf(jsonObject.get("sort").getAsString().toUpperCase());
        try{
            return new Keeper(path,keptFiles,mode,sort);
        }catch (InvalidPathException e){
            System.err.println("Invalid path: "+ path);
            return null;
        }
    }
}
