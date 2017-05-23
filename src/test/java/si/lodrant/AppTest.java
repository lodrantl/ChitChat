package si.lodrant;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.post;
import static io.restassured.RestAssured.delete;
import static org.hamcrest.Matchers.equalTo;
import static io.restassured.RestAssured.given;

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
		post("/users?username=").then()
			.assertThat()
			.body("status", equalTo("Bad Request(400): Cannot log you in if I do not know who you are (parameter missing)"))
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
	public void deleteExsistingUser() {
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
	public void deleteNonExsistingUser() {
		delete("/users?username=miki").then()
			.assertThat()
			.body("status", equalTo("User didn't exist in the first place."))
			.statusCode(200)
			.contentType("application/json;charset=UTF-8");
	}
	
	@Test
	public void deleteNoParameterUser() {
		delete("/users?username=").then()
			.assertThat()
			.body("status", equalTo("Bad Request(400): Cannot delete you if I do not know who you are (parameter missing)"))
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
		given()
		    .body("{\"global\":false, \"recipient\":\"mouse\", \"text\":\"bambam\"}")
		    .contentType("application/json").
		when()
			.post("/messages?username=miki").
		then()
			.assertThat()
			.body("status", equalTo("Message sent"))
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
	
	given()
	    .body("{\"global\":false, \"recipient\":\"mouse\", \"text\":\"bambam\"}")
	    .contentType("application/json").
	when()
		.post("/messages?username=miki").
	then()
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
		given()
		    .body("{\"global\":true, \"recipient\":\"\", \"text\":\"bambam\"}")
		    .contentType("application/json").
		when()
			.post("/messages?username=miki").
		then()
			.assertThat()
			.body("status", equalTo("Message sent"))
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
	
	given()
	    .body("{\"global\":false, \"recipient\":\"mouse\", \"text\":\"bambam\"}")
	    .contentType("application/json").
	when()
		.post("/messages?username=").
	then()
		.assertThat()
		.body("status", equalTo("Bad Request(400): Cannot send messages if I do not know who you are (parameter missing)"))
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
		given()
		    .body("{\"global\":false, \"recipient\":\"\", \"text\":\"bambam\"}")
		    .contentType("application/json").
		when()
			.post("/messages?username=miki").
		then()
			.assertThat()
			.body("status", equalTo("Bad Request(400): Cannot send messages if I do not tell recipient."))
			.statusCode(400)
			.contentType("application/json;charset=UTF-8");
	}

	@Test
	public void sendToUnactiveUser() {
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
		given()
		    .body("{\"global\":false, \"recipient\":\"pac\", \"text\":\"bambam\"}")
		    .contentType("application/json").
		when()
			.post("/messages?username=miki").
		then()
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
	
	public void receiveMessagesWithoutName() {
		get("/messages?username=").then()
			.assertThat()
			.body("status", equalTo("Bad Request(400): Cannot send messages if I do not know who you are (parameter missing)"))
			.statusCode(400)
			.contentType("application/json;charset=UTF-8");
	}
	
	
}
