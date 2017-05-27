API documentation
=================

## Table of contents

* [General](#general)
* [Get users](#get-users)
* [Log in](#log-in)
* [Log out](#log-out)
* [Receive messages](#receive-messages)
* [Send message](#send-message)


General
------

All dates are formated in ISO8601 date format (https://en.wikipedia.org/wiki/ISO_8601).

#### Workflow:
1. Register
2. Repeat:
	* Get messages
	* Get users
	* Send messages if wanted
3. Log out


**Get users**
----
  Returns json data about active users.

* **URL**

  /users

* **Method:**

  `GET`
  
* **URL Params**

  None

* **Data Params**

  None

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** 
    ```json
	[
		{
			"username" : "miki",
			"last_active" : "2017-05-25T11:01:02Z"
		},
		{
			"username": "pluto",
			"last_active": "2017-05-25T11:01:05Z"
		}
	]
	```
 
 
* **Sample Call:**

  ```java
    String responseBody = Request.Get("http://chitchat.andrej.com/users")
                          .execute()
                          .returnContent()
                          .asString();
    System.out.println(responseBody);
    });
    ```
 
**Log in**
----
  Registers the user on server and allows him to send and receive messages.

* **URL**

  /users

* **Method:**

  `POST`
  
* **URL Params**

  **Required:**
 
  `username=[string]`

* **Data Params**

  None

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** `{ "status" : "User logged in." }`
   
 
* **Error Response:**

  * **Code:** 403 FORBIDDEN <br />
    **Content:** `{ "status" : "Forbidden(403): User already exists" }`

* **Sample Call:**

  ```java
    URI uri = new URIBuilder("http://chitchat.andrej.com/users")
            .addParameter("username", "miki")
            .build();

    String responseBody = Request.Post(uri)
                                 .execute()
                                 .returnContent()
                                 .asString();
    });
  ```
  
**Log out**
----
  Logs user out

* **URL**

  /users

* **Method:**

  `DELETE`
  
*  **URL Params**

   **Required:**
 
   `username=[string]`

* **Data Params**

  None

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** `{ "status" : "User logged out." }`
 
 
* **Sample Call:**

  ```java
    URI uri = new URIBuilder("http://chitchat.andrej.com/users")
            .addParameter("username", "miki")
            .build();

    String responseBody = Request.Delete(uri)
                                 .execute()
                                 .returnContent()
                                 .asString();
    });
  ```

**Receive messages**
----
  Returns all unread messages

* **URL**

  /messages

* **Method:**

  `GET`
  
*  **URL Params**

   **Required:**
 
   `username=[string]`

* **Data Params**

  None

* **Success Response:**

  * **Code:** 200 <br />
    **Content (array is sorted by sent_at):** 
    ```json
    [
  		{
    		"global" : false,
    		"recipient" : "miki",
    		"sender" : "pluto",
    		"text": "How are you miki? Woof.",
    		"sent_at" : "2017-05-25T11:12:32Z"
 		},
  		{
    		"global" : true,
    		"recipient" : "miki",
    		"sender" : "pluto",
    		"text": "Hi, everybody.",
    		"sent_at" : "2017-05-25T11:12:38Z"
 		 }
	]
	```
 
* **Error Response:**

  * **Code:** 401 Unauthorized <br />
    **Content:** `{ "status" : "Unauthorized(401): You are not logged in." }`


* **Sample Call:**

  ```java
    URI uri = new URIBuilder("http://chitchat.andrej.com/messages")
            .addParameter("username", "miki")
            .build();

    String responseBody = Request.Get(uri)
                                 .execute()
                                 .returnContent()
                                 .asString();
    });
  ```

**Send message**
----
  Send a message to desired users or globally

* **URL**

  /messages

* **Method:**

  `POST`
  
*  **URL Params**

   **Required:**
 
   `username=[string]`

* **Data Params**
	
	```json
	{
		"global" : false,
		"recipient" : "pluto",
		"text" : "How are you doing Miki? Wooof."
	}
	```
	
	OR
	
	```json
	{
		"global" : true,
		"text" : "Hi everybody!"
	}
	```
	

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** `{ "status" : "Message sent" }`
 
* **Error Response:**

  * **Code:** 401 Unauthorized <br />
    **Content:** `{ "status" : "Unauthorized(401): You are not logged in." }`

* **Sample Call:**

  ```java
    URI uri = new URIBuilder("http://chitchat.andrej.com/messages")
            .addParameter("username", "miki")
            .build();

    String message = "{ \"global\" : true, \"text\" : \"Hi everybody!\"  }";

    String responseBody = Request.Post(uri)
            .bodyString(message, ContentType.APPLICATION_JSON)
            .execute()
            .returnContent()
            .asString();
    });
  ```
