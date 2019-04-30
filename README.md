Implementation Detail

We have used java.net package to implement the project. Nodes will have both Client and Server sockets, this is mainly because they need the server socket to give service for other nodes and the client socket for communicating with other nodes. In our implementation we have used 4 classes. Namely: Node.java, Data.java. ,Client.java, and NodeServer.java.

The Node class is responsible for creating Nodes. A node will have three properties node Id or key, IP address and port number. In this class we have implemented the setter and getter methods and a constrictor for creating a node.

The Data class is responsible for creating data objects. Data object will have also three properties data key, value and version. In this class we have also implemented the setters and getters methods and the constrictor for creating data objects.



The Client class is responsible for making request to get/store key value pairs and request a node to leave from a network. For this it uses a client socket, Dataoutputstream and Datainputstream classes for writing and reading streams from the network. To start a client we need to know in advance the address of an active node server.



The NodeServer class is responsible for handling all requests from other nodes and clients. In this class we have used server sockets for handling incoming connections and client sockets for making request from other nodes.

Here we will discuss in detail how the NodeServer class has been implemented. To start a node server, we will create a Node object by passing the node Id or key, node IP and port number, if it is the first node on the network, it will create a server socket and wait for incoming connections either from client or another node. If a client connected, it will give a service based on the request i.e. PUT/GET or LEAVE.
 

Except for the first node server if another node server wants to make a request to join the network it has to pass the address of the an active node server. Once the a request made, the requested node will check if it is the responsible node to handle the request based on the node key, otherwise it will pass the request to the responsible node on the network. When the responsible node get the request from a new node, first it will update its node list and pass the all the node lists on the network and data if their is any to be handled by the new node. After joining the network the new node will notify its presence to all other nodes and all the nodes will update their node list, and the new node starts to give service like the other nodes on the network. For handling node list we have used Arraylists from the collection framework.



Here we will discuss the procedures for execution:



1.	Open a terminal window and change your working director to the bin folder

2.	Run the following command for launching a node:

java NodeServer

Please enter IpAdd of node, local Port, node Id, (if it is not the first node) IpAdd of known node, port of known Node and node ID of known node to join the network


Then you will be asked to enter the IP address, port number and nodeId. For example to start a NodeServer with ip address: localhost port number: 2020 and nodeId: 20 the command will be as follows


localhost 2020 20

This will create the first node server

For the 2nd node to join the network it has to pass its address along with the address of the 1st node the following command shows how to launch the 2nd node


Assuming that the 2nd node has and IP address lolcalhost, port number 3030 and node id 30
