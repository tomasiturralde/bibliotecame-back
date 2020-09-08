package bibliotecame.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@ComponentScan("bibliotecame.back.config")
@ComponentScan("bibliotecame.back.Book")
@ComponentScan("bibliotecame.back.Author")
@ComponentScan("bibliotecame.back.Publisher")
@ComponentScan("bibliotecame.back.Tag")
@ComponentScan("bibliotecame.back.User")
public class BackApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackApplication.class, args);
	}

}
