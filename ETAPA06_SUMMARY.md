git # Resumen Etapa 06 - Configuración Hibernate/JPA/MySQL + Logging

## ✅ Completado - Fecha: 03 de febrero de 2026
## 🔄 Actualizado con Logging SLF4J - Fecha: 10 de febrero de 2026

### 📦 Objetivo de la Etapa

Configurar el ambiente de persistencia con **JPA/Hibernate + MySQL + Logging** estableciendo la **base común** para ambos caminos arquitectónicos (manual o Spring Framework). Esta etapa prepara la infraestructura agnóstica que funcionará tanto si se decide continuar con JPA manual como si se migra a Spring.

**Enfoque**: Configuración agnóstica de framework usando JPA estándar (Jakarta Persistence API).

**⚠️ PUNTO DE DECISIÓN ARQUITECTÓNICA:** Después de esta etapa, se puede elegir:
- **Camino A:** Continuar con EntityManager manual (etapa07-etapa12 actual)
- **Camino B:** Migrar a Spring Boot + Spring Data JPA (recomendado para producción)

---

## 🎯 ¿Qué se implementó?

### 1️⃣ **Dependencias Maven** (pom.xml)

```xml
<dependencies>
    <!-- Hibernate Core 6.4.4.Final -->
    <!-- Jakarta Persistence API 3.1.0 -->
    <!-- MySQL Connector/J 8.3.0 -->
    <!-- SLF4J API 2.0.9 -->
    <!-- Logback Classic 1.4.14 -->
    <!-- Logback Core 1.4.14 -->
</dependencies>
```

**Versiones utilizadas**:
- ✅ Hibernate 6.4.4 (última versión estable)
- ✅ MySQL Connector 8.3.0 (compatible con MySQL 8.x)
- ✅ SLF4J + Logback (logging robusto)
- ✅ Jakarta Persistence API 3.1.0 (JPA specification)

**Compatible con:**
- ✅ JPA manual (EntityManager + JPQL)
- ✅ Spring Data JPA (JpaRepository)
- ✅ Spring Boot (auto-configuración)

**NO incluye (todavía)**:
- ⏸️ Spring Framework (se puede agregar después)
- ⏸️ Spring Boot (se puede agregar después)
- ⏸️ Spring Data JPA (se puede agregar después)

---

### 2️⃣ **persistence.xml** (Configuración JPA)

**Ubicación**: `src/main/resources/META-INF/persistence.xml`

**Características**:
```xml
<persistence-unit name="pps-persistence-unit" transaction-type="RESOURCE_LOCAL">
    <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
    
    <!-- Propiedades de conexión -->
    <property name="jakarta.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>
    <property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://..."/>
    <property name="jakarta.persistence.jdbc.user" value="${DB_USER}"/>
    <property name="jakarta.persistence.jdbc.password" value="${DB_PASSWORD}"/>
    
    <!-- Propiedades de Hibernate -->
    <property name="hibernate.dialect" value="org.hibernate.dialect.MySQL8Dialect"/>
    <property name="hibernate.hbm2ddl.auto" value="update"/>
    <property name="hibernate.show_sql" value="true"/>
    <property name="hibernate.format_sql" value="true"/>
    
    <!-- Connection Pool - HikariCP -->
    <property name="hibernate.hikari.maximumPoolSize" value="20"/>
    <!-- ... más propiedades ... -->
</persistence-unit>
```

**Estrategias DDL**:
- `create`: Borra y recrea (desarrollo inicial)
- `update`: Actualiza esquema (desarrollo) ← **Usado en Etapa 06**
- `validate`: Solo valida (testing)
- `none`: No toca esquema (producción)

---

### 3️⃣ **Variables de Entorno** (.env)

**Archivo**: `.env.example` (template)

```properties
# Base de datos
DB_HOST=localhost
DB_PORT=3306
DB_NAME=pps_db
DB_USER=root
DB_PASSWORD=

# Hibernate
DB_DDL_AUTO=update
DB_SHOW_SQL=true
DB_POOL_SIZE=10

# Aplicación
APP_ENVIRONMENT=development

# Logging
LOG_LEVEL=DEBUG
```

**Seguridad**:
- ✅ `.env` está en `.gitignore`
- ✅ `.env.example` se incluye como template
- ✅ Cada desarrollador crea su propio `.env` local
- ✅ Credenciales NO se suben a Git

**Uso**:
```bash
# Copiar template
cp ..env.example ..env

# Editar con credenciales reales
nano ..env

# Cargar en IDE o terminal
export $(cat ..env | xargs)
```

