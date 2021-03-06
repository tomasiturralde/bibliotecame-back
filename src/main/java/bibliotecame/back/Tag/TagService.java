package bibliotecame.back.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository){
        this.tagRepository = tagRepository;
    }

    public TagModel saveTag(TagModel tagModel){
        return this.tagRepository.save(tagModel);
    }

    public TagModel findTagByName(String name){
        return this.tagRepository.findByName(name).orElseThrow(() -> new RuntimeException("Tag named: " + name + " not found!"));
    }

    public Optional<TagModel> exists(String name){
        return this.tagRepository.findByName(name);
    }

    public List<TagModel> findAllByNameWildcard(String name){
        Iterable<TagModel> tags = this.tagRepository.findByNameWildcard(name);
        List<TagModel> actualList = new ArrayList<>();
        tags.iterator().forEachRemaining(actualList::add);
        return actualList;
    }

    public List<TagModel> validate(List<TagModel> tags) {
        List<TagModel> newTags = new ArrayList<>();
        if(tags != null){
            for (TagModel tag : tags){
                Optional<TagModel> optionalTag = exists(tag.getName());
                if(optionalTag.isEmpty()) {
                    saveTag(tag);
                    newTags.add(tag);
                }
                else {
                    newTags.add(optionalTag.get());
                }
            }
        }
        return newTags;
    }
}


