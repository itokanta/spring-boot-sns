-- ====================================================
-- Spring Boot SNS - PostgreSQL データベースセットアップ
-- ====================================================
-- 
-- このスクリプトはPostgreSQLに管理者権限でログインして実行してください
-- 実行方法: psql -U postgres -f database_setup.sql
--

-- データベースとユーザーが既に存在する場合は削除（開発環境用）
-- 本番環境では注意して使用してください
DROP DATABASE IF EXISTS sns_db;
DROP USER IF EXISTS sns_user;

-- SNSアプリケーション用のユーザーを作成
CREATE USER sns_user WITH 
    PASSWORD 'sns_password'
    CREATEDB         -- データベース作成権限
    LOGIN;           -- ログイン権限

-- SNSアプリケーション用のデータベースを作成
CREATE DATABASE sns_db 
    WITH 
    OWNER sns_user           -- 所有者をsns_userに設定
    ENCODING 'UTF8'          -- 文字エンコーディング
    LC_COLLATE = 'C'        -- 照合順序
    LC_CTYPE = 'C'          -- 文字分類
    TEMPLATE template0;      -- テンプレート

-- データベースへの接続
\c sns_db

-- スキーマに対する権限付与（PostgreSQL 15以降で必要）
GRANT ALL ON SCHEMA public TO sns_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO sns_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO sns_user;

-- 将来作成されるオブジェクトに対する権限も付与
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO sns_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO sns_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO sns_user;

-- 作成確認用クエリ
\echo '=== データベース作成確認 ==='
SELECT datname, datowner, encoding FROM pg_database WHERE datname = 'sns_db';

\echo '=== ユーザー作成確認 ==='
SELECT usename, usecreatedb FROM pg_user WHERE usename = 'sns_user';

\echo '=== セットアップ完了 ==='
\echo 'データベース: sns_db'
\echo 'ユーザー: sns_user'
\echo 'パスワード: sns_password'
\echo '接続確認: psql -U sns_user -d sns_db -h localhost'
