package com.sselab.android.ipgettest.mail;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
/**
 * @author PanTeng
 * @version 1.0
 * @file JavaMail.java
 * @date 2017/8/5
 * @attention Copyright (C),2004-2017,SSELab, SEI, XiDian University
 */
public class JavaMail {
/*
    public static void main(String[] args)throws Exception{
        receive();
    }
*/
    /**
     * 接收邮件
     * @throws Exception
     */
    public static String receive() throws Exception{
        //准备连接服务器的会话信息
        Properties props = System.getProperties();
        props.put("mail.store.protocol", "pop3");      //协议
        props.put("mail.pop3.port", "110");            //端口
        props.put("mail.pop3.host", "pop3.163.com");   //pop3服务器
        //创建Session实例对象
        Session session = Session.getInstance(props);
        Store store = session.getStore("pop3");  //使用pop3会话机制，连接服务器
        store.connect("raspberrylocal11@163.com","changxin326");
        //获得收件箱
        Folder folder = store.getFolder("INBOX");
        /*
         * Folder.READER_ONLY: 只读权限
         * Folder.READ_WRITE:可读可写（可以修改邮件状态）
         */
        folder.open(Folder.READ_WRITE);                       //打开收件箱
         /*由于POP3协议无法获知邮件的状态，所以getUnreadMessageCount得到的是收件箱的邮件总数
         删除邮件数和新邮件数总是零*/
         System.out.println("未读邮件数：" + folder.getUnreadMessageCount());
         System.out.println("删除邮件数：" + folder.getDeletedMessageCount());
         System.out.println("新邮件数：" + folder.getNewMessageCount());
         System.out.println("邮件总数：" + folder.getMessageCount());

         //获得邮箱中的所有邮件，并解析
        String ip = null;                           // 服务器ip地址
        Message[] messages = folder.getMessages();
        ip = parseMessage(messages);
        //释放资源
        folder.close(true);
        store.close();
        return ip;
    }
    /**
     * 解析邮件
     * @param messages 要解析的邮件列表
     */
    private static String parseMessage(Message ...messages) throws MessagingException, Exception{
        if (messages == null || messages.length < 1)
            throw new MessagingException("未找到要解析的邮件！");
        String ip = null;                                       //服务器ip
        //解析所有邮件
        for (int i = messages.length - 1; i > 0; i--){
            MimeMessage msg = (MimeMessage) messages[i];
            String subject = getSubject(msg);
            System.out.println(subject);
            if (subject.contains("RasPi")){
                StringBuffer content = new StringBuffer(4096);
                getMailTextContent(msg, content);
                ip = getIp(content);
                System.out.println(ip);
                break;
            }
        }
        return ip;
    }

    /**
     * 解析邮件内容 获得IP地址
     * @param content 邮件内容
     * @return
     */
    private static String getIp(StringBuffer content) {
        int start = content.indexOf("wlan0");
        int end = content.indexOf("processor");
        String substring = content.substring(start, end);
        int start2 = substring.indexOf("inet addr:");
        int end2 = substring.indexOf("Bcast");
        String Ip = substring.substring(start2 + "inet addr:".length(), end2);
        return Ip;
    }


    /**
     * 获得邮件主题
     * @param msg 邮件内容
     * @return 解码后的邮件主题
     */
    private static String getSubject(MimeMessage msg) throws MessagingException, UnsupportedEncodingException {
        return MimeUtility.decodeText(msg.getSubject());
    }

    /**
     * 获得邮件文本内容
     * @param part 邮件体
     * @param content 存储邮件文本内容的字符串
     */
    private static void getMailTextContent(Part part, StringBuffer content) throws MessagingException, IOException {
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
        if (part.isMimeType("text/*") && !isContainTextAttach){
            content.append(part.getContent().toString());
        }else if (part.isMimeType("message/rfc822")){
            getMailTextContent((Part)part.getContent(), content);
        }else if (part.isMimeType("multipart/*")){
            Multipart multipart = (Multipart)part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++){
                BodyPart bodyPart = multipart.getBodyPart(i);
                getMailTextContent(bodyPart,content);
            }
        }
    }
}
