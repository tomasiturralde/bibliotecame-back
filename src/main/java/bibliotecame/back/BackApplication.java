package bibliotecame.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@ComponentScan("bibliotecame.back.Book")
@ComponentScan("bibliotecame.back.Tag")
@ComponentScan("bibliotecame.back.User")
@ComponentScan("bibliotecame.back.Auth")
@ComponentScan("bibliotecame.back.Security")
@ComponentScan("bibliotecame.back.Copy")
@ComponentScan("bibliotecame.back.Loan")
@ComponentScan("bibliotecame.back.Sanction")
@ComponentScan("bibliotecame.back.Extension")
@ComponentScan("bibliotecame.back.Review")
@ComponentScan("bibliotecame.back.Dashboard")
@ComponentScan("bibliotecame.back.Verification")
public class BackApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackApplication.class, args);
	}

}
