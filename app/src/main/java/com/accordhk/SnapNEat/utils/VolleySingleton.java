package com.accordhk.SnapNEat.utils;

import android.content.Context;
import android.util.Log;

import com.accordhk.SnapNEat.utils.LruBitmapCache;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by jm on 22/1/16.
 */
public class VolleySingleton {
    private static VolleySingleton mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mContext;

    private VolleySingleton(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
        mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache(LruBitmapCache.getCacheSize(context)));
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new VolleySingleton(context);
        }

        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if(mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
//            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext(), new HurlStack(null, createSslSocketFactory()));

//            HurlStack hurlStack = new HurlStack() {
//                @Override
//                protected HttpURLConnection createConnection(URL url) throws IOException {
//                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
//                    try {
//                        httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory());
//                        httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return httpsURLConnection;
//                }
//            };
//
//            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext(), hurlStack);

        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request <T> request) {
        try {
            Log.d("VOLLEY: ", "POST: "+request.getBody());
            Log.d("VOLLEY: ", "HEADER: "+request.getHeaders().toString());
        } catch (Exception authFailureError) {
            authFailureError.printStackTrace();
        }
        getRequestQueue().add(request);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }


//    // Let's assume your server app is hosting inside a server machine
//// which has a server certificate in which "Issued to" is "localhost",for example.
//// Then, inside verify method you can verify "localhost".
//// If not, you can temporarily return true
//    private HostnameVerifier getHostnameVerifier() {
//        return new HostnameVerifier() {
//            @Override
//            public boolean verify(String hostname, SSLSession session) {
//                //return true;
//                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
////                return hv.verify("localhost", session);
//                return hv.verify("snapneatdev.accordhkcloud.com", session);
//            }
//        };
//    }

//    private TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
//        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
//        return new TrustManager[]{
//                new X509TrustManager() {
//                    public X509Certificate[] getAcceptedIssuers() {
//                        return originalTrustManager.getAcceptedIssuers();
//                    }
//
//                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                        try {
//                            originalTrustManager.checkClientTrusted(certs, authType);
//                        } catch (CertificateException ignored) {
//                        }
//                    }
//
//                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                        try {
//                            originalTrustManager.checkServerTrusted(certs, authType);
//                        } catch (CertificateException ignored) {
//                        }
//                    }
//                }
//        };
//    }

//    private SSLSocketFactory getSSLSocketFactory()
//            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
//        CertificateFactory cf = CertificateFactory.getInstance("X.509");
//        InputStream caInput = mContext.getResources().openRawResource(R.raw.snapneat); // this cert file stored in \app\src\main\res\raw folder path
//
//        Certificate ca = cf.generateCertificate(caInput);
//        caInput.close();
//
//        KeyStore keyStore = KeyStore.getInstance("BKS");
//        keyStore.load(null, null);
//        keyStore.setCertificateEntry("ca", ca);
//
//        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//        tmf.init(keyStore);
//
//        TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());
//
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//        sslContext.init(null, wrappedTrustManagers, null);
//
//        return sslContext.getSocketFactory();
//    }

//    private static SSLSocketFactory createSslSocketFactory() {
//        TrustManager[] byPassTrustManagers = new TrustManager[]{new X509TrustManager() {
//            @Override
//            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//
//            }
//
//            @Override
//            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//
//            }
//
//            @Override
//            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                return new java.security.cert.X509Certificate[0];
//            }
//
//        }};
//
//        SSLContext sslContext = null;
//        SSLSocketFactory sslSocketFactory = null;
//        try {
//            sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(null, byPassTrustManagers, new SecureRandom());
//            sslSocketFactory = sslContext.getSocketFactory();
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
////            Log.e(TAG, StringUtils.EMPTY, e);
//        } catch (KeyManagementException e) {
////            Log.e(TAG, StringUtils.EMPTY, e);
//        }
//
//        return sslSocketFactory;
//    }
}
