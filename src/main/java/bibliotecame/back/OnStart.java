package bibliotecame.back;

import bibliotecame.back.Book.BookController;
import bibliotecame.back.Book.BookModel;
import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Copy.CopyService;
import bibliotecame.back.Loan.LoanController;
import bibliotecame.back.Loan.LoanModel;
import bibliotecame.back.Loan.LoanService;
import bibliotecame.back.Request.RequestForm;
import bibliotecame.back.Request.RequestModel;
import bibliotecame.back.Request.RequestService;
import bibliotecame.back.Request.RequestStatus;
import bibliotecame.back.Review.ReviewController;
import bibliotecame.back.Review.ReviewModel;
import bibliotecame.back.Sanction.SanctionController;
import bibliotecame.back.Sanction.SanctionForm;
import bibliotecame.back.Tag.TagModel;
import bibliotecame.back.User.UserController;
import bibliotecame.back.User.UserModel;
import bibliotecame.back.User.UserService;
import bibliotecame.back.Verification.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;


@Component
public class OnStart {

    private static final boolean RUN_ON_START = true;

    private final UserController userController;
    private final UserService userService;
    private final BookController bookController;
    private final LoanController loanController;
    private final ReviewController reviewController;
    private final LoanService loanService;
    private final CopyService copyService;
    private final RequestService requestService;
    private final VerificationService verificationService;
    private final SanctionController sanctionController;

    @Autowired
    public OnStart(UserController userController, BookController bookController, UserService userService, LoanController loanController, ReviewController reviewController, LoanService loanService, CopyService copyService, RequestService requestService, VerificationService verificationService, SanctionController sanctionController) {
        this.userController = userController;
        this.bookController = bookController;
        this.userService = userService;
        this.loanController = loanController;
        this.reviewController = reviewController;
        this.loanService = loanService;
        this.copyService = copyService;
        this.requestService = requestService;
        this.verificationService = verificationService;
        this.sanctionController = sanctionController;
    }

