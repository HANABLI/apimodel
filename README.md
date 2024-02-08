# apimodel
Building an API Using "enterprise" tools and technologies such as "Github, Java 17, IntelliJ, Maven, Jetty, JAX-RS, Jersey, AWS, RHEL". A from scratch project build into a fully functional and secure system, complete with OpenAPI documentation, running on Amazon AWS and available in the RapidAPI marketplace


tooltips :
#----------------------------------------------------
$ curl -k -vvv https://localhost:8443/  {-k param added to ignore secure mode} 
#-----------------------------------------------------
$ curl --cacert modelapi-server/src/main/resources/certs/ca.crt -vvv https://localhost:8443/ {curl using the certificate on option}
#-----------------------------------------------------