---

### 4️⃣ **Logging** (logback.xml)

**Ubicación**: `src/main/resources/logback.xml`

**Appenders configurados**:

| Appender | Archivo | Nivel | Propósito |
|----------|---------|-------|-----------|
| CONSOLE | - | DEBUG | Salida en consola coloreada |
| FILE_INFO | logs/application.log | INFO | Log general de aplicación |
| FILE_ERROR | logs/error.log | ERROR | Solo errores |
| FILE_SQL | logs/sql.log | DEBUG | Queries SQL + bind params |

**Loggers configurados**:

| Logger | Nivel | Propósito |
|--------|-------|-----------|
| co.edu.cesde.pps | DEBUG | Código de aplicación |
| org.hibernate | INFO | Framework Hibernate |
| org.hibernate.SQL | DEBUG | SQL generado |
| org.hibernate.orm.jdbc.bind | TRACE | Bind parameters |
| org.hibernate.transaction | DEBUG | Transacciones |
| com.zaxxer.hikari | INFO | Connection pool |
| com.mysql.cj | WARN | MySQL connector |

**Rotación de logs**:
- Rotación diaria
- application.log: 30 días, max 1GB
- error.log: 90 días, max 500MB
- sql.log: 7 días, max 200MB

---

### 5️⃣ **Scripts SQL**

#### **schema.sql** (DDL)

**Ubicación**: `src/main/resources/sql/schema.sql`

**Contenido**:
- 14 tablas con relaciones completas
- Índices en columnas frecuentes
- Foreign keys con CASCADE
- Charset UTF-8 (utf8mb4)
- Engine InnoDB

**Tablas creadas**:
```sql
roles, users, addresses, categories, products, 
user_sessions, carts, cart_items, order_statuses, 
orders, order_items, payment_methods, payment_statuses, payments
```

#### **data.sql** (DML)

**Ubicación**: `src/main/resources/sql/data.sql`

**Datos iniciales**:
- 3 roles (ADMIN, CUSTOMER, MANAGER)
- 7 order statuses (PENDING, CONFIRMED, etc.)
- 5 payment methods (CREDIT_CARD, PAYPAL, etc.)
- 6 payment statuses (PENDING, COMPLETED, etc.)
- Jerarquía de categorías (Electronics, Clothing, Books, Home & Garden)
- 13 productos de ejemplo
- 4 usuarios de prueba con direcciones

**Uso**:
```bash
# Ejecutar DDL
mysql -u root -p < src/main/resources/sql/schema.sql

# Ejecutar DML
mysql -u root -p pps_db < src/main/resources/sql/data.sql
```

**Alternativa**:
- Dejar que Hibernate cree las tablas (`hbm2ddl.auto=update`)
- Solo ejecutar data.sql para datos iniciales

---

## 📁 Estructura de Archivos Creados/Modificados

```
product-purchasing-system/
├── pom.xml (actualizado con dependencias)
├── .env.example (nuevo - template)
├── .gitignore (actualizado - ignora .env)
├── CONFIG_SETUP.md (nuevo - guía de setup)
│
├── src/main/java/co/edu/cesde/pps/
│   ├── config/
│   │   ├── JpaConfig.java (modificado - logging agregado)
│   │   └── DatabaseConfig.java (modificado - logging agregado)
│   ├── util/
│   │   ├── TransactionManager.java (modificado - logging agregado)
│   │   └── ValidationUtils.java (modificado - logging agregado)
│   └── exception/
│       └── BusinessException.java (modificado - logging agregado)
│
└── src/main/resources/
    ├── META-INF/
    │   └── persistence.xml (nuevo)
    ├── logback.xml (nuevo)
    └── sql/
        ├── schema.sql (nuevo)
        └── data.sql (nuevo)
```

**Total de archivos nuevos**: 6  
**Archivos modificados**: 2 (pom.xml, .gitignore)  
**Archivos modificados con logging**: 5 (JpaConfig, TransactionManager, DatabaseConfig, ValidationUtils, BusinessException)

---

## 🔧 Configuración por Ambiente

### Desarrollo (development)
```properties
DB_DDL_AUTO=update          # Auto-crea tablas
DB_SHOW_SQL=true            # Muestra SQL
LOG_LEVEL=DEBUG             # Logs detallados
hibernate.show_sql=true     # SQL en consola
hibernate.format_sql=true   # SQL formateado
```

