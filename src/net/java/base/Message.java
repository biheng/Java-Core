package net.java.base;

/**
 * Created with IntelliJ IDEA.
 * User: miracle
 * Date: 13-8-21
 * Time: 下午5:02
 * To change this template use File | Settings | File Templates.
 */
public class Message {
    private String from;
    private  String to;
    private String subject;
    private String content;

    public String getData() {
        return data;
    }

    private String data;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Message(String from, String to, String subject, String content) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.content = content;
        data = "Subject:" + subject + "\r\n" + content;
    }
}
