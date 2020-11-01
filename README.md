# worker
backend worker component written in java

# this app is dockerized.

build:
docker build -t IMG_NAME:TAG --build-arg JAR_FILE=NAME_OF_FILE .

run:
docker run -d -p 8080:8080 -e DB_URL='jdbc:mysql://192.168.99.100:3306/transport?useSSL=false&allowPublicKeyRetrieval=true' -e ROOT_PASS='secret' IMG_NAME:TAG
use your host, port, dbschema and root pass.

run DB image:
docker run  -p 3306:3306 --name sample-mysql -e MYSQL_ROOT_PASSWORD=secret -e MYSQL_DATABASE=transport -d mysql:8.0

image for worker is pullable from dockerhub repo: docker pull pchlebus/demo:worker



Hey guys,

the goal of the underlying EASy initiative is to enable digitalizing over 100 of transactions at faster pace, currently this was not achievable to all service transactions
accros all desired channels including web and mobile with currenct capacity of tribe. The project is led by Scot Brocklehurst and we delivered mvp version to production
last july for eht transaction with success. I was one of the contributor to this project as the part of kitten mittons team. We were working on web channel - Easy Web container component.
The project under heavy development with some significant architecture redesign including client sdk and xflowservice to make EASy even more scalable and maintanable over time.
These components however are not ready yet and are still shaping to meet the longterm goal, main Contributor for client SDK is Eric Carlton and Easy Peasy squad, ExpflowService team UOE team led by Nayan Patel.
Their roadmap assumes that the overall soution will be integrated with high degree of probability in December this year.
I will focus mainly on current state of EASy Web container that is run on production now. I will also try to summarize a bit what is intention of client SDK, and how to build the sdk and 
use it on the EASy host app.

Fisrt of all I would recommend to go through the docs on conflunce once again which should be enrtrypoint for all new developers, I havent seen this one in readme, so I assume that some of you
are not familiar with docs preapred by nandi, emilio or eric. Especially it is crucial to go over Emilio's expFflow description as expFlow is crucial part in defining new transaction.
To quicly summarize the explfow schema is the form descriptor.
An experience flow (XFlow) configuration object (XFlowConfig) is made of:
--------------------------------------------------------------------------------------------
flowId : unique flow identifier
botName: name of bot that runs this flow
flowName: human readable flow name
ereview: CMSR path for ereview number surrounded by curly braces
contentPath : Headless API container path from where to get localized resources
flowArguments: arguments to send to the cognitive store getAllIntents call and Headless API 
start: page reference to starting page id
pageData
Schema type: XFlowPageDataConfig

Defines a page title, subtitle, high risk transaction attribute and needs 2fa, array of content (XFlowPageDataContentConfig) and  transition (XFlowPageTransitionConfig) to the next step in the flow

content
Schema type: XFlowPageDataContentConfig

The content attribute of a pageData is an array of PageDataContent configuration items. Each configuration item can be of 2 types at this point: "apx-paragraph", or "intent" - other types may be supported in the future,
 Each type can have an arbitrary set of string properties specified in the attributes element.

Moreover, regardless of type, each PageDataContentConfig item can have transactionalRules and conditions 

transactionalRules
This element allows to set transaction or channel specific properties for an apex component, using documented property names for each specific component. simple example: required field
-----------------------------------------------------------------------------------------------------------

Now how the web container operates, so generally it consiste of two components : server and client

Server is express app powered by nodejs serving API to easy client app. I will go through some important parts of server codebase:


- config 
  - here are explfow transaction configs in json formats - as you can see some other transactions are added here as well aprt from eht like penny stock or international trading
  -apex-mapping required to map slot types served by cog API metadata requests to specifc apex components, this is required while resolving intent type entries in transaction config
  - cms files corresponding with transaciton configs, their main goal is to provide providence styling for apex web components where is it neccessary  
  generally config files will be externalized in future to make it out of scope of easy container deployment schedule
   - we ce got headless api client config for performing request via fmr headless api client library
   - middleawre: standard express middlewares applied here to be felix 2.0 app standards compliant that is cloud native standard for fidelity - enabling cors, csrf prevention, cookie settings, exposing health check endpoint and so on
   
   
 in cotroller there are 2  endpoints that are exposed here: GET and POST for /:form/:page path    form url segment is transation identifier

