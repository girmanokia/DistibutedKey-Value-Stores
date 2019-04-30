# DistibutedKey-Value-Stores

 1. Open a terminal window and change your working director to the bin folder  
 2. Run the following command for launching a node:      java NodeServer 
 Please enter IpAdd of node, local Port, node Id, (if it is not the first node) 
 IpAdd of known node, port of known Node and node ID of known node to join the network 
 
 
 Then you will be asked to enter the IP address, port number and nodeId. 
 
 For example to start a NodeServer with ip address: localhost port number: 2020 and nodeId: 20 the command will be as follows  
 localhost 2020 20 
 This will create the first node server 
 For the 2nd node to join the network it has to pass its address along with the address of the 1st node the following
 command shows how to launch the 2nd node 
 
 Assuming that the 2nd node has and IP address lolcalhost, port number 3030 and node id 30  
 
localhost 3030 30 localhost 2020 20 
 
 
To start the 3rd node the node can make request to any of the above two nodes and building the network continue like this.  
 To start a client at least one active node on the network is required. 
 For launching a client: 
 1. Open a terminal and change the director to the bin folder 
 2. Type the following command:  Java Client  Then you will be asked to enter the address of an active node server 
 Please enter IP, Port GET/PUT Key Value 
 
 
For storing a key value pair use the following command,
note that the client can make the request to any of the active node servers assuming that the client 
made the request on the first node server key 10 and value ten 

 localhost 2020 PUT 10 ten  
 For getting a value, assuming that the request made to the 2nd node server 
 localhost 3030 GET 10 
 
 
For making a request node server 2 to leave the network 
 localhost 3030 30 leave 
