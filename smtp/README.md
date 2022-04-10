# SMTP

This is a multi-threaded server.

We need privilegied permissions to run a server with a port below 1025, in our case port 25.



## MockMock

This is a mock server for mailing (TODO: explain what a mock server is)

Its repository is integrated using git [subtree](https://www.atlassian.com/git/tutorials/git-subtree) for it is simpler to use than git submodules.

```bash
git subtree add -P smtp/mockmock git@github.com:dgheig/MockMock.git master
```

### Changes Made

Some changes where made to use this program

* Clean-up maven pom file:
  * Remove all plugins and use `maven-assembly-plugin` plugin instead.
  * Fixed the name of the jar artifact to `MockMock.jar`
  * Fixed java version to Java 11
* Add `Dockerfile` configuration
* Complete `.gitignore` file





## Compilation and Launch

We won't use idea for compilation except to check the build.

From the `smtp` folder

### Compilation

```bash
mvn clean compile assembly:single
```



### Run

```bash
sudo java -jar target/SMTPServer.jar
```



## Docker

### Build image

```bash
docker build -t res-smtpserver .
```

### Run it standalone

```bash
docker run --name smtp res-smtpserver
```

Nb: add `-d` option to run it in the background

To get its ip address:

```bash
docker inspect smtp -f '{{.NetworkSettings.IPAddress}}'
```



## Docker-compose

It creates the whole docker architecture:

* SMTP server
* Mailing Mock Server: MockMock
* Fixed internal network addresses



Simply run:

```bash
docker-compose up -d
```



Find the bridged IP of your smtp server:

```bash
docker inspect smtp_smtp_1 -f '{{.NetworkSettings.Networks.smtp_default.IPAddress}}'
```
