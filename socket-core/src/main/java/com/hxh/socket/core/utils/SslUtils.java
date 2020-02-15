package com.hxh.socket.core.utils;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2020/2/8 11:14
 */
public class SslUtils {

    /**
     * Creates the key managers required to initiate the {@link SSLContext}, using a JKS keystore as an input.
     *
     * @param filepath - the path to the JKS keystore.
     * @param keystorePassword - the keystore's password.
     * @param keyPassword - the key's passsword.
     * @return {@link KeyManager} array that will be used to initiate the {@link SSLContext}.
     * @throws Exception
     */
    public static KeyManager[] createKeyManagers(String filepath, String keystorePassword, String keyPassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        InputStream inputStream = new FileInputStream(filepath);
        try {
            keyStore.load(inputStream, keystorePassword.toCharArray());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyPassword.toCharArray());
        return kmf.getKeyManagers();
    }



    /**
     * Creates the trust managers required to initiate the {@link SSLContext}, using a JKS keystore as an input.
     *
     * @param filepath - the path to the JKS keystore.
     * @param keystorePassword - the keystore's password.
     * @return {@link TrustManager} array, that will be used to initiate the {@link SSLContext}.
     * @throws Exception
     */
    public static TrustManager[] createTrustManagers(String filepath, String keystorePassword) throws Exception {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        InputStream inputStream = new FileInputStream(filepath);
        try {
            trustStore.load(inputStream, keystorePassword.toCharArray());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        return tmf.getTrustManagers();
    }

}
