package org.sandbox.security.openidc.keycloak;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;

public class KeycloakClientJwt {
  /**
   * Need to have Keycloak running on port 8180
   *
   * @param userName The {@link String} name of the user as it is in Keycloak.
   * @param password The user's {@link String} password.
   * @return the {@link DecodedJWT} resulting from the authorized request.
   */
  public static DecodedJWT getTokenFromKeycloak(final String userName, final String password) {
    Keycloak keycloak =
        KeycloakBuilder.builder()
            .serverUrl("http://localhost:8180/auth")
            .grantType(OAuth2Constants.PASSWORD)
            .realm("dev")
            .clientId("example-client")
            // I don't really care about this secret since this is a fake service.
            .clientSecret("1169ef64-ea8f-47eb-9e55-74fdda9ee398")
            .username(userName)
            // https://stackoverflow.com/questions/66701854/do-i-need-to-hash-my-passwords-for-keycloak-client
            .password(password)
            .resteasyClient(new ResteasyClientBuilderImpl().connectionPoolSize(10).build())
            .build();

    AccessTokenResponse atr = keycloak.tokenManager().getAccessToken();
    return JWT.decode(atr.getToken());
  }
}
