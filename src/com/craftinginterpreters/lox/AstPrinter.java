package com.craftinginterpreters.lox;

import java.util.List;
import java.util.UUID;

public class AstPrinter implements Expr.Visitor<GraphizNode>, Stmt.Visitor<GraphizNode> {

    void print(List<Stmt> statements) {
        System.out.println("digraph {");
        for (Stmt stmt : statements) {
            print(stmt);
        }
        System.out.println("}");
    }

    private void print (Stmt statement) {
        GraphizNode stmtNode = statement.accept(this);
        System.out.print(stmtNode.getUuid());
        System.out.println(stmtNode.getContent());
    }

    @Override
    public GraphizNode visitAssignExpr(Expr.Assign expr) {
        StringBuilder sb = new StringBuilder();
        String exprUUID = generateUUID();

        sb.append(" [label=\"type=Expr\nsubType=Assign\n")
                .append("name=")
                .append(expr.name.lexeme)
                .append("\"];\n");

        Expr value = expr.value;
        GraphizNode valueNode = value.accept(this);
        sb.append(valueNode.getUuid())
                .append(valueNode.getContent())
                .append(exprUUID)
                .append(" -> ")
                .append(valueNode.getUuid())
                .append(" [label=\"value\"];\n");

        return new GraphizNode(exprUUID, sb.toString());
    }

    @Override
    public GraphizNode visitBinaryExpr(Expr.Binary expr) {
        StringBuilder sb = new StringBuilder();
        String binaryUUID = generateUUID();

        sb.append(" [label=\"type=Expr\nsubType=Binary\n")
                .append("operator=")
                .append(expr.operator.lexeme)
                .append("\"];\n");

        Expr left = expr.left;
        GraphizNode leftNode = left.accept(this);
        sb.append(leftNode.getUuid())
                .append(leftNode.getContent())
                .append(binaryUUID)
                .append(" -> ")
                .append(leftNode.getUuid())
                .append(" [label=\"left\"];\n");

        Expr right = expr.right;
        GraphizNode rightNode = right.accept(this);
        sb.append(rightNode.getUuid())
                .append(rightNode.getContent())
                .append(binaryUUID)
                .append(" -> ")
                .append(rightNode.getUuid())
                .append(" [label=\"right\"];\n");

        return new GraphizNode(binaryUUID, sb.toString());
    }

    @Override
    public GraphizNode visitGroupingExpr(Expr.Grouping expr) {
        StringBuilder sb = new StringBuilder();
        String groupingUUID = generateUUID();

        sb.append(" [label=\"type=Expr\nsubType=Grouping\"];\n");

        Expr expression = expr.expression;
        GraphizNode expressionNode = expression.accept(this);
        sb.append(expressionNode.getUuid())
                .append(expressionNode.getContent())
                .append(groupingUUID)
                .append(" -> ")
                .append(expressionNode.getUuid())
                .append(" [label=\"expression\"];\n");

        return new GraphizNode(groupingUUID, sb.toString());
    }

    @Override
    public GraphizNode visitLiteralExpr(Expr.Literal expr) {
        StringBuilder sb = new StringBuilder();
        String literalUUID = generateUUID();

        sb.append(" [label=\"type=Expr\nsubType=Literal\n")
                .append("value=")
                .append(expr.value)
                .append("\"];\n");

        return new GraphizNode(literalUUID, sb.toString());
    }

    @Override
    public GraphizNode visitLogicalExpr(Expr.Logical expr) {
        StringBuilder sb = new StringBuilder();
        String logicalUUID = generateUUID();

        sb.append(" [label=\"type=Expr\nsubType=Logical\n")
                .append("operator=")
                .append(expr.operator.lexeme)
                .append("\"];\n");

        Expr left = expr.left;
        GraphizNode leftNode = left.accept(this);
        sb.append(leftNode.getUuid())
                .append(leftNode.getContent())
                .append(logicalUUID)
                .append(" -> ")
                .append(leftNode.getUuid())
                .append(" [label=\"left\"];\n");

        Expr right = expr.right;
        GraphizNode rightNode = right.accept(this);
        sb.append(rightNode.getUuid())
                .append(rightNode.getContent())
                .append(logicalUUID)
                .append(" -> ")
                .append(rightNode.getUuid())
                .append(" [label=\"right\"];\n");

        return new GraphizNode(logicalUUID, sb.toString());
    }

