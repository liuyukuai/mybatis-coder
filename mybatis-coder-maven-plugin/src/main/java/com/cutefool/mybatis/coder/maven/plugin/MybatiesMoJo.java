package com.cutefool.mybatis.coder.maven.plugin;

import com.cutefool.mybatis.coder.core.MyConfigurationParser;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.maven.MavenProgressCallback;
import org.mybatis.generator.maven.MavenShellCallback;
import org.mybatis.generator.maven.MyBatisGeneratorMojo;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 代码生成扩展
 *
 * @author : liuyk
 * @description: MybatiesMoJo
 * @className : MybatiesMoJo
 */
@SuppressWarnings("ALL")
@Mojo(name = "mybatisGenerate", defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresDependencyResolution = ResolutionScope.TEST)
public class MybatiesMoJo extends MyBatisGeneratorMojo {

    /**
     * Maven Project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    private MavenProject project;

    /**
     * Location of the configuration file.
     */
    @Parameter(property = "mybatis.generator.configurationFile",
            defaultValue = "${project.basedir}/src/main/resources/generatorConfig.xml", required = true)
    private String[] configurationFiles;

    /**
     * 默认配置文件目录
     */
    @Parameter(property = "mybatis.generator.configDir",
            defaultValue = "src/main/resources/mybatis", required = true)
    private String configDir;

    /**
     *
     */
    @Parameter(property = "mybatis.generator.localFile",
            defaultValue = "src/main/resources/mybatis/.local", required = true)
    private String localFile;

    /**
     * Specifies whether the mojo writes progress messages to the log.
     */
    @Parameter(property = "mybatis.generator.verbose", defaultValue = "false")
    private boolean verbose;

    /**
     * Specifies whether the mojo overwrites existing Java files. Default is false.
     * <br>
     * Note that XML files are always merged.
     */
    @Parameter(property = "mybatis.generator.overwrite", defaultValue = "false")
    private boolean overwrite;

    /**
     * Comma delimited list of contexts to generate.
     */
    @Parameter(property = "mybatis.generator.contexts")
    private String contexts;

    /**
     * Comma delimited list of table names to generate.
     */
    @Parameter(property = "mybatis.generator.tableNames")
    private String tableNames;


    @Override
    public void execute() throws MojoExecutionException {

        Set<String> contextsToRun = new HashSet<String>();
        if (StringUtility.stringHasValue(contexts)) {
            StringTokenizer st = new StringTokenizer(contexts, ",");
            while (st.hasMoreTokens()) {
                String s = st.nextToken().trim();
                if (s.length() > 0) {
                    contextsToRun.add(s);
                }
            }
        }

        Set<String> fullyqualifiedTables = new HashSet<String>();
        if (StringUtility.stringHasValue(tableNames)) {
            StringTokenizer st = new StringTokenizer(tableNames, ",");
            while (st.hasMoreTokens()) {
                String s = st.nextToken().trim();
                if (s.length() > 0) {
                    fullyqualifiedTables.add(s);
                }
            }
        }

        try {
            List<String> warnings = new ArrayList<String>();
            MyConfigurationParser cp = new MyConfigurationParser(
                    project.getProperties(), warnings);

            // 处理自定义配置，本地文件优先
            File file = new File(localFile);
            if (file.exists() && file.isFile()) {
                List<String> strings = Files.readAllLines(Paths.get(file.getAbsolutePath()));
                System.out.println(strings);
                if (Objects.nonNull(strings) && strings.size() > 0) {
                    this.doCreate(strings.toArray(new String[]{}), cp, warnings, contextsToRun, fullyqualifiedTables);
                    return;
                }
            }

            // 默认配置目录
            if (StringUtils.isNotBlank(configDir)) {
                List<String> files = this.files();
                if (Objects.nonNull(file) && files.size() > 0) {
                    this.doCreate(files.toArray(new String[]{}), cp, warnings, contextsToRun, fullyqualifiedTables);
                    return;
                }
            }
            // 最后配置文件
            for (String s : configurationFiles) {

                Configuration config = cp.parseConfiguration(new File(s));
                ShellCallback callback = new MavenShellCallback(this, overwrite);

                MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,
                        callback, warnings);

                myBatisGenerator.generate(new MavenProgressCallback(getLog(),
                        verbose), contextsToRun, fullyqualifiedTables);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void doCreate(String[] configurationFiles, MyConfigurationParser cp, List<String> warnings, Set<String> contextsToRun, Set<String> fullyqualifiedTables) {

        try {
            for (String s : configurationFiles) {

                Configuration config = cp.parseConfiguration(new File(s));
                ShellCallback callback = new MavenShellCallback(this, overwrite);

                MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,
                        callback, warnings);

                myBatisGenerator.generate(new MavenProgressCallback(getLog(),
                        verbose), contextsToRun, fullyqualifiedTables);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> files() throws IOException {
        List<File> classes = new ArrayList<>(64);
        Files.walkFileTree(Paths.get(configDir), new FindJavaVisitor(".xml", classes));
        return classes.stream().map(e -> e.getAbsolutePath()).collect(Collectors.toList());
    }


    @AllArgsConstructor
    @NoArgsConstructor
    private static class FindJavaVisitor extends SimpleFileVisitor<Path> {

        private String suffix;

        private List<File> classes;

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            String fileName = file.toString();
            if (fileName.endsWith(suffix)) {
                classes.add(file.toFile());
            }
            return FileVisitResult.CONTINUE;
        }
    }
}
