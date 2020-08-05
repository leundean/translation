# translation

This is a simple microservice for storing text strings (such as GUI-labels) and translations. It is based on an efficient data-model and values are easy to manipulate. It is built as a traditional service structure. Features are:

- Creating/Updating a placeholder key (e.g. "open") and translations (e.g. language-code: "se", language-translation: "öppna")
- All additions are idempotent: If no value exist (placeholder key or translation) it is created. If already existing it is updated/overwritten.
- Additions of key and one or multiple translations uses a request body object. A single translation can be made with a simple object, and even more simple additions can be made in the URL (be sure to URLEncode if so).
- Removing a placeholder key with its translations OR removing a particular translation
- Retrieving all placeholder keys AND/OR translations
- Getting the maximum translated length for a key (e.g for use in GUI buttons with limited space)
- Querying for one translated language from another translated language (translating)
- Note that a key can have any number of translated languages associated: Translations can gradually and arbitrarily be added to original keys.
- Spring components (SpringBoot, Spring WebFlux with MongoRepository)
- Integration tests (JUnit) with Spring WebTestClient
- Persistent storage in MongoDB

## Installation and use instructions

1. Install MongoDB (free community version available) <https://www.mongodb.com/try/download/community>
2. Start "mongodb" service
3. Clone and open this project (IntelliJ IDEA)
4. If necessery change properties for server address, port, dbname in "application.properties"
5. Build project with Maven
6. Run project
7. Doublecheck entries with "mongo" cli ("use <dbname>", "use <tablename>", and e.g. "db.<tablename>.find()" for listing all values)



## API

### POST /create
Request-body: {"key": "<placeholder-key>", "tls": {"<code1>":"<translation-lang1>", "<code2>":"<translation-lang2>", ...}}

Example:
/create
Request-body: {"key": "open", "tls": {"se":"öppna", "de":"öffnen"}} -> Response-code: OK

### POST /add/<code>
Request-body: {"key": "<placeholder-key>", "tl":"<translation-lang>"}

Example:
/add/se
Request-body: {"key": "open", "tl": "öppna"} -> Response-code: OK

### GET /add/<code>/<placeholder-key>/<translation-lang>

Example:
/add/se/open/%C3%B6ppna -> Response-code: OK

### DELETE /delete/<placeholder-key>

Example:
/delete/open -> Response-codes: OK or NO_CONTENT

### DELETE /delete-language/<code>/<placeholder-key>

Example:
/delete-language/se/open -> Response-codes: OK or NO_CONTENT

### GET /<code>/<placeholder-key>

Example: /se/open -> {"key: "open", "tl":"öppna"} or Response-code: NO_CONTENT

### GET /source/<code>/<query-code>/<query-translation>

Example:
/source/en/se/%C3%B6ppna -> {"key: "open", "tl":"open"} or Response-code: NO_CONTENT

### GET /maxlength/<placeholder-tag>

Example:
/maxlength/open  -> {"key": "open", tl": "7"} or Response-code: BAD_REQUEST

### GET /info

Example:
/info -> {"dbname": "translang","size": "4"}
