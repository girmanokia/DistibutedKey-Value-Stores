public class Data {

	private int key;
	private String value;
	private int version;

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Data(int key, String value, int version) {
		this.key = key;
		this.value = value;
		this.version = version;
	}

}
