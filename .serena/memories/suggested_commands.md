# Suggested commands

- JDK は SDKMAN の Temurin 25。ログインしていないシェルでは PATH に入らないので、
  `export JAVA_HOME=~/.sdkman/candidates/java/current` を先に実行する
- ビルドとテスト: `./gradlew build`（Docker がなければ Testcontainers のテストはスキップされる）
- テストを絞る: `./gradlew test --tests '*OpenApiNullabilityTest'`
- 起動: `./gradlew bootRun`（DevTools と docker-compose 連携で Postgres が起動する。Docker が必要）
- OpenAPI: 起動後に `/v3/api-docs` と `/swagger-ui.html`
- macOS: `sed -i ''`（BSD sed）。`timeout` コマンドはない
