package com.craftinginterpreters.lox;

import java.util.List;

public class AstPrinter implements Expr.Visitor<String>, Stmt.Visitor<String> {
    private List<Stmt> statements;

    public AstPrinter(List<Stmt> statements) {
        this.statements = statements;
    }

    public String print() {
        return null;
    }

    private String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        System.err.println("TODO > Implement 'visitAssignExpr' in 'AstPrinter'.");
        return null;
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitLogicalExpr(Expr.Logical expr) {
        System.err.println("TODO > Implement 'visitLogicalExpr' in 'AstPrinter'.");
        return null;
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        System.err.println("TODO > Implement 'visitVariableExpr' in 'AstPrinter'.");
        return null;
    }

    @Override
    public String visitCallExpr(Expr.Call expr) {
        System.err.println("TODO > Implement 'visitCallExpr' in 'AstPrinter'.");
        return null;
    }

    @Override
    public String visitGetExpr(Expr.Get expr) {
        System.err.println("TODO > Implement 'visitGetExpr' in 'AstPrinter'.");
        return null;
    }

    @Override
    public String visitSetExpr(Expr.Set expr) {
        System.err.println("TODO > Implement 'visitSetExpr' in 'AstPrinter'.");
        return null;
    }

    @Override
    public String visitThisExpr(Expr.This expr) {
        System.err.println("TODO > Implement 'visitThisExpr' in 'AstPrinter'.");
        return null;
    }

    @Override
    public String visitSuperExpr(Expr.Super expr) {
        System.err.println("TODO > Implement 'visitSuperExpr' in 'AstPrinter'.");
        return null;
    }

    @Override
    public String visitBlockStmt(Stmt.Block stmt) {
        return null;
    }

    @Override
    public String visitClassStmt(Stmt.Class stmt) {
        return null;
    }

    @Override
    public String visitExpressionStmt(Stmt.Expression stmt) {
        return null;
    }

    @Override
    public String visitFunctionStmt(Stmt.Function stmt) {
        return null;
    }

    @Override
    public String visitIfStmt(Stmt.If stmt) {
        return null;
    }

    @Override
    public String visitPrintStmt(Stmt.Print stmt) {
        return null;
    }

    @Override
    public String visitReturnStmt(Stmt.Return stmt) {
        return null;
    }

    @Override
    public String visitVarStmt(Stmt.Var stmt) {
        return null;
    }

    @Override
    public String visitWhileStmt(Stmt.While stmt) {
        return null;
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }
}
