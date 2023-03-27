package com.kiba.framework.utils.json;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class JsonUtils {

    public static   <T> T Deserialize(Class<T> tClass,String text) {
        if (text == "timeout") {
            return null;
        }
        try{
            T t = FastJsonUtils.toBean(text, tClass);
            return t;
        }catch (Exception ex){  return null; }

    }

    public static  String Serialize(Object obj){
        String text = FastJsonUtils.toJSONString(obj);
        return text;
    }

    public static  String Serialize2(Object obj){
        Gson gson=new GsonBuilder().registerTypeAdapterFactory(new GsonTypeAdapterFactory()).create();
        String json = gson.toJson(obj);
        return json;
    }
    public static  <T> T Deserialize2(Class<T> tClass, String json){

        Gson gson=new GsonBuilder().registerTypeAdapterFactory(new GsonTypeAdapterFactory()).create();
        return  gson.fromJson(json, tClass);
    }


    public static  <T,S> T Convert(Class<T> tClass,S source){
        Gson gson=new GsonBuilder().registerTypeAdapterFactory(new GsonTypeAdapterFactory()).create();
        String jsonStr = JsonUtils.Serialize(source) ;
        return gson.fromJson(jsonStr, tClass);
    }


}
 class GsonTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        final TypeAdapter<T> adapter = gson.getDelegateAdapter(this, type);
        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                adapter.write(out, value);
            }

            @Override
            public T read(JsonReader in) throws IOException {

                try {
                    return adapter.read(in);
                } catch (Throwable e) {
                    consumeAll(in);
                    return null;
                }

            }

            private void consumeAll(JsonReader in) throws IOException {
                if (in.hasNext()) {
                    JsonToken peek = in.peek();
                    if (peek == JsonToken.STRING) {
                        in.nextString();
                    } else if (peek == JsonToken.BEGIN_ARRAY) {
                        in.beginArray();
                        consumeAll(in);
                        in.endArray();
                    } else if (peek == JsonToken.BEGIN_OBJECT) {
                        in.beginObject();
                        consumeAll(in);
                        in.endObject();
                    } else if (peek == JsonToken.END_ARRAY) {
                        in.endArray();
                    } else if (peek == JsonToken.END_OBJECT) {
                        in.endObject();
                    } else if (peek == JsonToken.NUMBER) {
                        in.nextString();
                    } else if (peek == JsonToken.BOOLEAN) {
                        in.nextBoolean();
                    } else if (peek == JsonToken.NAME) {
                        in.nextName();
                        consumeAll(in);
                    } else if (peek == JsonToken.NULL) {
                        in.nextNull();
                    }
                }
            }
        };
    }
}

