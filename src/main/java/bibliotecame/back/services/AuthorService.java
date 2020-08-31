package bibliotecame.back.services;

import bibliotecame.back.models.AuthorModel;
import bibliotecame.back.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository){
        this.authorRepository = authorRepository;
    }

    public AuthorModel findAuthorById (int id){
        return this.authorRepository.findById(id).orElseThrow(() -> new RuntimeException("Author with id: " + id + " not found!"));
    }

    public AuthorModel saveAuthor(AuthorModel authorModel){
        return this.authorRepository.save(authorModel);
    }

    public AuthorModel findAuthorByName(String firstName, String lastName){
        return this.authorRepository.findByFirstNameAndLastName(firstName, lastName).orElseThrow(() -> new RuntimeException("Author named: " + firstName + " " + lastName + " not found!"));
    }

    public boolean exists(String firstName, String lastName){
        return this.authorRepository.findByFirstNameAndLastName(firstName,lastName).isPresent();
    }
}
