package translation.ctrl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import translation.config.MongoConfig;
import translation.model.LanguageObject;
import translation.model.Translation;
import translation.service.ITranslationService;
import translation.service.TranslationService;

import java.util.*;

@RestController
@Slf4j
public class TranslationCtrl {

    @Autowired
    ITranslationService translationService;

    // Adds one or more translations with key and target languages idempotently.
    // If no value (key or target language) exists it is created. If value exists it is overwritten.
    @PostMapping(value = {"/create"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody Translation translation) {
        log.info("/create");
        Optional<Translation> existing = translationService.findByKey(translation.getKey());
        if (!existing.isPresent()){
            log.info("new");
            translationService.create(translation);
        }
        else {
            log.info("existing");
            for (Map.Entry<String, String> entry: translation.getTls().entrySet()){
                existing.get().getTls().put(entry.getKey(), entry.getValue());
            }
            translationService.create(existing.get());
        }

    }

    // Adds a target language with key, specified by URL lang code
    // If no value (key or target language) exists it is created. If value exists it is overwritten.
    @PostMapping(value = {"/add/{code}"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@PathVariable String code, @RequestBody LanguageObject languageObject) {
        log.info("/add/" + code);
        Optional<Translation> existing = translationService.findByKey(languageObject.getKey());
        if (!existing.isPresent()){
            log.info("new");
            Map<String, String> tls = new HashMap<>();
            tls.put(code, languageObject.getTl());
            Translation translation = new Translation(languageObject.getKey(), tls);
            translationService.create(translation);
        }
        else {
            log.info("existing ");
            existing.get().getTls().put(code, languageObject.getTl());
            translationService.create(existing.get());
        }

    }

    // Simple method for adding by URL. Be careful with language and URL encoding.
    // If no value (key or target language) exists it is created. If value exists it is overwritten.
    @GetMapping(value = {"/add/{code}/{key}/{tl}"})
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@PathVariable String code, @PathVariable String key, @PathVariable String tl) {
        log.info("/add/" + code + "/" + key + "/" + tl);
        Optional<Translation> existing = translationService.findByKey(key);
        if (!existing.isPresent()){
            log.info("new");
            Map<String, String> tls = new HashMap<>();
            tls.put(code, tl);
            Translation translation = new Translation(key, tls);
            translationService.create(translation);
        }
        else {
            log.info("exist");
            existing.get().getTls().put(code, tl);
            translationService.create(existing.get());
        }
    }

    // Remove key (and any translations) from db
    @DeleteMapping(value = "/delete/{key}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity delete(@PathVariable String key) {
        log.info("/delete/" + key);
        Optional<Translation> existing = translationService.findByKey(key);
        if (!existing.isPresent()){
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        else {
            translationService.delete(key);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    // Remove specified translation from key
    @DeleteMapping(value = "/delete-language/{code}/{key}")
    public ResponseEntity deleteLang(@PathVariable String code, @PathVariable String key) {
        log.info("/delete/" + code + "/" + key);
        Optional<Translation> existing = translationService.findByKey(key);
        if (!existing.isPresent()){
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        else {
            existing.get().getTls().remove(code);
            translationService.create(existing.get());
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = {"/{code}/{key}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object getTargetLanguage(@PathVariable String code, @PathVariable String key) {
        log.info("/" + code + "/" + key + "/");
        Optional<Translation> existing = translationService.findByKey(key);
        if (!existing.isPresent()){
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        else {
            log.info("exist");
            if (existing.get().getTls().containsKey(code)) {
                return new LanguageObject(key, existing.get().getTls().get(code));
            }
            else {
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            }
        }
    }

    @GetMapping(value = {"/source/{code}/{srcCode}/{srcTl}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object getTargetFromAnother(@PathVariable String code, @PathVariable String srcCode, @PathVariable String srcTl) {
        log.info("/" + code + "/" + srcCode + "/" + srcTl);
        List<Translation> all = translationService.findAll();
        log.info("all: " + all.size());
        for (Translation translation : all){
            if (translation.getTls().get(srcCode).equals(srcTl)){
                log.info("key: " + translation.getKey());
                return new LanguageObject(translation.getKey(), translation.getTls().get(code));
            }
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = {"/maxlength/{key}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object getMaxLength(@PathVariable String key) {
        log.info("/maxlength/" + key + "/");
        Optional<Translation> existing = translationService.findByKey(key);
        if (!existing.isPresent()){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        else {
            log.info("exist");
            int maxLength = 0;
            for (Map.Entry<String, String> entry : existing.get().getTls().entrySet()){
                if (maxLength < entry.getValue().length()){
                    maxLength = entry.getValue().length();
                }
            }
            return new LanguageObject(key, "" + maxLength);
        }
    }

    @GetMapping(value = {"/allkeys", "/allkeys/{code}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> getAllKeys(@PathVariable(value = "code", required = false) Optional<String> code) {
        log.info("/allkeys/");
        List<Translation> all = translationService.findAll();
        List<String> allKeys = new ArrayList<>();
        for (Translation translation : all){
            if (code.isEmpty()){
                allKeys.add(translation.getKey());
            } else if (translation.getTls().containsKey(code.get())) {
                allKeys.add(translation.getKey());
            }
        }
        return allKeys;
    }

    @GetMapping(value = {"/alltls", "/alltls/{code}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Translation> getAllTls(@PathVariable(value = "code", required = false) Optional<String> code) {
        log.info("/alltls/");
        List<Translation> all = translationService.findAll();
        if (code.isPresent()) {
            all.removeIf(tl -> !tl.getTls().containsKey(code.get()));
        }
        return all;
    }

    @Autowired
    MongoConfig mongoConfig;

    @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> props() {
        Map<String, String> targetObject = new HashMap<>();
        targetObject.put("dbname", mongoConfig.getDatabaseName());
        targetObject.put("size", "" + translationService.size());
        return targetObject;
    }

}
