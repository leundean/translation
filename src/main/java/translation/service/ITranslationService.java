package translation.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import translation.model.Translation;

import java.util.List;
import java.util.Optional;

public interface ITranslationService {
    void create(Translation translation);

    Optional<Translation> findByKey(String key);

    List<Translation> findAll();

    void delete(String key);

    long size();
}