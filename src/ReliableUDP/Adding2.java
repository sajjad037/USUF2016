package ReliableUDP;


public class Adding2 {

	public int addingTwoNymber( int x, int y){
		return x +y;
	}
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		
		while(true){
		Reciever r = new Reciever(1000,2000);
		
		//Thread.sleep(1000);
		//System.err.println("isTransferComplete : "+ s.isTransferComplete); 
		
		System.out.print("data recieved is : "+ r.getData().getSequencerNumber());
		}

	}

}