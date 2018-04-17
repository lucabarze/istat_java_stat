This is a repository for reading data from ISTAT API from http://apistat.istat.it, 
formatted with JSON-stat and write the results into a relational database.

Modify the application.conf file with your database and query parameters.

Default databases are MySQL and Vertica. In case you want to use Vertica, you have to
get the drivers from https://my.vertica.com.

You can find an explanation of how to use ISTAT data here (https://medium.com/@vincpatruno/come-accedere-ai-dati-statistici-pubblicati-dallistituto-nazionale-di-statistica-istat-ca874316f5a9).

Tables should have two columns (date: Date, value: Number).
