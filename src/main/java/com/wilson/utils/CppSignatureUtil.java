package com.wilson.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wilson.common.SignaturePublicKeyInfo;
import org.apache.logging.log4j.util.Strings;
import org.jose4j.base64url.internal.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.Set;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author ryan.c.lee
 * TODO
 */
public class CppSignatureUtil {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private SignaturePublicKeyInfo info;
    private boolean ndcOverrideSignature;

    public CppSignatureUtil(Environment env) {
        this.info = new SignaturePublicKeyInfo(env);
        this.ndcOverrideSignature = Boolean.valueOf(env.getRequiredProperty("cpp.payment.ndc.override.signature"));
    }

    // Assume cpp_signature exist in requestBody
    public CppSignatureData verify(String requestBody, String requestUri) {

        ObjectMapper mapper = new ObjectMapper();

        JsonNode jsonNode;
        try {
            jsonNode = mapper.readTree(requestBody);
        } catch (IOException e) {
            log.error("[CppSignatureUtil] IOException at mapper.readTree");
            return new CppSignatureData(false, null, null);
        }

        // Assume cpp_signature exist in requestBody
        // Remove cpp_signature from original tree
        JsonNode cppSignatureNode = ((ObjectNode) jsonNode).remove("cpp_signature");
        String appCode = jsonNode.path("application_code").textValue();
        String keyId = "";

        // Get CppSignature in requestBody and extract as Text
        String cppSignatureText = cppSignatureNode.textValue();

        // Get application_code & kid from decodeJWT
        try {
            DecodedJWT decodedJWT = JWT.decode(cppSignatureText);
            keyId = decodedJWT.getKeyId();
            String merchantReference = decodedJWT.getClaim("merchant_reference").asString();
            String primaryMerchantReference = decodedJWT.getClaim("primary_merchant_reference").asString();
            if (Strings.isNotBlank(merchantReference) && merchantReference.length() > 9) {
                // e.g. "201015IBE-77586RR-TPE-CPPABC"
                appCode = merchantReference.substring(6,9);
            } else if (Strings.isNotBlank(primaryMerchantReference) && primaryMerchantReference.length() > 9) {
                // e.g. "201015IBE-CM586RR-TPE-CPPABC"
                // e.g. "'201110IBEHKG01856"
                appCode = primaryMerchantReference.substring(6,9);
            } else {
                appCode = decodedJWT.getClaim("application_code").asString();
            }

            String publicKeyBase64 = getPublicKeyString(keyId);
            String signAppCode = getSignAppCode(keyId);

            if (!checkOverrideSignature(signAppCode, appCode, requestUri)) {
                log.error("[CppSignatureUtil] This action is not allowed for this touch point");
                return new CppSignatureData(false, appCode, keyId);
            }

            PublicKey publicKey = getPublicKey(publicKeyBase64);

            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm).build(); //Reusable verifier instance

            DecodedJWT verifiedJWT = verifier.verify(cppSignatureText);

            byte[] payloadBytesArray = Base64.decodeBase64(verifiedJWT.getPayload());
            String cppSignaturePrePayload = new String(payloadBytesArray, StandardCharsets.UTF_8);

            JsonNode cppSignaturePayloadNode;
            try {
                cppSignaturePayloadNode = mapper.readTree(cppSignaturePrePayload);
            } catch (IOException e) {
                log.error("[CppSignatureUtil] IOException at mapper.readTree");
                return new CppSignatureData(false, null, null);
            }

            // 20200306 Hotfix Amets
            Set<Map.Entry<String, JsonNode>> requestBodyPayloadSet = StreamSupport.stream(Spliterators.spliteratorUnknownSize(jsonNode.fields(), 0), false)
                    .filter(pair -> !pair.getValue().isNull()).collect(Collectors.toSet());

            boolean valid = requestBodyPayloadSet.stream()
                    .allMatch(requestBodyPair -> requestBodyPair.getValue().equals(cppSignaturePayloadNode.get(requestBodyPair.getKey())));
            String cppSignaturePayload = mapper.writeValueAsString(cppSignaturePayloadNode);
            String requestBodyPayload = mapper.writeValueAsString(jsonNode);
            return new CppSignatureData(valid, appCode, keyId, cppSignaturePayload, requestBodyPayload);

        } catch (NoSuchAlgorithmException e) {
            log.error("[CppSignatureUtil] NoSuchAlgorithmException at getPublicKey(publicKeyBase64)");
            return new CppSignatureData(false, appCode, keyId);
        } catch (InvalidKeySpecException e) {
            log.error("[CppSignatureUtil] InvalidKeySpecException at getPublicKey(publicKeyBase64)");
            return new CppSignatureData(false, appCode, keyId);
        } catch (JWTVerificationException exception) {
            log.error("[CppSignatureUtil] JWTVerificationException at verifier.verify(cppSignatureText)", exception);
            return new CppSignatureData(false, appCode, keyId);            //Invalid signature/claims
        } catch (JsonProcessingException e) {
            log.error("[CppSignatureUtil] JsonProcessingException at mapper.writeValueAsString(objectNode)");
            return new CppSignatureData(false, appCode, keyId);
        }
    }

    private String getPublicKeyString(@NotNull String kid) {
        return this.info.getPublicKey(kid);
    }

    private String getSignAppCode(@NotNull String kid) {
        return this.info.getApplicationCode(kid);
    }

    private boolean checkOverrideSignature(String signAppCode, String requestAppCode, String requestUri) {
        if (signAppCode.equals("CPP")) {
            return true;
        }
        if (ndcOverrideSignature && (requestUri.contains("capture") || requestUri.contains("cancel") || requestUri.contains("order-enquiry")) && signAppCode.equals("NDC") && requestAppCode.startsWith("N")) {
            return true;
        }
        return signAppCode.equals(requestAppCode);
    }

    private PublicKey getPublicKey(String publicKeyBase64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyBase64));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(pubKeySpec);
    }

    public boolean isTestMode() {
        return info.isCppAppTestMode();
    }

    public boolean isCppSignatureEnable() {
        return info.isCppSignatureEnable();
    }

    public class CppSignatureData {

        private boolean valid;
        private String applicationCode;
        private String kid;

        private String requestBodyPayload;
        private String cppSignaturePayload;

        public CppSignatureData(boolean valid, String applicationCode, String kid) {
            this.valid = valid;
            this.applicationCode = applicationCode;
            this.kid = kid;
        }

        public CppSignatureData(boolean valid, String applicationCode, String kid, String cppSignaturePayload, String requestBodyPayload) {
            this.valid = valid;
            this.applicationCode = applicationCode;
            this.kid = kid;
            this.cppSignaturePayload = cppSignaturePayload;
            this.requestBodyPayload = requestBodyPayload;
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getApplicationCode() {
            return applicationCode;
        }

        public void setApplicationCode(String applicationCode) {
            this.applicationCode = applicationCode;
        }

        public String getKid() {
            return kid;
        }

        public void setKid(String kid) {
            this.kid = kid;
        }

        public String getRequestBodyPayload() {
            return requestBodyPayload;
        }

        public void setRequestBodyPayload(String requestBodyPayload) {
            this.requestBodyPayload = requestBodyPayload;
        }

        public String getCppSignaturePayload() {
            return cppSignaturePayload;
        }

        public void setCppSignaturePayload(String cppSignaturePayload) {
            this.cppSignaturePayload = cppSignaturePayload;
        }
    }


}
