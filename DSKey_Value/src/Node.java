public class Node {

	private int nodeId;
	private String nodeIp;
	private String nodePort;

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeIp() {
		return nodeIp;
	}

	public void setNodeIp(String nodeIp) {
		this.nodeIp = nodeIp;
	}

	public String getNodePort() {
		return nodePort;
	}

	public void setNodePort(String nodePort) {
		this.nodePort = nodePort;
	}

	public Node(int nodeId, String nodeIp, String nodePort) {
		this.nodeId = nodeId;
		this.nodeIp = nodeIp;
		this.nodePort = nodePort;
	}

}
