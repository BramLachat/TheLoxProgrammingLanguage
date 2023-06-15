package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

// The purpose of the Parser is to take a list of tokens as input
// and build an AST
public class Parser {

    private static class ParseError extends RuntimeException {}
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while(!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    private Stmt declaration() {
        try {
            if (currentTokenMatches(TokenType.VAR)) return varDeclaration();

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration() {
        Token name = consumeTokenOrThrow(TokenType.IDENTIFIER, "Expect variable name.");

        Expr initializer = null;
        if (currentTokenMatches(TokenType.EQUAL)) {
            initializer = expression();
        }

        consumeTokenOrThrow(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    private Stmt statement() {
        if (currentTokenMatches(TokenType.PRINT)) return printStatement();
        return expressionStatement();
    }

    private Stmt printStatement() {
        Expr value = expression();
        consumeTokenOrThrow(TokenType.SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Print(value);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consumeTokenOrThrow(TokenType.SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private Expr expression() {
        return equality();
    }

    private Expr equality() {
        Expr expr = comparison();

        // Example: a == b == c == d == e
        while (currentTokenMatches(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            /**
             * Since match() increases current to the next token if match is true,
             * previous() must be done to retrieve the just matched token.
             */
            Token operator = previousToken();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = term();

        while(currentTokenMatches(
                TokenType.GREATER,
                TokenType.GREATER_EQUAL,
                TokenType.LESS,
                TokenType.LESS_EQUAL)) {
            Token operator = previousToken();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while(currentTokenMatches(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previousToken();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
        Expr expr = unary();

        while(currentTokenMatches(TokenType.SLASH, TokenType.STAR)) {
            Token operator = previousToken();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (currentTokenMatches(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previousToken();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    private Expr primary() {
        if (currentTokenMatches(TokenType.FALSE)) return new Expr.Literal(false);
        if (currentTokenMatches(TokenType.TRUE)) return new Expr.Literal(true);
        if (currentTokenMatches(TokenType.NIL)) return new Expr.Literal(null);

        if (currentTokenMatches(TokenType.NUMBER, TokenType.STRING)) {
            return new Expr.Literal(previousToken().literal);
        }

        if (currentTokenMatches(TokenType.IDENTIFIER)) {
            return new Expr.Variable(previousToken());
        }

        if (currentTokenMatches(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consumeTokenOrThrow(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(getCurrentToken(), "Expect expression.");
    }

    // Renamed match() > currentTokenMatches()
    private boolean currentTokenMatches(TokenType... types) {
        for (TokenType type : types) {
            if (isCurrentTokenOfType(type)) {
                moveToNextToken();
                return true;
            }
        }

        return false;
    }

    // Renamed consume() > consumeTokenOrThrow()
    private Token consumeTokenOrThrow(TokenType type, String message) {
        if (isCurrentTokenOfType(type)) return moveToNextToken();
        throw error(getCurrentToken(), message);
    }

    // Renamed check() > isCurrentTokenOfType()
    private boolean isCurrentTokenOfType(TokenType type) {
        if (isAtEnd()) return false;
        return getCurrentToken().type == type;
    }

    // Renamed advance() > moveToNextToken()
    private Token moveToNextToken() {
        if (!isAtEnd()) current++;
        return previousToken();
    }

    private boolean isAtEnd() {
        return getCurrentToken().type == TokenType.EOF;
    }

    // Renamed peek() > getCurrentToken()
    private Token getCurrentToken() {
        return tokens.get(current);
    }

    // Renamed previous() > previousToken()
    private Token previousToken() {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    // https://craftinginterpreters.com/parsing-expressions.html#synchronizing-a-recursive-descent-parser
    private void synchronize() {
        moveToNextToken();

        while(!isAtEnd()) {
            if (previousToken().type == TokenType.SEMICOLON) return;

            switch (getCurrentToken().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            moveToNextToken();
        }
    }
}
