package com.begentgroup.samplevolley;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by dongja94 on 2016-05-29.
 */
public class OkHttpRequest<T> extends Request<T> {
    okhttp3.Request mRequest;
    Response.Listener<T> mListener;

    public OkHttpRequest(okhttp3.Request request, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(convertMethod(request.method()), request.url().toString(), errorListener);
        mRequest = request;
        mListener = listener;
    }

    public okhttp3.Request getOkHttpRequest() {
        return mRequest;
    }

    private static int convertMethod(String method) {
        if (method.equals("GET")) {
            return Method.GET;
        } else if (method.equals("POST")) {
            return Method.POST;
        } else if (method.equals("PUT")) {
            return Method.PUT;
        } else if (method.equals("DELETE")) {
            return Method.DELETE;
        } else if (method.equals("HEAD")) {
            return Method.HEAD;
        } else if (method.equals("PATCH")) {
            return Method.PATCH;
        }
        return Method.GET;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        if (response instanceof OkHttpNetworkResponse) {
            OkHttpNetworkResponse okNetworkResponse = (OkHttpNetworkResponse) response;
            okhttp3.Response resp = okNetworkResponse.getOkHttpResponse();
            try {
                return Response.success(parse(resp), null);
            } catch (Exception e) {
                e.printStackTrace();
                return Response.error(new VolleyError(e));
            }
        }
        return Response.error(new VolleyError("invalid response"));
    }

    Gson gson = new Gson();

    protected T parse(okhttp3.Response response) throws Exception {
        Type ss = getClass().getGenericSuperclass();
        Type t = ((ParameterizedType) ss).getActualTypeArguments()[0];
        return gson.fromJson(response.body().charStream(), t);
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }
}
