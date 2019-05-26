//    https://github.com/sinyukim/SimpleChat.git
import java.net.*;
import java.io.*;
import java.util.*;

public class NServer {

	public static void main(String[] args) {
		try{
			ServerSocket server = new ServerSocket(10001);
			System.out.println("Waiting connection...");
			HashMap hm = new HashMap();
			while(true){
				Socket sock = server.accept();
				ChatThread chatthread = new ChatThread(sock, hm);
				chatthread.start();
			} // while
		}catch(Exception e){
			System.out.println(e);
		}
	} // main
}

class ChatThread extends Thread{
	private Socket sock;
	private String id;
	private BufferedReader br;
	private HashMap hm;
	private boolean initFlag = false;

	private String[] banword = {"fuck", "shit", "FUCK", "bitch", "bastard"};

	public ChatThread(Socket sock, HashMap hm){
		this.sock = sock;
		this.hm = hm;
		try{
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
			br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			id = br.readLine();
			broadcast(id + " entered.");
			System.out.println("[Server] User (" + id + ") entered.");
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

					if(line.contains(banword[0]) || line.contains(banword[1]) || line.contains(banword[2]) || line.contains(banword[3]) || line.contains(banword[4])){
						    PrintWriter a = (PrintWriter)hm.get(id);
						    a.println("!!You have used inappropriate word!!");
                                                    a.flush();


			 }else if(line.equals("/quit")){
 					break;

				}else if(line.equals("/userlist")){
					send_userlist();

				}else if(line.indexOf("/to ") == 0){
					sendmsg(line);
				}else
					broadcast(id + " : " + line);
			}

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

	public void send_userlist(){
	   	Object o = hm.get(id);
	  	PrintWriter p1 = (PrintWriter)o;


      Set keyset = hm.keySet();
      int num = keyset.size();

			 p1.println("\n" +keyset);
			 p1.flush();


		 p1.println("\nNumber(s) of user is : " + num);
		 p1.flush();

  }

	public void sendmsg(String msg){
		int start = msg.indexOf(" ") +1;
		int end = msg.indexOf(" ", start);
		if(end != -1){
			String to = msg.substring(start, end);
			String msg2 = msg.substring(end+1);
			Object obj = hm.get(to);
			if(obj != null){
				PrintWriter pw = (PrintWriter)obj;
				pw.println(id + " whisphered. : " +msg2);
				pw.flush();
			} // if
		}
	} // sendmsg
	public void broadcast(String msg){


		synchronized(hm){
			Collection collection = hm.values();

			Iterator iter = collection.iterator();
			Object obj = hm.get(id);

			while(iter.hasNext()){

				PrintWriter pw = (PrintWriter)iter.next();
				if(pw != obj){
				pw.println(msg);
				pw.flush();
			}

			}
		}
	} // broadcast
}