    @Override
    public GraphizNode visitUnaryExpr(Expr.Unary expr) {
        StringBuilder sb = new StringBuilder();
        String unaryUUID = generateUUID();

        sb.append(" [label=\"type=Expr\nsubType=Unary\n")
                .append("operator=")
                .append(expr.operator.lexeme)
                .append("\"];\n");

        Expr right = expr.right;
        GraphizNode rightNode = right.accept(this);
        sb.append(rightNode.getUuid())
                .append(rightNode.getContent())
                .append(unaryUUID)
                .append(" -> ")
                .append(rightNode.getUuid())
                .append(" [label=\"right\"];\n");

        return new GraphizNode(unaryUUID, sb.toString());
    }

    @Override
    public GraphizNode visitVariableExpr(Expr.Variable expr) {
        StringBuilder sb = new StringBuilder();
        String variableUUID = generateUUID();

        sb.append(" [label=\"type=Expr\nsubType=Variable\n")
                .append("name=")
                .append(expr.name.lexeme)
                .append("\"];\n");

        return new GraphizNode(variableUUID, sb.toString());
    }

    @Override
    public GraphizNode visitCallExpr(Expr.Call expr) {
        StringBuilder sb = new StringBuilder();
        String callUUID = generateUUID();

        sb.append(" [label=\"type=Expr\nsubType=Call\"];\n");

        Expr callee = expr.callee;
        GraphizNode calleeNode = callee.accept(this);
        sb.append(calleeNode.getUuid())
                .append(calleeNode.getContent())
                .append(callUUID)
                .append(" -> ")
                .append(calleeNode.getUuid())
                .append(" [label=\"callee\"];\n");

        for (Expr argument : expr.arguments) {
            GraphizNode argumentNode = argument.accept(this);
            sb.append(argumentNode.getUuid())
                    .append(argumentNode.getContent())
                    .append(callUUID)
                    .append(" -> ")
                    .append(argumentNode.getUuid())
                    .append(" [label=\"argument\"];\n");
        }

        return new GraphizNode(callUUID, sb.toString());
    }

    @Override
    public GraphizNode visitGetExpr(Expr.Get expr) {
        StringBuilder sb = new StringBuilder();
        String getUUID = generateUUID();

        sb.append(" [label=\"type=Expr\nsubType=Get\n")
                .append("name=")
                .append(expr.name.lexeme)
                .append("\"];\n");

        Expr object = expr.object;
        GraphizNode objectNode = object.accept(this);
        sb.append(objectNode.getUuid())
                .append(objectNode.getContent())
                .append(getUUID)
                .append(" -> ")
                .append(objectNode.getUuid())
                .append(" [label=\"object\"];\n");

        return new GraphizNode(getUUID, sb.toString());
    }

    @Override
    public GraphizNode visitSetExpr(Expr.Set expr) {
        StringBuilder sb = new StringBuilder();
        String setUUID = generateUUID();

        sb.append(" [label=\"type=Expr\nsubType=Set\n")
                .append("name=")
                .append(expr.name.lexeme)
                .append("\"];\n");

        Expr object = expr.object;
        GraphizNode objectNode = object.accept(this);
        sb.append(objectNode.getUuid())
                .append(objectNode.getContent())
                .append(setUUID)
                .append(" -> ")
                .append(objectNode.getUuid())
                .append(" [label=\"object\"];\n");

        Expr value = expr.value;
        GraphizNode valueNode = value.accept(this);
        sb.append(valueNode.getUuid())
                .append(valueNode.getContent())
                .append(setUUID)
                .append(" -> ")
                .append(valueNode.getUuid())
                .append(" [label=\"value\"];\n");

        return new GraphizNode(setUUID, sb.toString());
    }

