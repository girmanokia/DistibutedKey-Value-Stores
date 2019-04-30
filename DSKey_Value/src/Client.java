import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) {

		System.out.println("Please enter IP, PORT PUT/GET KEY VALUE.");

		while (true) {

			startExecution();
		}

	}

	public static void startExecution() {

		@SuppressWarnings("resource")
		Scanner user_input = new Scanner(System.in);

		String input = user_input.nextLine();
		String[] splits = input.split(" ");
		if (splits.length <= 3) {
			System.out.println("Wrong command, again!");
		} else if (splits.length == 4 || splits.length == 5) {
			boolean put = splits.length > 4;
			String ip = splits[0];
			String port = splits[1];
			try {
				Socket connection = new Socket(ip, Integer.parseInt(port));
				DataOutputStream dos = new DataOutputStream(
						connection.getOutputStream());
				DataInputStream dis = new DataInputStream(
						connection.getInputStream());

				if (put) {

					dos.writeInt(1);
					dos.writeUTF(input);

					dos.close();
					dis.close();
					connection.close();

				} else if (splits[3].toString().equalsIgnoreCase("leave")) {

					dos.writeInt(7);
					dos.writeUTF(input);
					System.out.println("Node has left the network");

					dos.close();
					dis.close();
					connection.close();

				} else {

					dos.writeInt(2);
					dos.writeUTF(input);

					String value = dis.readUTF();

					if (value == "" || value == " " || value == null) {
						System.out
								.println("No corresponding value for the requested key");
					} else {

						System.out.println(" Value:" + " " + value);
					}

					dos.close();
					dis.close();
					connection.close();

				}

			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// System.out.println("NumberFormatException");
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// System.out.println("UnknownHostException");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// System.out.println("IOException");
			}

		} else {

			System.out.println("Please enter proper number of arguments.");
			startExecution();
		}

	}

}
