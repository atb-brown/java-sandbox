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