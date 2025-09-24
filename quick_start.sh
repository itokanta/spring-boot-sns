#!/bin/bash

# ====================================================
# Spring Boot SNS - クイックスタートスクリプト
# ====================================================
#
# このスクリプトはmacOS/Linux環境でのワンクリックセットアップ用です
# 実行方法: chmod +x quick_start.sh && ./quick_start.sh
#

set -e  # エラー時に停止

echo "🚀 Spring Boot SNS - クイックスタートスクリプト"
echo "=================================================="

# 色付きメッセージ用の関数
print_success() {
    echo "✅ $1"
}

print_error() {
    echo "❌ $1"
}

print_info() {
    echo "ℹ️  $1"
}

print_warning() {
    echo "⚠️  $1"
}

# PostgreSQLが起動しているかチェック
check_postgresql() {
    print_info "PostgreSQLの動作確認中..."
    
    if command -v psql >/dev/null 2>&1; then
        print_success "PostgreSQLがインストールされています"
    else
        print_error "PostgreSQLがインストールされていません"
        print_info "以下のコマンドでインストールしてください："
        echo "  macOS: brew install postgresql && brew services start postgresql"
        echo "  Ubuntu: sudo apt-get install postgresql"
        exit 1
    fi
    
    # PostgreSQLサービスの起動確認
    if pg_isready -h localhost -p 5432 >/dev/null 2>&1; then
        print_success "PostgreSQLが起動しています"
    else
        print_warning "PostgreSQLが起動していません。起動を試みます..."
        if command -v brew >/dev/null 2>&1; then
            brew services start postgresql
            sleep 3
            if pg_isready -h localhost -p 5432 >/dev/null 2>&1; then
                print_success "PostgreSQLを起動しました"
            else
                print_error "PostgreSQLの起動に失敗しました"
                exit 1
            fi
        else
            print_error "PostgreSQLを手動で起動してから再実行してください"
            exit 1
        fi
    fi
}

# データベースセットアップ
setup_database() {
    print_info "データベースをセットアップ中..."
    
    if psql -U postgres -f database_setup.sql >/dev/null 2>&1; then
        print_success "データベースセットアップ完了"
    else
        print_error "データベースセットアップに失敗しました"
        print_info "手動でセットアップしてください："
        echo "  psql -U postgres -f database_setup.sql"
        exit 1
    fi
}

# データベース動作確認
verify_database() {
    print_info "データベースの動作確認中..."
    
    if psql -U sns_user -d sns_db -f verify_database.sql >/dev/null 2>&1; then
        print_success "データベース動作確認完了"
    else
        print_warning "データベース確認で問題が発生しましたが、継続します"
    fi
}

# Gradleビルド
build_application() {
    print_info "アプリケーションをビルド中..."
    
    if ./gradlew build -x test >/dev/null 2>&1; then
        print_success "ビルド完了"
    else
        print_error "ビルドに失敗しました"
        print_info "詳細なエラーは以下で確認してください："
        echo "  ./gradlew build"
        exit 1
    fi
}

# Spring Bootアプリケーション起動
start_application() {
    print_info "Spring Bootアプリケーションを起動中..."
    print_warning "アプリケーションが起動したら Ctrl+C で停止できます"
    print_info "ブラウザで http://localhost:8080/ にアクセスしてください"
    
    echo ""
    echo "🌐 ブラウザでアクセス: http://localhost:8080/"
    echo "📋 新規投稿: http://localhost:8080/new"
    echo ""
    
    ./gradlew bootRun
}

# メイン処理
main() {
    echo ""
    check_postgresql
    echo ""
    setup_database
    echo ""
    verify_database
    echo ""
    build_application
    echo ""
    print_success "セットアップ完了！アプリケーションを起動します..."
    echo ""
    start_application
}

# スクリプト実行
main

print_success "Spring Boot SNSアプリケーションのセットアップが完了しました！"