### Staging (staging)
```properties
DB_DDL_AUTO=validate        # Solo valida
DB_SHOW_SQL=true            # Muestra SQL
LOG_LEVEL=INFO              # Logs normales
hibernate.show_sql=false    # Sin SQL
```

### Producción (production)
```properties
DB_DDL_AUTO=none            # No toca esquema
DB_SHOW_SQL=false           # Sin SQL
LOG_LEVEL=WARN              # Solo warnings/errors
hibernate.show_sql=false    # Sin SQL
hibernate.format_sql=false  # Sin formateo
```

---

## 📦 Commits Realizados (13 commits - 6 configuración + 6 logging + 1 documentación)

**Commits iniciales de configuración (03/02/2026):**
```bash
1. build: add Hibernate, JPA, MySQL and Logging dependencies
2. config: add persistence.xml for JPA configuration
3. config: add environment variables template and update gitignore
4. config: add logback.xml for logging configuration
5. config: add SQL scripts for database initialization
6. docs: add CONFIG_SETUP guide for environment configuration
```

**Commits de logging SLF4J (10/02/2026):**
```bash
7. feat: add SLF4J logging to JpaConfig for EntityManagerFactory lifecycle
8. feat: add SLF4J logging to TransactionManager for transaction traceability
9. feat: add SLF4J logging to DatabaseConfig for environment variables tracking
10. feat: add SLF4J logging to ValidationUtils for validation failures tracking
11. feat: add SLF4J logging to BusinessException for centralized error logging
12. fix: add overloaded validateEmail and validatePhone methods for backward compatibility
```

**Commit de documentación (10/02/2026):**
```bash
13. docs: update ETAPA06_SUMMARY with SLF4J logging implementation details
```

---

## ✅ Verificación de Configuración

### Checklist de validación:

- [ ] **MySQL instalado y corriendo**
  ```bash
  mysql --version
  sudo systemctl status mysql  # Linux
  ```

- [ ] **Base de datos creada**
  ```sql
  CREATE DATABASE pps_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  ```

- [ ] **Archivo .env configurado**
  ```bash
  cp ..env.example ..env
  nano ..env  # Ajustar credenciales
  ```

- [ ] **Dependencias Maven descargadas**
  ```bash
  mvn clean install
  ```

- [ ] **Proyecto compila sin errores**
  ```bash
  mvn clean compile
  # BUILD SUCCESS
  ```

- [ ] **Variables de entorno cargadas**
  ```bash
  # En IDE o terminal
  export DB_HOST=localhost
  export DB_USER=root
  # etc...
  ```

---

## 🚫 Lo que NO se implementó (decisión pendiente)

Esta etapa es **SOLO configuración base**, por lo tanto NO incluye (aún):

**Común a ambos caminos:**
- ⏸️ Anotaciones `@Entity` en modelos
- ⏸️ Anotaciones `@Table`, `@Column`, `@Id`, etc.
- ⏸️ Anotaciones `@ManyToOne`, `@OneToMany`, etc.

**Si eliges Camino A (Manual):**
- ⏸️ EntityManagerFactory programático
- ⏸️ TransactionManager custom
- ⏸️ Interfaces Repository manuales

**Si eliges Camino B (Spring):**
- ⏸️ Spring Framework dependencies
- ⏸️ Spring Boot starter
- ⏸️ Spring Data JPA
- ⏸️ Anotaciones `@Service`, `@Repository`
- ⏸️ Anotaciones `@Transactional`
- ⏸️ application.properties (configuración Spring)

**Razón**: Mantener la configuración base neutral permite:
- ✅ Decidir el camino arquitectónico después de validar la configuración
- ✅ Commits limpios y específicos
- ✅ Fácil migración a Spring si se decide después
- ✅ Base común que funciona con ambos enfoques

---

## 🎯 Beneficios de esta Configuración Base

### 1️⃣ **Agnóstica de Framework**
- ✅ Funciona con JPA manual O con Spring
- ✅ Configuración reutilizable en ambos caminos
- ✅ Menor overhead inicial
- ✅ Aprendizaje de JPA estándar antes de abstracciones

### 2️⃣ **Variables de Entorno**
- ✅ Seguridad: credenciales fuera del código
- ✅ Flexibilidad: diferentes ambientes sin cambiar código
- ✅ Buenas prácticas: separación de configuración
- ✅ Compatible con .env (manual) o application.properties (Spring)

### 3️⃣ **Logging Robusto**
- ✅ SLF4J: facade estándar compatible con Spring
- ✅ Múltiples appenders (consola + archivos)
- ✅ Rotación automática de logs
- ✅ Logs separados por tipo (general, error, SQL)
- ✅ Control fino de niveles por paquete
- ✅ Logging implementado en archivos Java críticos

