package com.company.delivery.infrastructure.security;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

/**
 * Generates and verifies TOTP (Time-based One-Time Password) codes.
 *
 * Compatible with Google Authenticator, Authy, and any RFC 6238 app.
 *
 * No extra dependencies needed - uses Java's built-in crypto.
 *
 * How it works:
 *   1. A secret is generated and stored on the user's account
 *   2. User scans QR code with their authenticator app
 *   3. App generates a 6-digit code every 30 seconds
 *   4. User enters the code, we verify it matches
 */
@Service
public class TotpService {

    private static final int    CODE_DIGITS   = 6;
    private static final int    TIME_STEP     = 30;       // seconds per code
    private static final int    WINDOW        = 1;        // allow 1 step drift
    private static final String ALGORITHM     = "HmacSHA1";

    // -------------------------------------------------------------------------
    // Secret generation
    // -------------------------------------------------------------------------

    /** Generate a new random Base32 secret for a user */
    public String generateSecret() {
        byte[] bytes = new byte[20];
        new SecureRandom().nextBytes(bytes);
        return base32Encode(bytes);
    }

    // -------------------------------------------------------------------------
    // QR code URI
    // -------------------------------------------------------------------------

    /**
     * Returns a URI that authenticator apps can read.
     * Format it into a QR code on your frontend:
     *   https://api.qrserver.com/v1/create-qr-code/?data=<encoded-uri>
     *
     * @param secret    the user's TOTP secret
     * @param studentId used as the account label in the authenticator app
     * @param issuer    your app name (e.g. "Campus Delivery")
     */
    public String buildQrUri(String secret, String studentId, String issuer) {
        return String.format(
            "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=6&period=30",
            encode(issuer), encode(studentId), secret, encode(issuer)
        );
    }

    // -------------------------------------------------------------------------
    // Verification
    // -------------------------------------------------------------------------

    /**
     * Verify a 6-digit code against the user's secret.
     * Checks the current window plus one step either side to handle clock drift.
     */
    public boolean verify(String secret, int code) {
        long timeStep = Instant.now().getEpochSecond() / TIME_STEP;
        for (int i = -WINDOW; i <= WINDOW; i++) {
            if (generateCode(secret, timeStep + i) == code) return true;
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // TOTP algorithm (RFC 6238)
    // -------------------------------------------------------------------------

    private int generateCode(String secret, long timeStep) {
        try {
            byte[] key     = base32Decode(secret);
            byte[] message = longToBytes(timeStep);
            Mac    mac     = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(key, ALGORITHM));
            byte[] hash    = mac.doFinal(message);

            int offset = hash[hash.length - 1] & 0x0F;
            int binary = ((hash[offset]     & 0x7F) << 24)
                       | ((hash[offset + 1] & 0xFF) << 16)
                       | ((hash[offset + 2] & 0xFF) << 8)
                       |  (hash[offset + 3] & 0xFF);

            return binary % (int) Math.pow(10, CODE_DIGITS);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate TOTP code", e);
        }
    }

    private byte[] longToBytes(long value) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return result;
    }

    // -------------------------------------------------------------------------
    // Base32 (RFC 4648) - no external library needed
    // -------------------------------------------------------------------------

    private static final String BASE32_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    private String base32Encode(byte[] data) {
        StringBuilder sb = new StringBuilder();
        int buffer = 0, bitsLeft = 0;
        for (byte b : data) {
            buffer = (buffer << 8) | (b & 0xFF);
            bitsLeft += 8;
            while (bitsLeft >= 5) {
                bitsLeft -= 5;
                sb.append(BASE32_CHARS.charAt((buffer >> bitsLeft) & 0x1F));
            }
        }
        if (bitsLeft > 0) sb.append(BASE32_CHARS.charAt((buffer << (5 - bitsLeft)) & 0x1F));
        return sb.toString();
    }

    private byte[] base32Decode(String input) {
        String upper = input.toUpperCase().replaceAll("[^A-Z2-7]", "");
        int outputLen = upper.length() * 5 / 8;
        byte[] result = new byte[outputLen];
        int buffer = 0, bitsLeft = 0, idx = 0;
        for (char c : upper.toCharArray()) {
            buffer = (buffer << 5) | BASE32_CHARS.indexOf(c);
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                bitsLeft -= 8;
                result[idx++] = (byte) ((buffer >> bitsLeft) & 0xFF);
            }
        }
        return result;
    }

    private String encode(String s) {
        return s.replace(" ", "%20").replace(":", "%3A").replace("@", "%40");
    }
}
