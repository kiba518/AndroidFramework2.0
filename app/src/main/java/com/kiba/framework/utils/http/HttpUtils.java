package com.kiba.framework.utils.http;



import static com.kiba.framework.utils.Utils.runOnUiThread;

import android.util.Log;


import com.kiba.framework.dto.base.BaseResult;
import com.kiba.framework.utils.ToastUtils;
import com.kiba.framework.utils.json.JsonUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *         调用代码
 *         HttpUtils.get("","", new HttpHelper.HttpData<LoginCommandResult>() {
 *             @Override
 *             public void getData(LoginCommandResult result) {
 *                 int code = result.code;
 *             }
 *         });
 */
public class HttpUtils {
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getX509TrustManager())
            .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
            .connectTimeout(10, TimeUnit.SECONDS)//设置连接超时时间
            .readTimeout(20, TimeUnit.SECONDS)//设置读取超时时间
            .build();


    private static void getRequest(String url, ICallback callback) throws IOException {
        new Thread() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    String result = response.body().string();
                    callback.Call(result);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("http异常", e.getMessage());
                    callback.Call(e.getMessage());

                }
            }
        }.start();

    }

    private static final MediaType mediaType  = MediaType.get("application/json; charset=utf-8");

    private static void postRequest(String url, String param, ICallback callback) throws IOException {
        new Thread() {
            @Override
            public void run() {

                RequestBody body = RequestBody.create(mediaType, param);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    String result = response.body().string();
                    callback.Call(result);
                } catch (IOException e) {
                    e.printStackTrace();
                    BaseResult baseResult=new BaseResult();
                    baseResult.code=-1;
                    callback.Call(JsonUtils.Serialize_FastJson(baseResult));


                }
            }
        }.start();

    }

    private interface ICallback {
        void Call(String con);
    }
    public interface HttpData<T>{
        public void getData(T result);

    }
    public static <T> void post(String param, String urlAddress, HttpData<T> httpData) {

        try {
            HttpUtils.postRequest(urlAddress, param, con -> {
                runOnUiThread(() -> {

                    BaseResult baseResult = JsonUtils.Deserialize_FastJson(BaseResult.class, con);
                    if (null != baseResult && baseResult.code == 1) {
                        Class thisClass = httpData.getClass();

                        Type[] superClassType = thisClass.getGenericInterfaces();
                        ParameterizedType pt = (ParameterizedType) superClassType[0];

                        Type[] genTypeArr = pt.getActualTypeArguments();
                        Type genType = genTypeArr[0];
                        Class c1= (Class) genTypeArr[0];

                        T result = (T)JsonUtils.Deserialize_FastJson(c1, con);
                        httpData.getData(result);

                    } else {
                        if (null != baseResult) {
                            ToastUtils.showToast("数据获取失败：" + baseResult.msg);
                        } else {
                            ToastUtils.showToast("数据获取失败");
                        }
                    }


                });
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    BaseResult baseResult = (BaseResult)JsonUtils.Deserialize_FastJson(BaseResult.class, "con");
    public static <T> void get(String param, String urlAddress, HttpData<T> httpData) {

        try {

            HttpUtils.getRequest(urlAddress, con -> {


                runOnUiThread(() -> {

                    BaseResult baseResult = (BaseResult)JsonUtils.Deserialize_FastJson(Object.class, con);
                    if (null != baseResult && baseResult.code == 1) {
                        Class thisClass = httpData.getClass();

                        Type[] superClassType = thisClass.getGenericInterfaces();
                        ParameterizedType pt = (ParameterizedType) superClassType[0];

                        Type[] genTypeArr = pt.getActualTypeArguments();
                        Type genType = genTypeArr[0];
                        Class c1= (Class) genTypeArr[0];

                        T result = (T)JsonUtils.Deserialize_FastJson(c1, con);
                        httpData.getData(result);

                    } else {
                        if (null != baseResult) {
                            ToastUtils.showToast("数据获取失败：" + baseResult.msg);
                        } else {
                            ToastUtils.showToast("数据获取失败");
                        }
                    }


                });
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
