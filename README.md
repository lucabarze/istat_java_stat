This is a repository for reading data from ISTAT API from http://apistat.istat.it, 
formatted with JSON-stat and write the results into a relational database.

Modify the application.conf file with your database and query parameters.

Default databases are MySQL and Vertica. In case you want to use Vertica, you have to
get the drivers from drivers here:

https://my.vertica.com/download/vertica/client-drivers/

Once you have the files you want (i.e. vertica-jdbc-9.0.1-7.jar) you should be able to run a command something like the following to add it to your local repository:

```
mvn install:install-file -Dfile={/path_to/vertica-jdbc-9.0.1-7.jar} -DgroupId=com.vertica -DartifactId=vjdbc -Dversion=9.0.1 -Dpackaging=jar
```

You can find an explanation of how to use ISTAT data here (https://medium.com/@vincpatruno/come-accedere-ai-dati-statistici-pubblicati-dallistituto-nazionale-di-statistica-istat-ca874316f5a9).

Tables should have two columns `(date: Date, value: Number)`.
