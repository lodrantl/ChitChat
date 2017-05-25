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
    ```javascript
	[
		{
			"username": "miki",
			"last_active": "2017-05-25T11:01:02Z"
		},
		{
			"username": "pluto",
			"last_active": "2017-05-25T11:01:05Z"
		}
	]
	```
 
 
* **Sample Call:**

  ```javascript
    $.ajax({
      url: "/users",
      dataType: "json",
      type : "GET",
      success : function(r) {
        console.log(r);
      }
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

  ```javascript
    $.ajax({
      url: "/users?username=miki",
      dataType: "json",
      type : "POST",
      success : function(r) {
        console.log(r);
      }
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

  ```javascript
    $.ajax({
      url: "/users?username=miki",
      dataType: "json",
      type : "DELETE",
      success : function(r) {
        console.log(r);
      }
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
    ```javascript
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

  ```javascript
    $.ajax({
      url: "/messages?username=miki",
      dataType: "json",
      type : "GET",
      success : function(r) {
        console.log(r);
      }
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
	
	```javascript
	{
		"global" : false,
		"recipient" : "pluto",
		"text" : "How are you doing Miki? Wooof."
	}
	```
	
	OR
	
	```javascript
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

  ```javascript
	var message = {
		"global" : true,
		"text" : "Hi everybody!"
	};
	
    $.ajax({
      url: "/messages?username=miki",
      dataType: "json",
      data: message,
      type : "POST",
      success : function(r) {
        console.log(r);
      }
    });
  ```
