/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http_server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @version Version 8, Update 261 2020/07/14
 * @author rnavarro
 */
public class HTTPService implements Runnable {

    private static final Logger LOG = Logger.getLogger(HTTPService.class.getName());

    private Socket clientSocket;
    private final PrintStream out;
    private BufferedReader in = null;

    public HTTPService(Socket c) throws IOException {
        clientSocket = c;
        this.in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        this.out = new PrintStream(clientSocket.getOutputStream());
    }

    @Override
    public void run() {

        try {
            String requestLine;
            String commandLine = null;

            // Leer la solicitud del cliente
            while ((requestLine = in.readLine()) != null) {
                if (requestLine.startsWith("GET")) {
                    LOG.info(requestLine);
                    commandLine = requestLine;
                }
                
                if (requestLine.startsWith("HEAD")) {
                    System.out.println("*********"+requestLine);
                }
                System.out.println(requestLine);

                // Si recibimos una linea en blanco, es el fin de la solicitud
                if (requestLine.isEmpty()) {
                    break;
                }
            }

            String fileName = null;
            if (commandLine == null) {
                notImplemented();
            } else {
                String tokens[] = commandLine.split("\\s+");

                fileName = tokens[1].substring(tokens[1].lastIndexOf('/') + 1);

                if (fileName.length() == 0) {
                    fileName = "index.html";
                }

                if (fileName.contains(".php")) {
                    fileName = fileName.substring(0, fileName.lastIndexOf('?'));
                }
            }

            Path fp = Paths.get(fileName);
            File filePointer = fp.toFile();

            if (fileName.contains(".php")) {
                LOG.info("FORM");
                doGet(commandLine);
            } else if (!filePointer.exists()) {
                notFound();
            } else if (fileName.endsWith(".ico") || fileName.endsWith(".png")
                    || fileName.endsWith(".jpg") || fileName.endsWith(".gif")) {

                LOG.info("IMAGE");
                sendIMGFile(filePointer);
            } else {
                LOG.info("TEXT");
                sendHTMLFile(filePointer);
            }

            clientSocket.close();

        } catch (IOException ex) {
            System.out.println("Error en la conexión");
        }

    }

    private String getExtension(String f) {
        int p = f.lastIndexOf('.');

        return f.substring(p + 1);
    }

    private void sendHTMLFile(File filePointer) {
        System.out.println(filePointer.getName() + ", " + filePointer.length());
        out.println("HTTP/1.1 200 OK");
        out.println(lastModified(filePointer));

        out.println("Content-Type: text/html; charset=utf-8");
        out.println("Content-Length: " + filePointer.length());
        out.println();

        FileReader file;
        try {
            file = new FileReader(filePointer);

            int data;

            while ((data = file.read()) != -1) {
                out.write(data);
                out.flush();
            }

            out.flush();
            file.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendIMGFile(File filePointer) {
        out.println("HTTP/1.1 200 OK");
        out.println(lastModified(filePointer));

        String content = getExtension(filePointer.getName());

        if (content.equals("ico")) {
            out.println("Content-Type: image/ico");
        }
        if (content.equals("png")) {
            out.println("Content-Type: image/png");
        }
        if (content.equals("jpg")) {
            out.println("Content-Type: image/jpg");
        }
        if (content.equals("gif")) {
            out.println("Content-Type: image/gif");
        }

        out.println("Content-Length: " + filePointer.length());
        out.println();
        out.flush();
        LOG.log(Level.INFO, "Conten-Length: {0}", filePointer.length());

        FileInputStream file;
        try {
            file = new FileInputStream(filePointer);

            int data;

            while ((data = file.read()) != -1) {
                out.write(data);
                //out.flush();
            }

            out.flush();
            file.close();
            LOG.info("IMG-DONE");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String lastModified(File f) {
        long d = f.lastModified();

        Date lastModified = new Date(d);

        return "Last-Modified: " + lastModified.toString();
    }

    public void notImplemented() {
        File f = new File("501.html");

        out.println("HTTP/1.1 501 Not Implemented");
        out.println("Content-Type: text/html; charset=utf-8");
        out.println("Content-Length : " + f.length());
        out.println();

        FileReader file = null;
        try {
            file = new FileReader(f);

            int data;

            while ((data = file.read()) != -1) {
                out.write(data);
                out.flush();
            }
            file.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void notFound() {
        File f = new File("404.html");

        out.println("HTTP/1.1 404 Not Found");
        out.println("Content-Type: text/html; charset=utf-8");
        out.println("Content-Length : " + f.length());
        out.println();

        FileReader file = null;
        try {
            file = new FileReader(f);

            int data;

            while ((data = file.read()) != -1) {
                out.write(data);
                out.flush();
            }
            file.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HTTPService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doGet(String commandLine) {
        StringBuilder response = new StringBuilder();
        System.out.println(commandLine);

        String query = commandLine.substring(commandLine.lastIndexOf('?') + 1,
                commandLine.lastIndexOf(' '));

        System.out.println(query);

        String[] tokens = query.split("\\&+");

        for (int i = 0; i < tokens.length; i++) {
            System.out.println(tokens[i]);
        }
    }

}
