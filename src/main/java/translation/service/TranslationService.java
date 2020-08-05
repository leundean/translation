package translation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import translation.TranslationRepository;
import translation.model.Translation;

import java.util.List;
import java.util.Optional;

@Service
public class TranslationService implements ITranslationService {
     
    @Autowired
    TranslationRepository tRep;

    public void create(Translation translation) {
        tRep.save(translation);
    }

    public Optional<Translation> findByKey(String key){
        return tRep.findByKey(key);
    }

    public List<Translation> findAll() {
        return tRep.findAll();
    }

    public void delete(String key) {
        tRep.deleteById(key);
    }

    public long size(){
        return tRep.count();
    }
}