import java.net.*;
import java.io.*;
import java.util.*;

public class ChatServer { //ChatServer는 객체없음,  불러줘야 돌아감

	public static void main(String[] args) {
		try{
			ServerSocket server = new ServerSocket(10001); //"server를 지정(10001)소켓이라고 한다"
			System.out.println("Waiting connection...");   //"Waiting connection... 이라는 말을 보여준다"
			HashMap hm = new HashMap(); //HashMap이라는 객체 생성(짝으로 저장(키,오브젝트)) /HashMap<String, PrintWriter>
			while(true){ //"true이니 계속 돌린다"
				Socket sock = server.accept(); //"server.accept()를 통해서 서버소켓을 받은 후 sock에 저장 / server대기" 
				ChatThread chatthread = new ChatThread(sock, hm); //"받은 sock과 만들엇던 hm을 chatthread로 
				chatthread.start(); //분신술 성공 // run을 부름
			} // while //여기까지가 main이 하는일
		}catch(Exception e){
			System.out.println(e); //"error 출력"
		}
	} // main
}

class ChatThread extends Thread{ //Thread손오공
	private Socket sock;
	private String id;
	private BufferedReader br; //"BufferedReader == read text
	private HashMap hm;
	private boolean initFlag = false;
	public ChatThread(Socket sock, HashMap hm){
		this.sock = sock;
		this.hm = hm;  //실체x, 주소를 가져옴
		try{
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream())); //" pw = 새로운 PrintWriter(byte단위를 char형태로씀(sock쓰기)"
			br = new BufferedReader(new InputStreamReader(sock.getInputStream()));  //" br = buffering char-input stream(byte단위를 char형태로읽음(sock읽기)"
			id = br.readLine(); //" id = br에서부터 읽은 것
			//중복이름 확인
			broadcast(id + " entered.");  //"id가 들어왓다는 것을 알림"
			System.out.println("[Server] User (" + id + ") entered."); //자기 화면에 누가 들어왓다고 print
			synchronized(hm){ //"hm 동기화" //줄 세우기
				hm.put(this.id, pw); //"this.id라는 key를 pw value에 mapping함 / 새 user"
			}
			initFlag = true;
		}catch(Exception ex){
			System.out.println(ex);  //"
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
					broadcast(id + " : " + line); //"to나 /quit가 아닐시 id : + line을 broadcast"
			}  //주요
		}catch(Exception ex){
			System.out.println(ex);
		}finally{
			synchronized(hm){
				hm.remove(id);  //"hm에서 id삭제
			}
			broadcast(id + " exited."); //"id + exited.라는 문구를 다른 user들에게 출력한다"
			try{
				if(sock != null) //"sock라는 소켓이 null이 아니면"
					sock.close(); //"sock라는 소켓객체를 닫는다"
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
		synchronized(hm){ //동기화
			Collection collection = hm.values(); //Hashmap의 모든값
			Iterator iter = collection.iterator(); //"collection의 값을 interate"
			while(iter.hasNext()){ //"iter가 다음이 있을 때"
				PrintWriter pw = (PrintWriter)iter.next(); //"pw는 iter.next를 PrintWriter 형식으로"
				pw.println(msg); //"pw출력"
				pw.flush(); //"데이터 내보내기"
			}
		}
	} // broadcast
}
