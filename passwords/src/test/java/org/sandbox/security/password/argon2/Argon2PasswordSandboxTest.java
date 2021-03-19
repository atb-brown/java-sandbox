package org.sandbox.security.password.argon2;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import org.junit.jupiter.api.Test;

class Argon2PasswordSandboxTest {

  private final Argon2PasswordSandbox argon2PwSbx = new Argon2PasswordSandbox();

  @Test
  void correctPasswords() {
    assertTrue(argon2PwSbx.checkPw("austin", "lolBad1"));
    assertTrue(argon2PwSbx.checkPw("sally", "lolBad2"));
    assertTrue(argon2PwSbx.checkPw("joe", "reallyLongPasswordThatIsKindaSecure1987***"));
  }

  @Test
  void incorrectPasswords() {
    assertFalse(argon2PwSbx.checkPw("austin", "lolBad2"));
    assertFalse(argon2PwSbx.checkPw("sally", "reallyLongPasswordThatIsKindaSecure1987***"));
    assertFalse(argon2PwSbx.checkPw("joe", "lolBad1"));
    assertFalse(argon2PwSbx.checkPw("joe", "aljllkjlaksjdffds"));
    assertFalse(argon2PwSbx.checkPw("kyle", "lolBad1"));
  }
}
