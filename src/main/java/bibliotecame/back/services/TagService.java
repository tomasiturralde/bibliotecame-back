package bibliotecame.back.services;

import bibliotecame.back.models.TagModel;
import bibliotecame.back.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository){
        this.tagRepository = tagRepository;
    }

    public TagModel findTagById (int id){
        return this.tagRepository.findById(id).orElseThrow(() -> new RuntimeException("Tag with id: " + id + " not found!"));
    }

    public TagModel saveTag(TagModel tagModel){
        return this.tagRepository.save(tagModel);
    }

    public TagModel findTagByName(String name){
        return this.tagRepository.findByName(name).orElseThrow(() -> new RuntimeException("Tag named: " + name + " not found!"));
    }

    public boolean exists(String name){
        return this.tagRepository.findByName(name).isPresent();
    }

    public boolean validate(List<TagModel> tags) {

        for (TagModel tag : tags){
            if(!exists(tag.getName())) return false;
        }
        return true;
    }
}


