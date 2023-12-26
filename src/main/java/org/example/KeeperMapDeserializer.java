package main.java.org.example;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class KeeperMapDeserializer implements JsonDeserializer<Map<String,Keeper>> {
    @Override
    public Map<String, Keeper> deserialize(JsonElement json, Type typeOfT,
                                           JsonDeserializationContext context) throws JsonParseException {

        Map<String, Keeper> result = new HashMap<>();

        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String key = entry.getKey();
                JsonElement valueElement = entry.getValue();

                // Assume the values are integers; you can adapt this part based on your specific requirements
                if (valueElement.isJsonObject()) {
                    Keeper keeper = context.deserialize(valueElement,Keeper.class);
                    if(keeper != null){
                        result.put(key, keeper);
                    }
                } else {
                    // Handle the case where the value is not an integer
                    throw new JsonParseException("Invalid value type for map deserialization");
                }
            }
        } else {
            // Handle the case where the JSON structure is not as expected
            throw new JsonParseException("Invalid JSON structure for map deserialization");
        }
        return result;
    }
}
