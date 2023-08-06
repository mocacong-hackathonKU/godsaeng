package godsaeng.server.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EncryptUtilsTest {

    @Test
    @DisplayName("입력이 주어지면 해당 문자열을 암호화한다")
    void encrypt() {
        String given = "abc123";

        String actual = EncryptUtils.encrypt(given);

        assertThat(actual).isNotEqualTo(given);
    }
}
