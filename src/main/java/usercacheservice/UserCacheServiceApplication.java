package usercacheservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class UserCacheServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserCacheServiceApplication.class, args);
    }

}
