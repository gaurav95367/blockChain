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
    public static ArrayList<String> userPassword=new ArrayList<String>();
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static Set<String> allNodes=new HashSet <String>();
	public static int difficulty = 5;
	// static int port = 8080;
    // previous hash = 100 , proof data =1 for genesis block we can set anything
    public static AES aes;
	private static void create_genesis_block(){
		Block genesis_block= new Block("100",99,-1,"-1",-1);
		blockchain.add(genesis_block);
	}
	// @Override
	public static Block create_new_block(String previousHash,int voterID,String password,int nominee){
		Block new_block= new Block(previousHash,99,voterID,password,nominee);

		blockchain.add(new_block);
		return new_block;
	}

	public static void main(String[] args) throws Exception {

        userPassword.add(aes.encrypt("security"));
        userPassword.add(aes.encrypt("network"));
        userPassword.add(aes.encrypt("dataCommunication"));
        System.out.println("3 voters are registered with their password as \nuser 1: security\nuser 2:network\nuser 3:dataCommunication");
        allNodes.add("8080");
		System.out.println("Sever is being started");
        HttpServer server = HttpServer.create(new InetSocketAddress(Integer.parseInt(args[0])), 0);
        System.out.println("Server started at localhost:"+Integer.parseInt(args[0]));
        System.out.print("Genesis ");
        create_genesis_block();
        server.createContext("/chain", new chain());
        server.createContext("/mine", new mine());
        server.createContext("/resolve", new resolve());
        server.setExecutor(null); // creates a default executor
        server.start();
    }
    public static Boolean checkIfAlreadyVoted(int len,int voterID){
        for(int i=0;i<len;i++){
            Block block = blockchain.get(i);
            if(block.voterID==voterID){
                return false;
            }
        }
        return true;
    }
    static class mine implements HttpHandler {
    	@Override
    	public void handle(HttpExchange t) throws IOException {
            URI requestedUri = t.getRequestURI();
            String query = requestedUri.getRawQuery();
            String parameters[]=query.split("[&]");
            if(parameters.length!=3){
                String response="invalid number of parameters, please provide three parameters , voterID, voter password and to whom voter want to vote\n";
                // flag=1;
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
            else{
                for(int i=0;i<parameters.length;i++){
                    if((parameters[i].split("[=]").length!=2)){
                        String response="invalid number of parameters, please provide three parameters , voterID, voter password and to whom voter want to vote\n";
                        t.sendResponseHeaders(200, response.length());
                        OutputStream os = t.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    }
                }
                int voterID=Integer.parseInt(parameters[0].split("[=]")[1]);
                String password=parameters[1].split("[=]")[1];
                int nominee=Integer.parseInt(parameters[2].split("[=]")[1]);
                if(!userPassword.get(voterID).equals(aes.encrypt(password))){
                    String response="invalid voterID or password, please check and try again to vote:------thank you\n" ;
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
                // if(flag==0){
                else{
                // System.out.println("wha");

            		int len=blockchain.size();
                    if(checkIfAlreadyVoted(len,voterID)){
                        Block last_block= blockchain.get(len-1);
                        String previousHash=last_block.hash;
                        Block new_block=create_new_block(previousHash,voterID,password,nominee);
                        // resolve_conflicts();
                        String response="";
                        response=response+ "\n Index: " +(blockchain.size()) ;
                        response=response+ "\n previousHash: " +new_block.previousHash ;
                        response=response+ "\n proof: " +new_block.nonce ;
                        response+= "\n voterID: " + new_block.voterID;
                        response+= "\n nominee: "+ new_block.nominee;
                        response=response+ "\n timestamp: " +new_block.timeStamp+"\n" ;
                        System.out.println(response);
                        t.sendResponseHeaders(200, response.length());
                        OutputStream os = t.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    }
                    else{
                        String response="You have already voted\n";
                        t.sendResponseHeaders(200, response.length());
                        OutputStream os = t.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    }
                }
            }
        }
    }
    static class chain implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            int len=blockchain.size();
            String response = "";
            for (int i=0;i<len ;i++ ){
            	// response="";
            	response=response+ "\n Index: " +(i);
            	response=response+ "\n previousHash: " +blockchain.get(i).previousHash;
            	response=response+ "\n proof: " +blockchain.get(i).nonce;
                response+= "\n voterID: " + blockchain.get(i).voterID;
                response+= "\n nominee: "+blockchain.get(i).nominee;
            	response=response+ "\n timestamp: " +blockchain.get(i).timeStamp+"\n";
            	// System.out.println(response);
            }
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();

        }
    }
    public static void resolve_conflicts() throws IOException{
        int allNodes_length=allNodes.size();
        String url;
        for(Iterator<String> it=allNodes.iterator();it.hasNext();){
            url="localhost:"+it.next()+"/chain";
            // url=it.next()+;
            // URL obj=new URL(url);
            // HttpURLConnection conn=(HttpURLConnection) obj.openConnection();
            // conn.setRequestMethod("GET");
            // int response_code=conn.getResponseCode();
            // System.out.println(obj);    public static void main(String[] args) throws Exception {
            URL oracle = new URL(url);
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                System.out.println(inputLine);
            in.close();
        }
    }
    static class resolve implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
	        int allNodes_length=allNodes.size();
            String url;
            for(Iterator<String> it=allNodes.iterator();it.hasNext();){
                url="172.16.114.58:"+it.next()+"/chain";
                System.out.println(url);
                URL oracle = new URL(url);

                HttpURLConnection yc = (HttpURLConnection) oracle.openConnection();
                // HttpURLConnection con = (HttpURLConnection) url.openConnection();

                yc.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    System.out.println(inputLine);
                String response="jksdjls";
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                in.close();

            }
        }
    }
}
