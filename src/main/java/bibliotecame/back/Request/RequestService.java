package bibliotecame.back.Request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.toIntExact;

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

    public Page<RequestDisplay> findAllPaged(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        List<RequestModel> result = new ArrayList<>();
        findAll().iterator().forEachRemaining(result::add);
//        result = result.stream().filter(searchForm::matches).collect(Collectors.toList());
        int total = result.size();
        int start = toIntExact(pageable.getOffset());
        int end = Math.min((start + pageable.getPageSize()), total);
        List<RequestDisplay> output = new ArrayList<>();
        if (start <= end) {
            List<RequestModel> temp = result.subList(start, end);
            temp.stream().forEach(requestModel -> output.add(new RequestDisplay(requestModel)));
        }
        return new PageImpl<>(output, pageable, result.size());
    }

    public Page<RequestDisplay> findAllPagedByStatus(int page, int size, RequestStatus status){
        Pageable pageable = PageRequest.of(page, size);
        List<RequestModel> result = new ArrayList<>();
        findAll().iterator().forEachRemaining(result::add);
        result = result.stream().filter(requestModel -> requestModel.getStatus().equals(status)).collect(Collectors.toList());
        int total = result.size();
        int start = toIntExact(pageable.getOffset());
        int end = Math.min((start + pageable.getPageSize()), total);
        List<RequestDisplay> output = new ArrayList<>();
        if (start <= end) {
            List<RequestModel> temp = result.subList(start, end);
            temp.stream().forEach(requestModel -> output.add(new RequestDisplay(requestModel)));
        }
        return new PageImpl<>(output, pageable, result.size());
    }

}
