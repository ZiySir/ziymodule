package me.ziyframework.module.data.process;

import com.google.common.base.Preconditions;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import me.ziyframework.process.common.BaseProcessor;

/**
 * .
 * created in 2026-02
 *
 * @author ziy
 */
public class SafeEntityJavacProcessor extends BaseProcessor {

    /**
     * 通过javac内部api的方式为每一个get方法创建OrThrow版本.
     */
    @Override
    protected void processing(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWithAny(Set.of(Entity.class, MappedSuperclass.class)).stream()
                .filter(ele -> ele.getKind() == ElementKind.CLASS && ele instanceof TypeElement)
                .map(ele -> (TypeElement) ele)
                .forEach(typeElement -> {
                    JCCompilationUnit unit = Preconditions.checkNotNull(toUnit(typeElement), "unit is null");
                    JavacProcessingEnvironment env = javacProcessingEnvironment;
                    TreeMaker treeMaker = TreeMaker.instance(env.getContext());
                    Names names = Names.instance(env.getContext());
                    SafeEntityTranslator safeEntityTranslator =
                            new SafeEntityTranslator(javacProcessingEnvironment, typeElement, treeMaker, names, types);
                    unit.accept(safeEntityTranslator);
                });
    }

    /**
     * 获取支持的注解类型.
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(Entity.class.getCanonicalName(), MappedSuperclass.class.getCanonicalName());
    }

    /**
     * 获取支持的源版本.
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
