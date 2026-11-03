# api-todo core

ToDo 用 REST API（Spring Boot）。DB 連携は未実装で、コントローラーは固定値を返すスタブ。

## ソースマップ
- ベースパッケージ: `dev.tminasen.api_todo`（`todoapi` ではない。Initializr がハイフンを `_` に置換した）
- `todo/`: リクエスト・レスポンス DTO（record）と `TodoController`（`/todos`）
- `openapi/`: `RecordNullabilityCustomizer`（JSpecify `@Nullable` を OpenAPI 3.1 に反映）と、record をスキャンして Bean を作る `OpenApiConfig`
- 設定: `src/main/resources/application.properties`（yml ではない）
- DB マイグレーション（`db/migration`）とエンティティはまだない

## 不変条件
- null 安全を3層で守る。コンパイル時（NullAway）、実行時（Bean Validation）、OpenAPI（required / null 許容）。DTO を追加・変更したら3層すべてに影響する
  - コンパイル時の設定と `@NullMarked` のルール: `mem:conventions`
  - OpenAPI の補正ルールと制約: `mem:openapi/nullability`
- バージョン一覧と、選定の根拠: `mem:tech_stack`
- よく使うコマンドと、macOS / JDK の注意点: `mem:suggested_commands`
- 作業完了時に実行するコマンド: `mem:task_completion`
