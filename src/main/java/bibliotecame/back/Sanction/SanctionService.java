package bibliotecame.back.Sanction;

import bibliotecame.back.User.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.toIntExact;

@Service
public class SanctionService {

    private final SanctionRepository sanctionRepository;

    @Autowired
    public SanctionService(SanctionRepository sanctionRepository) {
        this.sanctionRepository = sanctionRepository;
    }

    public SanctionModel findSanctionById(int id){
        return this.sanctionRepository.findById(id).orElseThrow(() -> new RuntimeException("Sanction with id: " + id + " not found!"));
    }

    public SanctionModel saveSanction(SanctionModel sanctionModel){
        return this.sanctionRepository.save(sanctionModel);
    }

    public boolean userIsSanctioned(UserModel userModel){
        return this.sanctionRepository.findByUser(userModel).isPresent();
    }

    public Page<SanctionModel> getSanctionList(int page, int size, String search){
        Pageable pageable = PageRequest.of(page, size);
        return sanctionRepository.findAllByUserOrReasonAndActive(pageable, search.toLowerCase(), LocalDate.now());
    }

    public Page<SanctionDisplay> findAllByEmailOrStartDateOrEndDate(int page, int size, String search){
        Pageable pageable = PageRequest.of(page, size);
        List<SanctionModel> result = new ArrayList<>();
        sanctionRepository.findAll().iterator().forEachRemaining(result::add);
        result = result.stream().filter(sM -> sM.getUser().getEmail().toLowerCase().contains(search) || sM.getCreationDate().toString().contains(search) || sM.getEndDate().toString().contains(search)).collect(Collectors.toList());
        int total = result.size();
        int start = toIntExact(pageable.getOffset());
        int end = Math.min((start + pageable.getPageSize()), total);
        List<SanctionDisplay> output = new ArrayList<>();
        if (start <= end) {
            List<SanctionModel> temp = result.subList(start, end);
            temp.stream().forEach(sM -> output.add(new SanctionDisplay(sM.getId(),sM.getUser().getEmail(),sM.getCreationDate(),sM.getEndDate(), sM.getReason())));
        }
        return new PageImpl<>(output, pageable, result.size());
    }
}
