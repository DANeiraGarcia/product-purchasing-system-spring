# Configuración del Proyecto - Product Purchasing System

## Descripción
Este documento describe la configuración técnica del proyecto,
cómo arrancarlo localmente y los cambios realizados en la migración
de configuración manual a Spring Boot autoconfiguration.

---

## Stack de Configuración

| Componente | Tecnología |
|------------|------------|
| Framework | Spring Boot |
| ORM | Spring Data JPA / Hibernate |
| Base de datos | MySQL 8 |
| Variables de entorno | dotenv-java 5.2.2 |
| Configuración | application.yml |

---

## Arranque Local - Paso a Paso

1. Clonar el repositorio
```bash
git clone 
```

2. Copiar el archivo de variables de entorno
```bash
cp .env.example .env
```

3. Editar `.env` con tus valores locales
```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=pps_db
DB_USER=user_pps
DB_PASSWORD=TuPasswordSegura@2026
```

4. Crear la base de datos en MySQL
```sql
CREATE DATABASE pps_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

5. Correr la aplicación desde `PpsApplication` o con Maven
```bash
mvn spring-boot:run
```

---

## Variables de Entorno

| Variable | Descripción | Default |
|----------|-------------|---------|
| DB_HOST | Host del servidor MySQL | localhost |
| DB_PORT | Puerto de MySQL | 3306 |
| DB_NAME | Nombre de la base de datos | pps_db |
| DB_USER | Usuario de MySQL | user_pps |
| DB_PASSWORD | Contraseña de MySQL | *(vacío)* |
| DB_DDL_AUTO | Estrategia DDL de Hibernate | update |
| DB_SHOW_SQL | Mostrar SQL en logs | true |
| DB_POOL_SIZE | Tamaño del pool de conexiones | 10 |
| LOG_LEVEL | Nivel de logging | DEBUG |
| LOG_SQL_LEVEL | Nivel de logging SQL | DEBUG |
| LOG_SQL_BIND_LEVEL | Nivel de logging bind params | TRACE |

---

## Estrategias DDL por Ambiente

| Ambiente | DB_DDL_AUTO | DB_SHOW_SQL | LOG_LEVEL |
|----------|-------------|-------------|-----------|
| development | update | true | DEBUG |
| staging | validate | true | INFO |
| production | none | false | WARN |

---

## Flujo de Carga de Configuración
PpsApplication.main()
|
DotenvDevelopmentLoader.load()   ← carga .env a System.properties
|
SpringApplication.run()          ← Spring Boot arranca
|
application.yml                  ← lee ${VARIABLES} de System.properties
|
DataSource + JPA autoconfigured  ← Spring Boot configura todo

---

## Archivos Legado

Estos archivos fueron parte de la configuración manual anterior.
Se conservan como referencia histórica pero **no están en el flujo activo**.

| Archivo | Ubicación | Reemplazado por |
|---------|-----------|-----------------|
| `DatabaseConfig.java` | `config/` | `application.yml` |
| `JpaConfig.java` | `config/` | Spring Boot autoconfiguration |
| `TransactionManager.java` | `util/` | `@Transactional` de Spring |
| `persistence.xml` | `resources/META-INF/` | `application.yml` |

---

## Seguridad

- El archivo `.env` **nunca debe subirse a Git**
- `.env` está incluido en `.gitignore`
- Usar `.env.example` como plantilla sin valores reales
- En producción usar variables de entorno del servidor, no archivo `.env`

---

## Dependencias Clave

```xml
<!-- Variables de entorno -->
<dependency>
    <groupId>io.github.cdimascio</groupId>
    <artifactId>java-dotenv</artifactId>
    <version>5.2.2</version>
</dependency>

<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- MySQL Driver -->
<dependency>
    <groupId>com.mysql.cj</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>