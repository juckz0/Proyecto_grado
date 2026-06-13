package com.aplicacion.login.service;



import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;


import javax.crypto.SecretKey;

import java.security.InvalidKeyException;
import java.time.Duration;
import java.time.Instant;

@Service
public class TotpService {
  private final TimeBasedOneTimePasswordGenerator totpGenerator;

  public TotpService() throws Exception {
    this.totpGenerator = new TimeBasedOneTimePasswordGenerator(
      Duration.ofSeconds(30), 6);
  }

  public boolean verify(String code, String base32Secret) throws InvalidKeyException {
    Base32 base32 = new Base32();
    byte[] keyBytes = base32.decode(base32Secret);
    SecretKey key = new javax.crypto.spec.SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA1");
    int generated = totpGenerator.generateOneTimePassword(key, Instant.now());
    return String.valueOf(generated).equals(code);
  }
}
