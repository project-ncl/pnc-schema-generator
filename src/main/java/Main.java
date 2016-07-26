import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        HibernateExporter exporter = new HibernateExporter("org.hibernate.dialect.PostgreSQLDialect", "org.jboss.pnc.model");
        exporter.setGenerateDropQueries(true);
        exporter.exportToConsole();
        exporter.export(new File("schema.sql"));
    }
}
