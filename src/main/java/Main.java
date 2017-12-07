import org.apache.log4j.BasicConfigurator;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        BasicConfigurator.configure();
        HibernateExporter exporter = new HibernateExporter("org.hibernate.dialect.PostgreSQLDialect", "org.jboss.pnc.model");
        exporter.export("schema.sql");
    }
}