GET is used to retrieve form page metadata:
1. As you  know by now the exp flow defines each page and contained content entries per page for transaction, this is the the json file

and what happens here:
1.  resolve tridion keys with actual values using headless api ( the content is served in markdown format to make it channel agnostic, web and mobile can consume these and convert to suitalbe representation on its end )
2. resolve intent types if there are any on the page in expflow, intent types could be located in two places: 
 under content or under next section
 
if the content is intent type then the metadata cognitive store req. is reuqired to get all slots specified in transactionalRules by bot and intentName, intents are workflows designed in cogdesigner
that are part of bot definition supporting transaction, you have to be aware that bot is specific for each transaciton, each bot represents single transaction, Mikalai is POC here as he was mainly contributing
to bot creation for eht transation

if the content is located in next then it means that there are multiple scenarios possbile for next navigation, thats why the intent type here has to be resolved as well calling cog api here

if all intents and cms keys are resolved the page content is served to client


for next endpoint wihich is formfulfill we basically are passing slots array with key value pairs and utterance to call right intent like  updateintent defined under bot
additional feature implemented recently by Piotr was enabling to take screenshot of specific pages and send these along with other slots,
each page is additionally marked as with additial atrribute screenshot{enabled;true, sessionImageKey: XXX}, puppeter is used here to convert html string to binary image encoded in base64 format


client is vuejs app responsible for consuming web container server api which serves form metadata and finally render properly the form page. The two most important components
here are ComponentFactory and FormContainer.  I will show the metadata locally

FormContainer renders the form page based on received metadata content entries (apx/pvd or intent) leveraging componentFactory to actually render it using createElement funciton with properties like ids, stlying attrs, events listeners etc. Each new component have to be impl. within facotry method here. FormContainer is responsible here to pass comp. config in unified way for each page comp. entry.  It is good so that you get familiar with using createElement functions for rendering DOM nodes if you would like to 
extend facotry with new apx component.
I will send you link to docs as well: https://vuejs.org/v2/guide/render-function.html   


2fa is url segment based, is fired when hrt url segment is found in url of page, 2fa modal is triggered          

summing up right now adding new transaciton involves:
- adding static ocntent on tridion
- adding transcation config file in json
- adding correspoding providence styling in cms config file ( would be also served from tridion in future as I remember)
- adding bot and required cognitive data providers in graphql if required

this are the main steps to get new transation working

now lets move to SDK client library, the main intention here it to make sdk framework agnostic, now it wont matter if the EASy host app is angular/react or vue app, sdk is using native browser api and is framework indepenent,
second thing librry will be in future shipped by CDN as an external library within webpack bundler which is industry standard now, it would enable to not redeploy the host app once new sdk is released (using latest ver)

------------------------------------------------------------------------------------------


Build XFlow Web SDK

Navigate to the directory that the XFlow Web SDK was cloned into.

In a terminal, run: npm run build

How to Run

Navigate to the directory that the XFlow Web SDK was cloned into.

In a terminal run: npm run dev

Once EASy application build completes, navigate to: http://localhost:3000/prgw/digital/easy/eht/education
---------------------------------------------------------------------------------------------------------------

sdk responsibilities:

rendering components using browser native api
2fa handling
tracking user actions in the flow ( trackservice written by Eric will be moved to sdk)
events firing:  "next"/ "completed" to notify Easy container to change the route/destroy eventBus listeners on container end
easy container responsibilites:

routing
loading indicator handling
sdk methods call


easy host app is ussing callbacks to communicate with sdk, the whole sdk library is written in ts for type safety and avoid runtime errors.

Curently EASYPEASY squad is heavily wokring on it.

