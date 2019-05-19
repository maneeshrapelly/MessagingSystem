# MessagingSystem
REST API for outgoing and incoming messages

REST API server is deployed at AWS under this end point:
http://MessaginApplication.us-east-2.elasticbeanstalk.com/

API /inbound/sms/:
This API can be accesses by requesting a HTTP POST method to URL http://MessaginApplication.us-east-2.elasticbeanstalk.com/inbound/sms/

API /outbound/sms/:
This API can be accessed by requesting a HTTP POST method to URL http://MessaginApplication.us-east-2.elasticbeanstalk.com/outbound/sms/

Steps to build:
1) Load the project from this repository into Eclipse
2) Build war file
File->Export->Web->WAR file-><select project and destination path>->Finish
3) Copy the war file generated at destination path to Web server(e.g. Tomcat)
4) Test the RESt APIs with Postman client by requesting HTTP POST method to <localhost:8080>/inbound/sms/ and <localhost:8080>/outbound/sms/

Steps to test the RESt service hosted in AWS:
1) Install Postman client application to test REST API. Follow below steps in Postman client
2) Select HTTP method as "POST" and URL as one of the API URLs mentioned above
3) Select authentication type as "Basic Authentication" and provide username and password fields from accounts table from postgres database
4) Write HTTP body in json format with from, to and text fields:
e.g: {"from": "441224980094",
"to":"441224459571",
"text": "HELLO"}
5) Verify the API returned response is expected output
6) Sample HTTP request from Postman client looks like below, json text is the body part:
POST /outbound/sms/ HTTP/1.1
Host: MessaginApplication.us-east-2.elasticbeanstalk.com
Content-Type: application/json
Authorization: Basic YXpyMjo1NFAyRU9LUTQ3
User-Agent: PostmanRuntime/7.13.0
Accept: */*
Cache-Control: no-cache
Postman-Token: b79c574e-9b74-4f60-a663-7f514d6b60be,cc576602-673a-4811-b1ec-ef26776d6f0a
Host: messaginapplication.us-east-2.elasticbeanstalk.com
accept-encoding: gzip, deflate
content-length: 62
Connection: keep-alive
cache-control: no-cache

{"from": "441224980094",
"to":"441224459571",
"text": "HELLO"}
