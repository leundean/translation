package translation;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import translation.model.Translation;

import java.util.Optional;

@Component
public interface TranslationRepository extends MongoRepository<Translation, String> {

    @Query("{ 'key': ?0 }")
    Optional<Translation> findByKey(final String key);

//    @Query("{ 'id': ?0 }")
//    Translation findById(final int id);

}