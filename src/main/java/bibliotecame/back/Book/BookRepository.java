package bibliotecame.back.Book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends PagingAndSortingRepository<BookModel, Integer> {

    Optional<BookModel> findById(int id);

    Iterable<BookModel> findAllByActive(boolean active);

    Optional<BookModel> findByTitleAndAuthorAndPublisherAndYear(String title, String author, String publisher, int year);

    @Query(value = "select b from BookModel b where" +
            " lower(b.title) like %:search%" +
            " or lower(b.author) like %:search%" +
            " or lower(b.publisher) like %:search%" +
            " or exists (select t from b.tags t" +
            " where lower(t.name) like %:search%) order by b.title")
    Page<BookModel> findAllByTitleOrAuthorOrPublisherOrTags(Pageable pageable,@Param("search")String title);

    @Query(value = "select b from BookModel b where" +
            " b.active = True and ( " +
            " lower(b.title) like %:search% " +
            " or lower(b.author) like %:search%" +
            " or lower(b.publisher) like %:search% " +
            " or exists (select t from b.tags t" +
            " where lower(t.name) like %:search%)) order by b.title")
    Page<BookModel> findAllByTitleOrAuthorOrPublisherOrTagsAndActive(Pageable pageable,@Param("search")String title);

}
