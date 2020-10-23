package bibliotecame.back.Request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestService {

    private final RequestRepository requestRepository;

    @Autowired
    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public RequestModel saveRequest(RequestModel requestModel){
        return this.requestRepository.save(requestModel);
    }

    public Iterable<RequestModel> findAll(){
        return this.requestRepository.findAll();
    }

    public RequestModel findById(int id) { return this.requestRepository.findById(id).orElseThrow(() -> new RuntimeException("bibliotecame.back.Request with id: " + id + " not found!")); }

}