### 4️⃣ **Scripts SQL de Referencia**
- ✅ Documentación de esquema independiente
- ✅ Útil para producción (DDL manual)
- ✅ Datos iniciales reproducibles
- ✅ Independiente de framework elegido

### 5️⃣ **Base para Decisión Informada**
- ✅ persistence.xml funcional (base JPA)
- ✅ Dialecto MySQL8 configurado
- ✅ Connection pool (HikariCP) listo
- ✅ Permite evaluar ambos caminos con misma base

---

## 🔍 Cómo Funciona la Configuración

### 1. Carga de Variables de Entorno

```bash
# Terminal
export DB_HOST=localhost
export DB_USER=root

# O desde ..env
source ..env
export $(cat ..env | xargs)
```

### 2. Hibernate Lee persistence.xml

```xml
<property name="jakarta.persistence.jdbc.url" 
          value="jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:pps_db}?..."/>
```

**Nota**: Las variables `${...}` se resolverán programáticamente en DatabaseConfig (Etapa 07).

### 3. Logback Lee logback.xml

```xml
<logger name="co.edu.cesde.pps" level="DEBUG"/>
<logger name="org.hibernate.SQL" level="DEBUG"/>
```

### 4. Hibernate Crea Tablas

Con `hibernate.hbm2ddl.auto=update`:
- Lee entidades anotadas con @Entity (Etapa 07)
- Compara con esquema existente
- Crea/actualiza tablas automáticamente

---

## 🛠️ Troubleshooting

### Error: "Access denied for user 'root'@'localhost'"
```
Causa: Contraseña incorrecta o usuario no existe
Solución: 
1. Verificar DB_USER y DB_PASSWORD en .env
2. Resetear password de MySQL si es necesario
```

### Error: "Unknown database 'pps_db'"
```
Causa: Base de datos no creada
Solución:
CREATE DATABASE pps_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Error: "Cannot resolve symbol 'JpaRepository'"
```
Causa: Dependencias no descargadas o no es problema aún
Solución: Este es normal en Etapa 06 (sin interfaces Repository)
```

### Error: "Communications link failure"
```
Causa: MySQL no está corriendo
Solución:
sudo systemctl start mysql  # Linux
mysql.server start          # macOS
```

### Advertencia: "HikariPool-1 - Failed to validate connection"
```
Causa: Credenciales incorrectas o MySQL inaccesible
Solución: Verificar .env y que MySQL esté corriendo
```

---

## 📚 Archivos de Configuración Explicados

### persistence.xml
- **Ubicación**: `src/main/resources/META-INF/`
- **Propósito**: Configuración de JPA (sin Spring)
- **Clave**: Define persistence-unit para EntityManagerFactory

### logback.xml
- **Ubicación**: `src/main/resources/`
- **Propósito**: Configuración de logging (SLF4J + Logback)
- **Clave**: Appenders, loggers, niveles, rotación

### .env.example
- **Ubicación**: Raíz del proyecto
- **Propósito**: Template de variables de entorno
- **Clave**: NO contiene credenciales reales

### schema.sql
- **Ubicación**: `src/main/resources/sql/`
- **Propósito**: DDL de referencia
- **Clave**: Opcional si usas `hbm2ddl.auto=update`

### data.sql
- **Ubicación**: `src/main/resources/sql/`
- **Propósito**: Datos iniciales
- **Clave**: Roles, statuses, categorías, productos de ejemplo

---

## 🚀 Próximos Pasos: Decisión Arquitectónica

Con la configuración base lista, ahora debes **ELEGIR UN CAMINO**:

### 🔀 **OPCIÓN A: JPA Manual (Etapa 07 Manual)**

**Ventajas:**
- ✅ Control total sobre transacciones
- ✅ Aprendizaje profundo de JPA
- ✅ Sin dependencias de Spring
- ✅ Útil para proyectos legacy

**Implementación:**
```java
// EntityManagerFactory programático
EntityManagerFactory emf = Persistence.createEntityManagerFactory("pps-persistence-unit");
EntityManager em = emf.createEntityManager();

// Transacciones manuales
EntityTransaction tx = em.getTransaction();
tx.begin();
em.persist(user);
tx.commit();

