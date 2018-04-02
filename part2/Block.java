//package lol;
import java.util.Date;

public class Block {
	
	public  String hash;
	public  String previousHash; 
	public  int proof_data; //our data will be a simple message.
	public  long timeStamp; //as number of milliseconds since 1/1/1970.
	public  int nonce;
	
	
	//Block Constructor.  
	public Block(String previousHash,int data) {
//		this.proof_data = ;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		
		this.hash = calculateHash(); //Making sure we do this after we set the other values.
		proof_of_work(3);
	}
	
	//Calculate new hash based on blocks contents
	public  String calculateHash() {
		String calculatedhash = StringUtil.applySha256( 
				previousHash +
				Long.toString(timeStamp) +
				Integer.toString(nonce) + 
				Integer.toString(proof_data) 
				);
		return calculatedhash;
	}
	public  String calculate_block_hash(Block block) {
		String calculatedhash = StringUtil.applySha256( 
				block.previousHash +
				Long.toString(block.timeStamp) +
				Integer.toString(block.nonce) + 
				Integer.toString(block.proof_data) 
				);
		return calculatedhash;
	}
	public void print(){
		System.out.println("hash : "+hash);
		System.out.println("previousHash : "+ previousHash);
		System.out.println("data : "+proof_data);
		System.out.println("timeStamp : "+timeStamp);
		System.out.println("nonce : "+nonce);

	}


	public  int proof_of_work(int difficulty) {
		String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0" 
		while(!hash.substring( 0, difficulty).equals(target)) {
			nonce ++;
			hash = calculateHash();
		}
		System.out.println("Block Mined!!! : " + hash);
		return nonce;
	}
}
