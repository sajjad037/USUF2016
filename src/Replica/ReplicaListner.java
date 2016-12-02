package Replica;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

import Models.Enums;
import Models.UDPMessage;
import StaticContent.*;
import Utilities.CLogger;
import Utilities.Serializer;

public class ReplicaListner implements Runnable {
	private CLogger clogger;
	private DatagramSocket serverSocket;
	private Thread t = null;
	private boolean continueUDP = true;
	private long sequencerNumber= 0;
	int port =0;
	Enums.UDPSender machineName ;
	
	public ReplicaListner(CLogger clogger, int port, Enums.UDPSender machineName) {
		this.clogger = clogger;
		this.port = port;
		this.machineName = machineName;
	}

	@Override
	public void run() {
		try {
			serverSocket = new DatagramSocket(port);
			byte[] receiveData = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];
			//byte[] sendData = new byte[SIZE_BUFFER_REQUEST];
			String msg = machineName.toString() + " UDP Server Is UP!";

			System.out.println(msg);
			clogger.log(msg);
			while (continueUDP) {

				// Read request
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				
				byte[] message = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
		        UDPMessage udpMessage = Serializer.deserialize(message);
		        UDPMessage ackMessage = null;
				// Clear received buffer
				receiveData = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];
				boolean receivedStatus = false;
				switch (udpMessage.getSender()) {
				case Sequencer:						
					receivedStatus = true;
					// Perform Operations.
					msg = "Executing Opernation : "+udpMessage.getOpernation()+", on Server :"+ udpMessage.getServerName();
					System.out.println(msg);
					clogger.log(msg);
					ackMessage = new UDPMessage(this.machineName, udpMessage.getSequencerNumber(), udpMessage.getServerName(), udpMessage.getOpernation(), Enums.UDPMessageType.Reply);
					break;
					
				case RMUlan:
				case RMSajjad:
				case RMUmer:
				case RMFeras:
					receivedStatus = true;
					msg = udpMessage.getSender().toString()+" Server contacted for HeartBeat.";
					System.out.println(msg);
					clogger.log(msg);
					ackMessage = new UDPMessage(this.machineName, udpMessage.getSequencerNumber(), udpMessage.getServerName(), udpMessage.getOpernation(), Enums.UDPMessageType.Reply);
					break;

				default:
					msg = "Unknow Sender : "+ udpMessage.getSender();
					System.out.println(msg);
					clogger.log(msg);
					break;
				}
				
				if(receivedStatus)
				{
					//Send Acknowledge.					
					ackMessage.setStatus(true);
					byte[] sendData = Serializer.serialize(ackMessage);
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
					serverSocket.send(sendPacket);

					// Clear Send buffer
					sendData = new byte[StaticContent.UDP_REQUEST_BUFFER_SIZE];
				}
			}
		} catch (Exception ex) {
			clogger.logException("on starting UDP Server", ex);
			ex.printStackTrace();
		}

	}
	
	/**
	 * Start the server thread
	 */
	public void start()
	{
		t = new Thread(this);
		t.start();
	}
	
	/**
	 * Execute a join on the thread
	 * @throws InterruptedException 
	 */
	public void join() throws InterruptedException 
	{
		if(t == null)
			return;
		
		t.join();
	}
	
	/**
	 * Stop the server thread
	 */
	public void stop()
	{
		continueUDP = false;
	}
	

}