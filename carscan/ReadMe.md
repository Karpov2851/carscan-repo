# Rest API for Pushing Image Files to AWS S3

### How To Run it


* Run java -jar carscan.jar for creating serving the application on embeded tomcat
* Hit the Url http://localhost:8080/cs-ws/upload-image with form-data as imageFile. Max size is 10 mb since, which is configurable


### Docker Setuo
For serving the application as an image use the following steps

* Run the command docker build -t carscan.jar . which will create an image in docker container
* Following the previous step run docker run -p 9090:8080 carscan.jar will serve the application on port 9090 on the container. 
* Hit the Url http://localhost:9090/cs-ws/upload-image with form-data as imageFile , to access the service on the container

### Salient Features of the application
* Complete end to end testing with PowerMock and Mockito has been used in this application
* Logging with appenders is also present in the application


### Features that could be added
* Swagger ui for easy documentation of the rest api
* Advanced packaging for production/uat environments