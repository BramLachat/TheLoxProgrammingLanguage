package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.Arrays;
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
            if (currentTokenMatches(TokenType.CLASS)) return classDeclaration();
            if (currentTokenMatches(TokenType.FUN)) return function("function");
            if (currentTokenMatches(TokenType.VAR)) return varDeclaration();

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt classDeclaration() {
        Token name = consumeTokenOrThrow(TokenType.IDENTIFIER, "Expect class name.");
        consumeTokenOrThrow(TokenType.LEFT_BRACE, "Expect '{' before class body.");

        List<Stmt.Function> methods = new ArrayList<>();
        while (!isCurrentTokenOfType(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            methods.add(function("method"));
        }

        consumeTokenOrThrow(TokenType.RIGHT_BRACE, "Expect '}' after class body.");

        return new Stmt.Class(name, methods);
    }

    private Stmt.Function function(String kind) {
        Token name = consumeTokenOrThrow(TokenType.IDENTIFIER, "Expect " + kind + " name.");
        consumeTokenOrThrow(TokenType.LEFT_PAREN, "Expect '(' after " + kind + " name.");
        List<Token> parameters = new ArrayList<>();
        if (!isCurrentTokenOfType(TokenType.RIGHT_PAREN)) {
            do {
                if (parameters.size() >= 255) {
                    error(getCurrentToken(), "Can't have more than 255 parameters.");
                }

                parameters.add(consumeTokenOrThrow(TokenType.IDENTIFIER, "Expect parameter name."));
            } while (currentTokenMatches(TokenType.COMMA));
        }
        consumeTokenOrThrow(TokenType.RIGHT_PAREN, "Expect ')' after parameters");

        consumeTokenOrThrow(TokenType.LEFT_BRACE, "Expect '{' before " + kind + " body.");
        List<Stmt> body = block();
        return new Stmt.Function(name, parameters, body);
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
        if (currentTokenMatches(TokenType.FOR)) return forStatement();
        if (currentTokenMatches(TokenType.IF)) return ifStatement();
        if (currentTokenMatches(TokenType.PRINT)) return printStatement();
        if (currentTokenMatches(TokenType.RETURN)) return returnStatement();
        if (currentTokenMatches(TokenType.WHILE)) return whileStatement();
        if (currentTokenMatches(TokenType.LEFT_BRACE)) return new Stmt.Block(block());
        return expressionStatement();
    }

    private Stmt forStatement() {
        consumeTokenOrThrow(TokenType.LEFT_PAREN, "Expect '(' after 'for'.");

        Stmt initializer;
        if (currentTokenMatches(TokenType.SEMICOLON)) {
            initializer = null;
        } else if (currentTokenMatches(TokenType.VAR)) {
            initializer = varDeclaration();
        } else {
            initializer = expressionStatement();
        }

        Expr condition = null;
        if (!isCurrentTokenOfType(TokenType.SEMICOLON)) {
            condition = expression();
        }
        consumeTokenOrThrow(TokenType.SEMICOLON, "Expect ';' after loop condition.");

        Expr increment = null;
        if (!isCurrentTokenOfType(TokenType.RIGHT_PAREN)) {
            increment = expression();
        }
        consumeTokenOrThrow(TokenType.RIGHT_PAREN, "Expect ')' after for clauses.");

        Stmt body = statement();

        if (increment != null) {
            body = new Stmt.Block(
                    Arrays.asList(
                            body,
                            new Stmt.Expression(increment)
                    )
            );
        }

        if (condition == null) condition = new Expr.Literal(true);
        body = new Stmt.While(condition, body);

        if (initializer != null) {
            body = new Stmt.Block(Arrays.asList(initializer, body));
        }

        return body;
    }

    private Stmt ifStatement() {
        consumeTokenOrThrow(TokenType.LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consumeTokenOrThrow(TokenType.RIGHT_PAREN, "Expect ')' after if condition.");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (currentTokenMatches(TokenType.ELSE)) {
            elseBranch = statement();
        }

        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private Stmt printStatement() {
        Expr value = expression();
        consumeTokenOrThrow(TokenType.SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Print(value);
    }

    private Stmt returnStatement() {
        Token keyword = previousToken();
        Expr value = null;
        if (!isCurrentTokenOfType(TokenType.SEMICOLON)) {
            value = expression();
        }

        consumeTokenOrThrow(TokenType.SEMICOLON, "Expect ';' after return value.");
        return new Stmt.Return(keyword, value);
    }

    private Stmt whileStatement() {
        consumeTokenOrThrow(TokenType.LEFT_PAREN, "Expect '(' after 'while'.");
        Expr condition = expression();
        consumeTokenOrThrow(TokenType.RIGHT_PAREN, "Expect ')' after condition.");
        Stmt body = statement();

        return new Stmt.While(condition, body);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consumeTokenOrThrow(TokenType.SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();

        while (!isCurrentTokenOfType(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consumeTokenOrThrow(TokenType.RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Expr expression() {
        return assignment();
    }

    private Expr assignment() {
        Expr expr = or();

        if (currentTokenMatches(TokenType.EQUAL)) {
            Token equals = previousToken();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
            }

            // If the left-hand side expression isn’t a valid assignment target,
            // we fail with a syntax error. That ensures we report an error on code like this:
            // a + b = c;
            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private Expr or() {
        Expr expr = and();

        while (currentTokenMatches(TokenType.OR)) {
            Token operator = previousToken();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr and() {
        Expr expr = equality();

        while (currentTokenMatches(TokenType.AND)) {
            Token operator = previousToken();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
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

        return call();
    }

    private Expr call() {
        Expr expr = primary();

        while (true) {
            if (currentTokenMatches(TokenType.LEFT_PAREN)) {
                expr = finishCall(expr);
            } else {
                break;
            }
        }

        return expr;
    }

    private Expr finishCall(Expr callee) {
        List<Expr> arguments = new ArrayList<>();
        if (!isCurrentTokenOfType(TokenType.RIGHT_PAREN)) {
            do {
                if (arguments.size() >= 255) {
                    error(getCurrentToken(), "Can't have more than 255 arguments.");
                }
                arguments.add(expression());
            } while (currentTokenMatches(TokenType.COMMA));
        }

        Token paren = consumeTokenOrThrow(TokenType.RIGHT_PAREN, "Expect ')' after arguments.");

        return new Expr.Call(callee, paren, arguments);
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
