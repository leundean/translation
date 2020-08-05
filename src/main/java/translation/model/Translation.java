package translation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Translation {

    @Id
    private String key;
    private Map<String, String> tls;

}
