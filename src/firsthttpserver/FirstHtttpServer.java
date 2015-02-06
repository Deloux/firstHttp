package firsthttpserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Lars Mortensen
 */
public class FirstHtttpServer {

    static int port = 8080;
    static String ip = "127.0.0.1";

    public static void main(String[] args) throws Exception {
        if (args.length == 2) {
            port = Integer.parseInt(args[0]);
            ip = args[1];
        }
        HttpServer server = HttpServer.create(new InetSocketAddress(ip, port), 0);
        server.createContext("/welcome", new WelcomePageHandler());
        server.createContext("/headers", new HeadersRequestHandler());
        server.createContext("/pages/", new FileRequestHandler());
        server.createContext("/parameters", new ParameterRequestHandler());
        server.setExecutor(null); // Use the default executor
        server.start();
        System.out.println("Server started, listening on port: " + port);
    }

    static class WelcomePageHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {
            String response = "Welcome to my very first almost home made Web Server :-)";
            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html>\n");
            sb.append("<html>\n");
            sb.append("<head>\n");
            sb.append("<title>My fancy Web Site</title>\n");
            sb.append("<meta charset='UTF-8'>\n");
            sb.append("</head>\n");
            sb.append("<body>\n");
            sb.append("<h2>Welcome to my very first home made Web Server :-)</h2>\n");
            sb.append("</body>\n");
            sb.append("</html>\n");
            response = sb.toString();
            Headers h = he.getResponseHeaders();
            h.add("Content-Type", "text/html");
            he.sendResponseHeaders(200, response.length());
            try (PrintWriter pw = new PrintWriter(he.getResponseBody())) {
                pw.print(response); //What happens if we use a println instead of print --> Explain
            }
        }
    }

    static class HeadersRequestHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {
            Map requestHeaders = he.getRequestHeaders();

            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html>\n");
            sb.append("<html>\n");
            sb.append("<head>\n");
            sb.append("<title>My fancy Web Site</title>\n");
            sb.append("<meta charset='UTF-8'>\n");
            sb.append("</head>\n");
            sb.append("<body>\n");
            sb.append("<table border='1'>\n");
            sb.append("<tr><th>header</th><th>value</th></tr>");
            for (Object key : requestHeaders.keySet()) {
                sb.append("<tr><td>" + key.toString() + "</td><td>" + requestHeaders.get(key) + "</td></tr>");
            }
            sb.append("</table>\n");
            sb.append("</body>\n");
            sb.append("</html>\n");
            String response = sb.toString();
            Headers h = he.getResponseHeaders();
            h.add("Content-Type", "text/html");
            he.sendResponseHeaders(200, response.length());
            try (PrintWriter pw = new PrintWriter(he.getResponseBody())) {
                pw.print(response); //What happens if we use a println instead of print --> Explain
            }
        }
    }

    static class FileRequestHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {
            String contentFolder = "public/";
            File file = new File(contentFolder
                    + "index.html");
            byte[] bytesToSend = new byte[(int) file.length()];
            try {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(bytesToSend, 0, bytesToSend.length);
            } catch (IOException ie) {
                ie.printStackTrace();
            }
            he.sendResponseHeaders(200, bytesToSend.length);
            try (OutputStream os = he.getResponseBody()) {
                os.write(bytesToSend, 0, bytesToSend.length);
            }
        }
    }
    
    static class ParameterRequestHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html>\n");
            sb.append("<html>\n");
            sb.append("<head>\n");
            sb.append("<title>My fancy Web Site</title>\n");
            sb.append("<meta charset='UTF-8'>\n");
            sb.append("</head>\n");
            sb.append("<body>\n");
            
            if(he.getRequestMethod().equals("GET")){
                sb.append("<h1 style='display:block;margin:0 auto;'>Method: "+he.getRequestMethod()+"</h1>");
                sb.append("Get-Parameters: "+he.getRequestURI().getQuery());
            }else if(he.getRequestMethod().equals("POST")){
                sb.append("<h1 style='display:block;margin:0 auto;'>Method: "+he.getRequestMethod()+"</h1>");
                Scanner scan = new Scanner(he.getRequestBody());
                while(scan.hasNext()){
                    sb.append("Request body, with Post-parameters: "+scan.nextLine());
                }
            }else{
                sb.append(he.getRequestMethod()+" not supported");
            }
            
            sb.append("</body>\n");
            sb.append("</html>\n");
            String response = sb.toString();
            Headers h = he.getResponseHeaders();
            h.add("Content-Type", "text/html");
            he.sendResponseHeaders(200, response.length());
            try (PrintWriter pw = new PrintWriter(he.getResponseBody())) {
                pw.print(response); //What happens if we use a println instead of print --> Explain
            }
        }
    }
}
