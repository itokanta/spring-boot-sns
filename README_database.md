# 📊 Spring Boot SNS - データベースセットアップガイド

PostgreSQLデータベースのセットアップ手順を説明します。

## 🚀 セットアップ手順

### 1. PostgreSQLのインストール確認

```bash
# PostgreSQLがインストールされているか確認
psql --version

# インストールされていない場合
# macOS (Homebrew)
brew install postgresql
brew services start postgresql

# Windows
# PostgreSQL公式サイトからインストーラーをダウンロード
# https://www.postgresql.org/download/windows/

# Docker使用の場合
docker run --name postgres-sns \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:15
```

### 2. データベースとユーザーの作成

#### 方法1: SQLスクリプト実行（推奨）

```bash
# プロジェクトルートで実行
psql -U postgres -f database_setup.sql
```

#### 方法2: 手動実行

```bash
# PostgreSQLに管理者でログイン
psql -U postgres

# 以下のコマンドを順次実行
DROP DATABASE IF EXISTS sns_db;
DROP USER IF EXISTS sns_user;

CREATE USER sns_user WITH PASSWORD 'sns_password' CREATEDB LOGIN;

CREATE DATABASE sns_db 
  WITH OWNER sns_user 
  ENCODING 'UTF8' 
  LC_COLLATE = 'C' 
  LC_CTYPE = 'C' 
  TEMPLATE template0;

\c sns_db
GRANT ALL ON SCHEMA public TO sns_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO sns_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO sns_user;

\q
```

### 3. 接続確認

```bash
# 作成したユーザーでデータベースに接続
psql -U sns_user -d sns_db -h localhost

# 接続成功後、以下で確認
\dt  # テーブル一覧（最初は空）
\q   # 終了
```

## 🔧 Spring Boot設定

`src/main/resources/application.properties` の設定内容：

```properties
# データベース接続設定
spring.datasource.url=jdbc:postgresql://localhost:5432/sns_db
spring.datasource.username=sns_user
spring.datasource.password=sns_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate設定
spring.jpa.hibernate.ddl-auto=create-drop  # 開発用
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

## 🚦 アプリケーション起動

```bash
# プロジェクトルートで実行
./gradlew bootRun

# 起動成功ログの確認例：
# Hibernate: create table posts (...)
# Started SpringBootSnsApplication in X.XXX seconds
```

## 🌐 ブラウザでアクセス

```
http://localhost:8080/
```

## 🔍 トラブルシューティング

### よくあるエラーと対処法

#### 1. `role "sns_user" does not exist`

**原因**: データベースユーザーが作成されていない

**対処法**:
```bash
psql -U postgres -c "CREATE USER sns_user WITH PASSWORD 'sns_password' CREATEDB LOGIN;"
```

#### 2. `database "sns_db" does not exist`

**原因**: データベースが作成されていない

**対処法**:
```bash
psql -U postgres -c "CREATE DATABASE sns_db WITH OWNER sns_user;"
```

#### 3. `Connection refused`

**原因**: PostgreSQLサービスが起動していない

**対処法**:
```bash
# macOS
brew services start postgresql

# Windows
# サービス管理からPostgreSQLを開始

# Docker
docker start postgres-sns
```

#### 4. `authentication failed`

**原因**: パスワードが間違っている

**対処法**:
```bash
psql -U postgres -c "ALTER USER sns_user WITH PASSWORD 'sns_password';"
```

### 権限エラーの場合

```sql
-- PostgreSQLに管理者でログインして実行
\c sns_db
GRANT ALL PRIVILEGES ON DATABASE sns_db TO sns_user;
GRANT ALL ON SCHEMA public TO sns_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO sns_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO sns_user;
```

## 📊 データベース状態確認

```sql
-- 投稿テーブルの確認
\d posts

-- 投稿データの確認
SELECT * FROM posts ORDER BY created_at DESC;

-- 投稿数の確認
SELECT COUNT(*) FROM posts;
```

## 🗑️ クリーンアップ（開発時）

```sql
-- 全投稿削除
DELETE FROM posts;

-- テーブル削除
DROP TABLE IF EXISTS posts;

-- データベースとユーザー削除
DROP DATABASE IF EXISTS sns_db;
DROP USER IF EXISTS sns_user;
```

## 🔄 本番環境での注意事項

1. **DDL設定変更**: `spring.jpa.hibernate.ddl-auto=validate` または `update`
2. **強力なパスワード**: デフォルトパスワードを変更
3. **接続プール**: HikariCPの設定調整
4. **バックアップ**: 定期的なデータベースバックアップ

---

**Spring Boot SNSアプリケーションのデータベース環境が正常にセットアップされました！** 🎉
