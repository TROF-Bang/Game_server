/**
 * @author Tarcisio da Rocha (Prof. DCOMP/UFS)
 */
package TCP_Test;

import java.net.*;
import java.io.*;

public class TCPClient{
    public static void main(String[] args){
        try {
            System.out.print("[ Conectando com o Servidor TCP    ..................  ");
            Socket sock = new Socket("127.0.0.1", 3300);
            System.out.println("[OK] ]");
            
            InputStream is = sock.getInputStream(); 
            OutputStream os = sock.getOutputStream(); 
            String msg = "Hi";
            byte[] buf = msg.getBytes(); 

            System.out.print("[ Enviando mensagem    ..............................  ");
            os.write(buf);
            System.out.println("[OK] ]");
        }catch(Exception e){System.out.println(e);}    
        System.out.println("[ FIM ]");
    }
}