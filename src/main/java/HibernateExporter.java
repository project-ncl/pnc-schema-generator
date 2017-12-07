import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;

public class HibernateExporter {

    private String dialect;
    private String entityPackage;

    public HibernateExporter(String dialect, String entityPackage) {
        this.dialect = dialect;
        this.entityPackage = entityPackage;
    }

    public void export(String exportFile) {

        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", dialect);

        Dialect hibDialect = Dialect.getDialect(properties);

        MetadataSources metadata = new MetadataSources(
                new StandardServiceRegistryBuilder()
                        .applySetting("hibernate.dialect", hibDialect)
                        .build());

        addClassestoMetadataSources(metadata);

        SchemaExport export = new SchemaExport();

        export.setDelimiter(";");
        export.setFormat(true);
        export.setOutputFile(exportFile);

        export.execute(EnumSet.of(TargetType.SCRIPT), SchemaExport.Action.BOTH, metadata.buildMetadata());

    }

    private void addClassestoMetadataSources(MetadataSources metadata) {

        try {
            List<Class> clazz = getClasses(entityPackage);
            for (Class a : clazz) {
                metadata.addAnnotatedClass(a);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("rawtypes")
    private List<Class> getClasses(String packageName) throws Exception {
        File directory = null;
        try {
            ClassLoader cld = getClassLoader();
            URL resource = getResource(packageName, cld);
            directory = new File(resource.getFile());
        } catch (NullPointerException ex) {
            throw new ClassNotFoundException(packageName + " (" + directory + ") does not appear to be a valid package");
        }
        return collectClasses(packageName, directory);
    }

    private ClassLoader getClassLoader() throws ClassNotFoundException {
        ClassLoader cld = Thread.currentThread().getContextClassLoader();
        if (cld == null) {
            throw new ClassNotFoundException("Can't get class loader.");
        }
        return cld;
    }

    private URL getResource(String packageName, ClassLoader cld) throws ClassNotFoundException {
        String path = packageName.replace('.', '/');
        URL resource = cld.getResource(path);
        if (resource == null) {
            throw new ClassNotFoundException("No resource for " + path);
        }
        return resource;
    }

    @SuppressWarnings("rawtypes")
    private List<Class> collectClasses(String packageName, File directory) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        if (directory.exists()) {
            String[] files = directory.list();
            for (String file : files) {
                if (file.endsWith(".class")) {
                    // removes the .class extension
                    classes.add(Class.forName(packageName + '.' + file.substring(0, file.length() - 6)));
                }
            }
        } else {
            throw new ClassNotFoundException(packageName + " is not a valid package");
        }
        return classes;
    }
}
