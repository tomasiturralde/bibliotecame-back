package bibliotecame.back.Copy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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

}
