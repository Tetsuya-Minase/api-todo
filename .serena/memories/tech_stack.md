# Tech stack

- Java 25（Gradle toolchain）、Gradle 9.8.2（wrapper）、Kotlin DSL
- Spring Boot 4.1.2（webmvc, validation, data-jpa, flyway, actuator, docker-compose, devtools）。Jackson 3
- PostgreSQL（`compose.yaml` の `postgres:latest`。テストでは Testcontainers）
- springdoc-openapi-starter-webmvc-ui 3.1.1
  - Boot 4.1 に対応するのは 3.1 系（Boot 4.0 は 3.0 系、Boot 3.5 は 2.8/2.9 系）
  - `springdoc.api-docs.version=openapi_3_1`
- null 注釈: JSpecify 1.0.1（spring-core 経由で入るので、直接の依存はない）。`org.springframework.lang` や jakarta / javax の `@Nullable` は使わない
- Error Prone 2.51.0、NullAway 0.14.3、Gradle プラグイン `net.ltgt.errorprone` 5.1.1
  - プラグイン 5.x では `options.errorprone.enabled` が Property（`isEnabled` ではない）
- Lombok は使わない
