ChitChat
-----------

[![Build Status](https://travis-ci.org/lodrantl/ChitChat.svg?branch=master)](https://travis-ci.org/lodrantl/ChitChat)

Chat server namenjen študentom matematike, preko katerega se lahko z uporabo lastnih odjemalcev pogovarjajo s svojimi sošolci.

Za testiranje lahko iz tega GitHub repozitorija (zavihek releases) prenesete jar datoteko z imenom "chitchat-X.Y.Z.jar" in jo lokalno zaženete z zagonom tega ukaza v konzoli.

```java -jar ime-prenesene-datoteke.jar```

Server bo potem dosegljiv na: http://localhost:8080

V računalniški učilnici smo na vajah ugotovili, da imamo probleme z cachem zahtevkov. Nekdo na poti od naših računalnikov do strežnika si zahteveh zapomne in nam vrne stare podatke. Ukanemo ga lahko tako, da vsakemu zahtevku v url dodamo trenutni čas v ms kot nek neuporaben parameter.
```java
String time = Long.toString(new Date().getTime());

URI uri = new URIBuilder("http://chitchat.andrej.com/users")
        .addParameter("stop_cache", time)
        .build();

String responseBody = Request.Get(uri)
                             .execute()
                             .returnContent()
                             .asString();

System.out.println(responseBody);
```

#### [Dokumentacija vmesnika](./API.md)