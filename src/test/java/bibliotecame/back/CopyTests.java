package bibliotecame.back;

import bibliotecame.back.Copy.CopyModel;
import bibliotecame.back.Copy.CopyService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CopyTests {
    @Autowired
    CopyService copyService;

    CopyModel copy;

    @BeforeAll
    void setUp(){
        copy = new CopyModel();
    }

    @Test
    void testGettersAndSetters(){
        String id = "ABC123";

        copy.setId(id);
        assertThat(copy.getId()).isEqualTo(id);

        copy.setActive(true);
        assertThat(copy.getActive()).isTrue();

        copy.setActive(false);
        assertThat(copy.getActive()).isFalse();
    }

    @Test
    void testFindCopyById(){
        String id = "ABC123";

        copy.setId(id);
        copyService.saveCopy(copy);

        assertThat(copyService.findCopyById(id)).isNotNull();
    }
}
