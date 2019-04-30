import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;

public class NodeServer extends Thread {

	public static ArrayList<Node> nodeList = new ArrayList<Node>();
	public static ArrayList<Integer> nodeIdList = new ArrayList<Integer>();
	public static ServerSocket servSock;
	public static Socket clientSock;
	public static String fileName;
	public static String currentIp;
	public static String currentPort;
	public static int currentId;
	public static Node prveNode;
	public static Node nextNode;
	public static Node currentNode;
	private static String updatedStringData = "";

	public NodeServer() {

	}

	public static void main(String[] args) throws FileNotFoundException,
			UnsupportedEncodingException {

		System.out
				.println("Please enter IpAdd of node ,local Port number,"
						+ " node Id, (if it is not the first node) IpAdd of known node, port of known Node and node ID of known node to join the network");

		startExecution();

	}

	public static String getAllNodes() {

		String allNodes = " ";
		if (nodeList.size() != 0) {

			for (int i = 0; i < nodeList.size(); i++) {

				allNodes = allNodes + nodeToString(nodeList.get(i)) + " ";
			}

		}

		return allNodes;

	}

	public static Node stringToNode(String nodeString) {
		Node node = null;

		String[] splits = nodeString.split(" ");

		node = new Node(Integer.valueOf(splits[0]), splits[1], splits[2]);
		return node;

	}

