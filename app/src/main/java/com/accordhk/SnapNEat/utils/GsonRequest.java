package com.accordhk.SnapNEat.utils;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by jm on 7/2/16.
 */
public class GsonRequest<T> extends Request<T> {
    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Map<String, String> mParams;
    private final Response.Listener<T> listener;

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
//    public GsonRequest(String url, Class<T> clazz, Map<String, String> headers,
//                       Listener<T> listener, ErrorListener errorListener) {
//        super(Method.GET, url, errorListener);
//        this.clazz = clazz;
//        this.headers = headers;
//        this.listener = listener;
//        this.mParams = null;
//    }

    /**
     * Make a POST request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param params Map of request params
     */
//    public GsonRequest(int method, String url, Class<T> clazz, Map<String, String> params,
//                       Listener<T> listener, ErrorListener errorListener) {
//
//        super(method, url, errorListener);
//        this.clazz = clazz;
//        this.mParams = params;
//        this.listener = listener;
//        this.headers = null;
//    }

    public GsonRequest(int method, String url, Class<T> clazz, Map<String, String> params, Map<String, String> headers,
                       Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.mParams = params;
        this.listener = listener;
        this.headers = headers;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {

        if(mParams != null) {
            for (Map.Entry<String, String> entry : mParams.entrySet())
            {
                Log.d("PARAMS key ", entry.getKey());
                Log.d("PARAMS value ", entry.getValue());
            }
        }

        return mParams != null ? mParams : super.getParams();
    }



    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            Log.d("parseNetworkResponse:", json);
            return Response.success(
                    gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }
}
