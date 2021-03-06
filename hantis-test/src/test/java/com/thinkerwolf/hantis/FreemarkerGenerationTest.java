package com.thinkerwolf.hantis;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.junit.Test;

import com.thinkerwolf.hantis.test.DBInfo;
import com.thinkerwolf.hantis.test.TableInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class FreemarkerGenerationTest {

    //@Test
    public void freemarker() throws Throwable {
        Class<?>[] classTypes = new Class<?>[]{
                //Object.class,

                //Boolean.class,
                //Byte.class,
                //Short.class,
                //Integer.class,
                //Long.class,
                //Float.class,
                //Double.class,
                //BigDecimal.class,
                //String.class,
                //Timestamp.class,
                //Time.class,
                //Blob.class,
                //Clob.class,
                //byte[].class
                Character.class
        };
        // Class<?>[] classTypes = new Class<?>[] { int[].class };
        String fileName = "src/main/java/com/thinkerwolf/hantis/common/type/{%type%}TypeHandler.java";
        String templateLoadingDictory = "src/main/java/com/thinkerwolf/hantis/";
        String templateName = "typeHandler.ftl";

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
        cfg.setDirectoryForTemplateLoading(new File(templateLoadingDictory));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        Template temp = cfg.getTemplate(templateName);
        Map<String, Object> root = new HashMap<>();
        for (Class<?> type : classTypes) {
            root.put("packageName", "com.thinkerwolf.hantis.common.type");
            Class<?> classType = type;
            String suffix;
            if (type.isArray()) {
                classType = type.getComponentType();
                String simpleName = classType.getSimpleName();
                suffix = Character.toUpperCase(simpleName.charAt(0)) + simpleName.substring(1) + 's';
            } else {
                suffix = classType.getSimpleName();
            }
            root.put("type", suffix);
            if (classType.getName().startsWith("java.lang") || !classType.getName().contains(".")) {
                root.put("isNeedImport", false);
            } else {
                root.put("importClass", classType.getName());
                root.put("isNeedImport", true);
            }
            root.put("realType", type.getSimpleName());

            Writer out = new OutputStreamWriter(new FileOutputStream(fileName.replaceAll("\\{%type%\\}", suffix)));
            // Writer out = new OutputStreamWriter(System.out);
            temp.process(root, out);
        }

    }

    @Test
    public void genrateEntity() throws Exception {
    	TableInfo tableInfo = new TableInfo();
    	tableInfo.setPackageName("com.thinkerwolf.hantis.example");
    	tableInfo.setOutputLocation("src/test/java/com/thinkerwolf/hantis/example");
        tableInfo.setTableNames("user");

        DBInfo dbInfo = new DBInfo();
    	dbInfo.setDriver("com.mysql.cj.jdbc.Driver");
    	dbInfo.setUrl("jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC");
        dbInfo.setUser("root");
        dbInfo.setPassword("1234");
        dbInfo.setSchameName("test");
        
        TableInfo.generate(tableInfo, dbInfo);
        
    }

}
