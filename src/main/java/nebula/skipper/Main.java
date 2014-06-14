package nebula.skipper;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

public class Main {
    public static final String wrapperProperties = "gradle/wrapper/gradle-wrapper.properties";

    public static void main(String[] args) {
        String pwd = System.getProperty("user.dir");
        Main main = new Main();
        main.runWithWrapper(args, pwd);
    }

    public void runWithWrapper(String[] args, String pwd) {
        File pwdFile = new File(pwd);

        // Peek into the wrapper properties file, to see if they have a preferred distribution
        String distributionUrl = lookupDistributionUrl(pwdFile);

        GradleConnector connector = GradleConnector.newConnector().forProjectDirectory(pwdFile);

        // Configure the tooling API
        if (distributionUrl != null && !distributionUrl.isEmpty()) {
            try {
                URI distributionUri = new URI(distributionUrl);
                connector.useDistribution(distributionUri);
            } catch(URISyntaxException use) {
                System.err.println("distributionUrl in ");
            }
        }

        ProjectConnection connection = null;
        try {
            connection = connector.connect();
            connection.newBuild()
                    .forTasks(args)
                    .run();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     *
     * @param pwdFile Base of a gradle project
     * @return null if there was no distributionUrl was found
     */
    public String lookupDistributionUrl(File pwdFile) {
        File wrapperPropertiesFile = new File(pwdFile, wrapperProperties);
        Properties wrapperProperties = new Properties();
        if (wrapperPropertiesFile.exists()) {
            try {
                Reader reader = new BufferedReader(new FileReader(wrapperPropertiesFile));
                wrapperProperties.load(reader);
            } catch (Exception e) {
            }
        }
        return wrapperProperties.getProperty("distributionUrl");
    }
}
