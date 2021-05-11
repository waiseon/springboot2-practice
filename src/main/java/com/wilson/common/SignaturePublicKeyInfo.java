package com.wilson.common;

import net.logstash.logback.argument.StructuredArguments;
import org.apache.cxf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignaturePublicKeyInfo {
    private static final String SPACE = " ";
    private boolean cppSignatureEnable;
    private boolean cppAppTestMode;
    private Map<String, AppPublicKeyInfo> publicKey = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    class AppPublicKeyInfo {
        private String appCpde;
        private String publicKey;

        AppPublicKeyInfo(String appCpde, String publicKey) {
            this.appCpde = appCpde;
            this.publicKey = publicKey;
        }

        private String getAppCpde() {
            return appCpde;
        }

        private void setAppCpde(String appCpde) {
            this.appCpde = appCpde;
        }

        private String getPublicKey() {
            return publicKey;
        }

        private void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }
    }

    public SignaturePublicKeyInfo(Environment env) {
        cppSignatureEnable = Boolean.TRUE.toString().equals(env.getRequiredProperty("cpp.signature.check.switch"));
        cppAppTestMode = Boolean.TRUE.toString().equals(env.getRequiredProperty("cpp.app.test.mode"));
        List<String> touchpointAppCodeList = Arrays.asList(env.getRequiredProperty("cpp.sign.tp-app-code-list").split(","));

        for (String touchpointAppCode : touchpointAppCodeList) {
            try {
                String[] envKeys = {"CPP_SIGN_"+touchpointAppCode+"_PUBLIC_KEY1", "CPP_SIGN_"+touchpointAppCode+"_PUBLIC_KEY2", "CPP_SIGN_"+touchpointAppCode+"_PUBLIC_KEY3"};
                for (String envKey : envKeys) {
                    if (isKeyPairValid(env.getRequiredProperty(envKey))) {
                        String[] keypair = env.getRequiredProperty(envKey).split(SPACE);
                        publicKey.put(keypair[0], new AppPublicKeyInfo(touchpointAppCode, keypair[1]));
                    }
                }
            } catch (IllegalStateException e) {
                log.warn("Missing sign key "+touchpointAppCode,
                        StructuredArguments.keyValue("exception", String.valueOf(e)),
                        StructuredArguments.keyValue("exception_stacktrace", Arrays.toString(e.getStackTrace())));
            }
        }
    }

    private boolean isKeyPairValid(String keyPairString) {
        return (!StringUtils.isEmpty(keyPairString) && keyPairString.split(SPACE).length == 2);
    }

    public String getPublicKey(String kid) {
        if (publicKey.containsKey(kid)) {
            return publicKey.get(kid).getPublicKey();
        } else {
            return "";
        }
    }

    public String getApplicationCode(String kid) {
        if (publicKey.containsKey(kid)) {
            return publicKey.get(kid).getAppCpde();
        } else {
            return "";
        }
    }

    /**
     * if true, the cppSignature validation will be turn on.
     * if false, will not check the signature.
     */
    public boolean isCppSignatureEnable() {
        return cppSignatureEnable;
    }

    /**
     * if it is test mode, can skip signature by header
     */
    public boolean isCppAppTestMode() {
        return cppAppTestMode;
    }
}
