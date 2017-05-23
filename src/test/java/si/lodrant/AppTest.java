package si.lodrant;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.post;
import static org.hamcrest.Matchers.equalTo;

import org.jooby.test.JoobyRule;
import org.junit.Rule;
import org.junit.Test;

import si.lodrant.chitchat.App;

/**
 * @author Luka Lodrant, Lenart Treven
 * Integration test suite for ChitChat
 */
public class AppTest {

	/**
	 * Every test gets a new and blank App instance
	 */
	@Rule
	public JoobyRule app = new JoobyRule(new App());

	@Test
	public void emptyUser() {
		get("/users").then()
				.assertThat()
				.body(equalTo("[]"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");
	}

	@Test
	public void addUser() {
		post("/users?username=miki").then()
				.assertThat()
				.body("status", equalTo("User logged in"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");

		get("/users").then()
				.assertThat()
				.body("[0].username", equalTo("miki"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");
	}

	@Test
	public void addEmptyUser() {
		// TODO
	}

	@Test
	public void addTakenUser() {
		// TODO
	}

	@Test
	public void deleteUser() {
		// TODO
	}

	@Test
	public void recieveEmptyMessage() {
		// TODO
	}
	
	@Test
	public void recieveWithoutLogin() {
		// TODO
	}

	@Test
	public void sendAndRecieveMessage() {
		// TODO
	}

	@Test
	public void sendAndRecieveGlobalMessage() {
		// TODO
	}

	@Test
	public void sendWithoutUsername() {
		// TODO
	}

	@Test
	public void sendWithoutRecipient() {
		// TODO
	}

	@Test
	public void sendWithoutLogin() {
		// TODO
	}
}
