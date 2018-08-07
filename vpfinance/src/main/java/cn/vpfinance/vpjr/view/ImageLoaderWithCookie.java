package cn.vpfinance.vpjr.view;

import android.content.Context;
import android.text.TextUtils;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by Administrator on 2016/11/8.
 */
public class ImageLoaderWithCookie extends BaseImageDownloader {

    private SSLSocketFactory mSSLSocketFactory;

    public ImageLoaderWithCookie(Context context) {
        super(context);
        SSLContext sslContext = sslContextForTrustedCertificates();
        mSSLSocketFactory = sslContext.getSocketFactory();
    }

    public ImageLoaderWithCookie(Context context, int connectTimeout, int readTimeout) {
        super(context, connectTimeout, readTimeout);
        SSLContext sslContext = sslContextForTrustedCertificates();
        mSSLSocketFactory = sslContext.getSocketFactory();
    }

    /**
     * sessionId  获取图片
     * @param url
     * @param extra
     * @return
     * @throws IOException
     */
    @Override
    protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
        // Super...
        HttpURLConnection connection = super.createConnection(url, extra);
        if (extra != null && !TextUtils.isEmpty(extra.toString())) {//不做处理，其他地方图片加载出问题
            connection.setRequestProperty("Cookie",extra.toString());//extra就是SessionId
        }
        return connection;
    }


    /**
     * https  加载图片
     * @param imageUri
     * @param extra
     * @return
     * @throws IOException
     */

    @Override
    protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
        HttpURLConnection conn = this.createConnection(imageUri, extra);
        /*URL url = null;
        try {
            url = new URL(imageUri);
        } catch (MalformedURLException e) {
        }
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();*/
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);

        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setSSLSocketFactory(mSSLSocketFactory);
            ((HttpsURLConnection) conn).setHostnameVerifier((DO_NOT_VERIFY));
        }
        BufferedInputStream inputStream = null;
        try{
            inputStream = new BufferedInputStream(conn.getInputStream(), BUFFER_SIZE);
        }catch (Exception e){
            e.printStackTrace();
        }
        return inputStream;
    }

    // always verify the host - dont check for certificate
    final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    public SSLContext sslContextForTrustedCertificates() {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, null);
            //javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }catch (KeyManagementException e) {
            e.printStackTrace();
        }finally {
            return sc;
        }
    }

    class miTM implements javax.net.ssl.TrustManager,
            javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }
        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }
        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }
}
