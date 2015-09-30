# perfrepo-client
Wrapper for Perfrepo client

Pushes data from *.csv file to perfrepo via  [PerfRepo](https://github.com/PerfRepo/PerfRepo).

Maven command:
```
mvn clean compile assembly:single
```

Java arguments structure:
```
java -Dhost=HOST:PORT -Durl=URL -DbasicHash=base64(username:password) -DsettingsFile=SETTINGS.yml -DtestUID=haw_write_stability_test -DtestExecutionName= -jar PerfRepo-Client-Wrapper-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```
