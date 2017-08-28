package learn.util;

import com.google.gson.Gson;

public class GsonUtils {

    private static Gson gson;

    static {
        gson = new Gson();
    }

    public static <T> T parseJsonToObject(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static String parseObjectToJson(Object object) {
        return gson.toJson(object);
    }
}
