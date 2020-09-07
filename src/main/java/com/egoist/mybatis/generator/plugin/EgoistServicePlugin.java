package com.egoist.mybatis.generator.plugin;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.Context;

import java.util.ArrayList;
import java.util.List;

public class EgoistServicePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> generatedJavaFiles = new ArrayList<GeneratedJavaFile>();
        String pojoName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        String targetPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
        String importPojo = String.format("%s.%s", targetPackage, pojoName);
        String importedExample = String.format("%s.%sExample", targetPackage, pojoName);

        /*
         * 接口
         */
        Interface interfaceFile = getServiceInterface(introspectedTable);
        interfaceFile.addImportedType(new FullyQualifiedJavaType(importPojo));
        interfaceFile.addImportedType(new FullyQualifiedJavaType(importedExample));

        /*
         * 实现类
         */
        TopLevelClass classFile = getServiceImpl(introspectedTable);
        classFile.addImportedType(importPojo);
        classFile.addImportedType(importedExample);
        classFile.addImportedType(interfaceFile.getType());
        classFile.addSuperInterface(new FullyQualifiedJavaType(interfaceFile.getType().getShortName()));
        this.addServiceAnnotation(classFile);

        Context context = introspectedTable.getContext();

        //添加类文件
        GeneratedJavaFile javaFile = new GeneratedJavaFile(classFile,
                context.getJavaModelGeneratorConfiguration().getTargetProject(),
                context.getJavaFormatter());
        generatedJavaFiles.add(javaFile);

        // 添加接口文件
        GeneratedJavaFile javaFileInterface = new GeneratedJavaFile(interfaceFile,
                context.getJavaModelGeneratorConfiguration().getTargetProject(),
                context.getJavaFormatter());
        generatedJavaFiles.add(javaFileInterface);

        return generatedJavaFiles;
    }

    /**
     * 创建类
     *
     * @param introspectedTable 参数
     * @return
     */
    private TopLevelClass getServiceImpl(IntrospectedTable introspectedTable) {
        String packagePath = getFilePackagePath(introspectedTable, "service.impl");

        TopLevelClass topLevelClass = new TopLevelClass(packagePath + '.'
                + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "ServiceImpl");
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass.setAbstract(false);
        return topLevelClass;
    }

    /**
     * 创建接口
     *
     * @param introspectedTable 参数
     * @return
     */
    private Interface getServiceInterface(IntrospectedTable introspectedTable) {
        String packagePath = getFilePackagePath(introspectedTable, "service");
        Interface anInterface = new Interface(packagePath + '.'
                + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "Service");
        anInterface.setVisibility(JavaVisibility.PUBLIC);
        return anInterface;
    }

    /**
     * 获取文件包的路径路径
     *
     * @param introspectedTable 参数
     * @param service           默认路径
     * @return
     */
    private String getFilePackagePath(IntrospectedTable introspectedTable, String service) {
        Context context = introspectedTable.getContext();
        String targetPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
        return StringUtils.substringBefore(targetPackage, "pojo") + service;
    }

    /**
     * 添加spring注解
     *
     * @param topLevelClass 参数
     */
    private void addServiceAnnotation(TopLevelClass topLevelClass) {
        topLevelClass.addImportedType("org.springframework.stereotype");
        topLevelClass.addAnnotation("@Service");
    }

}
