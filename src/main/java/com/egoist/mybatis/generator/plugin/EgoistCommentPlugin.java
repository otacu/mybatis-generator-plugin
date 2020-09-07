package com.egoist.mybatis.generator.plugin;

import com.egoist.mybatis.generator.plugin.constant.PluginConstant;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * 生成代码注释的插件
 */
public class EgoistCommentPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return this.generateCommentForField(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return this.generateCommentForMethod(method, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return this.generateCommentForMethod(method, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    private boolean generateCommentForMethod(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (introspectedColumn != null && StringUtils.isNotBlank(introspectedColumn.getRemarks())) {
            String remarks = getFormatRemarks(introspectedColumn.getRemarks());
            method.addJavaDocLine("/**");
            method.addJavaDocLine(" * " + remarks);
            method.addJavaDocLine(" */");
            return true;
        }
        return true;
    }

    protected boolean generateCommentForField(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (introspectedColumn != null && StringUtils.isNotBlank(introspectedColumn.getRemarks())) {
            String remarks = getFormatRemarks(introspectedColumn.getRemarks());
            field.addJavaDocLine("/**");
            field.addJavaDocLine(" * " + remarks);
            field.addJavaDocLine(" */");
            return true;
        }
        return true;
    }

    /**
     * 截取出业务注释
     *
     * @param remarks 数据库字段上的注释
     * @return 业务注释
     */
    private String getFormatRemarks(String remarks) {
        remarks = remarks.trim();
        if (StringUtils.isBlank(remarks) || !remarks.contains(PluginConstant.COMMENT_PREFIX)) {
            return remarks;
        }
        return remarks.substring(0, remarks.indexOf(PluginConstant.COMMENT_PREFIX));
    }
}
