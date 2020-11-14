package bibliotecame.back;

import bibliotecame.back.Email.Email;
import bibliotecame.back.Email.EmailSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmailTests {
    Email email;
    EmailSender emailSender;

    @BeforeAll
    void setUp(){
        email = new Email();
        emailSender = new EmailSender();
    }

    @Test
    void testGettersAndSetters() throws AddressException {
        String body = "<br>Nos alegra que te hayas registrado en <strong>Bibliotecame</strong>, solo hace falta que verifiques tu cuenta para poder acceder a la aplicación. <br><br>";
        String subject = "¡Bienvenido a Bibliotecame!";

        email.setBody(body);
        assertThat(email.getBody()).isEqualTo(body);

        email.setSubject(subject);
        assertThat(email.getSubject()).isEqualTo(subject);

        InternetAddress recipient = new InternetAddress("bibliotecame.notificaciones");
        email.setRecipient(recipient);
        assertThat(email.getRecipient()).isEqualTo(recipient);
    }
}
