package com.begentgroup.samplevolley;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by dongja94 on 2016-05-29.
 */
public class CustomRequest<T> extends Request<T> {
    Response.Listener<T> mListener;
    public CustomRequest(int method, String url, Response.Listener<T> listener,
                         Response.ErrorListener erroListener) {
        super(method, url, erroListener);
        mListener = listener;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        T result = doParse(parsed);
        return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
    }

    Gson gson = new Gson();
    protected T doParse(String text) {
        try {
            Type ss = getClass().getGenericSuperclass();
            Type t = ((ParameterizedType)ss).getActualTypeArguments()[0];
            return gson.fromJson(text, t);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }
}
