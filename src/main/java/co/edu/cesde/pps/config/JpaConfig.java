package co.edu.cesde.pps.config;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
/**
 * @deprecated LEGADO - Ya no está en el flujo activo.
 * Spring Boot autoconfigura el EntityManagerFactory automáticamente
 * desde application.yml usando Spring Data JPA.
 * Esta clase se conserva como referencia histórica.
 */
@Deprecated
public final class JpaConfig {
    private static final Logger log = LoggerFactory.getLogger(JpaConfig.class);
    private static final String PERSISTENCE_UNIT_NAME = "pps-persistence-unit";
    private static volatile EntityManagerFactory entityManagerFactory;
    private JpaConfig() {
        throw new AssertionError("JpaConfig is a utility class and cannot be instantiated");
    }
    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            synchronized (JpaConfig.class) {
                if (entityManagerFactory == null) {
                    log.info("Initializing EntityManagerFactory for persistence unit: {}", PERSISTENCE_UNIT_NAME);
                    entityManagerFactory = buildEntityManagerFactory();
                    log.info("EntityManagerFactory initialized successfully");
                }
            }
        }
        return entityManagerFactory;
    }
    public static EntityManager createEntityManager() {
        log.debug("Creating new EntityManager");
        EntityManager em = getEntityManagerFactory().createEntityManager();
        log.debug("EntityManager created successfully");
        return em;
    }
    public static void close() {
        synchronized (JpaConfig.class) {
            if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
                log.info("Closing EntityManagerFactory");
                entityManagerFactory.close();
                log.info("EntityManagerFactory closed successfully");
            }
            entityManagerFactory = null;
        }
    }
    private static EntityManagerFactory buildEntityManagerFactory() {
        try {
            log.debug("Building EntityManagerFactory with custom JDBC properties");
            Map<String, Object> props = new HashMap<>();
            String jdbcUrl = DatabaseConfig.getJdbcUrl();
            String jdbcUser = DatabaseConfig.getDbUser();
            log.debug("JDBC URL: {}", jdbcUrl);
            log.debug("JDBC User: {}", jdbcUser);
            props.put("jakarta.persistence.jdbc.url", jdbcUrl);
            props.put("jakarta.persistence.jdbc.user", jdbcUser);
            props.put("jakarta.persistence.jdbc.password", DatabaseConfig.getDbPassword());
            props.put("jakarta.persistence.jdbc.driver", DatabaseConfig.getDriverClassName());
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, props);
            log.info("EntityManagerFactory created with persistence unit: {}", PERSISTENCE_UNIT_NAME);
            return emf;
        } catch (Exception e) {
            log.error("Failed to create EntityManagerFactory: {}", e.getMessage(), e);
            throw new RuntimeException("Could not initialize JPA EntityManagerFactory", e);
        }
    }
}
