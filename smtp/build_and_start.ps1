cd server
mvn clean compile assembly:single
cd ..
cd mockmock
mvn clean compile assembly:single
cd ..
docker-compose up -d
