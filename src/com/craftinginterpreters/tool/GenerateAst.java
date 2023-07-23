package com.craftinginterpreters.tool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAst(outputDir, "Expr", Arrays.asList(

                // An assignment expression looks like:
                // IDENTIFIER "=" assignment
                // eg. makeList().head.next = node;
                "Assign     :   Token name, Expr value",

                // A binary expression looks like:
                // expression operator expression
                "Binary     :   Expr left, Token operator, Expr right",

                // A function call expression looks like:
                // primary ( "(" arguments? ")" )*
                "Call       :   Expr callee, Token paren, List<Expr> arguments",

                // A "get expression" or "property access" looks like:
                // primary ( "(" arguments? ")" | "." IDENTIFIER )*
                "Get        :   Expr object, Token name",

                // A grouping expression looks like:
                // "(" expression ")"
                "Grouping   :   Expr expression",

                // A literal expression looks like:
                // NUMBER | STRING | "true" | "false" | "nil"
                "Literal    :   Object value",

                // A logical expression looks like:
                // expression ( "and" | "or" ) expression
                "Logical    :   Expr left, Token operator, Expr right",

                // A "set expression" or "property assignment" looks like:
                // ( call "." )? IDENTIFIER "=" assignment
                "Set        :   Expr object, Token name, Expr value",

                "This       :   Token keyword",

                // A unary expression looks like:
                // ( "-" | "!" ) expression
                "Unary      :   Token operator, Expr right",

                // A variable expression (for accessing a variable) looks like:
                // IDENTIFIER
                "Variable   :   Token name"
        ));

        defineAst(outputDir, "Stmt", Arrays.asList(

                // A block statement looks like:
                // "{" declaration* "}"
                "Block      :   List<Stmt> statements",

                // A class statement looks like:
                // "class" IDENTIFIER "{" function* "}"
                "Class      :   Token name, List<Stmt.Function> methods",

                // An expression statement looks like:
                // expression ";"
                "Expression :   Expr expression",

                // A function statement looks like:
                // IDENTIFIER "(" parameters? ")" block
                "Function   :   Token name, List<Token> params, List<Stmt> body",

                // An if statement looks like:
                // "if" "(" expression ")" statement ( "else" statement )?
                "If         :   Expr condition, Stmt thenBranch, Stmt elseBranch",

                // A print statement looks like:
                // "print" expression ";"
                "Print      :   Expr expression",

                // A return statement looks like:
                // "return" expression ";"
                "Return     :   Token keyword, Expr value",

                // The rule for declaring a variable looks like:
                // "var" IDENTIFIER ( "=" expression )? ";"
                "Var        :   Token name, Expr initializer",

                // A while statement looks like:
                // "while" "(" expression ")" statement
                "While      :   Expr condition, Stmt body"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws FileNotFoundException, UnsupportedEncodingException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package com.craftinginterpreters.lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("// This file was generated by the GenerateAst class.");
        writer.println("abstract class " + baseName + " {");

        // Visitor pattern.
        // https://www.newthinktank.com/2012/11/visitor-design-pattern-tutorial/
        defineVisitor(writer, baseName, types);

        // The AST classes.
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        writer.println();
        writer.println("    abstract <R> R accept(Visitor<R> visitor);");

        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("    interface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("        R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
        }

        writer.println("    }");
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        writer.println("    static class " + className + " extends " + baseName + " {");

        String[] fields = fieldList.split(", ");

        // Fields.
        for (String field : fields) {
            writer.println("        final " + field + ";");
        }

        // Constructor.
        writer.println("        " + className + "(" + fieldList + ") {");

        // Store parameters in fields.
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println("            this." + name + " = " + name + ";");
        }

        writer.println("        }");

        // Visitor pattern.
        writer.println();
        writer.println("        @Override");
        writer.println("        <R> R accept(Visitor<R> visitor) {");
        writer.println("            return visitor.visit" + className + baseName + "(this);");
        writer.println("        }");

        writer.println("    }");
    }
}
