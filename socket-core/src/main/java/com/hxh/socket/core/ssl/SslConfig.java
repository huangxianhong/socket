package com.hxh.socket.core.ssl;

import com.hxh.socket.core.transport.Mode;

/**
 * description:
 *
 * @author huangxianhong
 * @version v1.0.0
 * @date Created in 2020/2/8 11:28
 */
public class SslConfig {

    /**客户端还是服务端*/
    private Mode mode = Mode.SERVER;
    /**SSL/TLS*/
    private String protocol = "TLSv1.2";

    private String ksKeysFilepath;
    private String ksKeystorePassword = "storepass";
    private String ksKeyPassword = "keypass";

    private String ksTrustFilePath;
    private String ksTrustKeystorePassword = "storepass";

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getKsKeysFilepath() {
        return ksKeysFilepath;
    }

    public void setKsKeysFilepath(String ksKeysFilepath) {
        this.ksKeysFilepath = ksKeysFilepath;
    }

    public String getKsKeystorePassword() {
        return ksKeystorePassword;
    }

    public void setKsKeystorePassword(String ksKeystorePassword) {
        this.ksKeystorePassword = ksKeystorePassword;
    }

    public String getKsKeyPassword() {
        return ksKeyPassword;
    }

    public void setKsKeyPassword(String ksKeyPassword) {
        this.ksKeyPassword = ksKeyPassword;
    }

    public String getKsTrustFilePath() {
        return ksTrustFilePath;
    }

    public void setKsTrustFilePath(String ksTrustFilePath) {
        this.ksTrustFilePath = ksTrustFilePath;
    }

    public String getKsTrustKeystorePassword() {
        return ksTrustKeystorePassword;
    }

    public void setKsTrustKeystorePassword(String ksTrustKeystorePassword) {
        this.ksTrustKeystorePassword = ksTrustKeystorePassword;
    }
}
