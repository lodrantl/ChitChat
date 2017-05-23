package si.lodrant;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.post;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;

import org.jooby.test.JoobyRule;
import org.jooby.test.MockRouter;
import org.junit.ClassRule;
import org.junit.Test;

import si.lodrant.chitchat.App;
import si.lodrant.chitchat.entities.StandardResponse;

/**
 * @author jooby generator
 */
public class AppTest {

  /**
   * One app/server for all the test of this class. If you want to start/stop a new server per test,
   * remove the static modifier and replace the {@link ClassRule} annotation with {@link Rule}.
   */
  @ClassRule
  public static JoobyRule app = new JoobyRule(new App());

  @Test
  public void emptyTest() {
    get("/users")
        .then()
        .assertThat()
        .body(equalTo("[]"))
        .statusCode(200)
        .contentType("application/json;charset=UTF-8");
  }
  
  @Test
  public void addUser() {
    post("/users?username=miki")
        .then()
        .assertThat()
        .body("status", equalTo("User logged in"))
        .statusCode(200)
        .contentType("application/json;charset=UTF-8");
  }
  
  @Test
  public void emptyTest2() {
    get("/users")
        .then()
        .assertThat()
        .body("[0].username", equalTo("miki"))
        .statusCode(200)
        .contentType("application/json;charset=UTF-8");
  }
}
