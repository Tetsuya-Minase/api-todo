# Conventions

## null 安全
- main の各パッケージに `package-info.java` と `@NullMarked` を置く。パッケージを新しく作ったら必ず追加する（NullAway は `OnlyNullMarked` なので、付け忘れたパッケージは検査されない）
- `@Nullable`（JSpecify）は TYPE_USE なので、型の直前に書く（`@Nullable String note`）
- NullAway は `compileJava` だけで ERROR。`compileTestJava` では Error Prone を無効にしている（テストは main と同じパッケージにあり、`@NullMarked` が及んで過剰に反応するため）

## DTO
- record で書く。命名はリクエストが `*Request`、レスポンスが `*Response`。OpenAPI の補正はこの接尾辞で判定するので、命名は守る（`mem:openapi/nullability`）
- リクエストの非 null フィールドには `@NotNull`、文字列なら `@NotBlank` を付ける。コントローラーでは `@Valid @RequestBody` で受ける
- エラーは Problem Details（`spring.mvc.problemdetails.enabled=true`、`application/problem+json`）

## テスト
- TDD（テストを先に書く）。名前は `should_<期待>_when_<条件>`。`@Nested` でまとめる。本文は Given / When / Then
- Web のテストは `@SpringBootTest` + `@AutoConfigureMockMvc` + `@ActiveProfiles("nodb")` + `MockMvcTester`
  - `nodb` プロファイル（`src/test/resources/application-nodb.properties`）は DataSource / Hibernate JPA / Flyway の自動構成を外し、Docker なしで動く
- Testcontainers を使うテストには `@Testcontainers(disabledWithoutDocker = true)` を付ける
- OpenAPI 3.1 の `type` は、型が1つなら文字列、複数なら配列で出力される。テストでは両方の形を扱う
