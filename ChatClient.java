import java.net.*;
import java.io.*;

public class ChatClient {

	public static void main(String[] args) {
		if(args.length != 2){ // 글자가 2개 더 안붙어잇으면
			System.out.println("Usage : java ChatClient <username> <server-ip>");  //사용법 알려줌
			System.exit(1);  //끝냄
		}
		Socket sock = null;
		BufferedReader br = null;
		PrintWriter pw = null;  //"PrintWriter == 파일을 만들어 입력받고 파일 내용을 출력"
		boolean endflag = false;
		try{
			sock = new Socket(args[1], 10001);   //"null이엿던 sock -> Socker(args[1], 10001)"
			pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));  //" pw = 새로운 PrintWriter(byte단위를 char형태로씀(sock쓰기)"
			br = new BufferedReader(new InputStreamReader(sock.getInputStream())); // " br = buffering char-input stream(byte단위를 char형태로읽음(sock읽기)"
			BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in)); //"새BufferedReader keyboard = system에서 받은 값을 char로 전환 후 BufferedReader형태로"
			// send username.
			pw.println(args[0]); //thread 한테 보냄/////////////////////////////////////////
			pw.flush();  //"stream에 남아있는 데이터를 내보냄"
			InputThread it = new InputThread(sock, br); //"it라는 InputThread의 소켓은sock, BufferedReader는 br"
			it.start(); //"it를 시작"
			String line = null; 
			while((line = keyboard.readLine()) != null){ //"keyboard로부터 입력값이 있으면"
				pw.println(line); //line을 pw에 보냄
				pw.flush(); //"데이터 내보냄"
				if(line.equals("/quit")){ // "/quit"이면
					endflag = true;
					break; //끝냄
				}
			}
			System.out.println("Connection closed."); //"Connection closed. 출력"
		}catch(Exception ex){ 
			if(!endflag)
				System.out.println(ex);
		}finally{
			try{
				if(pw != null)  //"pw가 null이 아니면"
					pw.close();  //"pw닫기"
			}catch(Exception ex){}
			try{
				if(br != null)  //"br이 null이 아니면"
					br.close(); //"br닫기"
			}catch(Exception ex){}
			try{
				if(sock != null) //"sock이 null이 아니면"
					sock.close();  //"sock 닫기"
			}catch(Exception ex){}
		} // finally
	} // main
} // class

class InputThread extends Thread{
	private Socket sock = null;
	private BufferedReader br = null;
	public InputThread(Socket sock, BufferedReader br){
		this.sock = sock;
		this.br = br;
	}
	public void run(){
		try{
			String line = null;
			while((line = br.readLine()) != null){ //"line이 있으면"
				System.out.println(line); //"line출력"
			}
		}catch(Exception ex){
		}finally{
			try{
				if(br != null) //"br이 null이 아니면"
					br.close(); //"br닫기"
			}catch(Exception ex){}
			try{
				if(sock != null) //"sock이 null이 아니면"
					sock.close(); //"sock 닫기"
			}catch(Exception ex){}
		}
	} // InputThread
}
