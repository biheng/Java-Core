package net.java.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import sun.misc.BASE64Encoder;

/**
 * Created with IntelliJ IDEA.
 * User: miracle
 * Date: 13-8-21
 * Time: 下午4:59
 * To change this template use File | Settings | File Templates.
 */
public class MailSender {
    private String smtpServer = "smtp.163.com";
    private int port = 25;
    String username = new BASE64Encoder().encode("tqcenglish1990@163.com".getBytes());
    String password = new BASE64Encoder().encode("3ifnotmewho?".getBytes());

    public static void main(String[] args) {
        Message msg = new Message("tqcenglish1990@163.com", "tqcenglish1990@163.com", "hello", "hi, Imiss you");
        try {
            new MailSender().sendMail(msg);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        return new PrintWriter(outputStream, true); //auto flush true
    }

    private BufferedReader getReader(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    private void sendMail(Message msg) throws IOException {
        Socket socket;
        socket = new Socket(smtpServer, port);
        BufferedReader bufferedReader = getReader(socket);
        PrintWriter printWriter = getWriter(socket);
        String localhost = InetAddress.getLocalHost().getHostAddress();
       // sendAndReceive("null", bufferedReader, printWriter);
        sendAndReceive("HELO" + localhost, bufferedReader, printWriter);
        sendAndReceive("AUTH LOGIN", bufferedReader, printWriter);
        sendAndReceive(username, bufferedReader, printWriter);
        sendAndReceive(password, bufferedReader, printWriter);
        sendAndReceive("MAIL FROM:<" + msg.getFrom() + ">", bufferedReader, printWriter);
        sendAndReceive("RCPT TO:<" + msg.getTo() + ">", bufferedReader, printWriter);
        sendAndReceive("DATA", bufferedReader, printWriter);
        printWriter.print(msg.getData());
        sendAndReceive(".", bufferedReader, printWriter);
        sendAndReceive("QUIT", bufferedReader, printWriter);
        if (socket != null) {
            socket.close();
        }
    }

    private void sendAndReceive(String s, BufferedReader br, PrintWriter pw) throws IOException {
        if (s != null) {
            System.out.println("Client>" + s);
            pw.println(s);
        }
        String response;
        if ((response = br.readLine()) != null) {
            System.out.println("Server>" + response);
        }
    }
}


