/**
 * @author Tarcisio da Rocha (Prof. DCOMP/UFS)
 */
package TCP_Test;

import java.net.*;
import java.io.*;

public class TCPServer{
    public static void main(String[] args){
        
        try {
            System.out.print("[ Iniciando Servidor TCP    .........................  ");
            ServerSocket ss = new ServerSocket(3300, 5, InetAddress.getByName("127.0.0.1"));
            System.out.println("[OK] ]");
            
            System.out.print("[ Aquardando pedidos de conect    ..................  ");
            Socket sock = ss.accept(); 
            System.out.println("[OK] ]");
            
            InputStream is = sock.getInputStream(); 
            OutputStream os = sock.getOutputStream(); 
            byte[] buf = new byte[20]; 

            System.out.print("[ Aguardando recebimento de mensagem   ..............  ");
            is.read(buf); 
            System.out.println("[OK] ]");
            
            String msg = new String(buf); // Mapeando vetor de bytes recebido para String
            
            System.out.println("  Mensagem recebida: "+ msg);
        }catch(Exception e){System.out.println(e);}    
        System.out.println("[ FIM ]");
    }
}