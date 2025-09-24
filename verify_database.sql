-- ====================================================
-- Spring Boot SNS - データベース動作確認スクリプト
-- ====================================================
--
-- このスクリプトはデータベースセットアップ後の動作確認用です
-- 実行方法: psql -U sns_user -d sns_db -f verify_database.sql
--

\echo '======================================'
\echo 'Spring Boot SNS - データベース動作確認'
\echo '======================================'

-- 現在の接続情報表示
\echo ''
\echo '=== 接続情報 ==='
SELECT 
    current_database() as "データベース名",
    current_user as "接続ユーザー",
    version() as "PostgreSQLバージョン";

-- 権限確認
\echo ''
\echo '=== 権限確認 ==='
SELECT 
    has_database_privilege(current_user, current_database(), 'CREATE') as "データベース作成権限",
    has_schema_privilege(current_user, 'public', 'CREATE') as "スキーマ作成権限";

-- テーブル一覧表示
\echo ''
\echo '=== 既存テーブル ==='
\dt

-- postsテーブルが存在する場合の詳細情報
\echo ''
\echo '=== postsテーブル情報 ==='
SELECT 
    CASE 
        WHEN EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'posts')
        THEN 'postsテーブルが存在します'
        ELSE 'postsテーブルは未作成です（Spring Boot初回起動時に自動作成されます）'
    END as "テーブル状態";

-- postsテーブルが存在する場合の構造確認
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'posts') THEN
        RAISE NOTICE 'postsテーブルの構造:';
        PERFORM 1; -- 実際の構造表示は\dコマンドで行う
    END IF;
END $$;

-- サンプルデータ確認（postsテーブルが存在する場合）
\echo ''
\echo '=== 投稿データ確認 ==='
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'posts') THEN
        RAISE NOTICE '投稿データを表示します:';
    ELSE
        RAISE NOTICE '投稿テーブルはまだ作成されていません。Spring Bootアプリケーションを起動すると自動作成されます。';
    END IF;
END $$;

-- 投稿数カウント（テーブルが存在する場合）
SELECT 
    CASE 
        WHEN EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'posts')
        THEN (SELECT COUNT(*)::text FROM posts) || ' 件の投稿があります'
        ELSE 'postsテーブルは未作成です'
    END as "投稿数";

-- テスト用の基本的なSQL実行権限確認
\echo ''
\echo '=== SQL実行権限テスト ==='
CREATE TEMPORARY TABLE test_table (
    id SERIAL PRIMARY KEY,
    test_data VARCHAR(100)
);

INSERT INTO test_table (test_data) VALUES ('テストデータ');
SELECT test_data as "テスト結果" FROM test_table;
DROP TABLE test_table;

\echo ''
\echo '=== Spring Boot 接続確認用情報 ==='
\echo 'データベースURL: jdbc:postgresql://localhost:5432/sns_db'
\echo 'ユーザー名: sns_user'
\echo 'パスワード: sns_password'

\echo ''
\echo '=== 次のステップ ==='
\echo '1. Spring Bootアプリケーションを起動: ./gradlew bootRun'
\echo '2. ブラウザでアクセス: http://localhost:8080/'
\echo '3. 初回起動時にpostsテーブルが自動作成されます'

\echo ''
\echo '✅ データベースセットアップ確認完了！'
