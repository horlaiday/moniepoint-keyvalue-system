# moniepoint-keyvalue-system
A network-available persistent Key/Value system take home assignment.


Please ensure your system has git and maven installed.

Then go to your system command prompt(window user) and terminal(mac-os)

Copy and paste below command to a directory where you want to run the project.

git clone https://github.com/horlaiday/moniepoint-keyvalue-system.git

OR

git clone git@github.com:horlaiday/moniepoint-keyvalue-system.git

The press enter on your computer keyboard key.

Then after a successful download, navigate inside this project folder to view the sub-dir.

Copy and paste below command inside the project.

  ./mvnw spring-boot:run

Press enter on the keyboard to run the project.
This will download all the required files/dependencies for the project and start the server on port:8080.

To test if its running successfully, copy and paste below endpoint on your system browser, then press enter.

http://localhost:8080/swagger-ui/index.html

Above exposed 5 methods & endpoints

POST /api/kvstore/{key} – Store a single key-value pair.
GET /api/kvstore/{key} – Retrieve a value by its key.
GET /api/kvstore/range?startKey=&endKey= – Retrieve values for a range of keys.
POST /api/kvstore/batchPut – Insert multiple key-value pairs in a single request.
DELETE /api/kvstore/{key} – Delete a key-value pair.

Testing the project with below CURL command.

..kindly note the port you are running on in case you change the port on the properties file.

1. Put(Key, Value)
curl --location 'http://localhost:8080/api/kvstore/3' \
--header 'Content-Type: application/json' \
--data '{"Olaide":"4"}'

2. Read(Key)
curl --location 'http://localhost:8080/api/kvstore/2'

3. ReadKeyRange(StartKey, EndKey)
curl --location 'http://localhost:8080/api/kvstore/range?startKey=1&endKey=5' 

4. BatchPut(..keys, ..values)

5. Delete(key)
curl --location --request DELETE 'http://localhost:8080/api/kvstore/1' 


BONUS:
1. Replicate data to multiple nodes
2. Handle automatic failover to the other nodes
   
To activates above 2 implementation, you will need to alter some values on the properties file

locate:  replicate.data=false
change to : replicate.data=true

Also set correct location of your replica server/locations

follower.nodes={valid storage location}
node.id={valid storage location}
all.nodes={valid app-node, valid app-node}
initial.leader=master-node


...for immediate support call 2348032372090