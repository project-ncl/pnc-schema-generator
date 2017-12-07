import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import com.google.common.reflect.ClassPath;

public class HibernateExporter {

    private String dialect;
    private String entityPackage;

    private boolean generateCreateQueries = true;
    private boolean generateDropQueries = false;

    private Configuration hibernateConfiguration;

    public HibernateExporter(String dialect, String entityPackage) {
        this.dialect = dialect;
        this.entityPackage = entityPackage;

        hibernateConfiguration = createHibernateConfig();
    }

    public void export(OutputStream out, boolean generateCreateQueries, boolean generateDropQueries) {

        Dialect hibDialect = Dialect.getDialect(hibernateConfiguration.getProperties());
        try (PrintWriter writer = new PrintWriter(out)) {

            if (generateCreateQueries) {
                String[] createSQL = hibernateConfiguration.generateSchemaCreationScript(hibDialect);
                write(writer, createSQL, FormatStyle.DDL.getFormatter());
            }
            if (generateDropQueries) {
                String[] dropSQL = hibernateConfiguration.generateDropSchemaScript(hibDialect);
                write(writer, dropSQL, FormatStyle.DDL.getFormatter());
            }
        }
    }

    public void export(File exportFile) throws FileNotFoundException {

        export(new FileOutputStream(exportFile), generateCreateQueries, generateDropQueries);
    }

    public void exportToConsole() {

        export(System.out, generateCreateQueries, generateDropQueries);
    }

    private void write(PrintWriter writer, String[] lines, Formatter formatter) {

        for (String string : lines)
            writer.println(formatter.format(string) + ";");
    }

    private Configuration createHibernateConfig() {

        hibernateConfiguration = new Configuration();

        try {
            ClassPath classPath = ClassPath.from(this.getClass().getClassLoader());

            for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClasses(entityPackage)) {
                Class<?> cl = classInfo.load();
                hibernateConfiguration.addAnnotatedClass(cl);
                System.out.println("Mapped = " + cl.getName());
            }

            hibernateConfiguration.setProperty(AvailableSettings.DIALECT, dialect);
            return hibernateConfiguration;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public boolean isGenerateDropQueries() {
        return generateDropQueries;
    }

    public void setGenerateDropQueries(boolean generateDropQueries) {
        this.generateDropQueries = generateDropQueries;
    }

    public Configuration getHibernateConfiguration() {
        return hibernateConfiguration;
    }

    public void setHibernateConfiguration(Configuration hibernateConfiguration) {
        this.hibernateConfiguration = hibernateConfiguration;
    }
}
