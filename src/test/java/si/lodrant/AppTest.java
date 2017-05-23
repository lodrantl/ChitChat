package si.lodrant;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.post;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.contains;

import org.jooby.test.JoobyRule;
import org.junit.Rule;
import org.junit.Test;

import si.lodrant.chitchat.App;

/**
 * @author Luka Lodrant
 * @author Lenart Treven
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

		post("/users?username=mouse").then()
				.assertThat()
				.body("status", equalTo("User logged in"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");

		get("/users").then()
				.assertThat()
				.body("", hasSize(2))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");
	}

	@Test
	public void addEmptyUser() {
		post("/users?username=").then()
				.assertThat()
				.body("status", equalTo("Bad Request(400): Parameter username is empty"))
				.statusCode(400)
				.contentType("application/json;charset=UTF-8");
	}

	@Test
	public void addTakenUser() {
		post("/users?username=miki").then()
				.assertThat()
				.body("status", equalTo("User logged in"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");

		post("/users?username=miki").then()
				.assertThat()
				.body("status", equalTo("Forbidden(403): User already exists"))
				.statusCode(403)
				.contentType("application/json;charset=UTF-8");
	}

	@Test
	public void deleteExistingUser() {
		post("/users?username=miki").then()
				.assertThat()
				.body("status", equalTo("User logged in"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");

		delete("/users?username=miki").then()
				.assertThat()
				.body("status", equalTo("User logged out"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");

		get("/users").then()
				.assertThat()
				.body(equalTo("[]"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");
	}

	@Test
	public void deleteNonExistingUser() {
		delete("/users?username=miki").then()
				.assertThat()
				.body("status", equalTo("User didn't exist in the first place."))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");
	}

	@Test
	public void deleteNoParameterUser() {
		delete("/users").then()
				.assertThat()
				.body("status", equalTo("Bad Request(400): Required parameter 'username' is not present"))
				.statusCode(400)
				.contentType("application/json;charset=UTF-8");

		delete("/users?username=").then()
				.assertThat()
				.body("status", equalTo("Bad Request(400): Parameter username is empty"))
				.statusCode(400)
				.contentType("application/json;charset=UTF-8");
	}

	@Test
	public void sendMessage() {
		post("/users?username=miki").then()
				.assertThat()
				.body("status", equalTo("User logged in"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");

		post("/users?username=mouse").then()
				.assertThat()
				.body("status", equalTo("User logged in"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");

		given().body("{\"global\":false, \"recipient\":\"mouse\", \"text\":\"bambam\"}")
				.contentType("application/json")
				.when()
				.post("/messages?username=miki")
				.then()
				.assertThat()
				.body("status", equalTo("Message sent"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");

		get("/messages?username=mouse").then()
				.assertThat()
				.body("global", contains(false))
				.body("recipient", contains("mouse"))
				.body("text", contains("bambam"))
				.body("sender", contains("miki"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");
	}

	@Test
	public void sendWithoutLogin() {
		post("/users?username=mouse").then()
				.assertThat()
				.body("status", equalTo("User logged in"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");

		given().body("{\"global\":false, \"recipient\":\"mouse\", \"text\":\"bambam\"}")
				.contentType("application/json")
				.when()
				.post("/messages?username=miki")
				.then()
				.assertThat()
				.body("status", equalTo("Unauthorized(401): You are not logged in."))
				.statusCode(401)
				.contentType("application/json;charset=UTF-8");
	}

	@Test
	public void sendGlobalMessage() {
		post("/users?username=miki").then()
				.assertThat()
				.body("status", equalTo("User logged in"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");

		post("/users?username=mouse").then()
				.assertThat()
				.body("status", equalTo("User logged in"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");

		post("/users?username=pac").then()
				.assertThat()
				.body("status", equalTo("User logged in"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");

		given().body("{\"global\":true, \"recipient\":\"\", \"text\":\"sentbymiki\"}")
				.contentType("application/json")
				.when()
				.post("/messages?username=miki")
				.then()
				.assertThat()
				.body("status", equalTo("Message sent"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");

		given().body("{\"global\":true, \"recipient\":\"hehe\", \"text\":\"sentbymiki2\"}")
				.contentType("application/json")
				.when()
				.post("/messages?username=miki")
				.then()
				.assertThat()
				.body("status", equalTo("Message sent"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");

		given().body("{\"global\":true, \"text\":\"sentbymouse\"}")
				.contentType("application/json")
				.when()
				.post("/messages?username=mouse")
				.then()
				.assertThat()
				.body("status", equalTo("Message sent"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");

		get("/messages?username=mouse").then()
				.assertThat()
				.body("", hasSize(2))
				.body("[0].global", equalTo(true))
				.body("[0].recipient", equalTo("mouse"))
				.body("[0].text", equalTo("sentbymiki"))
				.body("[0].sender", equalTo("miki"))
				.body("[1].global", equalTo(true))
				.body("[1].recipient", equalTo("mouse"))
				.body("[1].text", equalTo("sentbymiki2"))
				.body("[1].sender", equalTo("miki"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");

		get("/messages?username=pac").then()
				.assertThat()
				.body("", hasSize(3))
				.body("[0].global", equalTo(true))
				.body("[0].recipient", equalTo("pac"))
				.body("[0].text", equalTo("sentbymiki"))
				.body("[0].sender", equalTo("miki"))
				.body("[1].global", equalTo(true))
				.body("[1].recipient", equalTo("pac"))
				.body("[1].text", equalTo("sentbymiki2"))
				.body("[1].sender", equalTo("miki"))
				.body("[2].global", equalTo(true))
				.body("[2].recipient", equalTo("pac"))
				.body("[2].text", equalTo("sentbymouse"))
				.body("[2].sender", equalTo("mouse"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");
	}

	@Test
	public void sendWithoutUsername() {
		post("/users?username=mouse").then()
				.assertThat()
				.body("status", equalTo("User logged in"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");

		given().body("{\"global\":false, \"recipient\":\"mouse\", \"text\":\"bambam\"}")
				.contentType("application/json")
				.when()
				.post("/messages?username=")
				.then()
				.assertThat()
				.body("status", equalTo("Bad Request(400): Parameter username is empty"))
				.statusCode(400)
				.contentType("application/json;charset=UTF-8");

		given().body("{\"global\":false, \"recipient\":\"mouse\", \"text\":\"bambam\"}")
				.contentType("application/json")
				.when()
				.post("/messages")
				.then()
				.assertThat()
				.body("status", equalTo("Bad Request(400): Required parameter 'username' is not present"))
				.statusCode(400)
				.contentType("application/json;charset=UTF-8");

	}

	@Test
	public void sendWithoutRecipient() {
		post("/users?username=miki").then()
				.assertThat()
				.body("status", equalTo("User logged in"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");

		post("/users?username=mouse").then()
				.assertThat()
				.body("status", equalTo("User logged in"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");

		given().body("{\"global\":false, \"recipient\":\"\", \"text\":\"bambam\"}")
				.contentType("application/json")
				.when()
				.post("/messages?username=miki")
				.then()
				.assertThat()
				.body("status", equalTo("Bad Request(400): Cannot send a message without a recipient."))
				.statusCode(400)
				.contentType("application/json;charset=UTF-8");

		given().body("{\"global\":false, \"text\":\"bambam\"}")
				.contentType("application/json")
				.when()
				.post("/messages?username=miki")
				.then()
				.assertThat()
				.body("status", equalTo("Bad Request(400): Cannot send a message without a recipient."))
				.statusCode(400)
				.contentType("application/json;charset=UTF-8");
	}

	@Test
	public void sendToInactiveUser() {
		post("/users?username=miki").then()
				.assertThat()
				.body("status", equalTo("User logged in"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");

		post("/users?username=mouse").then()
				.assertThat()
				.body("status", equalTo("User logged in"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");

		given().body("{\"global\":false, \"recipient\":\"pac\", \"text\":\"bambam\"}")
				.contentType("application/json")
				.when()
				.post("/messages?username=miki")
				.then()
				.assertThat()
				.body("status", equalTo("Message sent"))
				.statusCode(200)
				.contentType("application/json;charset=UTF-8");
	}

	@Test
	public void receiveMessagesWithoutLogin() {
		get("/messages?username=miki").then()
				.assertThat()
				.body("status", equalTo("Unauthorized(401): You are not logged in."))
				.statusCode(401)
				.contentType("application/json;charset=UTF-8");
	}

	@Test
	public void receiveMessagesWithoutName() {
		get("/messages?username=").then()
				.assertThat()
				.body("status", equalTo("Bad Request(400): Parameter username is empty"))
				.statusCode(400)
				.contentType("application/json;charset=UTF-8");

		get("/messages").then()
				.assertThat()
				.body("status", equalTo("Bad Request(400): Required parameter 'username' is not present"))
				.statusCode(400)
				.contentType("application/json;charset=UTF-8");

	}
}
