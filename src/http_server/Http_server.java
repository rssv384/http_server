/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * @version Version 8, Update 261 2020/07/14
 * @author Raúl Soto
 */
public class Http_server {
    
    public static final int HTTP_PORT = 8080;
    
    private static final Logger LOG = Logger.getLogger(Http_server.class.getName());
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       
        // http://localhost:8080
        // http://localhost:8080/bakamitai.html
        // http://localhost:8080/kurosekai.jpg
        // http://localhost:8080/formulario.html
        try {
            ServerSocket serverSocket
                    = new ServerSocket( HTTP_PORT  );
            LOG.info("SERVER UP");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                LOG.info("CONNECT" + clientSocket.toString());
                
                Thread serviceProcess = new Thread(new HTTPService(clientSocket));
                
                serviceProcess.start();
            }

        } catch (IOException e) {
            LOG.warning("Exception caught when trying to listen on port "
                    + HTTP_PORT + " or listening for a connection");
            LOG.warning(e.getMessage());
        }
    }
    
}
