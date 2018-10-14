# SSOLite

This is a Single Sign On library using Spring Security 5.

It consists of a server that performs authentication and a client that
uses authenticated information provided by the server.
The server does not provide authorization mechanism. Authorization is
a client's job.

## Samples

### Server sample using Spring
'sample-server' is a server sample. To run it:
```sh
$ ./gradlew sample-server:bootRun
```
You can access this server with http://localhost:8080/. In the login
page, you enter the username and password to login.

### Client sample using Spring
'sample-client-spring' is a client sample using Spring. To run it:
```sh
$ ./gradlew sample-client-spring:bootRun
```
You can access this server with http://127.0.0.1:8081/. You will see
the server login page if the server is running. After entering the
correct username and password, you will retern the client page.

This client sample also has local login mechanism. You can access
http://127.0.0.1:8081/login to login with the client-local username
and password.

### Client sample using Struts2
'sample-client-struts2' is a client sample using Struts2. To run it:
```sh
$ ./gradlew sample-client-struts2:appRun
```
You can access this server with http://127.0.0.1:8081/. You will see
the server login page if the server is running. After entering the
correct username and password, you will retern the client page.

This client sample also has local login mechanism. You can access
http://127.0.0.1:8081/login to login with the client-local username
and password.

## Mechanism
TODO

## Server Configuration
To using SSOLite server library, you should set some configurations in
the server's application.properties (or application.yml, etc.).
```
# Set true on the server application.
ssolite.server.enabled = true

# Default redirecting URL after login.
# 'internal:' indicates the relative path from the context root.
ssolite.server.default-top-page-url = internal:/  #

# Permitted client URL that is used in the single sign on.
ssolite.server.permitted-domains[0] = http://<host>/sso-login
...
```

## License
Apache License 2.0. See LICENSE file for details.
