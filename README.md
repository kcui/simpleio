#A simple Dropwizard/Docker application

Features:
basic annotation, POST methods that echo back

To build and run:
docker build .
docker run <image id>
docker run -p 8080:8080 -p 8081:8081 <image id>
p flags bind 8080 and 8081 (default Docker ports)

TODO:
implement REST client controller (played around with authorization, not working yet)