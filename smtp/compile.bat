call mvn -f mockmock package
call mvn -f server clean compile assembly:single
call mvn -f client clean compile assembly:single