    @Override
    public GraphizNode visitThisExpr(Expr.This expr) {
        StringBuilder sb = new StringBuilder();
        String thisUUID = generateUUID();

        sb.append(" [label=\"type=Expr\nsubType=This\n")
                .append("keyword=")
                .append(expr.keyword.lexeme)
                .append("\"];\n");

        return new GraphizNode(thisUUID, sb.toString());
    }

    @Override
    public GraphizNode visitSuperExpr(Expr.Super expr) {
        StringBuilder sb = new StringBuilder();
        String superUUID = generateUUID();

        sb.append(" [label=\"type=Expr\nsubType=Super\n")
                .append("keyword=")
                .append(expr.keyword.lexeme)
                .append("\nmethod=")
                .append(expr.method.lexeme)
                .append("\"];\n");

        return new GraphizNode(superUUID, sb.toString());

    }

    @Override
    public GraphizNode visitBlockStmt(Stmt.Block stmt) {
        StringBuilder sb = new StringBuilder();
        String blockUUID = generateUUID();

        sb.append(" [label=\"type=Stmt\nsubType=Block\"];\n");
        for (Stmt statement : stmt.statements) {
            GraphizNode stmtNode = statement.accept(this);
            sb.append(stmtNode.getUuid())
                    .append(stmtNode.getContent())
                    .append(blockUUID)
                    .append(" -> ")
                    .append(stmtNode.getUuid())
                    .append(" [label=\"statement\"];\n");
        }

        return new GraphizNode(blockUUID, sb.toString());
    }

    @Override
    public GraphizNode visitClassStmt(Stmt.Class stmt) {
        StringBuilder sb = new StringBuilder();
        String classUUID = generateUUID();

        sb.append(" [label=\"type=Stmt\nsubType=Class\nname=")
                .append(stmt.name.lexeme)
                .append("\nsuperClass=TODO")
//                .append(stmt.superclass.name.lexeme) // TODO
                .append("\"];\n");
        for (Stmt.Function method : stmt.methods) {
            GraphizNode methodNode = method.accept(this);
            sb.append(methodNode.getUuid())
                    .append(methodNode.getContent())
                    .append(classUUID)
                    .append(" -> ")
                    .append(methodNode.getUuid())
                    .append(" [label=\"method\"];\n");
        }

        return new GraphizNode(classUUID, sb.toString());
    }

    @Override
    public GraphizNode visitExpressionStmt(Stmt.Expression stmt) {
        StringBuilder sb = new StringBuilder();
        String expressionUUID = generateUUID();

        sb.append(" [label=\"type=Stmt\nsubType=Expression\"];\n");

        Expr expression = stmt.expression;
        GraphizNode expressionNode = expression.accept(this);
        sb.append(expressionNode.getUuid())
                .append(expressionNode.getContent())
                .append(expressionUUID)
                .append(" -> ")
                .append(expressionNode.getUuid())
                .append(" [label=\"expression\"];\n");

        return new GraphizNode(expressionUUID, sb.toString());
    }

    @Override
    public GraphizNode visitFunctionStmt(Stmt.Function stmt) {
        StringBuilder sb = new StringBuilder();
        String functionUUID = generateUUID();

        sb.append(" [label=\"type=Stmt\nsubType=Function\nname=")
                .append(stmt.name.lexeme)
                .append("\nparams=[");
        for (Token param : stmt.params) {
            sb.append(param.lexeme)
                    .append(",");
        }
        sb.append("]\"];\n");

        for (Stmt bodyStmt : stmt.body) {
            GraphizNode bodyNode = bodyStmt.accept(this);
            sb.append(bodyNode.getUuid())
                    .append(bodyNode.getContent())
                    .append(functionUUID)
                    .append(" -> ")
                    .append(bodyNode.getUuid())
                    .append(" [label=\"bodyStmt\"];\n");
        }


        return new GraphizNode(functionUUID, sb.toString());
    }

