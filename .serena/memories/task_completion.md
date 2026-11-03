# Task completion

- `export JAVA_HOME=~/.sdkman/candidates/java/current && ./gradlew build`（NullAway の ERROR があるとコンパイルで失敗する）
- Testcontainers のテストは Docker がないとスキップされ、build は成功してしまう。DB に関わる変更をしたら、Docker を起動して実行する
- カバレッジは 80% 以上が基準。ただしビルドに JaCoCo は組み込んでいない（MTH-45）
- フォーマッターとリンターは導入していない
- コミットと push はユーザーの明示的な承認が必要
