Table of contents
=================

* [Get users](#get-users)
* [Log in](#log-in)
* [Log out](#log-out)
* [Receive messages](#receive-messages)
* [Send message](#send-message)

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
    **Content:** `{ id : 12, name : "Michael Bloom" }`
 
 
* **Sample Call:**

  ```javascript
    $.ajax({
      url: "/users/1",
      dataType: "json",
      type : "GET",
      success : function(r) {
        console.log(r);
      }
    });
 
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
   
 
* **Error Response:**

  * **Code:** 403 FORBIDDEN <br />
    **Content:** `{ error : "User already exists." }`

* **Sample Call:**

  ```javascript
    $.ajax({
      url: "/users/1",
      dataType: "json",
      type : "GET",
      success : function(r) {
        console.log(r);
      }
    });
  ```
  
**Log out**
----
  Returns json data about active users.

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
    **Content:** `{ id : 12, name : "Michael Bloom" }`
 
 
* **Sample Call:**

  ```javascript
    $.ajax({
      url: "/users/1",
      dataType: "json",
      type : "GET",
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
    **Content:** `{ id : 12, name : "Michael Bloom" }`
 
* **Error Response:**

  * **Code:** 401 Unauthorized <br />
    **Content:** `{ error : "Login first, then get messages." }`


* **Sample Call:**

  ```javascript
    $.ajax({
      url: "/users/1",
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

  None

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** `{ id : 12, name : "Michael Bloom" }`
 
* **Error Response:**

  * **Code:** 401 Unauthorized <br />
    **Content:** `{ error : "Login first, then send messages." }`

* **Sample Call:**

  ```javascript
    $.ajax({
      url: "/users/1",
      dataType: "json",
      type : "GET",
      success : function(r) {
        console.log(r);
      }
    });
  ```
