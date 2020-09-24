package bibliotecame.back;

import bibliotecame.back.Auth.AuthController;
import bibliotecame.back.Auth.LoginForm;
import bibliotecame.back.Book.BookController;
import bibliotecame.back.Book.BookModel;
import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Loan.LoanController;
import bibliotecame.back.Tag.TagModel;
import bibliotecame.back.User.UserController;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Component
public class OnStart {

    private static final boolean RUN_ON_START = true;

    private final UserController userController;
    private final UserService userService;
    private final BookController bookController;
    private final LoanController loanController;
    private final AuthController authController;

    @Autowired
    public OnStart(UserController userController, BookController bookController, UserService userService, LoanController loanController, AuthController authController) {
        this.userController = userController;
        this.bookController = bookController;
        this.userService = userService;
        this.loanController = loanController;
        this.authController = authController;
    }

    @EventListener
    public void appReady(ApplicationReadyEvent event) {
        if(RUN_ON_START) {

            try {
                // users
                if (!userService.emailExists("salvador.fortes@mail.austral.edu.ar")) {
                    UserModel admin = new UserModel("salvador.fortes@mail.austral.edu.ar", "contraseña", "Salvador", "Fortes", "011 5555 1234");
                    admin.setAdmin(true);
                    userService.saveUser(admin);
                }
                UserModel user1 = userController.createUser(new UserModel("francisca.canton@ing.austral.edu.ar", "franchu123", "Francisca", "Canton", "011 2222 3333")).getBody();
                UserModel user2 = userController.createUser(new UserModel("esther.reyes@ing.austral.edu.ar", "123qweasd", "Esther", "Reyes", "011 3333 4444")).getBody();
                UserModel user3 = userController.createUser(new UserModel("marc.ivanov@ing.austral.edu.ar", "iva123456", "Marc", "Ivanov", "011 4444 0987")).getBody();
                UserModel user4 = userController.createUser(new UserModel("juanluis.llorens@ing.austral.edu.ar", "juan1234", "Juan Luis", "Lorens", "011 1234 5678")).getBody();

                //libros
                List<TagModel> tags1 = new ArrayList<>();
                tags1.add(new TagModel("Filosofía"));
                tags1.add(new TagModel("Metafísica"));
                BookModel book1 = bookController.checkAndCreateBook(new BookModel("Crítica de la razón pura", 1781, "Immanuel Kant", "NoBooks", tags1)).getBody();

                List<TagModel> tags2 = new ArrayList<>();
                tags2.add(new TagModel("Filosofía"));
                BookModel book2 = bookController.checkAndCreateBook(new BookModel("El mundo de Sofía", 1991, "Jostein Gaarder", "Ediciones Siruela", tags2)).getBody();

                List<TagModel> tags3 = new ArrayList<>();
                tags3.add(new TagModel("Historia"));
                BookModel book3 = bookController.checkAndCreateBook(new BookModel("Sapiens: De animales a dioses", 2011, "Yuval Noah Harari", "DEBATE", tags3)).getBody();

                List<TagModel> tags4 = new ArrayList<>();
                tags4.add(new TagModel("Sátira"));
                BookModel book4 = bookController.checkAndCreateBook(new BookModel("Orgullo y prejuicio", 1813, "Jane Austen", "ALBA", tags4)).getBody();

                List<TagModel> tags5 = new ArrayList<>();
                tags5.add(new TagModel("Poesía"));
                tags5.add(new TagModel("Novela"));
                BookModel book5 = bookController.checkAndCreateBook(new BookModel("Ilíada", 1892, "Homero", "Fontana", tags5)).getBody();

                List<TagModel> tags6 = new ArrayList<>();
                tags6.add(new TagModel("Ingeniería"));
                BookModel book6 = bookController.checkAndCreateBook(new BookModel("Ciencia E Ingenieria De Materiales", 1995, "William Castiller", "Reverté", tags6)).getBody();

                BookModel book7 = bookController.checkAndCreateBook(new BookModel("Inteligencia Artificial - Con Aplicaciones A La Ingenia", 2010, "Pedro Ponce Cruz", "Alfaomega", tags6)).getBody();

                List<TagModel> tags8 = new ArrayList<>();
                tags8.add(new TagModel("Política"));
                BookModel book8 = bookController.checkAndCreateBook(new BookModel("Como Mueren Las Democracias", 2010, "Steven Levinsky", "Ariel", tags8)).getBody();

                List<TagModel> tags9 = new ArrayList<>();
                tags9.add(new TagModel("Psicologia"));
                BookModel book9 = bookController.checkAndCreateBook(new BookModel("Psicologia Para Mentes Inquietas", 2016, "Marcus Weeks", "DK Publishing", tags9)).getBody();

                List<TagModel> tags10 = new ArrayList<>();
                tags10.add(new TagModel("Ciencia"));
                BookModel book10 = bookController.checkAndCreateBook(new BookModel("Breve historia del tiempo", 1988, "Stephen Hawking", "Bantam Books", tags10)).getBody();

                //ejemplares
                List<CopyModel> copies1 = new ArrayList<>();
                copies1.add(new CopyModel("C2DF9M"));
                copies1.add(new CopyModel("DSUCO7"));
                copies1.add(new CopyModel("3EVPRG"));
                Objects.requireNonNull(book1).setCopies(copies1);
                bookController.checkAndUpdateBook(book1.getId(), book1);

                List<CopyModel> copies2 = new ArrayList<>();
                copies2.add(new CopyModel("PO737J"));
                copies2.add(new CopyModel("MDGBGX"));
                copies2.add(new CopyModel("ET5F5Q"));
                Objects.requireNonNull(book2).setCopies(copies2);
                bookController.checkAndUpdateBook(book2.getId(), book2);

                List<CopyModel> copies3 = new ArrayList<>();
                copies3.add(new CopyModel("X40F1T"));
                copies3.add(new CopyModel("28PS5A"));
                copies3.add(new CopyModel("X9ZTKV"));
                Objects.requireNonNull(book3).setCopies(copies3);
                bookController.checkAndUpdateBook(book3.getId(), book3);

                List<CopyModel> copies4 = new ArrayList<>();
                copies4.add(new CopyModel("TEA09O"));
                copies4.add(new CopyModel("U2GD8U"));
                copies4.add(new CopyModel("9IHVIJ"));
                Objects.requireNonNull(book4).setCopies(copies4);
                bookController.checkAndUpdateBook(book4.getId(), book4);

                List<CopyModel> copies5 = new ArrayList<>();
                copies5.add(new CopyModel("ZU3XJN"));
                copies5.add(new CopyModel("L0D5S4"));
                copies5.add(new CopyModel("E35HMG"));
                Objects.requireNonNull(book5).setCopies(copies5);
                bookController.checkAndUpdateBook(book5.getId(), book5);

                List<CopyModel> copies6 = new ArrayList<>();
                copies6.add(new CopyModel("7V3U2W"));
                copies6.add(new CopyModel("4X7AGW"));
                copies6.add(new CopyModel("6MLMF4"));
                Objects.requireNonNull(book6).setCopies(copies6);
                bookController.checkAndUpdateBook(book6.getId(), book6);

                List<CopyModel> copies7 = new ArrayList<>();
                copies7.add(new CopyModel("EVMGOZ"));
                copies7.add(new CopyModel("BTO7HT"));
                copies7.add(new CopyModel("8W1ASG"));
                Objects.requireNonNull(book7).setCopies(copies7);
                bookController.checkAndUpdateBook(book7.getId(), book7);

                List<CopyModel> copies8 = new ArrayList<>();
                copies8.add(new CopyModel("J570C8"));
                copies8.add(new CopyModel("AREWN7"));
                copies8.add(new CopyModel("PJP1AN"));
                Objects.requireNonNull(book8).setCopies(copies8);
                bookController.checkAndUpdateBook(book8.getId(), book8);

                List<CopyModel> copies9 = new ArrayList<>();
                copies9.add(new CopyModel("TG4NK7"));
                copies9.add(new CopyModel("L6KSPR"));
                copies9.add(new CopyModel("ZRZTZU"));
                Objects.requireNonNull(book9).setCopies(copies9);
                bookController.checkAndUpdateBook(book9.getId(), book9);

                List<CopyModel> copies10 = new ArrayList<>();
                copies10.add(new CopyModel("P8PFUL"));
                copies10.add(new CopyModel("TCAZYD"));
                copies10.add(new CopyModel("64ZVJA"));
                Objects.requireNonNull(book10).setCopies(copies10);
                bookController.checkAndUpdateBook(book10.getId(), book10);

                //loans
                loanController.checkAndCreateLoan(user1, book1);
                loanController.checkAndCreateLoan(user2, book4);
                loanController.checkAndCreateLoan(user3, book7);
                loanController.checkAndCreateLoan(user4, book10);
                loanController.checkAndCreateLoan(user1, book2);
                loanController.checkAndCreateLoan(user2, book5);
                loanController.checkAndCreateLoan(user3, book9);
                loanController.checkAndCreateLoan(user1, book3);
                loanController.checkAndCreateLoan(user2, book6);
                loanController.checkAndCreateLoan(user3, book8);



            } catch (Exception ignored) {
            }

        }
    }

}
