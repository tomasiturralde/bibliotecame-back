package bibliotecame.back.Book;

import bibliotecame.back.Tag.TagModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Query(value = "select b from BookModel b where" +
            " b.active = True and ( " +
            " lower(b.title) like %:title% " +
            " and lower(b.author) like %:author%" +
            " and cast( b.year as string) like %:year%" +
            " and lower(b.publisher) like %:publisher%) " +
            " order by b.title")
    Page<BookModel> findAllByTitleAndAuthorAndPublisherAndYearAndActive(Pageable pageable,@Param("title")String title, @Param("author")String author, @Param("publisher")String publisher, @Param("year")String year);

    @Query(value = "select b from BookModel b where" +
            " lower(b.title) like %:title% " +
            " and lower(b.author) like %:author%" +
            " and lower(b.publisher) like %:publisher% " +
            " and cast( b.year as string) like %:year%" +
            " order by b.title")
    Page<BookModel> findAllByTitleAndAuthorAndPublisherAndYear(Pageable pageable,@Param("title")String title, @Param("author")String author, @Param("publisher")String publisher, @Param("year")String year);

}
