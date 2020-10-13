package bibliotecame.back;

import bibliotecame.back.Book.BookController;
import bibliotecame.back.Book.BookModel;
import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Extension.ExtensionController;
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
    private final ExtensionController extensionController;

    @Autowired
    public OnStart(UserController userController, BookController bookController, UserService userService, LoanController loanController, ExtensionController extensionController) {
        this.userController = userController;
        this.bookController = bookController;
        this.userService = userService;
        this.loanController = loanController;
        this.extensionController = extensionController;
    }

    @EventListener
    public void appReady(ApplicationReadyEvent event) {
        if(RUN_ON_START) {

            try {
                // users
                if(!userService.emailExists("admin@ing.austral.edu.ar")) {
                    UserModel admin = new UserModel("admin@ing.austral.edu.ar", "admin123", "admin", "admin", "111111111");
                    admin.setAdmin(true);
                    userService.saveUser(admin);
                }
                UserModel user1 = (UserModel) userController.createUser(new UserModel("francisca.canton@ing.austral.edu.ar", "franchu123", "Francisca", "Canton", "011 2222 3333")).getBody();
                UserModel user2 = (UserModel) userController.createUser(new UserModel("esther.reyes@ing.austral.edu.ar", "123qweasd", "Esther", "Reyes", "011 3333 4444")).getBody();
                UserModel user3 = (UserModel) userController.createUser(new UserModel("marc.ivanov@ing.austral.edu.ar", "iva123456", "Marc", "Ivanov", "011 4444 0987")).getBody();
                UserModel user4 = (UserModel) userController.createUser(new UserModel("juanluis.llorens@ing.austral.edu.ar", "juan1234", "Juan Luis", "Lorens", "011 1234 5678")).getBody();

                //libros
                List<TagModel> tags1 = new ArrayList<>();
                tags1.add(new TagModel("Filosofía"));
                tags1.add(new TagModel("Metafísica"));
                BookModel book1 = (BookModel)bookController.checkAndCreateBook(new BookModel("Crítica de la razón pura", 1781, "Immanuel Kant", "NoBooks", tags1)).getBody();

                List<TagModel> tags2 = new ArrayList<>();
                tags2.add(new TagModel("Filosofía"));
                BookModel book2 = (BookModel)bookController.checkAndCreateBook(new BookModel("El mundo de Sofía", 1991, "Jostein Gaarder", "Ediciones Siruela", tags2)).getBody();

                List<TagModel> tags3 = new ArrayList<>();
                tags3.add(new TagModel("Historia"));
                BookModel book3 = (BookModel)bookController.checkAndCreateBook(new BookModel("Sapiens: De animales a dioses", 2011, "Yuval Noah Harari", "DEBATE", tags3)).getBody();

                List<TagModel> tags4 = new ArrayList<>();
                tags4.add(new TagModel("Sátira"));
                BookModel book4 = (BookModel)bookController.checkAndCreateBook(new BookModel("Orgullo y prejuicio", 1813, "Jane Austen", "ALBA", tags4)).getBody();

                List<TagModel> tags5 = new ArrayList<>();
                tags5.add(new TagModel("Poesía"));
                tags5.add(new TagModel("Novela"));
                BookModel book5 = (BookModel)bookController.checkAndCreateBook(new BookModel("Ilíada", 1892, "Homero", "Fontana", tags5)).getBody();

                List<TagModel> tags6 = new ArrayList<>();
                tags6.add(new TagModel("Ingeniería"));
                BookModel book6 = (BookModel)bookController.checkAndCreateBook(new BookModel("Ciencia E Ingenieria De Materiales", 1995, "William Castiller", "Reverté", tags6)).getBody();

                List<TagModel> tags7 = new ArrayList<>();
                tags7.add(new TagModel("Ingeniería"));
                BookModel book7 = (BookModel)bookController.checkAndCreateBook(new BookModel("Inteligencia Artificial - Con Aplicaciones A La Ingenia", 2010, "Pedro Ponce Cruz", "Alfaomega", tags7)).getBody();

                List<TagModel> tags8 = new ArrayList<>();
                tags8.add(new TagModel("Política"));
                BookModel book8 = (BookModel)bookController.checkAndCreateBook(new BookModel("Como Mueren Las Democracias", 2010, "Steven Levinsky", "Ariel", tags8)).getBody();

                List<TagModel> tags9 = new ArrayList<>();
                tags9.add(new TagModel("Psicologia"));
                BookModel book9 = (BookModel)bookController.checkAndCreateBook(new BookModel("Psicologia Para Mentes Inquietas", 2016, "Marcus Weeks", "DK Publishing", tags9)).getBody();

                List<TagModel> tags10 = new ArrayList<>();
                tags10.add(new TagModel("Ciencia"));
                BookModel book10 = (BookModel)bookController.checkAndCreateBook(new BookModel("Breve historia del tiempo", 1988, "Stephen Hawking", "Bantam Books", tags10)).getBody();

                //tanda 2 de libros
                List<TagModel> tags11 = new ArrayList<>();
                tags11.add(new TagModel("Ingeniería"));
                tags11.add(new TagModel("Matemática"));
                BookModel book11 = (BookModel)bookController.checkAndCreateBook(new BookModel("Análisis Matemático 1", 2006, "García Venturini", "Ediciones Cooperativas", tags11)).getBody();

                List<TagModel> tags12 = new ArrayList<>();
                tags12.add(new TagModel("Ingeniería"));
                tags12.add(new TagModel("Estadística"));
                BookModel book12 = (BookModel)bookController.checkAndCreateBook(new BookModel("Probabilidad Y Estadistica Para Ingenieria Ciencias", 1999, "WALPOLE", "Pearson", tags12)).getBody();

                List<TagModel> tags13 = new ArrayList<>();
                tags13.add(new TagModel("Historia"));
                BookModel book13 = (BookModel)bookController.checkAndCreateBook(new BookModel("Manuel Belgrano", 2016, "Felipe Pigna", "Ariel", tags13)).getBody();

                List<TagModel> tags14 = new ArrayList<>();
                tags14.add(new TagModel("Historia"));
                BookModel book14 = (BookModel)bookController.checkAndCreateBook(new BookModel("Guerra y paz", 1864, "León Tolstói", "Ediciones Siruela", tags14)).getBody();

                List<TagModel> tags15 = new ArrayList<>();
                tags15.add(new TagModel("Historia"));
                BookModel book15 = (BookModel)bookController.checkAndCreateBook(new BookModel("Homo deus", 2015, "Yuval Noah Harari", "Harvill Secker", tags15)).getBody();

                List<TagModel> tags16 = new ArrayList<>();
                tags16.add(new TagModel("Política"));
                BookModel book16 = (BookModel)bookController.checkAndCreateBook(new BookModel("Politica Moral", 1995, "George Lakoff", "CAPITAN SWING LIBROS", tags16)).getBody();

                List<TagModel> tags17 = new ArrayList<>();
                tags17.add(new TagModel("Tecnología"));
                BookModel book17 = (BookModel)bookController.checkAndCreateBook(new BookModel("El Orden Del Tiempo", 2017, "Carlo Rovelli", "Anagrama", tags17)).getBody();

                List<TagModel> tags18 = new ArrayList<>();
                tags18.add(new TagModel("Tecnología"));
                BookModel book18 = (BookModel)bookController.checkAndCreateBook(new BookModel("El Fetiche De La Tecnologia", 2015, "Henrique Novaes", "Continente", tags18)).getBody();

                List<TagModel> tags19 = new ArrayList<>();
                tags19.add(new TagModel("Derecho"));
                BookModel book19 = (BookModel)bookController.checkAndCreateBook(new BookModel("Codigo De Las Mentes Extraordinarias", 2016, "Vishen Lakhiani", "EDAF", tags19)).getBody();

                List<TagModel> tags20 = new ArrayList<>();
                tags20.add(new TagModel("Derecho"));
                BookModel book20 = (BookModel)bookController.checkAndCreateBook(new BookModel("Lineamientos De Derecho Penal", 2019, "Raul Zaffaroni", "Ediar", tags20)).getBody();

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
                loanController.checkAndCreateLoan(user4, book3);

                //tanda 2 de ejemplares
                List<CopyModel> copies11 = new ArrayList<>();
                copies11.add(new CopyModel("C3DF9M"));
                Objects.requireNonNull(book11).setCopies(copies11);
                bookController.checkAndUpdateBook(book11.getId(), book11);

                List<CopyModel> copies12 = new ArrayList<>();
                copies12.add(new CopyModel("PP737J"));
                Objects.requireNonNull(book12).setCopies(copies12);
                bookController.checkAndUpdateBook(book12.getId(), book12);

                List<CopyModel> copies13 = new ArrayList<>();
                copies13.add(new CopyModel("X50F1T"));
                Objects.requireNonNull(book13).setCopies(copies13);
                bookController.checkAndUpdateBook(book13.getId(), book13);

                List<CopyModel> copies14 = new ArrayList<>();
                copies14.add(new CopyModel("TFA09O"));
                Objects.requireNonNull(book14).setCopies(copies14);
                bookController.checkAndUpdateBook(book14.getId(), book14);

                List<CopyModel> copies15 = new ArrayList<>();
                copies15.add(new CopyModel("ZV3XJN"));
                Objects.requireNonNull(book15).setCopies(copies15);
                bookController.checkAndUpdateBook(book15.getId(), book15);

                List<CopyModel> copies16 = new ArrayList<>();
                copies16.add(new CopyModel("7W3U2W"));
                Objects.requireNonNull(book16).setCopies(copies16);
                bookController.checkAndUpdateBook(book16.getId(), book16);

                List<CopyModel> copies17 = new ArrayList<>();
                copies17.add(new CopyModel("EWMGOZ"));
                Objects.requireNonNull(book17).setCopies(copies17);
                bookController.checkAndUpdateBook(book17.getId(), book17);

                List<CopyModel> copies18 = new ArrayList<>();
                copies18.add(new CopyModel("J670C8"));
                Objects.requireNonNull(book18).setCopies(copies18);
                bookController.checkAndUpdateBook(book18.getId(), book18);

                List<CopyModel> copies19 = new ArrayList<>();
                copies19.add(new CopyModel("TH4NK7"));
                Objects.requireNonNull(book19).setCopies(copies19);
                bookController.checkAndUpdateBook(book19.getId(), book19);

                List<CopyModel> copies20 = new ArrayList<>();
                copies20.add(new CopyModel("P9PFUL"));
                Objects.requireNonNull(book20).setCopies(copies20);
                bookController.checkAndUpdateBook(book20.getId(), book20);


                //loans
                loanController.checkAndCreateLoan(user1, book11);
                loanController.checkAndCreateLoan(user2, book14);
                loanController.checkAndCreateLoan(user3, book17);
                loanController.checkAndCreateLoan(user4, book20);
                loanController.checkAndCreateLoan(user1, book12);
                loanController.checkAndCreateLoan(user2, book15);
                loanController.checkAndCreateLoan(user3, book19);
                loanController.checkAndCreateLoan(user4, book13);


            } catch (Exception ignored) {
            }

        }
    }

}