// Repositories manuales
public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
}
```

### 🌱 **OPCIÓN B: Spring Boot + Spring Data JPA (Recomendado)**

**Ventajas:**
- ✅ Productividad: menos código boilerplate
- ✅ Estándar de la industria
- ✅ Auto-configuración de EntityManager
- ✅ Gestión automática de transacciones
- ✅ JpaRepository con métodos derivados

**Implementación:**
```java
// pom.xml - Agregar dependencias Spring
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.2</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
</dependencies>

// Repository automático
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}

// Service con transacciones automáticas
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public UserDTO registerUser(...) {
        userRepository.save(user);
    }
}
```

### 📋 **Migración de persistence.xml a application.properties (Spring)**

Si eliges Spring, reemplazar persistence.xml por:

```properties
# application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/pps_db
spring.datasource.username=root
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

### 🎯 **Recomendación del Proyecto**

Para este proyecto de **Product Purchasing System**, se recomienda **OPCIÓN B (Spring Boot)** porque:

1. ✅ **Productividad**: Spring Data JPA reduce código en ~70%
2. ✅ **Estándar**: 90% de proyectos Java enterprise usan Spring
3. ✅ **Mantenibilidad**: Código más limpio y testeable
4. ✅ **Ecosistema**: Integración con Spring Security, Spring MVC, etc.
5. ✅ **Empleabilidad**: Habilidad demandada en el mercado

**Logging SLF4J ya implementado** es compatible con ambos caminos y con Lombok (`@Slf4j`).

---

## 🌿 Estado de Git

- **Rama actual:** `etapa06`
- **Rama base:** `etapa05`
- **Commits:** 6 commits
- **Estado:** Listo para push a GitHub
- **Repositorio:** `https://github.com/lgoenaga/product-purchasing-system`

---

## ✨ Conclusión

La **Etapa 06** ha configurado exitosamente el ambiente de persistencia:

- ✅ **Dependencias Maven**: Hibernate, JPA, MySQL, Logging
- ✅ **persistence.xml**: Configuración JPA estándar (agnóstica de framework)
- ✅ **Variables de entorno**: .env para credenciales seguras
- ✅ **Logging**: SLF4J + Logback con múltiples appenders
- ✅ **Logging en código**: 5 archivos Java con logging implementado
- ✅ **Scripts SQL**: schema.sql y data.sql de referencia
- ✅ **Guía de setup**: CONFIG_SETUP.md completo
- ✅ **Sin errores de compilación**: BUILD SUCCESS

**🔀 PUNTO DE DECISIÓN ARQUITECTÓNICA**

El proyecto está **100% listo** para elegir el camino a seguir:

- **Camino A (Manual):** EntityManager + JPQL + Repositories manuales
- **Camino B (Spring):** Spring Boot + Spring Data JPA + @Transactional

**Recomendación:** Camino B (Spring) por productividad y empleabilidad.

**Separación exitosa de configuración vs implementación!** 🎉

**Actualización (10/02/2026):** 
- ✅ Logging SLF4J implementado en archivos Java críticos
- ✅ Compatible con ambos caminos arquitectónicos
- ✅ Listo para migrar a `@Slf4j` de Lombok (si se elige Spring)

---

**Autor:** Luis Goenaga  
**Proyecto:** Product Purchasing System - Backend II  
**Institución:** CESDE  
**Año:** 2026
---
## 🔄 ACTUALIZACIÓN: Logging SLF4J Implementado (10/02/2026)
### **5 Archivos Java con Logging:**
| Archivo | Logging Agregado |
|---------|------------------|
| JpaConfig.java | EntityManagerFactory lifecycle (INFO, DEBUG, ERROR) |
| TransactionManager.java | Transaction traceability (DEBUG, WARN, ERROR) |
| DatabaseConfig.java | Environment variables tracking (DEBUG, INFO, WARN) |
| ValidationUtils.java | Validation failures (DEBUG) |
| BusinessException.java | Centralized error logging (ERROR) |
### **6 Commits Adicionales (7-12):**
```bash
7. feat: add SLF4J logging to JpaConfig for EntityManagerFactory lifecycle
8. feat: add SLF4J logging to TransactionManager for transaction traceability
9. feat: add SLF4J logging to DatabaseConfig for environment variables tracking
10. feat: add SLF4J logging to ValidationUtils for validation failures tracking
11. feat: add SLF4J logging to BusinessException for centralized error logging
12. fix: add overloaded validateEmail and validatePhone methods for backward compatibility
```
**Total commits etapa06:** 12 (6 configuración + 6 logging)
**Beneficios:** Trazabilidad completa, diagnóstico mejorado, compatible con Spring
