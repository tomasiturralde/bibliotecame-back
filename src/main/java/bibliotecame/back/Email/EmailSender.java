package bibliotecame.back.Email;

import com.sun.mail.smtp.SMTPSendFailedException;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {

    public void notifyWithGmail(Email email) {
        String sender = "bibliotecame.notificaciones";
        String password = "bibliotecameadmin";

        String sender2 = "bibliotecame.verificaciones";
        String password2 = "bibliotecame123";

        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.user", sender);
        props.put("mail.smtp.clave", password);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "587");

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(sender));
            message.addRecipient(Message.RecipientType.TO, email.recipient);
            message.setSubject(email.subject);
            message.setText(email.body);
            message.setContent(email.body, "text/html; charset=utf-8");
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", sender, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        }
        catch (SMTPSendFailedException fe){
            try {
                message.setFrom(new InternetAddress(sender2));
                message.addRecipient(Message.RecipientType.TO, email.recipient);
                message.setSubject(email.subject);
                message.setText(email.body);
                message.setContent(email.body, "text/html; charset=utf-8");
                Transport transport = session.getTransport("smtp");
                transport.connect("smtp.gmail.com", sender2, password2);
                transport.sendMessage(message, message.getAllRecipients());
                transport.close();
            }
            catch (MessagingException me){
                me.printStackTrace();
            }
        }
        catch (MessagingException me) {
            me.printStackTrace();
        }
    }

}
