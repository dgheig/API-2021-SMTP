# SMTP

This is a multi-threaded server.

We need privilegied permissions to run a server with a port below 1025, in our case port 25.



### Usage

All commands are run from the `smtp/` folder

1. Compile your programs

   * Linux

     ```bash
     ./compile.sh
     ```

   * Windows

     ```powershell
     
     ```

     

2. Launch your server MockMock. We use docker to isolate the process and avoid permission issue with port 25

   ```bash
   docker-compose up -d mockmock  # This will automaticly build the image
   ```

   Alternatively, we can launch our relay server too

   ```
   docker-compose up -d
   ```

3. Display all your containers

   ```bash
   docker container ls -f label=RES
   ```

4. Retrieve their ip using the name of the container

   ```bash
   docker inspect smtp_mockmock_1 -f '{{.NetworkSettings.Networks.smtp_default.IPAddress}}'
   ```

5. Edit your configuration file

   ```bash
   {
       "server": "localhost",
       "port": 2500,
       "emailsFile": "tests/emails.txt",
       "messageFolder": "tests/messages2",
       "victimCount": 3
   }
   ```

6. Launch your client using your configuration file

   ```bash
   java -jar client/target/SpamCampaign.jar tests/config.json
   ```

   



## MockMock

This is a mock server for mailing. It behaves exactly as a real SMTP server would when handling messages and responding, but won't have any external impact as sending the mail to its recipients.

Its repository is integrated using git [subtree](https://www.atlassian.com/git/tutorials/git-subtree) for it is simpler to use than git submodules.

```bash
git subtree add -P smtp/mockmock git@github.com:dgheig/MockMock.git master
```

### Changes Made

Some changes where made to use this program

* Clean-up maven pom file:
  * Fixed java version to Java 11
* Add `Dockerfile` configuration
* Complete `.gitignore` file

```bash
mvn package
```



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

It creates the whole docker architecture and fowards the ports (this was needed for simplification on windows plateforms):

* SMTP server (Ports: 25 -> 2525)
* Mailing Mock Server: MockMock (Ports: 25 -> 2500, 8282 -> 8282)



Simply run:

```bash
docker-compose up -d
```



Find the bridged IP of your smtp server:

```bash
docker inspect smtp_smtp_1 -f '{{.NetworkSettings.Networks.smtp_default.IPAddress}}'
```
