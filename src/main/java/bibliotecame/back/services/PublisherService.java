package bibliotecame.back.services;

import bibliotecame.back.models.AuthorModel;
import bibliotecame.back.models.PublisherModel;
import bibliotecame.back.repository.AuthorRepository;
import bibliotecame.back.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublisherService {

    private PublisherRepository publisherRepository;

    @Autowired
    public PublisherService(PublisherRepository publisherRepository){
        this.publisherRepository = publisherRepository;
    }

    public PublisherModel findPublisherById (int id){
        return this.publisherRepository.findById(id).orElseThrow(() -> new RuntimeException("Publisher with id: " + id + " not found!"));
    }

    public PublisherModel savePublisher(PublisherModel publisherModel){
        return this.publisherRepository.save(publisherModel);
    }

    public PublisherModel findPublisherByName(String name){
        return this.publisherRepository.findByName(name).orElseThrow(() -> new RuntimeException("Author named: " + name +" not found!"));
    }

    public boolean exists(String name){
        return this.publisherRepository.findByName(name).isPresent();
    }
}
