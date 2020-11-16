package bibliotecame.back.Copy;

import bibliotecame.back.Book.BookModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CopyService {

    private final CopyRepository copyRepository;

    @Autowired
    public CopyService(CopyRepository copyRepository) {
        this.copyRepository = copyRepository;
    }

    public CopyModel saveCopy(CopyModel copyModel){
        return this.copyRepository.save(copyModel);
    }

    public boolean exists(String id){
        return this.copyRepository.findById(id).isPresent();
    }

    public CopyModel findCopyById(String id){
        return this.copyRepository.findById(id).orElseThrow(() -> new RuntimeException("bibliotecame.back.Copy with id: " + id + " not found!"));
    }

    public boolean copyActivationWithDisabledBook(List<CopyModel> copies, BookModel book){
        if(book.isActive()) return false;
        for(CopyModel copy : copies){
            if(exists(copy.getId()) && copy.getActive() && !findCopyById(copy.getId()).getActive()) return true;
        }
        return false;
    }

    public boolean loanedCopyIsBeingDeactivated(List<CopyModel> copies){
        return copies.stream().anyMatch(copy -> exists(copy.getId()) && copy.getBooked() && !copy.getActive());
    }

    public boolean newCopyWithDisabledBook(List<CopyModel> copies, BookModel book){
        if(book.isActive()) return false;
        return copies.stream().anyMatch(c -> !exists(c.getId()));
    }

}
