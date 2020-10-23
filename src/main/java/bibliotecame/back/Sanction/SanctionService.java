package bibliotecame.back.Sanction;

import bibliotecame.back.User.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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
}