    @EventListener
    public void appReady(ApplicationReadyEvent event) {
        if(RUN_ON_START) {

            try {
                if(!userService.emailExists("admin@ing.austral.edu.ar")) {
                    UserModel admin = new UserModel("admin@ing.austral.edu.ar", "admin123", "Liliana", "admin", "111111111");
                    admin.setAdmin(true);
                    admin.setVerified(true);
                    userService.saveUser(admin);
                }
                UserModel user1 = (UserModel) userController.createUser(new UserModel("francisca.canton@ing.austral.edu.ar", "franchu123", "Francisca", "Canton", "011 2222 3333")).getBody();
                UserModel user2 = (UserModel) userController.createUser(new UserModel("esther.reyes@ing.austral.edu.ar", "123qweasd", "Esther", "Reyes", "011 3333 4444")).getBody();
                UserModel user3 = (UserModel) userController.createUser(new UserModel("marc.ivanov@ing.austral.edu.ar", "iva123456", "Marc", "Ivanov", "011 4444 0987")).getBody();
                UserModel user4 = (UserModel) userController.createUser(new UserModel("juanluis.llorens@ing.austral.edu.ar", "juan1234", "Juan Luis", "Lorens", "011 1234 5678")).getBody();

                user1.setVerified(true);
                user2.setVerified(true);
                user3.setVerified(true);
                user4.setVerified(true);
                userService.saveWithoutEncryption(user1);
                userService.saveWithoutEncryption(user2);
                userService.saveWithoutEncryption(user3);
                userService.saveWithoutEncryption(user4);
                verificationService.deleteVerification(verificationService.findVerificationByUserModel(user1));
                verificationService.deleteVerification(verificationService.findVerificationByUserModel(user2));
                verificationService.deleteVerification(verificationService.findVerificationByUserModel(user3));
                verificationService.deleteVerification(verificationService.findVerificationByUserModel(user4));



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

                loanController.checkAndCreateLoan(user1, book1);
                loanController.checkAndCreateLoan(user2, book4);
                loanController.checkAndCreateLoan(user3, book7);
                loanController.checkAndCreateLoan(user4, book10);
                loanController.checkAndCreateLoan(user1, book2);
                loanController.checkAndCreateLoan(user2, book5);
                LoanModel loanToReview9 = (LoanModel)loanController.checkAndCreateLoan(user3, book9).getBody();
                LoanModel loanToReview10 = (LoanModel)loanController.checkAndCreateLoan(user4, book3).getBody();

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

                LoanModel loanToReview1 = (LoanModel) loanController.checkAndCreateLoan(user1, book11).getBody();
                LoanModel loanToReview2 = (LoanModel) loanController.checkAndCreateLoan(user2, book14).getBody();
                LoanModel loanToReview3 = (LoanModel) loanController.checkAndCreateLoan(user3, book17).getBody();
                LoanModel loanToReview4 = (LoanModel) loanController.checkAndCreateLoan(user4, book20).getBody();
                LoanModel loanToReview5 = (LoanModel) loanController.checkAndCreateLoan(user1, book12).getBody();
                LoanModel loanToReview6 = (LoanModel) loanController.checkAndCreateLoan(user2, book15).getBody();
                LoanModel loanToReview7 = (LoanModel) loanController.checkAndCreateLoan(user3, book19).getBody();
                LoanModel loanToReview8 = (LoanModel) loanController.checkAndCreateLoan(user4, book13).getBody();

                assert loanToReview1 != null;
                loanToReview1.setWithdrawalDate(LocalDate.now());
                loanToReview1.setReturnDate(LocalDate.now());
                loanController.setWithdrawalPostAdminCheck(loanToReview1.getId());
                loanController.setReturnPostAdminCheck(loanToReview1.getId());
                ReviewModel reviewModel1 = new ReviewModel();
                reviewModel1.setDescription("Muy buen libro, lo recomiendo!");
                reviewModel1.setValue(5);
                reviewController.createReviewKnowingUser(reviewModel1, book11.getId(), user1);

                assert loanToReview2 != null;
                loanToReview2.setWithdrawalDate(LocalDate.now());
                loanToReview2.setReturnDate(LocalDate.now());
                loanController.setWithdrawalPostAdminCheck(loanToReview2.getId());
                loanController.setReturnPostAdminCheck(loanToReview2.getId());
                ReviewModel reviewModel2 = new ReviewModel();
                reviewModel2.setDescription("Muy dificil de entender, pero buen contenido.");
                reviewModel2.setValue(3);
                reviewController.createReviewKnowingUser(reviewModel2, book14.getId(), user2);

                assert loanToReview3 != null;
                loanToReview3.setWithdrawalDate(LocalDate.now());
                loanToReview3.setReturnDate(LocalDate.now());
                loanController.setWithdrawalPostAdminCheck(loanToReview3.getId());
                loanController.setReturnPostAdminCheck(loanToReview3.getId());
                ReviewModel reviewModel3 = new ReviewModel();
                reviewModel3.setDescription("Pesimo libro, me dormi en el segundo capitulo.");
                reviewModel3.setValue(2);
                reviewController.createReviewKnowingUser(reviewModel3, book17.getId(), user3);

                assert loanToReview4 != null;
                loanToReview4.setWithdrawalDate(LocalDate.now());
                loanToReview4.setReturnDate(LocalDate.now());
                loanController.setWithdrawalPostAdminCheck(loanToReview4.getId());
                loanController.setReturnPostAdminCheck(loanToReview4.getId());
                ReviewModel reviewModel4 = new ReviewModel();
                reviewModel4.setDescription("Muy buen libro, lo recomiendo! La version es medio vieja ya.");
                reviewModel4.setValue(4);
                reviewController.createReviewKnowingUser(reviewModel4, book20.getId(), user4);

                assert loanToReview5 != null;
                loanToReview5.setWithdrawalDate(LocalDate.now());
                loanToReview5.setReturnDate(LocalDate.now());
                loanController.setWithdrawalPostAdminCheck(loanToReview5.getId());
                loanController.setReturnPostAdminCheck(loanToReview5.getId());
                ReviewModel reviewModel5 = new ReviewModel();
                reviewModel5.setDescription("Usa palabras complicadas para confundirte, no me ayudo para nada con el estudio.");
                reviewModel5.setValue(1);
                reviewController.createReviewKnowingUser(reviewModel5, book12.getId(), user1);

                assert loanToReview6 != null;
                loanToReview6.setWithdrawalDate(LocalDate.now());
                loanToReview6.setReturnDate(LocalDate.now());
                loanController.setWithdrawalPostAdminCheck(loanToReview6.getId());
                loanController.setReturnPostAdminCheck(loanToReview6.getId());
                ReviewModel reviewModel6 = new ReviewModel();
                reviewModel6.setDescription("Me faltan palabras para explicar lo malo que es.");
                reviewModel6.setValue(1);
                reviewController.createReviewKnowingUser(reviewModel6, book15.getId(), user2);

                assert loanToReview7 != null;
                loanToReview7.setWithdrawalDate(LocalDate.now());
                loanToReview7.setReturnDate(LocalDate.now());
                loanController.setWithdrawalPostAdminCheck(loanToReview7.getId());
                loanController.setReturnPostAdminCheck(loanToReview7.getId());
                ReviewModel reviewModel7 = new ReviewModel();
                reviewModel7.setDescription("No lo recomendaría salvo que sea la ultima opción.");
                reviewModel7.setValue(2);
                reviewController.createReviewKnowingUser(reviewModel7, book19.getId(), user3);

                assert loanToReview8 != null;
                loanToReview8.setWithdrawalDate(LocalDate.now());
                loanToReview8.setReturnDate(LocalDate.now());
                loanController.setWithdrawalPostAdminCheck(loanToReview8.getId());
                loanController.setReturnPostAdminCheck(loanToReview8.getId());
                ReviewModel reviewModel8 = new ReviewModel();
                reviewModel8.setDescription("No estaba ni tan tan, ni muy muy");
                reviewModel8.setValue(3);
                reviewController.createReviewKnowingUser(reviewModel8, book13.getId(), user4);

                assert loanToReview9 != null;
                loanToReview9.setWithdrawalDate(LocalDate.now());
                loanToReview9.setReturnDate(LocalDate.now());
                loanController.setWithdrawalPostAdminCheck(loanToReview9.getId());
                loanController.setReturnPostAdminCheck(loanToReview9.getId());
                ReviewModel reviewModel9 = new ReviewModel();
                reviewModel9.setDescription("Estaba muy bueno, pero no me convenció al final");
                reviewModel9.setValue(4);
                reviewController.createReviewKnowingUser(reviewModel9, book9.getId(), user3);

                assert loanToReview10 != null;
                loanToReview10.setWithdrawalDate(LocalDate.now());
                loanToReview10.setReturnDate(LocalDate.now());
                loanController.setWithdrawalPostAdminCheck(loanToReview10.getId());
                loanController.setReturnPostAdminCheck(loanToReview10.getId());
                ReviewModel reviewModel10 = new ReviewModel();
                reviewModel10.setDescription("La verdad que me abrió los ojos a una nueva perspectiva.");
                reviewModel10.setValue(5);
                reviewController.createReviewKnowingUser(reviewModel10, book3.getId(), user4);

                UserModel userEx1 = (UserModel) userController.createUser(new UserModel("claude.vonriegan@ing.austral.edu.ar", "goldendeer123", "Claude", "Von Riegan", "011 2121 3333")).getBody();
                UserModel userEx2 = (UserModel) userController.createUser(new UserModel("ignatz.victor@ing.austral.edu.ar", "iloveart25", "Ignatz", "Victor", "011 3543 4444")).getBody();
                UserModel userEx3 = (UserModel) userController.createUser(new UserModel("marianne.vonedmund@ing.austral.edu.ar", "myscrest77", "Marianne", "Von Edmund", "011 4124 0987")).getBody();
                UserModel userEx4 = (UserModel) userController.createUser(new UserModel("leonie.pinelli@ing.austral.edu.ar", "jeralt1000", "Leonie", "Pinelli", "011 1234 5678")).getBody();
                UserModel userEx5 = (UserModel) userController.createUser(new UserModel("holst.goneril@ing.austral.edu.ar", "singlehandedly10", "Holst", "Goneril", "011 2112 3356")).getBody();

                userEx1.setVerified(true);
                userEx2.setVerified(true);
                userEx3.setVerified(true);
                userEx4.setVerified(true);
                userEx5.setVerified(true);
                userService.saveWithoutEncryption(userEx1);
                userService.saveWithoutEncryption(userEx2);
                userService.saveWithoutEncryption(userEx3);
                userService.saveWithoutEncryption(userEx4);
                userService.saveWithoutEncryption(userEx5);
                verificationService.deleteVerification(verificationService.findVerificationByUserModel(userEx1));
                verificationService.deleteVerification(verificationService.findVerificationByUserModel(userEx2));
                verificationService.deleteVerification(verificationService.findVerificationByUserModel(userEx3));
                verificationService.deleteVerification(verificationService.findVerificationByUserModel(userEx4));
                verificationService.deleteVerification(verificationService.findVerificationByUserModel(userEx5));

                List<TagModel> tagsEx1 = new ArrayList<>();
                tags20.add(new TagModel("Novela"));
                BookModel bookEx1 = (BookModel)bookController.checkAndCreateBook(new BookModel("El libro de Seiros, parte I", 890, "Lady Rhea", "Fodlan Publishing", tagsEx1)).getBody();

                List<TagModel> tagsEx2 = new ArrayList<>();
                tags20.add(new TagModel("Novela"));
                BookModel bookEx2 = (BookModel)bookController.checkAndCreateBook(new BookModel("El libro de Seiros, parte II", 891, "Lady Rhea", "Fodlan Publishing", tagsEx2)).getBody();

                List<TagModel> tagsEx3 = new ArrayList<>();
                tags20.add(new TagModel("Poesía"));
                BookModel bookEx3 = (BookModel)bookController.checkAndCreateBook(new BookModel("Loveless", 2020, "Genesis Rhapsodos", "Midgar finest", tagsEx3)).getBody();

                List<TagModel> tagsEx4 = new ArrayList<>();
                tags20.add(new TagModel("Historia"));
                BookModel bookEx4 = (BookModel)bookController.checkAndCreateBook(new BookModel("History of Fodlan: Crescent Moon War", 881, "Macuil", "Adrestian imprint", tagsEx4)).getBody();

                List<TagModel> tagsEx5 = new ArrayList<>();
                tags20.add(new TagModel("Ingeniería"));
                BookModel bookEx5 = (BookModel)bookController.checkAndCreateBook(new BookModel("Head-First Dessign Patterns", 2005, "Kathy Sierra", "O'Reilly", tagsEx5)).getBody();

                List<CopyModel> copiesEx1 = new ArrayList<>();
                copiesEx1.add(new CopyModel("SE1R0SI-001"));
                copiesEx1.add(new CopyModel("SE1R0SI-002"));
                copiesEx1.add(new CopyModel("SE1R0SI-003"));
                Objects.requireNonNull(bookEx1).setCopies(copiesEx1);
                bookController.checkAndUpdateBook(bookEx1.getId(), bookEx1);

                List<CopyModel> copiesEx2 = new ArrayList<>();
                copiesEx2.add(new CopyModel("SE1R0SII-001"));
                copiesEx2.add(new CopyModel("SE1R0SII-002"));
                copiesEx2.add(new CopyModel("SE1R0SII-003"));
                Objects.requireNonNull(bookEx2).setCopies(copiesEx2);
                bookController.checkAndUpdateBook(bookEx2.getId(), bookEx2);

                List<CopyModel> copiesEx3 = new ArrayList<>();
                copiesEx3.add(new CopyModel("LLS001"));
                copiesEx3.add(new CopyModel("LLS010"));
                copiesEx3.add(new CopyModel("LLS011"));
                Objects.requireNonNull(bookEx3).setCopies(copiesEx3);
                bookController.checkAndUpdateBook(bookEx3.getId(), bookEx3);

                List<CopyModel> copiesEx4 = new ArrayList<>();
                copiesEx4.add(new CopyModel("GDN012"));
                copiesEx4.add(new CopyModel("BEA042"));
                copiesEx4.add(new CopyModel("BLI504"));
                Objects.requireNonNull(bookEx4).setCopies(copiesEx4);
                bookController.checkAndUpdateBook(bookEx4.getId(), bookEx4);

                List<CopyModel> copiesEx5 = new ArrayList<>();
                copiesEx5.add(new CopyModel("OBS995"));
                copiesEx5.add(new CopyModel("VIS524"));
                copiesEx5.add(new CopyModel("STR465"));
                Objects.requireNonNull(bookEx5).setCopies(copiesEx5);
                bookController.checkAndUpdateBook(bookEx5.getId(), bookEx5);

                    List<UserModel> users = new ArrayList<>();
                    users.add(userService.findUserById(userEx1.getId()));
                    users.add(userEx2);
                    users.add(userEx3);
                    users.add(userEx4);
                    users.add(userEx5);
                    List<CopyModel> copies = new ArrayList<>();
                    copies.add(copiesEx1.get(0));
                    copies.add(copiesEx2.get(0));
                    copies.add(copiesEx3.get(0));
                    copies.add(copiesEx4.get(0));
                    copies.add(copiesEx5.get(0));
                    copies.add(copiesEx1.get(1));
                    copies.add(copiesEx2.get(1));
                    copies.add(copiesEx3.get(1));
                    copies.add(copiesEx4.get(1));
                    copies.add(copiesEx5.get(1));
                    List<BookModel> books = new ArrayList<>();
                    books.add(bookEx1);
                    books.add(bookEx2);
                    books.add(bookEx3);
                    books.add(bookEx4);
                    books.add(bookEx5);

                    LocalDate date = LocalDate.of(2020, Month.JANUARY,1);
                    for (int i = 0; i <10 ; i++) {
                        date = date.plus(Period.ofDays(4));
                        for (int j = 0; j <5; j++) {
                            LoanModel loan = loanService.saveLoan(new LoanModel(copies.get(j),date,date.plus(Period.ofDays(5))));
                            copies.get(j).setBooked(true);
                            copyService.saveCopy(copies.get(j));
                            userService.addLoan(users.get(j),loan);
                            loan.setWithdrawalDate( date );
                            loan.setReturnDate( date.plus(Period.ofDays(2)) );
                            loanService.saveLoan(loan);
                            if(i==0){
                                Random rand = new Random();
                                ReviewModel review = new ReviewModel();
                                review.setValue(rand.nextInt((5) + 1) + 1);
                                reviewController.createReviewKnowingUser(review, books.get(j).getId(), users.get(j));
                            }
                        }
                    }
                    date = LocalDate.now();

                        for (int j = 0; j <5; j++) {
                            LoanModel loan = loanService.saveLoan(new LoanModel(copies.get(j),date,date.plus(Period.ofDays(365))));
                            copies.get(j).setBooked(true);
                            copyService.saveCopy(copies.get(j));
                            userService.addLoan(users.get(j),loan);
                            loan.setWithdrawalDate( date );
                            loanService.saveLoan(loan);
                        }

                        for (int j = 0; j <5; j++) {
                            LoanModel loan = loanService.saveLoan(new LoanModel(copies.get(j+5),date.minus(Period.ofDays(5)),date));
                            copies.get(j+5).setBooked(true);
                            copyService.saveCopy(copies.get(j+5));
                            userService.addLoan(users.get(j),loan);
                            loan.setWithdrawalDate( date );
                            loanService.saveLoan(loan);
                        }

                RequestForm form = new RequestForm();
                form.setTitle("Higher creativity");
                form.setAuthor("Willis W. Harman");
                form.setReason("Un gran libro para despejarse.");
                createAndSaveRequest(form,userEx1);

                form = new RequestForm();
                form.setTitle("Harry Potter and the Philosopher's Stone");
                form.setAuthor("J. K. Rowling");
                form.setReason("Parte de la icónica saga que sumaría mucho como lectura recreativa.");
                createAndSaveRequest(form,userEx1);

                form = new RequestForm();
                form.setTitle("Harry Potter and the Chamber of Secrets");
                form.setAuthor("J. K. Rowling");
                form.setReason("Parte de la icónica saga que sumaría mucho como lectura recreativa.");
                createAndSaveRequest(form,userEx1);

                form = new RequestForm();
                form.setTitle("Harry Potter and the Prisoner of Azkaban");
                form.setAuthor("J. K. Rowling");
                form.setReason("Parte de la icónica saga que sumaría mucho como lectura recreativa.");
                createAndSaveRequest(form,userEx1);

                form = new RequestForm();
                form.setTitle("Harry Potter and the Goblet of Fire");
                form.setAuthor("J. K. Rowling");
                form.setReason("Parte de la icónica saga que sumaría mucho como lectura recreativa.");
                createAndSaveRequest(form,userEx1);

                form = new RequestForm();
                form.setTitle("Harry Potter and the Order of the Phoenix");
                form.setAuthor("J. K. Rowling");
                form.setReason("Parte de la icónica saga que sumaría mucho como lectura recreativa.");
                createAndSaveRequest(form,userEx1);

                form = new RequestForm();
                form.setTitle("Harry Potter and the Half-Blood Prince");
                form.setAuthor("J. K. Rowling");
                form.setReason("Parte de la icónica saga que sumaría mucho como lectura recreativa.");
                createAndSaveRequest(form,userEx1);

                form = new RequestForm();
                form.setTitle("Deathly Hallows");
                form.setAuthor("J. K. Rowling");
                form.setReason("Parte de la icónica saga que sumaría mucho como lectura recreativa.");
                createAndSaveRequest(form,userEx1);

                form = new RequestForm();
                form.setTitle("Lineamientos De Derecho Penal");
                form.setAuthor("Raul Zaffaroni");
                form.setReason("Siempre que quiero reservarlo está agotado, agreguen más ejemplares!");
                createAndSaveRequest(form,userEx1);

                form = new RequestForm();
                form.setTitle("Loveless");
                form.setAuthor("Genesis Rhapsodos");
                form.setReason("Siempre que quiero reservarlo está agotado, agreguen más ejemplares!");
                createAndSaveRequest(form,userEx1);

                UserModel userS1 = (UserModel) userController.createUser(new UserModel("alba.rodriguez@ing.austral.edu.ar", "soyalba123", "Alba", "Rodriguez", "011 5555 3333")).getBody();
                UserModel userS2 = (UserModel) userController.createUser(new UserModel("bautista.fernandez@ing.austral.edu.ar", "soybauti123", "Bautista", "Fernandez", "011 5555 4444")).getBody();
                UserModel userS3 = (UserModel) userController.createUser(new UserModel("constanza.fino@ing.austral.edu.ar", "soyconi123", "Constanza", "Fino", "011 5555 0987")).getBody();
                UserModel userS4 = (UserModel) userController.createUser(new UserModel("daniel.cora@ing.austral.edu.ar", "soydani123", "Daniel", "Cora", "011 5555 5678")).getBody();
                UserModel userS5 = (UserModel) userController.createUser(new UserModel("elisa.gomez@ing.austral.edu.ar", "soyelisa123", "Elisa", "Gomez", "011 5555 3356")).getBody();
                UserModel userS6 = (UserModel) userController.createUser(new UserModel("facundo.cura@ing.austral.edu.ar", "soyfacu123", "Facundo", "Cura", "011 2121 5555")).getBody();
                UserModel userS7 = (UserModel) userController.createUser(new UserModel("gaston.carpi@ing.austral.edu.ar", "soygaston123", "Gaston", "Carpi", "011 3543 5555")).getBody();
                UserModel userS8 = (UserModel) userController.createUser(new UserModel("hernan.lora@ing.austral.edu.ar", "soyhernan123", "Hernan", "Lora", "011 4124 5555")).getBody();
                UserModel userS9 = (UserModel) userController.createUser(new UserModel("isabel.mano@ing.austral.edu.ar", "soyisabel123", "Isabel", "Mano", "011 1234 5555")).getBody();
                UserModel userS10 = (UserModel) userController.createUser(new UserModel("justo.nanin@ing.austral.edu.ar", "soyjusto123", "Justo", "Nanin", "011 2112 5555")).getBody();

                SanctionForm sanctionForm = new SanctionForm();
                sanctionForm.setEmail(userS1.getEmail());
                sanctionForm.setEndDate(LocalDate.now().plus(Period.ofWeeks(3)));
                sanctionForm.setReason("Devolvio un libro en mal estado.");
                sanctionController.checkAndCreateSanction(sanctionForm);

                sanctionForm = new SanctionForm();
                sanctionForm.setEmail(userS2.getEmail());
                sanctionForm.setEndDate(LocalDate.now().plus(Period.ofWeeks(5)));
                sanctionForm.setReason("Me miro mal.");
                sanctionController.checkAndCreateSanction(sanctionForm);

                sanctionForm = new SanctionForm();
                sanctionForm.setEmail(userS3.getEmail());
                sanctionForm.setEndDate(LocalDate.now().plus(Period.ofWeeks(2)));
                sanctionForm.setReason("No sabe tener una conversacion.");
                sanctionController.checkAndCreateSanction(sanctionForm);

                sanctionForm = new SanctionForm();
                sanctionForm.setEmail(userS4.getEmail());
                sanctionForm.setEndDate(LocalDate.now().plus(Period.ofWeeks(3)));
                sanctionForm.setReason("Robo un lapiz de la biblioteca.");
                sanctionController.checkAndCreateSanction(sanctionForm);

                sanctionForm = new SanctionForm();
                sanctionForm.setEmail(userS5.getEmail());
                sanctionForm.setEndDate(LocalDate.now().plus(Period.ofWeeks(4)));
                sanctionForm.setReason("Escribio todo el libro");
                sanctionController.checkAndCreateSanction(sanctionForm);

                sanctionForm = new SanctionForm();
                sanctionForm.setEmail(userS6.getEmail());
                sanctionForm.setEndDate(LocalDate.now().plus(Period.ofWeeks(3)));
                sanctionForm.setReason("Devolvio un libro en mal estado.");
                sanctionController.checkAndCreateSanction(sanctionForm);

                sanctionForm = new SanctionForm();
                sanctionForm.setEmail(userS7.getEmail());
                sanctionForm.setEndDate(LocalDate.now().plus(Period.ofWeeks(3)));
                sanctionForm.setReason("Robo una lapicera de la biblioteca.");
                sanctionController.checkAndCreateSanction(sanctionForm);

                sanctionForm = new SanctionForm();
                sanctionForm.setEmail(userS8.getEmail());
                sanctionForm.setEndDate(LocalDate.now().plus(Period.ofWeeks(4)));
                sanctionForm.setReason("Grito.");
                sanctionController.checkAndCreateSanction(sanctionForm);

                sanctionForm = new SanctionForm();
                sanctionForm.setEmail(userS9.getEmail());
                sanctionForm.setEndDate(LocalDate.now().plus(Period.ofWeeks(2)));
                sanctionForm.setReason("Ya muchas veces se retraso.");
                sanctionController.checkAndCreateSanction(sanctionForm);

                sanctionForm = new SanctionForm();
                sanctionForm.setEmail(userS10.getEmail());
                sanctionForm.setEndDate(LocalDate.now().plus(Period.ofWeeks(3)));
                sanctionForm.setReason("Ya muchas veces se retraso.");
                sanctionController.checkAndCreateSanction(sanctionForm);


                if(!userService.emailExists("demo.bibliotecame@ing.austral.edu.ar")) {
                    UserModel demoUser = new UserModel("demo.bibliotecame@ing.austral.edu.ar","demo123","demo user","demo user","11111111");
                    demoUser.setVerified(true);
                    demoUser.setActive(true);
                    userService.saveUser(demoUser);

                    LoanModel loan = loanService.saveLoan(new LoanModel(copies1.get(1),LocalDate.now(),LocalDate.now().plus(Period.ofDays(5))));
                    copies1.get(1).setBooked(true);
                    copyService.saveCopy(copies1.get(1));
                    userService.addLoan(demoUser,loan);
                    loan.setWithdrawalDate(LocalDate.now());
                    loan.setReturnDate( LocalDate.now() );
                    loanService.saveLoan(loan);

                    loan = loanService.saveLoan(new LoanModel(copies1.get(2),LocalDate.now(),LocalDate.now().plus(Period.ofDays(5))));
                    copies1.get(2).setBooked(true);
                    copyService.saveCopy(copies1.get(2));
                    userService.addLoan(demoUser,loan);
                    loan.setWithdrawalDate(LocalDate.now());
                    loan.setReturnDate( LocalDate.now() );
                    loanService.saveLoan(loan);

                    loan = loanService.saveLoan(new LoanModel(copies1.get(1),LocalDate.now(),LocalDate.now().plus(Period.ofDays(5))));
                    copies1.get(1).setBooked(true);
                    copyService.saveCopy(copies1.get(1));
                    userService.addLoan(demoUser,loan);
                    loanService.saveLoan(loan);

                    loan = loanService.saveLoan(new LoanModel(copies2.get(1),LocalDate.now().minus(Period.ofDays(10)),LocalDate.now().minus(Period.ofDays(5))));
                    copies2.get(1).setBooked(true);
                    copyService.saveCopy(copies2.get(1));
                    userService.addLoan(demoUser,loan);
                    loan.setWithdrawalDate(LocalDate.now().minus(Period.ofDays(9)));
                    loanService.saveLoan(loan);

                    RequestModel rm1 = new RequestModel(new RequestForm("To kill a mockingbird","Harper Lee","¡Es un gran libro clasico!"));
                    RequestModel rm2 = new RequestModel(new RequestForm("The great gatsby","F. Scott Fitzgerald","Me parece un libro interesante."));
                    RequestModel rm3 = new RequestModel(new RequestForm("One hundred years of solitude","Gabriel García Márquez","Leí en un foro que es muy bueno."));
                    RequestModel rm4 = new RequestModel(new RequestForm("In cold blood","Truman Capote","Es una gran novela que tomó muchos años de investigación para realizarse"));
                    RequestModel rm5 = new RequestModel(new RequestForm("Brave New World","Aldous Huxley","Es muy controversial, pero da mucho que pensar"));
                    RequestModel rm6 = new RequestModel(new RequestForm("To kill a mockingbird","Harper Lee","Se que rechazaron la solicitud previa, pero insisto en que es muy buen libro."));

                    rm1.setStatus(RequestStatus.REJECTED);
                    rm2.setStatus(RequestStatus.REJECTED);
                    rm3.setStatus(RequestStatus.APPROVED);
                    rm4.setStatus(RequestStatus.PENDING);
                    rm5.setStatus(RequestStatus.PENDING);
                    rm6.setStatus(RequestStatus.APPROVED);

                    List<RequestModel> requestModels = new ArrayList<>();
                    requestModels.add(rm1);
                    requestModels.add(rm2);
                    requestModels.add(rm3);
                    requestModels.add(rm4);
                    requestModels.add(rm5);
                    requestModels.add(rm6);

                    requestModels.forEach(rm -> {
                        rm.setDate(LocalDate.now());
                        rm.setUser(demoUser);
                        requestService.saveRequest(rm);
                    });
                }



            } catch (Exception ignored) {
            }

        }
    }

    private void createAndSaveRequest(RequestForm form, UserModel user){
        RequestModel request = new RequestModel(form);
        request.setUser(user);
        request.setDate(LocalDate.now());
        request.setStatus(RequestStatus.PENDING);
        requestService.saveRequest(request);
    }


}
