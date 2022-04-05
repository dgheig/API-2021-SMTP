# SMTP

This is a multi-threaded server.

We need privilegied permissions to run a server with a port below 1025, in our case port 25.



## Compilation and Launch

We won't use idea for compilation except to check the build.

From the `smtp` folder

### Compilation

```bash
mvn clean compile assembly:single
```



### Run

```bash
sudo java -jar target/SMTPServer-1.0-jar-with-dependencies.jar
```

