package translation.config;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {


    @Value("${dbName}")
    private String dbname;

    @Value("${host}")
    private String host;

    @Value("${port}")
    private int port;

    @Override
    public String getDatabaseName() {
        return dbname;
    }

    @Override
    public MongoClient mongoClient() {
        return new MongoClient(host, port);
    }

}