package org.jmatrix.core.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * @author jmatrix
 * @date 16/7/13
 */
public class HttpsAuthentication {

    public static void standardHttpsAuth() {
        String req = "https://g.alicdn.com/alilog/mlog/aplus_v2.js";
        String appSsl = "https://app-ssl.vipstatic.com/app-ssl/moapi/sha1.js";
        String imgSSL = "https://img.vipstatic.com/vfc/image/2016/07/15/178/6dce47167881439f87de60ffc3e87e65.jpg";
        String appstatic = "https://app-ssl.vipstatic.com/app-ssl/moapi/test";

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(appstatic);

        try {
            HttpResponse response = httpClient.execute(httpGet);
            System.out.println(response.getStatusLine());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        standardHttpsAuth();
    }

}
