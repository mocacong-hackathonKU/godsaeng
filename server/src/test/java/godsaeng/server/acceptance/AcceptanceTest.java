package godsaeng.server.acceptance;

import godsaeng.server.support.DatabaseCleanerCallback;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(DatabaseCleanerCallback.class)
public class AcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUP() {
        RestAssured.port = port;
    }
}
