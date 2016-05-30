package com.begentgroup.samplevolley;

import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by dongja94 on 2016-05-29.
 */
public class OkHttpNetwork implements Network {

    OkHttpClient mClient;
    public OkHttpNetwork() {
        this(null);
    }

    public OkHttpNetwork(OkHttpClient client) {
        if (client == null) {
            client = new OkHttpClient.Builder().build();
        }
        mClient = client;
    }

    @Override
    public NetworkResponse performRequest(Request<?> request) throws VolleyError {
        if (request instanceof OkHttpRequest) {
            OkHttpRequest okRequest = (OkHttpRequest)request;
            okhttp3.Request rq = okRequest.getOkHttpRequest();
            try {
                Response response = mClient.newCall(rq).execute();
                NetworkResponse networkResponse = new OkHttpNetworkResponse(response);
                if (response.isSuccessful()) {
                    return networkResponse;
                }
                throw new VolleyError(networkResponse);
            } catch (IOException e) {
                e.printStackTrace();
                throw new VolleyError(e);
            }
        } else {
            okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
            builder.url(request.getUrl());
            Map<String,String> headers = request.getHeaders();
            if (headers != null) {
                Set<String> keys = headers.keySet();
                for (String key : keys) {
                    String value = headers.get(key);
                    builder.header(key, value);
                }
            }
            switch (request.getMethod()) {
                case Request.Method.POST :
                    builder.post(RequestBody.create(MediaType.parse(request.getBodyContentType()),request.getBody()));
                    break;
                case Request.Method.PUT :
                    builder.put(RequestBody.create(MediaType.parse(request.getBodyContentType()),request.getBody()));
                    break;
                case Request.Method.DELETE :
                    builder.delete();
                    break;
                case Request.Method.HEAD :
                    builder.head();
                    break;
                case Request.Method.PATCH :
                    builder.patch(RequestBody.create(MediaType.parse(request.getBodyContentType()),request.getBody()));
                    break;
            }
            try {
                Response response = mClient.newCall(builder.build()).execute();
                Headers hders = response.headers();
                Map<String,String> hh = new HashMap<>();
                for (String name : hders.names()) {
                    hh.put(name, hders.get(name));
                }
                NetworkResponse resp = new NetworkResponse(response.code(), response.body().bytes(), hh, false);
                if (response.isSuccessful()) {
                    return resp;
                }
                throw new VolleyError(resp);
            } catch (IOException e) {
                e.printStackTrace();
                throw new VolleyError(e);
            }
        }
    }
}
