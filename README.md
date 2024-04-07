# SSOLite

This is a lightweight Single Sign On library using Spring Security 5.

It consists of a server that performs authentication and a client that
uses authenticated information provided by the server.
The server does not provide authorization mechanism. Authorization is
a client's job.

## Spring Version Compatibility
SSOLite provides support for Spring Security and Spring Boot.

Spring Security underwent major changes in version 6, so the SSOLite
versions are divided into 1.0.x, which supports Spring Security 5,
and 2.0.x, which supports Spring Security 6.

| SSOLite | Spring Security | Spring Boot |
|---------|-----------------|-------------|
| 1.0.x   | 5.x             | 2.x         |
| 2.0.x   | 6.x             | 3.x         |


## Samples

### Server sample using Spring
'sample-server-spring' is a server sample using Spring. To run it:
```sh
$ ./gradlew sample-server-spring:bootRun
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
correct username and password, you will return the client page.

This client sample also has local login mechanism. You can access
http://127.0.0.1:8081/login to login with the client-local username
and password.

### Client sample using Struts2
'sample-client-struts2' is a client sample using Struts2. To run it:
```sh
$ ./gradlew sample-client-struts2:tomcatRun
```
You can access this server with http://127.0.0.1:8081/. You will see
the server login page if the server is running. After entering the
correct username and password, you will return the client page.

This client sample also has local login mechanism. You can access
http://127.0.0.1:8081/login to login with the client-local username
and password.

## Mechanism
TODO

## Server Configuration
To use SSOLite server library for Spring, set some properties in the
server's application.properties (or application.yml, etc.).
```
# Set true on the server application.
ssolite.server.enabled = true

# Default redirecting URL after login.
# 'internal:' indicates the relative path from the context root.
ssolite.server.default-top-page-url = internal:/

# Permitted client URL that is used in the single sign on.
ssolite.server.permitted-domains[0] = http://<host>/sso-login
...
```

## License
Apache License 2.0. See LICENSE file for details.
