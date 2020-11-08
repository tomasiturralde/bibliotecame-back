package bibliotecame.back.Request;

import bibliotecame.back.User.UserModel;
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

    public boolean isValid(RequestForm form){
        if(form.getTitle()==null || form.getAuthor()==null || form.getReason()==null) return false;
        return !form.getTitle().isEmpty() && !form.getAuthor().isEmpty() && !form.getReason().isEmpty();
    }

    public Page<RequestDisplay> findAllPaged(int page, int size, String search){
        Pageable pageable = PageRequest.of(page, size);
        List<RequestModel> result = new ArrayList<>();
        findAll().iterator().forEachRemaining(result::add);
        result = result.stream().filter(rm -> rm.getUser().getEmail().toLowerCase().contains(search) || rm.getAuthor().toLowerCase().contains(search) || rm.getTitle().toLowerCase().contains(search) || rm.getDate().toString().contains(search) || rm.getStatus().getLabel().toLowerCase().contains(search))
                .collect(Collectors.toList());
        int total = result.size();
        int start = toIntExact(pageable.getOffset());
        int end = Math.min((start + pageable.getPageSize()), total);
        List<RequestDisplay> output = new ArrayList<>();
        if (start <= end) {
            List<RequestModel> temp = result.subList(start, end);
            temp.stream().forEach(requestModel -> output.add(new RequestDisplay(requestModel, true)));
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
            temp.stream().forEach(requestModel -> output.add(new RequestDisplay(requestModel, true)));
        }
        return new PageImpl<>(output, pageable, result.size());
    }

    public Page<RequestDisplay> findAllPagedByUser(int page, int size, UserModel user) {
        Pageable pageable = PageRequest.of(page, size);
        List<RequestModel> result = new ArrayList<>();
        findAll().iterator().forEachRemaining(result::add);
        result = result.stream().filter(requestModel -> requestModel.getUser().getId() == user.getId()).collect(Collectors.toList());
        int total = result.size();
        int start = toIntExact(pageable.getOffset());
        int end = Math.min((start + pageable.getPageSize()), total);
        List<RequestDisplay> output = new ArrayList<>();
        if (start <= end) {
            List<RequestModel> temp = result.subList(start, end);
            temp.stream().forEach(requestModel -> output.add(new RequestDisplay(requestModel, false)));
        }
        return new PageImpl<>(output, pageable, result.size());
    }
}
