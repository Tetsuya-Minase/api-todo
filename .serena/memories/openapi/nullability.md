# OpenAPI nullability

springdoc 3.1.1 は record コンポーネントの JSpecify `@Nullable` を読まない。そのため `RecordNullabilityCustomizer` で補正する。

## ルール
- スキーマ名とクラスの単純名が一致する record（`dev.tminasen.api_todo` 配下をスキャンしたもの）だけを補正する
- `@Nullable` なし → `required` に入れる
- `@Nullable` あり → null を許容する
  - 型を持つプロパティは `types` に `"null"` を足す
  - `$ref` など型を持たないプロパティは `oneOf: [元のスキーマ, {type: null}]` で包む。参照先のスキーマは書き換えない
  - `*Response` では、Jackson が null のキーも出力するので `required` にも入れる。それ以外（`*Request` など）では入れない
- `required` は重複させない。properties にないコンポーネントは無視する

## 制約（対応は YouTrack の MTH-42〜44）
- 単純名が重複すると、`toMap` が例外を投げて起動に失敗する。テストの入れ子 record もスキャン対象に入るので、本番 DTO と同じ名前を付けない
- プロパティはコンポーネント名で照合するので、`@JsonProperty` で名前を変えたものは補正されない
