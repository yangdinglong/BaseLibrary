package com.roobo.baselibiray.utils.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.roobo.meetpro.constans.HttpConstants;
import com.roobo.meetpro.request.BaseRequest;
import com.roobo.meetpro.response.Response;
import com.roobo.meetpro.utils.CommonUtils;
import com.roobo.meetpro.utils.SharedPreferencesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/***
 * http https网络请求框架
 */

public class HttpEngine {

    private static final String TAG = HttpEngine.class.getSimpleName();

    public static HttpEngine mHttpEngine;

    private ApiService mApiService;//接口对象
    private Context mContext;


    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }
        return ssfFactory;
    }

    public static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static final class MyConverters extends Converter.Factory {
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                                Retrofit retrofit) {
            if (type == JSONObject.class) {
                return JsonObjectConverter.INSTANCE;
            }
            if (type == Bitmap.class) {
                return BitmapConverter.INSTANCE;
            }
            return null;
        }

        static final class JsonObjectConverter implements Converter<ResponseBody, JSONObject> {
            static final JsonObjectConverter INSTANCE = new JsonObjectConverter();

            @Override
            public JSONObject convert(ResponseBody value) throws IOException {
                try {
                    return new JSONObject(value.string());
                } catch (JSONException e) {
                    throw new JsonParseException(e.getMessage());
                }
            }
        }

        static final class BitmapConverter implements Converter<ResponseBody, Bitmap> {
            static final BitmapConverter INSTANCE = new BitmapConverter();

            @Override
            public Bitmap convert(ResponseBody value) throws IOException {
                byte[] bytes = value.bytes();
                if (bytes == null) throw new IOException("response is null");

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap == null) throw new IOException("fail to decode response");

                return bitmap;
            }
        }
    }

    public static HttpEngine getInstance(Context mContext) {
        if (mHttpEngine == null) {
            synchronized (HttpEngine.class) {
                if (mHttpEngine == null) {
                    mHttpEngine = new HttpEngine(mContext);
                }
            }
        }
        return mHttpEngine;
    }

    private HttpEngine(Context mContext) {
        this.mContext = mContext.getApplicationContext();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpConstants.DEFAULT_BASE_URL)
                .client(getClient())
                .addConverterFactory(new MyConverters())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        mApiService = retrofit.create(ApiService.class);
    }

    /***
     * 获取okhttp client
     * @return
     */
    private OkHttpClient getClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(TAG, message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                String token = SharedPreferencesUtil.getToken(mContext, "");
                Log.d(TAG, "[intercept] token:" + token);
                if (TextUtils.isEmpty(token)) {
                    Request request = original.newBuilder()
                            .addHeader(HttpConstants.KEY_AGENT, HttpConstants.AGENT_ANDROID)//Android端
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                } else {
                    Request request = original.newBuilder()
                            .addHeader(HttpConstants.KEY_AGENT, HttpConstants.AGENT_ANDROID)//Android端
                            .addHeader(HttpConstants.KEY_AUTHORIZATION, token)
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                }
            }
        };

        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(interceptor)
                .connectTimeout(HttpConstants.TIMEOUT_DEFAULT, TimeUnit.SECONDS)
                .readTimeout(HttpConstants.TIMEOUT_DEFAULT, TimeUnit.SECONDS)
                .writeTimeout(HttpConstants.TIMEOUT_DEFAULT, TimeUnit.SECONDS)
                .sslSocketFactory(createSSLSocketFactory())
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });

        return mBuilder.build();
    }

    public void post(String url, final ResultListener listener) {
        if (!commonCheck(url, listener)) {
            return;
        }
        Log.d(TAG, "[post] url = " + url);
        Call call = mApiService.post(url);
        enqueueCall(call, listener);
    }

    public void post(String url, BaseRequest request, final ResultListener listener) {
        if (!commonCheck(url, listener)) {
            return;
        }
        String reqJson = CommonUtils.toJsonString(request);
        Log.d(TAG, "[post] request = " + reqJson + "; url = " + url);

        Call call = mApiService.post(url, RequestBody.create(MediaType.parse("application/json"), reqJson));
        enqueueCall(call, listener);
    }

    public void get(String url, final ResultListener listener) {
        if (!commonCheck(url, listener)) {
            return;
        }
        Log.d(TAG, "[get] url = " + url);
        Call call = mApiService.get(url);
        enqueueCall(call, listener);
    }

    public void patch(String url, BaseRequest request, final ResultListener listener) {
        if (!commonCheck(url, listener)) {
            return;
        }
        String reqJson = CommonUtils.toJsonString(request);
        Log.d(TAG, "[patch] request = " + reqJson + "; url = " + url);

        Call call = mApiService.patch(url, RequestBody.create(MediaType.parse("application/json"), reqJson));
        enqueueCall(call, listener);
    }

    public void upload(String url, File file, String fileName, final ResultListener listener) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        Call call = mApiService.upload(url, fileName, requestBody);
        enqueueCall(call, listener);
    }

    private void enqueueCall(Call call, final ResultListener listener) {
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (listener == null) {
                    Log.d(TAG, "[onResponse] listener is null");
                    return;
                }
                if (response == null) {
                    Log.d(TAG, "onResponse] response is null");
                    listener.onFail(HttpConstants.CODE_DEFAULT_ERROR, HttpConstants.MSG_NO_RESPONSE);
                    return;
                }
                handleToken(response);
                if (!response.isSuccessful()) {//httpcode判断
                    listener.onFail(response.raw().code(), response.raw().message());
                    Log.d(TAG, "onResponse] http code=" + response.raw().code() + " message=" + response.raw().message());
                    getResponseMessage(response);
                    return;
                }
                Response result = new Gson().fromJson(response.body().toString(), Response.class);
                if (!isSuccess(result)) {//业务code判断
                    listener.onFail(result.getCode(), result.getMsg());
                } else {
                    try {
                        listener.onSuccess(new JSONObject(response.body().toString()).optString(HttpConstants.KEY_DATA));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onFail(HttpConstants.CODE_ERROR_JSON, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (listener != null) {
                    listener.onFail(HttpConstants.CODE_DEFAULT_ERROR, t.getLocalizedMessage());
                }
            }
        });
    }

    private void handleToken(retrofit2.Response<JsonObject> response) {
        Log.d(TAG, "[handleToken]");
        Headers responseHeaders = response.headers();
        int requestHeadersLength = responseHeaders.size();
        for (int i = 0; i < requestHeadersLength; i++) {
            String headerName = responseHeaders.name(i);
            String headerValue = responseHeaders.get(headerName);
            Log.d(TAG, "[handleToken]----------->Name:" + headerName + "------------>Value:" + headerValue + "\n");
        }
        String token = responseHeaders.get(HttpConstants.KEY_AUTHORIZATION);
        if (!TextUtils.isEmpty(token)) {
            Log.d(TAG, "[handleToken] new token:" + token);
            SharedPreferencesUtil.setToken(mContext, token);
        }
    }

    private String getResponseMessage(retrofit2.Response<JsonObject> response) {
        Response result;
        String msg = HttpConstants.MSG_DEFAULT;
        int code = HttpConstants.CODE_DEFAULT_ERROR;
        if (response.errorBody() != null && !TextUtils.isEmpty(response.errorBody().toString())) {
            try {
                String detail = response.errorBody().string();
                result = new Gson().fromJson(detail, Response.class);
                if (result != null) {
                    code = result.getCode();
                    msg = result.getMsg();
                }
            } catch (IOException e) {
                e.printStackTrace();
                code = HttpConstants.CODE_ERROR_JSON;
                msg = e.getLocalizedMessage();
            } catch (Exception e) {
                e.printStackTrace();
                code = HttpConstants.CODE_ERROR_JSON;
                msg = e.getLocalizedMessage();
            }

        }
        Log.d(TAG, "[getResponseMessage] code=" + code + " msg=" + msg);
        return msg;
    }

    private boolean commonCheck(String url, ResultListener listener) {
        Log.d(TAG, "[commonCheck]");
        String msg = "";
        int code = 0;
        if (!NetworkUtils.isNetworkAvailable(mContext)) {
            msg = HttpConstants.MSG_ERROR_NET;
            code = HttpConstants.CODE_ERROR_NET;
        } else if (TextUtils.isEmpty(url)) {
            msg = HttpConstants.MSG_ERROR_EMPTY_URL;
            code = HttpConstants.CODE_ERROR_EMPTY_URL;
        } else if (!url.startsWith(HttpConstants.HOST_URL_PREFIX_1) && !url.startsWith(HttpConstants.HOST_URL_PREFIX_2)) {
            msg = HttpConstants.MSG_ERROR_URL_NOT_CORRECT;
            code = HttpConstants.CODE_ERROR_URL_NOT_CORRECT;
        }

        if (!TextUtils.isEmpty(msg)) {
            postFail(listener, code, msg);
            return false;
        }

        return true;
    }

    private void postFail(ResultListener listener, int code, String message) {
        Log.d(TAG, "[postFail] code: " + code + "; message:" + message);
        if (listener != null) {
            listener.onFail(code, message);
        }
    }

    /**
     * 判断服务器是否返回错误
     *
     * @param result
     * @return
     */
    private boolean isSuccess(Response result) {
        if (result != null && result.getCode() == HttpConstants.CODE_SUCCESS) {
            return true;
        }
        return false;

    }
}
