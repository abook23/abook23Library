package com.abook23.utils.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author abook23 2015-9-6 14:47:27
 * @version 2.0
 */
public class JsonUtils {
    private static Gson gson;

    public static String ToJson(Object object) {
        if (gson == null)
            gson = new Gson();
        String json = gson.toJson(object);
        return json;
    }

    /**
     * json to Hash
     *
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static HashMap<String, Object> JsonToHasMap(JSONObject jsonObject) throws JSONException {
        HashMap<String, Object> hs = new HashMap<String, Object>();
        JSONObject object = jsonObject;
        Iterator<?> iterator = object.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            Object value = object.get(key);

            if (value instanceof JSONObject) {
                JSONObject jsonObject2 = new JSONObject(value.toString());
                hs.put(key, JsonToHasMap(jsonObject2));
            } else {
                if (value instanceof JSONArray) {
                    JSONArray array = new JSONArray(value.toString());
                    List<HashMap<?, ?>> list = new ArrayList<HashMap<?, ?>>();
                    for (int i = 0; i < array.length(); i++) {
                        list.add(JsonToHasMap(array.getJSONObject(i)));
                    }
                    hs.put(key, list);
                } else {
                    hs.put(key, value);
                }
            }
        }
        return hs;
    }

    /**
     * jsonArray to List or List<HashMap<String, Object>>
     *
     * @param jsonArray
     * @return
     * @throws JSONException
     */
    public static List<Object> JsonToList(JSONArray jsonArray) throws JSONException {
        List<Object> list = new ArrayList<>();
        JSONArray array = jsonArray;
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONObject) {
                HashMap<String, Object> hs = JsonToHasMap((JSONObject) value);
                list.add(hs);
            } else {
                list.add(JsonToList((JSONArray) value));
            }
        }
        return list;
    }

    /**
     * JSON to Bean
     *
     * @param json
     * @param class1
     * @param <T>
     * @return
     */
    public static <T> T JsonToBean(String json, Class<T> class1) {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        Gson gson = builder.create();
        T t = gson.fromJson(json, class1);
        return t;
    }

    public static <T> T JsonToBean(Object object, Class<T> class1){
        String json = ToJson(object);
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        Gson gson = builder.create();
        T t = gson.fromJson(json, class1);
        return t;
    }

    public static <T> List<T> JsonToBean(List<?> list, Class<T> class1) {
        List<T> list_t = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String json = ToJson(list.get(i));
            T t = JsonToBean(json, class1);
            list_t.add(t);
        }
        return list_t;
    }
}
