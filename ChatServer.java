import java.net.*;
import java.io.*;
import java.util.*;

public class ChatServer {

	public static void main(String[] args) {
		try{
			ServerSocket server = new ServerSocket(10001); //"server를 지정(10001)소켓이라고 한다"
			System.out.println("Waiting connection...");   //"Waiting connection... 이라는 말을 보여준다"
			HashMap hm = new HashMap(); //HashMap이라는 객체 생성(짝으로 저장(키,오브젝트))
			while(true){ //"true이니 계속 돌린다"
				Socket sock = server.accept(); //"server.accept()를 통해서 서버소켓을 받은 후 sock에 저장 / server대기" 
				ChatThread chatthread = new ChatThread(sock, hm);/////////////////////////////////////////
				chatthread.start(); //분신술 성공 // run을 부름
			} // while //여기까지가 main이 하는일
		}catch(Exception e){
			System.out.println(e);
		}
	} // main
}

class ChatThread extends Thread{ //Thread손오공
	private Socket sock;
	private String id;
	private BufferedReader br;
	private HashMap hm;
	private boolean initFlag = false;
	public ChatThread(Socket sock, HashMap hm){
		this.sock = sock;
		this.hm = hm;
		try{
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
			br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			id = br.readLine();
			broadcast(id + " entered.");
			System.out.println("[Server] User (" + id + ") entered."); //자기 화면에 누가 들어왓다고 print
			synchronized(hm){
				hm.put(this.id, pw);
			}
			initFlag = true;
		}catch(Exception ex){
			System.out.println(ex);
		}
	} // construcor
	public void run(){
		try{
			String line = null;
			while((line = br.readLine()) != null){
				if(line.equals("/quit")) // "/quit"입력시 run 종료
					break;
				if(line.indexOf("/to ") == 0){ // "/to"로 들어오면 sendmsg (ex)/to kim 뭬롱
					sendmsg(line);
				}else
					broadcast(id + " : " + line);
			}  //주요
		}catch(Exception ex){
			System.out.println(ex);
		}finally{
			synchronized(hm){
				hm.remove(id);
			}
			broadcast(id + " exited.");
			try{
				if(sock != null)
					sock.close();
			}catch(Exception ex){}
		}
	} // run
	public void sendmsg(String msg){
		int start = msg.indexOf(" ") +1; 
		int end = msg.indexOf(" ", start); //이름과 대화내용 구분
		if(end != -1){
			String to = msg.substring(start, end);
			String msg2 = msg.substring(end+1);
			Object obj = hm.get(to); //to이름을 가진 map
			if(obj != null){
				PrintWriter pw = (PrintWriter)obj;
				pw.println(id + " whisphered. : " + msg2); //쓰기
				pw.flush();
			} // if
		}
	} // sendmsg
	public void broadcast(String msg){
		synchronized(hm){ //무엇?!?!
			Collection collection = hm.values(); //Hashmap의 모든값
			Iterator iter = collection.iterator();
			while(iter.hasNext()){
				PrintWriter pw = (PrintWriter)iter.next();
				pw.println(msg);
				pw.flush();
			}
		}
	} // broadcast
}