	public static void notifayAllNodes() {

		for (int i = 0; i < nodeList.size(); i++) {

			if (nodeList.get(i).getNodePort() != currentPort) {

				try {
					Socket sock = new Socket(nodeList.get(i).getNodeIp(),
							Integer.valueOf(nodeList.get(i).getNodePort()));
					DataOutputStream dos = new DataOutputStream(
							sock.getOutputStream());
					dos.writeInt(4);
					dos.writeUTF(nodeToString(new Node(currentId, currentIp,
							currentPort)));
					dos.close();
					sock.close();

				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

	}

	public static String allNodeId() {

		String allNodeID = "";

		if (nodeIdList.size() != 0) {

			for (int i = 0; i < nodeIdList.size(); i++) {

				allNodeID = allNodeID + " " + nodeIdList.get(i).toString();
			}
		}

		return allNodeID;
	}

	public static String nodeToString(Node node) {

		String nodeInfo = "";

		nodeInfo = Integer.toString(node.getNodeId()) + " " + node.getNodeIp()
				+ " " + node.getNodePort() + " ";

		return nodeInfo;
	}

	public static void startExecution() throws FileNotFoundException,
			UnsupportedEncodingException {

		@SuppressWarnings("resource")
		Scanner user_input = new Scanner(System.in);
		String input = user_input.nextLine();

		String[] splits = input.split(" ");
		String ip = splits[0];
		String port = splits[1];
		String nodeId = splits[2];

		currentId = Integer.valueOf(nodeId);
		currentIp = ip;
		currentPort = port;

		if (Integer.valueOf(currentPort) > 1024
				&& Integer.valueOf(currentPort) < 49151) {

			if (splits.length == 3) {

				currentNode = new Node(currentId, currentIp, currentPort);
				nodeList.add(currentNode);
				nodeIdList.add(Integer.valueOf(nodeId));
				createTextFile(port);
				System.out.println("First node with node id: " + currentId
						+ "started the network");
				System.out.println("New node with node id: " + currentId + " "
						+ "has succesfully joined the network" + '\n');
				System.out.println("Prev node id: " + " "
						+ getPreviousNodeID(currentNode) + " "
						+ "Next node id: " + " " + getNextNodeID(currentNode));

				waitForConnection(Integer.parseInt(port));

			} else if (splits.length == 6) {

				String ipConn = splits[3];
				String portConn = splits[4];
				String nodeIdconn = splits[5];
				createTextFile(port);
				currentNode = new Node(currentId, currentIp, currentPort);
				nodeList.add(currentNode);
				nodeIdList.add(Integer.valueOf(currentNode.getNodeId()));
				String allNodesString = makeConnection(nodeIdconn, ipConn,
						portConn);
				stringToNodes(allNodesString);
				notifayAllNodes();
				nextNode = getNextNode(currentNode);
				getDataFromPreviousNode(nextNode);
				System.out.println("New node with node id: " + currentId + " "
						+ "has succesfully joined the network" + '\n');
				System.out.println("Prev node id: " + " "
						+ getPreviousNodeID(currentNode) + " "
						+ "Next node id: " + " " + getNextNodeID(currentNode));
				waitForConnection(Integer.parseInt(port));
			} else {

				System.out.println("Enter proper number of arguments");

				startExecution();

			}

		} else {

			System.out
					.println("Please enter a port number between 1024 and 49151.");

			startExecution();
		}

	}

	public static void stringToData(String data) {

		String[] keyvalue = data.split(" ");

		for (int i = 1; i < (keyvalue.length) - ((keyvalue.length) / 3); i = i + 6) {

			writeToFile(new Data(Integer.valueOf(keyvalue[i + 1]),
					keyvalue[i + 3], 1));

		}

	}

	public static void stringToDataForLeaving(String data) {

		String[] keyvalue = data.split(" ");

		for (int i = 0; i < (keyvalue.length) - 5; i = i + 5) {

			writeToFile(new Data(Integer.valueOf(keyvalue[i + 1]),
					keyvalue[i + 3], 1));

		}

	}

	public static String inetAddToString(InetAddress ipAdd) {

		String ip = String.valueOf(ipAdd);

		return ip;
	}

	private static void getDataFromPreviousNode(Node nextNode) {

		if (nodeIdList.size() > 1) {

			try {
				Socket sock = new Socket(nextNode.getNodeIp(),
						Integer.valueOf(nextNode.getNodePort()));
				DataOutputStream dos = new DataOutputStream(
						sock.getOutputStream());
				dos.writeInt(5);
				DataInputStream dis = new DataInputStream(sock.getInputStream());

				String data = dis.readUTF();
				if (!data.isEmpty()) {
					stringToData(data);
				}
				dis.close();
				dos.close();
				sock.close();

			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private static String makeConnection(String nodeIdconn, String ipConn,
			String portConn) {

		String allNodes = "";

		try {
			Socket sock = new Socket(ipConn, Integer.valueOf(portConn));

			DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
			dos.writeInt(3);
			DataInputStream din = new DataInputStream(sock.getInputStream());
			allNodes = din.readUTF();
			dos.close();
			din.close();
			sock.close();

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return allNodes;
	}

	private static void createTextFile(String port)
			throws FileNotFoundException, UnsupportedEncodingException {

		fileName = "node" + port + ".txt";

		PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		writer.close();

	}

	private static void writeToFile(Data data) {

		int key = data.getKey();
		String value = data.getValue();
		String version = String.valueOf(data.getVersion());
		String keyvalue = key + "  " + value + "  " + version + '\n';

		try {
			FileWriter flieWriter = new FileWriter(fileName, true);
			BufferedWriter bfw = new BufferedWriter(flieWriter);
			bfw.write(keyvalue);
			bfw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String readFromFile(String key) {

		String line = "";
		String value = "";
		String[] keySearch;

		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {

				keySearch = line.split(" ");

				if (key.equals(keySearch[0])) {

					value = keySearch[2];
					break;

				}
			}
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + fileName + "'");
		}

		return value;

	}

	private static void waitForConnection(int localport) {

		try {
			servSock = new ServerSocket(localport);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		while (true) {

			try {
				clientSock = servSock.accept();
				DataInputStream dis = new DataInputStream(
						clientSock.getInputStream());
				int command = dis.readInt();

				switch (command) {
				case 1: {
					// PUT
					String input = dis.readUTF();
					String[] value = input.split(" ");
					int version = 1;
					DataOutputStream dos = new DataOutputStream(
							clientSock.getOutputStream());

					int key = Integer.valueOf(value[3]);

					int previousId = getPreviousNodeID(currentNode);

					Collections.sort(nodeIdList);

					if ((key > Collections.max(nodeIdList))
							|| ((key <= Collections.min(nodeIdList)) && (previousId == Collections
									.max(nodeIdList)))) {
						Node node = getNodeForNodeID(Collections
								.min(nodeIdList));

						Socket sock = new Socket(node.getNodeIp(),
								Integer.valueOf(node.getNodePort()));

						DataOutputStream dosnew = new DataOutputStream(
								sock.getOutputStream());
						dosnew.writeInt(6);
						dosnew.writeUTF(input);
						dosnew.close();
						sock.close();

					} else if ((key <= currentId && key > previousId)
							|| (nodeIdList.size() == 1)) {
						writeToFile(new Data(key, value[4], version));

					} else {

						Node node = null;

						Collections.sort(nodeIdList);

						for (int i = 0; i < nodeIdList.size(); i++) {

							if (key <= nodeIdList.get(i)) {

								node = getNodeForNodeID(nodeIdList.get(i));
								break;
							}

						}

						Socket sock = new Socket(node.getNodeIp(),
								Integer.valueOf(node.getNodePort()));
						DataOutputStream dosnew = new DataOutputStream(
								sock.getOutputStream());
						dosnew.writeInt(6);
						dosnew.writeUTF(input);
						dosnew.close();
						sock.close();
					}

					dis.close();
					dos.close();

				}
					break;
				case 2: {
					// GET

					String key = dis.readUTF();
					String[] split = key.split(" ");

					String realKey = split[3];
					String value = " ";

					int dataKey = Integer.valueOf(realKey);

					DataOutputStream dos = new DataOutputStream(
							clientSock.getOutputStream());
					int nearestNode = getNearestNodeForKey(dataKey);
					Node node = getNodeForNodeID(nearestNode);
					if (node.getNodeId() == currentId) {
						value = readFromFile(realKey);
						dos.writeUTF(value);
						dos.close();
						dis.close();
					} else {

						Socket sock = new Socket(node.getNodeIp(),
								Integer.valueOf(node.getNodePort()));
						DataOutputStream localDOS = new DataOutputStream(
								sock.getOutputStream());
						DataInputStream localDIS = new DataInputStream(
								sock.getInputStream());
						localDOS.writeInt(2);
						localDOS.writeUTF(key);
						value = localDIS.readUTF();
						dos.writeUTF(value);
						localDIS.close();
						localDOS.close();
						sock.close();

					}

					dos.close();
					dis.close();

				}
					break;
				case 3: {
					// Pass node list for new node
					String allnodes = getAllNodes();

					DataOutputStream dos = new DataOutputStream(
							clientSock.getOutputStream());
					dos.writeUTF(allnodes);
					dos.close();
					dis.close();
				}
					break;

				case 4: {
					// Update nodes
					String update = dis.readUTF();
					nodeList.add(stringToNode(update));
					nodeIdList.add(stringToNode(update).getNodeId());
					dis.close();

				}
					break;

				case 5: {

					// Pass data for next node

					prveNode = getPreviousNode(currentNode);
					String keyvalueForPrevNode = readDataForPrevNode(prveNode);
					DataOutputStream dos = new DataOutputStream(
							clientSock.getOutputStream());
					dos.writeUTF(keyvalueForPrevNode);
					updateFile();
					dos.close();
					dis.close();

				}
					break;
				case 6: {

					String input = dis.readUTF();
					String[] value = input.split(" ");
					int version = 1;
					int key = Integer.valueOf(value[3]);
					DataOutputStream dos = new DataOutputStream(
							clientSock.getOutputStream());
					writeToFile(new Data(key, value[4], version));
					dis.close();
					dos.close();

				}
					break;
				case 7: {
					// Pass data for next node while leaving

					Node node = getNextNode(currentNode);
					String data = getAllDataForLeaving();
					Socket sock = new Socket(node.getNodeIp(),
							Integer.valueOf(node.getNodePort()));
					DataOutputStream dos = new DataOutputStream(
							sock.getOutputStream());
					dos.writeInt(8);
					dos.writeUTF(data);
					dos.close();
					sock.close();
					notifayForLeaving();
					clientSock.close();
					System.exit(0);

				}
					break;
				case 8: {
					// Marshaling data for proper storage
					String data = dis.readUTF();
					stringToDataForLeaving(data);
					dis.close();

				}
					break;

				case 9: {

					// Update nodeList and nodeIdList after node leaves

					int key = Integer.valueOf(dis.readUTF());

					for (int i = 0; i < nodeIdList.size(); i++) {
						if (key == nodeIdList.get(i)) {
							nodeIdList.remove(i);
							nodeList.remove(getNodeForNodeID(key));
							break;

						}

					}

					dis.close();

				}
					break;

				}

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}

	private static void notifayForLeaving() {

		for (int i = 0; i < nodeList.size(); i++) {

			if (nodeList.get(i).getNodePort() != currentPort) {

				try {
					Socket sock = new Socket(nodeList.get(i).getNodeIp(),
							Integer.valueOf(nodeList.get(i).getNodePort()));
					DataOutputStream dos = new DataOutputStream(
							sock.getOutputStream());
					dos.writeInt(9);
					dos.writeUTF(String.valueOf(currentId));
					dos.close();
					sock.close();

				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

	}

	public static int getNearestNodeForKey(int key) {

		Collections.sort(nodeIdList);

		int value = 0;

		if (key > Collections.max(nodeIdList)
				|| key <= Collections.min(nodeIdList)) {

			value = Collections.min(nodeIdList);

		} else {

			for (int i = 1; i < nodeIdList.size(); i++) {

				if (nodeIdList.get(i) >= key) {

					value = nodeIdList.get(i);
					break;
				}

			}

		}
		return value;
	}

	private static String readDataForPrevNode(Node prevenode) {

		Collections.sort(nodeIdList);

		String line;
		String value = " ";
		int key = prevenode.getNodeId();
		String[] keySearch;

		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {

				keySearch = line.split(" ");
				int datakey = Integer.valueOf(keySearch[0]);

				if (nodeIdList.size() == 2
						|| key == Collections.max(nodeIdList)
						|| key == Collections.min(nodeIdList)) {

					if (key > currentId) {

						if (datakey > currentId && datakey <= key) {

							value = value + " " + line + " ";
						} else {
							updatedStringData = updatedStringData + " " + line
									+ " ";
						}

					} else {

						if (datakey > currentId || datakey <= key) {

							value = value + " " + line + " ";

						} else {

							updatedStringData = updatedStringData + " " + line
									+ " ";
						}

					}

				} else if (nodeIdList.size() > 2
						&& (key != Collections.max(nodeIdList) && key != Collections
								.min(nodeIdList))) {

					if (datakey <= key) {

						value = value + " " + line + " ";

					} else {

						updatedStringData = updatedStringData + " " + line
								+ " ";
					}

				}

			}

			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + fileName + "'");
		}
		return value;
	}

	public static void updateFile() throws IOException {

		createTextFile(currentPort);

		stringToData(updatedStringData);

	}

	public static int getPreviousNodeID(Node node) {

		Collections.sort(nodeIdList, Collections.reverseOrder());

		int nodeId = Integer.valueOf(node.getNodeId());

		if (nodeIdList.size() == 1) {

			return nodeId;
		} else if (nodeIdList.size() == 2) {

			if (nodeId == nodeIdList.get(0)) {
				return nodeIdList.get(1);
			} else {

				return nodeIdList.get(0);
			}

		} else {
			int last = Collections.max(nodeIdList);
			int first = Collections.min(nodeIdList);

			if (nodeId == first) {

				return last;
			} else {
				Iterator<Integer> iterator = nodeIdList.iterator();
				while (iterator.hasNext()) {
					int next = iterator.next();
					if (next < nodeId) {
						nodeId = next;
						break;
					}
				}

				return nodeId;

			}

		}

	}

	public static int getNextNodeID(Node node) {

		int nodeId = Integer.valueOf(node.getNodeId());

		if (nodeIdList.size() == 1) {

			return nodeId;
		} else if (nodeIdList.size() == 2) {
			if (nodeId == nodeIdList.get(0)) {
				return nodeIdList.get(1);
			} else {
				return nodeIdList.get(0);
			}

		} else {

			Collections.sort(nodeIdList);

			int first = Collections.min(nodeIdList);
			int last = Collections.max(nodeIdList);

			if (nodeId == last) {

				return first;
			} else {

				Iterator<Integer> iterator = nodeIdList.iterator();

				while (iterator.hasNext()) {
					int next = iterator.next();
					if (next > nodeId) {
						nodeId = next;
						break;
					}

				}

				return nodeId;

			}

		}

	}

	public static Node getPreviousNode(Node node) {

		int previousNodeId = getPreviousNodeID(node);

		for (int i = 0; i < nodeList.size(); i++) {

			if (previousNodeId == nodeList.get(i).getNodeId()) {
				node = nodeList.get(i);
			}
		}

		return node;
	}

	public static Node getNextNode(Node node) {

		int nextNodeId = getNextNodeID(node);

		for (int i = 0; i < nodeList.size(); i++) {

			if (nextNodeId == nodeList.get(i).getNodeId()) {

				node = nodeList.get(i);
			}

		}

		return node;
	}

	public static ArrayList<Node> stringToNodes(String nodeString) {

		String[] nodesListSring = nodeString.split(" ");

		int length = nodesListSring.length;

		for (int i = 0; i < length; i = i + 4) {

			Node node = new Node(Integer.valueOf(nodesListSring[i + 1]),
					nodesListSring[i + 2], nodesListSring[i + 3]);

			nodeList.add(node);
			nodeIdList.add(node.getNodeId());
		}

		return nodeList;
	}

	public static Node getNodeForNodeID(int nodeID) {

		Node node = null;

		for (int i = 0; i < nodeList.size(); i++) {

			if (nodeList.get(i).getNodeId() == nodeID) {
				node = nodeList.get(i);
			}
		}

		return node;
	}

	public static String getAllDataForLeaving() {

		String data = " ";
		String line = " ";
		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {

				data = data + line + " ";
			}
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + fileName + "'");
		}

		return data;
	}

}
