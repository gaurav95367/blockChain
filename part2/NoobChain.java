import java.security.Security;
import java.util.*;
import java.util.Base64;
import java.security.Provider;
//import com.google.gson.GsonBuilder;
import java.io.*;
import java.net.*;
import com.sun.net.httpserver.*;
// import javax.servlet.http.*;
public class NoobChain {
	
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static Set<String> allNodes=new HashSet <String>();
	public static int difficulty = 5;
	static int port = 8080;

	// previous hash = 100 , proof data =1 for genesis block we can set anything

	private static void create_genesis_block(){
		Block genesis_block= new Block("100",99);
		blockchain.add(genesis_block);
	}
	// @Override
	public static Block create_new_block(String previousHash){
		Block new_block= new Block(previousHash,99);

		blockchain.add(new_block);
		return new_block;
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Sever is being started");
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        create_genesis_block();
        System.out.println("Server started at localhost:"+port);
        server.createContext("/chain", new chain());
        server.createContext("/mine", new mine());
        server.createContext("/register", new register());
        server.setExecutor(null); // creates a default executor
        server.start();
    }
    static class mine implements HttpHandler {
    	@Override
    	public void handle(HttpExchange t) throws IOException {
    		int len=blockchain.size();
            Block last_block= blockchain.get(len-1);
            String previousHash=last_block.hash;
            Block new_block=create_new_block(previousHash);
            String response="";
            response=response+ "<p> Index: " +(blockchain.size()) + "</p>";
            response=response+ "<p> previousHash: " +new_block.previousHash + "</p>";
            response=response+ "<p> proof: " +new_block.nonce + "</p>";
            response=response+ "<p> timestamp: " +new_block.timeStamp + "</p>";
            System.out.println(response);
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    static class chain implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            int len=blockchain.size();
            String response = "";
            for (int i=0;i<len ;i++ ){
            	// response="";
            	response=response+ "<p> Index: " +(i+1) + "</p>";
            	response=response+ "<p> previousHash: " +blockchain.get(i).previousHash + "</p>";
            	response=response+ "<p> proof: " +blockchain.get(i).nonce + "</p>";
            	response=response+ "<p> timestamp: " +blockchain.get(i).timeStamp + "</p>";
            	// System.out.println(response);
            }
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
            
        }
    }
     static class register implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
	        URI requestedUri = t.getRequestURI();
	        String query = requestedUri.getRawQuery();
	        String parameters[]=query.split("[=]");
	        String response = "";
	        if(parameters.length!=2)
	        	response="invalid parameters:\n";
	        else{
	        	allNodes.add(parameters[1]);
	        	// System.out.println(parameters[1]);
	        	response="Nodes has been added";
	        }	         
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
            
        }
    }
}
