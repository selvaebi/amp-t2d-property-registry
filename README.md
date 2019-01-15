# amp-t2d-property-registry
Registry service for AMP T2D properties.
 
 ### OAuth2 authenticated rest content server
 
 The authentication is provided by keycloak authentication server.
 
 To perform any valid authenticated request please follow the below steps,
 
 #### What you need are the following:
 
 * Client ID
 * Client Secret
 * Registered user email and password
 
 #### Requesting Access Token
   Now replace the values needed in the following link and put it in a web browser.
  
  ``` curl --data "grant_type=password&client_id=<clent_id>&client_secret=<client_secret>&username
  =<registeredUserEmail>&password=<password>" https://www.ebi.ac.uk/ega/ampt2d/auth/realms/Ampt2d/protocol/openid-connect/token ```
 
 You should get something like this:
 
 ``` {  
 “access_token”:“XXXXX”,
 “expires_in”:3600,
 “id_token”:“XXXXX”,
 “refresh_token”:“XXXXX”,
 “token_type”:“Bearer”
 } 
 ``` 
 
 You now have an access token you can use in your API call.
 
 #### Accessing the endpoints
   To access one of the secure rest services we send the access_token like this
 
 ```
 curl -H "Authorization: Bearer 27647b94-1f9f-4945-ae8f-6521d48fdcad" <host>:<port>/properties/
 ```
 
### Sending tokens in swagger
 Please pass the token in Authorize box value as "Bearer \<Token>"