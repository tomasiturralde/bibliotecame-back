package bibliotecame.back.Request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public Page<RequestModel> findAllByStatus(int page, int size, RequestStatus status){
        return requestRepository.findAllByStatus(status,PageRequest.of(page, size));
    }

    public Page<RequestModel> findAll(int page, int size){
        return requestRepository.findAll(PageRequest.of(page,size));
    }

}
