package me.ziyframework.module.data.process;

import com.google.common.base.CaseFormat;
import com.sun.source.tree.Tree.Kind;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCReturn;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;
import java.util.HashSet;
import java.util.Set;
import javax.lang.model.element.TypeElement;
import me.ziyframework.process.common.BaseTreeTranslator;
import me.ziyframework.process.common.utils.JcUtil;

/**
 * Translator：直接修改.
 * created in 2026-02
 *
 * @author ziy
 */
public class SafeEntityTranslator extends BaseTreeTranslator {

    public SafeEntityTranslator(
            JavacProcessingEnvironment processingEnvironment,
            TypeElement typeElement,
            TreeMaker treeMaker,
            Names names,
            Types types) {
        super(processingEnvironment, typeElement, treeMaker, names, types);
    }

    /**
     * 构建新方法的 JcTree 并返回.
     */
    @Override
    protected List<JCTree> visitClassDef0(JCClassDecl tree) {
        if (tree.getKind() != Kind.CLASS) {
            return List.nil();
        }

        // 收集字段
        java.util.List<JCVariableDecl> vars = collectField(tree);
        Set<String> existingMethodNames = collectExistingMethodNames(tree);

        String qualifiedName = typeElement.getQualifiedName().toString();
        processingEnvironment.getMessager().printNote("SafeEntityTreeTranslator: processing class " + qualifiedName);

        ListBuffer<JCTree> newDefs = new ListBuffer<>();
        for (JCVariableDecl var : vars) {
            String fieldName = var.name.toString();
            String methodName = "get" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, fieldName) + "OrThrow";

            if (!existingMethodNames.contains(methodName)) {
                processingEnvironment
                        .getMessager()
                        .printNote("SafeEntityTreeTranslator: generating " + methodName + "() for field " + fieldName);
                JCMethodDecl method = buildMethodDecl(var, methodName);
                newDefs.append(method);
            }
        }

        if (!newDefs.isEmpty()) {
            processingEnvironment
                    .getMessager()
                    .printNote(
                            "SafeEntityTreeTranslator: generated " + newDefs.size() + " methods for " + qualifiedName);
        }

        return newDefs.toList();
    }

    /**
     * 收集类中的实例变量.
     */
    public java.util.List<JCVariableDecl> collectField(JCTree.JCClassDecl classDecl) {
        return classDecl.defs.stream()
                .filter(def -> def instanceof JCVariableDecl)
                .map(def -> (JCVariableDecl) def)
                .filter(var -> !var.getType().type.isPrimitive())
                .filter(var -> (var.mods.flags & Flags.STATIC) == 0)
                .toList();
    }

    /**
     * 收集类中已存在的无参方法名
     */
    private Set<String> collectExistingMethodNames(JCTree.JCClassDecl classDecl) {
        Set<String> existingNames = new HashSet<>();
        for (JCTree member : classDecl.defs) {
            if (member instanceof JCMethodDecl method && method.params.isEmpty()) {
                existingNames.add(method.name.toString());
            }
        }
        return existingNames;
    }

    /**
     * 构建方法声明
     */
    private JCMethodDecl buildMethodDecl(JCVariableDecl field, String methodName) {
        String fieldName = field.name.toString();

        // 创建 this.fieldName
        JCIdent thisIdent = treeMaker.Ident(names._this);
        JCFieldAccess fieldAccess = treeMaker.Select(thisIdent, field.name);

        // 创建 return 语句: return Preconditions.checkNotNull(this.fieldName, "fieldName is null")
        JCReturn returnStmt =
                treeMaker.Return(buildCheckNotNullCall(fieldAccess, treeMaker.Literal(fieldName + " is null")));

        // 创建方法体
        JCBlock methodBody = treeMaker.Block(0, List.of(returnStmt));

        // 创建方法定义
        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC),
                names.fromString(methodName),
                field.vartype,
                List.nil(),
                List.nil(),
                List.nil(),
                methodBody,
                null);
    }

    /**
     * 构建 Preconditions.checkNotNull 方法调用
     */
    private JCMethodInvocation buildCheckNotNullCall(JCExpression arg1, JCLiteral arg2) {
        JCExpression preconditionsExpr = JcUtil.memberAccess(treeMaker, names, "com.google.common.base.Preconditions");
        JCFieldAccess checkNotNullMethod = treeMaker.Select(preconditionsExpr, names.fromString("checkNotNull"));
        return treeMaker.Apply(List.nil(), checkNotNullMethod, List.of(arg1, arg2));
    }
}
