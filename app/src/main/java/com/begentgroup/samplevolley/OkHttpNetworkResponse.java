package com.begentgroup.samplevolley;

import com.android.volley.NetworkResponse;

import okhttp3.Response;

/**
 * Created by dongja94 on 2016-05-29.
 */
public class OkHttpNetworkResponse extends NetworkResponse {
    Response mResponse;
    public OkHttpNetworkResponse(Response response) {
        super(response.code(), null, null, false, 0 );
        mResponse = response;
    }

    public Response getOkHttpResponse() {
        return mResponse;
    }
}