    @Override
    public GraphizNode visitIfStmt(Stmt.If stmt) {
        StringBuilder sb = new StringBuilder();
        String ifUUID = generateUUID();

        sb.append(" [label=\"type=Stmt\nsubType=If\"];\n");

        Expr condition = stmt.condition;
        GraphizNode conditionNode = condition.accept(this);
        sb.append(conditionNode.getUuid())
                .append(conditionNode.getContent())
                .append(ifUUID)
                .append(" -> ")
                .append(conditionNode.getUuid())
                .append(" [label=\"condition\"];\n");

        Stmt thenBranch = stmt.thenBranch;
        GraphizNode thenBranchNode = thenBranch.accept(this);
        sb.append(thenBranchNode.getUuid())
                .append(thenBranchNode.getContent())
                .append(ifUUID)
                .append(" -> ")
                .append(thenBranchNode.getUuid())
                .append(" [label=\"thenBranch\"];\n");

        Stmt elseBranch = stmt.elseBranch;
        GraphizNode elseBranchNode = elseBranch.accept(this);
        sb.append(elseBranchNode.getUuid())
                .append(elseBranchNode.getContent())
                .append(ifUUID)
                .append(" -> ")
                .append(elseBranchNode.getUuid())
                .append(" [label=\"elseBranch\"];\n");

        return new GraphizNode(ifUUID, sb.toString());
    }

    @Override
    public GraphizNode visitPrintStmt(Stmt.Print stmt) {
        StringBuilder sb = new StringBuilder();
        String printUUID = generateUUID();

        sb.append(" [label=\"type=Stmt\nsubType=Print\"];\n");

        Expr expression = stmt.expression;
        GraphizNode expressionNode = expression.accept(this);
        sb.append(expressionNode.getUuid())
                .append(expressionNode.getContent())
                .append(printUUID)
                .append(" -> ")
                .append(expressionNode.getUuid())
                .append(" [label=\"expression\"];\n");

        return new GraphizNode(printUUID, sb.toString());
    }

    @Override
    public GraphizNode visitReturnStmt(Stmt.Return stmt) {
        StringBuilder sb = new StringBuilder();
        String returnUUID = generateUUID();

        sb.append(" [label=\"type=Stmt\nsubType=Return\"];\n");

        Expr value = stmt.value;
        GraphizNode valueNode = value.accept(this);
        sb.append(valueNode.getUuid())
                .append(valueNode.getContent())
                .append(returnUUID)
                .append(" -> ")
                .append(valueNode.getUuid())
                .append(" [label=\"value\"];\n");

        return new GraphizNode(returnUUID, sb.toString());
    }

    @Override
    public GraphizNode visitVarStmt(Stmt.Var stmt) {
        StringBuilder sb = new StringBuilder();
        String varUUID = generateUUID();

        sb.append(" [label=\"type=Stmt\nsubType=Var\n")
                .append("name=")
                .append(stmt.name.lexeme)
                .append("\"];\n");

        if (stmt.initializer != null) {
            Expr initializer = stmt.initializer;
            GraphizNode initializerNode = initializer.accept(this);
            sb.append(initializerNode.getUuid())
                    .append(initializerNode.getContent())
                    .append(varUUID)
                    .append(" -> ")
                    .append(initializerNode.getUuid())
                    .append(" [label=\"initializer\"];\n");
        }

        return new GraphizNode(varUUID, sb.toString());
    }

    @Override
    public GraphizNode visitWhileStmt(Stmt.While stmt) {
        StringBuilder sb = new StringBuilder();
        String whileUUID = generateUUID();

        sb.append(" [label=\"type=Stmt\nsubType=While\"];\n");

        Expr condition = stmt.condition;
        GraphizNode conditionNode = condition.accept(this);
        sb.append(conditionNode.getUuid())
                .append(conditionNode.getContent())
                .append(whileUUID)
                .append(" -> ")
                .append(conditionNode.getUuid())
                .append(" [label=\"condition\"];\n");

        Stmt body = stmt.body;
        GraphizNode bodyNode = body.accept(this);
        sb.append(bodyNode.getUuid())
                .append(bodyNode.getContent())
                .append(whileUUID)
                .append(" -> ")
                .append(bodyNode.getUuid())
                .append(" [label=\"body\"];\n");

        return new GraphizNode(whileUUID, sb.toString());
    }

    private String generateUUID() {
        return "node" + UUID.randomUUID().toString().replaceAll("-", "");
    }
}
