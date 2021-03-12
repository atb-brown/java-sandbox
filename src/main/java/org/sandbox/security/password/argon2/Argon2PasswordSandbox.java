package org.sandbox.security.password.argon2;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

class Argon2PasswordSandbox {

  public static void main(String[] args) {
    // Print out the results of a password and salt
    String correct = generateArgon2idSensitive("lolBad2", "24e95182-fafa-4d4f-9f3c-05a4eca25b14");
    System.out.println(correct);

    // Read a hashed pass from a file and check to see if hash matches.
    final Argon2PasswordSandbox a2pw = new Argon2PasswordSandbox();
    final String userName = "sally";
    final String rawPass = "lolBad2";
    System.out.println(a2pw.checkPw(userName, rawPass));
  }

  public boolean checkPw(final String uName, final String rawPass) {
    Reader reader =
        new InputStreamReader(
            this.getClass()
                .getResourceAsStream("/org/sandbox/security/password/argon2/passwords.json"));
    final JsonObject json = new Gson().fromJson(reader, JsonObject.class);

    // Find the right json object for the user.
    JsonObject user = null;
    for (JsonElement je : json.get("users").getAsJsonArray()) {
      final JsonObject jsonUserObj = (JsonObject) je;
      if (jsonUserObj.get("name").getAsString().equals(uName)) {
        user = jsonUserObj;
        break;
      }
    }
    if (user == null) {
      return false;
    }

    final String hp = user.get("hp").getAsString();
    final String s = user.get("s").getAsString();

    return hp.equals(generateArgon2idSensitive(rawPass, s));
  }

  private static String generateArgon2idSensitive(String password, String salt) {
    int opsLimit = 10;
    int memLimit = 65536;
    int outputLength = 32;
    int parallelism = 1;
    Argon2Parameters.Builder builder =
        new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
            .withVersion(Argon2Parameters.ARGON2_VERSION_13) // 19
            .withIterations(opsLimit)
            .withMemoryAsKB(memLimit)
            .withParallelism(parallelism)
            .withSalt(salt.getBytes(StandardCharsets.UTF_8));
    Argon2BytesGenerator gen = new Argon2BytesGenerator();
    gen.init(builder.build());
    byte[] result = new byte[outputLength];
    gen.generateBytes(password.getBytes(StandardCharsets.UTF_8), result, 0, result.length);
    return Base64.getEncoder().encodeToString(result);
  }

  private static String generateArgon2idSensitiveWith(
      String password, String salt, int ops, int mem) {
    Instant start = Instant.now();

    int outputLength = 32;
    int parallelism = 1;
    Argon2Parameters.Builder builder =
        new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
            .withVersion(Argon2Parameters.ARGON2_VERSION_13) // 19
            .withIterations(ops)
            .withMemoryAsKB(mem)
            .withParallelism(parallelism)
            .withSalt(salt.getBytes(StandardCharsets.UTF_8));
    Argon2BytesGenerator gen = new Argon2BytesGenerator();
    gen.init(builder.build());
    byte[] result = new byte[outputLength];
    gen.generateBytes(password.getBytes(StandardCharsets.UTF_8), result, 0, result.length);
    return ops + "/" + mem + ": " + (Instant.now().toEpochMilli() - start.toEpochMilli());
  }

  private static void testParams() {
    System.out.println(
        generateArgon2idSensitiveWith(
            "badPassword", "24e95182-fafa-4d4f-9f3c-05a4eca25b14", 1, 1024));
    System.out.println(
        generateArgon2idSensitiveWith(
            "badPassword", "24e95182-fafa-4d4f-9f3c-05a4eca25b14", 10, 1024));
    System.out.println(
        generateArgon2idSensitiveWith(
            "badPassword", "24e95182-fafa-4d4f-9f3c-05a4eca25b14", 1, 65536));
    // 10 iterations with mem limit of 65536 is common on the internet and runs on a lot of
    // computers for about 1 second.
    System.out.println(
        generateArgon2idSensitiveWith(
            "badPassword", "24e95182-fafa-4d4f-9f3c-05a4eca25b14", 10, 65536));
    // A StackOverflow user answered one of my questions using these parameters, but it's pretty
    // slow with such a high mem limit.
    System.out.println(
        generateArgon2idSensitiveWith(
            "badPassword", "24e95182-fafa-4d4f-9f3c-05a4eca25b14", 4, 1048576));
    System.out.println(
        generateArgon2idSensitiveWith(
            "badPassword", "24e95182-fafa-4d4f-9f3c-05a4eca25b14", 10, 1048576));
  }
}
