package com.personal.framework.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class GsonUtils {

    private static Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").disableHtmlEscaping().create();

    public static Gson getGson() {
        return mGson;
    }

    /**
     * fromJson
     * Gfro
     *
     * @param json json
     * @param c    c
     * @param <T>  <T>
     * @return T
     */
    public static <T> T fromJson(String json, Class<T> c) throws JsonSyntaxException {
        return mGson.fromJson(json, c);
    }

    /**
     * fromJson
     *
     * @param json json
     * @param c    c
     * @param <T>  <T>
     * @return T
     */
    public static <T> T fromJson(JsonElement json, Class<T> c) throws JsonSyntaxException {
        return mGson.fromJson(json, c);
    }

    /**
     * fromJson
     *
     * @param json json
     * @param type type
     * @param <T>  <T>
     * @return T
     */
    public static <T> T fromJson(JsonElement json, Type type) throws JsonSyntaxException {
        return mGson.fromJson(json, type);
    }

    /**
     * fromJson
     *
     * @param json json
     * @param type type
     * @param <T>  <T>
     * @return T
     */
    public static <T> T fromJson(String json, Type type) throws JsonSyntaxException {
        return mGson.fromJson(json, type);
    }

    /**
     * fromJson
     *
     * @param json json
     * @param type type
     * @param <T>  <T>
     * @return T
     */
    public static <T> T fromJson(Reader json, Type type) throws JsonIOException, JsonSyntaxException {
        return mGson.fromJson(json, type);
    }

    /**
     * toJson
     *
     * @param src src
     * @return String
     */
    public static String toJson(Object src) {
        return mGson.toJson(src);
    }

    /**
     * 字符串转jsonobject
     *
     * @param string
     * @return
     */
    public static JsonObject toJsonObject(String string) {
        if (TextUtils.isEmpty(string))
            return null;

        JsonParser jp = new JsonParser();
        try {
            JsonElement element = jp.parse(string);
            if (null != element)
                return element.getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JsonArray toJsonArray(String s) {
        if (TextUtils.isEmpty(s))
            return null;

        JsonParser jp = new JsonParser();
        try {
            JsonElement element = jp.parse(s);
            if (null != element)
                return element.getAsJsonArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 转换jsonArray为jsonObject
     *
     * @param jsonArray
     * @return
     */
    public static JsonObject getJsonObjectFromArray(JsonArray jsonArray) {
        JsonObject jsonObject = new JsonObject();
        if (null == jsonArray || jsonArray.size() <= 0)
            return jsonObject;

        Iterator<JsonElement> iterator = jsonArray.iterator();
        Set<Map.Entry<String, JsonElement>> entries;
        Iterator<Map.Entry<String, JsonElement>> entryIterator;
        Map.Entry<String, JsonElement> temp;

        while (iterator.hasNext()) {
            entries = iterator.next().getAsJsonObject().entrySet();

            if (null != entries && !entries.isEmpty()) {
                entryIterator = entries.iterator();
                if (null == entryIterator)
                    break;

                while (entryIterator.hasNext()) {
                    temp = entryIterator.next();
                    jsonObject.add(temp.getKey(), temp.getValue());
                }
            }
        }
        return jsonObject;
    }

    /**
     * map转jsonObject
     *
     * @param map
     * @return
     */
    public static JsonObject getJsonFromMap(Map<String, String> map) {
        if (null == map || map.isEmpty())
            return null;
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            jsonObject.addProperty(entry.getKey(), entry.getValue());
        }
        return jsonObject;
    }

}
