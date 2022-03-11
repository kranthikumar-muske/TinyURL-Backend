# TinyURL-Backend
Front end deployed App: https://github.com/kranthikumar-muske/MyTinyURL

This backend code is serveles(deployed to cloud), you dont need to setup local to test it.

## API endpoints:

* getLongURL:
  https://w8pnbp6qkg.execute-api.us-east-2.amazonaws.com/Prod/getLongURL?shortURL=www.ty.l/lVf07
  - retrieves the longURL for a given shortURL

* createShortURL:
  https://w8pnbp6qkg.execute-api.us-east-2.amazonaws.com/Prod/createShortURL?longURL=www.longURL.com/913abc
  - Generates a shortURL from the given longURL
  - Duplicates are not created

* deleteShortURL:
  https://w8pnbp6qkg.execute-api.us-east-2.amazonaws.com/Prod/deleteShortURL?shortURL=www.ty.l/lVf07
  - shortURLs can be deleted

* getStats:
  - https://w8pnbp6qkg.execute-api.us-east-2.amazonaws.com/Prod/getShortURLStats?shortURL=www.ty.l/lVf07
  - Lists the number of times a short url has been accessed in the last 24 hours, past week, and all time. 


### Technologies used:

* AWS DynamoDB - for data persistent
* AWS Lambda - for computational logic
* AWS APIGateway - REST API end points
* Google Guava Cache - to speed up the retrieval process
* Java - language to code
* Maven - build tool
* Git - source control

you can also run those API endpints in postman
![image](https://user-images.githubusercontent.com/61674292/157797854-cf174c28-00c4-4c1b-b176-825d96824545.png)



If you would like to make any code changes and redeploy the APIS, follow below steps:

* install intelliJ with AWS toolkit
* create an aws account if you dont have one.
* connect to aws account from your intelliJ using aws toolkit
* clone this project master branch and open it in intelliJ
* make code changes
* right click on the project and click on deploy serverless application
* now goto aws API gateway in aws console and grab the endpoints
* you can test them in postam or use in any projects

