package org.sandbox.security.openidc.keycloak;

import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

public class KeycloakClientCreateRealm {
  /**
   * Need to have Keycloak running on port 8180. Will create a realm with a random name, but will
   * use hardcode values for the clientId, username, and userPassword.
   *
   * @return {@code true} if the creation succeeds.
   */
  public static boolean createRealmWithClientAndUser() {
    final String realm = "test-realm-" + RandomStringUtils.randomAlphabetic(8);
    final String clientId = "test-client";
    final String username = "test-user";
    final String userPassword = "test-user-pass";

    return createRealmWithClientAndUser(realm, clientId, username, userPassword);
  }

  /**
   * Need to have Keycloak running on port 8180.
   *
   * @param realm The {@link String} "name" of the realm. Should be unique.
   * @param clientId The {@link String} "name" of the client. Should be unique.
   * @param username The {@link String} "name" of the user. Should be unique.
   * @param userPassword The {@link String} password for the user to log in.
   * @return {@code true} if the creation succeeds.
   */
  public static boolean createRealmWithClientAndUser(
      final String realm, final String clientId, final String username, final String userPassword) {
    // Create keycloak instance for setting things up initially
    Keycloak keycloak =
        KeycloakBuilder.builder()
            .serverUrl("http://localhost:8180/auth")
            .grantType(OAuth2Constants.PASSWORD)
            .realm("master")
            .username("admin")
            .password("admin")
            .clientId("admin-cli")
            .resteasyClient(new ResteasyClientBuilderImpl().connectionPoolSize(10).build())
            .build();

    // Create the realm
    RealmRepresentation realmRep = new RealmRepresentation();
    realmRep.setRealm(realm);
    realmRep.setEnabled(true);
    realmRep.setEventsEnabled(true);
    realmRep.setEnabledEventTypes(
        Arrays.asList("LOGIN_ERROR", "CODE_TO_TOKEN_ERROR", "CLIENT_LOGIN_ERROR"));
    realmRep.setEventsExpiration(172800);
    keycloak.realms().create(realmRep);

    // Create the client
    ClientRepresentation clientRep = new ClientRepresentation();
    clientRep.setClientId(clientId);
    clientRep.setSecret(UUID.randomUUID().toString());
    clientRep.setProtocol("openid-connect");
    // This will make it confidential
    clientRep.setPublicClient(false);
    clientRep.setDirectAccessGrantsEnabled(true);
    clientRep.setServiceAccountsEnabled(true);
    clientRep.setEnabled(true);

    final RealmResource realmResource = keycloak.realm(realm);
    Response response = realmResource.clients().create(clientRep);

    // Remember the unique id (which, confusingly, is not the same as the clientId...)
    final String clientUuid = realmResource.clients().findByClientId(clientId).get(0).getId();

    // Create client role
    RoleRepresentation roleRep = new RoleRepresentation();
    roleRep.setName("basic_role");
    roleRep.setClientRole(true);

    // Add role to client
    ClientResource clientResource = realmResource.clients().get(clientUuid);
    clientResource.roles().create(roleRep);

    // Create the user's password
    CredentialRepresentation credRep = new CredentialRepresentation();
    credRep.setType(CredentialRepresentation.PASSWORD);
    credRep.setValue(userPassword);
    credRep.setTemporary(false);

    // Create the user
    UserRepresentation userRep = new UserRepresentation();
    userRep.setUsername(username);
    userRep.setCredentials(Collections.singletonList(credRep));
    userRep.setEnabled(true);

    realmResource.users().create(userRep);

    // Add client role to user
    UserResource userResource =
        realmResource.users().get(realmResource.users().search(username).get(0).getId());
    userResource.roles().clientLevel(clientUuid).add(clientResource.roles().list());

    // Get the JWT access token
    DecodedJWT jwt =
        KeycloakClientJwt.getTokenFromKeycloak(
            realm, clientId, clientRep.getSecret(), username, userPassword);

    return response.getStatus() == 201 && jwt.getToken() != null;
  }
}
