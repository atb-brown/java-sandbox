package org.sandbox.security.openidc.keycloak;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class KeycloakClientJwtTest {

  /** Make sure Keycloak is running. */
  @Test
  void getAJwt() {
    String jwtToken = KeycloakClientJwt.getTokenFromKeycloak("user1", "user1Pass").getToken();
    // I've noticed that they always start with eyJ, so I had to figure out why.
    // https://stackoverflow.com/questions/49517324/why-header-and-payload-in-the-jwt-token-always-starts-with-eyj
    assertTrue(jwtToken.startsWith("eyJ"));
  }
}
