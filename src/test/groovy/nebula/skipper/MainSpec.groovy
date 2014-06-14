package nebula.skipper

import com.energizedwork.spock.extensions.TempDirectory
import spock.lang.Specification;

public class MainSpec extends Specification {

    @TempDirectory File tmpDir

    def 'can read Properties'() {
        def wrapperFile = new File(tmpDir, 'gradle/wrapper/gradle-wrapper.properties')
        wrapperFile.parentFile.mkdirs()
        wrapperFile << '''
            #Fri Jun 13 11:07:08 PDT 2014
            distributionBase=GRADLE_USER_HOME
            distributionPath=wrapper/dists
            zipStoreBase=GRADLE_USER_HOME
            zipStorePath=wrapper/dists
            distributionUrl=https\\://services.gradle.org/distributions/gradle-1.12-all.zip'''.stripIndent()

        when:
        String url = new Main().lookupDistributionUrl(tmpDir)

        then:
        url == 'https://services.gradle.org/distributions/gradle-1.12-all.zip'
    }

    def 'handle no file'() {
        def wrapperFile = new File(tmpDir, 'gradle/wrapper/gradle-wrapper.properties')
        wrapperFile.parentFile.mkdirs()

        when:
        String url = new Main().lookupDistributionUrl(tmpDir)

        then:
        url == null
    }

    def 'run wrapper'() {
        def wrapperFile = new File(tmpDir, 'gradle/wrapper/gradle-wrapper.properties')
        wrapperFile.parentFile.mkdirs()
        wrapperFile << '''
            #Fri Jun 13 11:07:08 PDT 2014
            distributionBase=GRADLE_USER_HOME
            distributionPath=wrapper/dists
            zipStoreBase=GRADLE_USER_HOME
            zipStorePath=wrapper/dists
            distributionUrl=https\\://services.gradle.org/distributions/gradle-1.11-all.zip'''.stripIndent()

        new File(tmpDir, 'build.gradle') << '''
            task output << {
                file('test.output') << "${gradle.gradleVersion}"
            }
            '''.stripIndent()
        when:
        Main main = new Main()
        main.runWithWrapper(['output'] as String[], tmpDir.absolutePath)

        then:
        def output = new File(tmpDir, 'test.output')
        output.exists()
        output.text == '1.11'
    }

}
