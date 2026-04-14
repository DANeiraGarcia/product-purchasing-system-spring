package co.edu.cesde.pps.util;
import co.edu.cesde.pps.config.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @deprecated LEGADO - Ya no está en el flujo activo.
 * Spring Boot maneja las transacciones automáticamente
 * con @Transactional de org.springframework.transaction.annotation.
 * Esta clase se conserva como referencia histórica.
 */
@Deprecated
public final class TransactionManager {
    private static final Logger log = LoggerFactory.getLogger(TransactionManager.class);
    private TransactionManager() {
        throw new AssertionError("TransactionManager is a utility class and cannot be instantiated");
    }
    public static <T> T executeInTransaction(Function<EntityManager, T> operation) {
        EntityManager em = JpaConfig.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            log.debug("Starting transaction");
            tx.begin();
            T result = operation.apply(em);
            log.debug("Committing transaction");
            tx.commit();
            log.debug("Transaction committed successfully");
            return result;
        } catch (RuntimeException ex) {
            log.error("Transaction failed with RuntimeException: {}", ex.getMessage());
            log.debug("Rolling back transaction due to RuntimeException", ex);
            rollbackIfActive(tx);
            throw ex;
        } catch (Exception ex) {
            log.error("Transaction failed with Exception: {}", ex.getMessage());
            log.debug("Rolling back transaction due to Exception", ex);
            rollbackIfActive(tx);
            throw new RuntimeException("Transaction failed: " + ex.getMessage(), ex);
        } finally {
            if (em.isOpen()) {
                log.debug("Closing EntityManager");
                em.close();
            }
        }
    }
    public static void executeInTransaction(Consumer<EntityManager> operation) {
        executeInTransaction(em -> {
            operation.accept(em);
            return null;
        });
    }
    public static <T> T executeReadOnly(Function<EntityManager, T> operation) {
        log.debug("Executing read-only operation");
        EntityManager em = JpaConfig.createEntityManager();
        try {
            T result = operation.apply(em);
            log.debug("Read-only operation completed successfully");
            return result;
        } finally {
            if (em.isOpen()) {
                log.debug("Closing EntityManager from read-only operation");
                em.close();
            }
        }
    }
    private static void rollbackIfActive(EntityTransaction tx) {
        try {
            if (tx != null && tx.isActive()) {
                log.warn("Rolling back active transaction");
                tx.rollback();
                log.debug("Transaction rolled back successfully");
            }
        } catch (Exception e) {
            log.error("Rollback failed: {}", e.getMessage(), e);
        }
    }
}
