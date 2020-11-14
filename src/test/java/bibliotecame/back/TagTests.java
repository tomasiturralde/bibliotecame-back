package bibliotecame.back;

import bibliotecame.back.Tag.TagModel;
import bibliotecame.back.Tag.TagService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TagTests {
    @Autowired
    TagService tagService;

    TagModel tag;

    @BeforeAll
    void setUp(){
        tag = new TagModel();
    }

    @Test
    void testGettersAndSetters(){
        String name = "Historia";

        tag.setName(name);
        assertThat(tag.getName()).isEqualTo(name);
    }

    @Test
    void testFindByNameAndWildcard(){
        String name = "Historia Argentina";

        tag.setName(name);
        tagService.saveTag(tag);

        assertThat(tagService.findTagByName(name)).isNotNull();
        assertThat(tagService.findAllByNameWildcard("Hist").size()).isGreaterThanOrEqualTo(1);
    }
}
