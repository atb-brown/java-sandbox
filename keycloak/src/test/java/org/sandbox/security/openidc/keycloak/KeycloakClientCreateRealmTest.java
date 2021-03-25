package org.sandbox.security.openidc.keycloak;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class KeycloakClientCreateRealmTest {

  @Test
  void createClient() {
    assertTrue(KeycloakClientCreateRealm.createRealmWithClientAndUser());
  }
}
