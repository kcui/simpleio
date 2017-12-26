# A simple Dropwizard/Docker application

## Features

basic annotation, POST methods that echo back

## Build/run

Just Docker:

`docker build .`

`docker run -p 8080:8080 -p 8081:8081 <image id>`

p flags bind 8080 and 8081 (default Docker ports)

To run the Docker container in the minikube VM, simply run `eval $(minikube docker-env)` before building and running the container.

## Example usages

`> curl 'localhost:8080/query?message=hello'`
`You passed hello`

`> curl -X POST -d 'message=helloparam' localhost:8080/param`
`You posted hello`

`> curl -X POST -d 'message=hellopost' localhost:8080/post`
`You posted hellopost`

## Todo
implement REST client controller (played around with authorization, not working yet)