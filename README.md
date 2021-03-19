# Austin's Java Sandbox

Sometimes I just want to test something small without messing up a larger project. This is where I experiment and,
generally, make a mess of things. Please don't judge my code from this - I'm going for proof-of-concept here.

## Password Hashing

I recently decided to learn about password hashing.

Some things I knew before I started my research:
  * Don't store raw, plaintext passwords on your database.
  * Not much else...

Some things I learned during my research:
  * Some people talk about encrypting passwords, but what they really mean is hashing
    * Also learned that we should use salting to avoid rainbow table attacks
    * https://www.thesslstore.com/blog/difference-encryption-hashing-salting/
  * Google, as they like to do, broke an existing popular hashing algorithm
    * https://shattered.io/
  * I should use argon2 to hash my passwords
    * https://stackoverflow.com/questions/66594009/java-argon2-hashing/66596100#66596100

## Obtaining JWT with Keycloak

I wanted to learn more about OAuth2 and authorized services, so that led me
to [Keycloak and OpenID Connect](https://www.keycloak.org/docs/latest/securing_apps/).

Some things I knew before I started my research:

* Oauth2 is used to restrict access to resources/services to authorized users
* Oauth2 gives authorized users tokens which act like temporary passwords

Some things I learned during my research:

* OpenID Connect (OIDC) is a wrapper on top of Oauth2
  * https://developer.okta.com/blog/2019/10/21/illustrated-guide-to-oauth-and-oidc
* The token given by OIDC contains a payload with a multiple fields of data called (claims).
* Keycloak's Java client natively supports getting OIDC JWT Access Tokens.
  * https://gist.github.com/thomasdarimont/52152ed68486c65b50a04fcf7bd9bbde#file-keycloakclientauthexample-java-L109-L123
  * The above example was a little out of
    date. [I had some compilation and runtime errors](https://stackoverflow.com/questions/66701269/resteasyclient-incompatibleclasschangeerror-resteasyproviderfactory-getcontextd)
    .