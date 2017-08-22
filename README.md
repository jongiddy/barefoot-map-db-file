# Barefoot Map File Maker

Convert the contents of a Barefoot roads database to a file.

To use a file input to map match:

- Build this code using Maven:
```sh
$ mvn install
```

- Run the JAR file from the `target` directory to create a roadmap file with a `.bfmap` suffix;
```sh
$ java -jar barefoot-mapfilemaker-1.0-SNAPSHOT-jar-with-dependencies.jar /vagrant/db.properties /vagrant/mymap.bfmap
```

- Set the database property `database.name` to the path of the roadmap file without the suffix. The `database.road-types` property must also exist, but other properties are ignored:
```properties
database.name=/vagrant/mymap
database.road-types=/vagrant/road-types.json
```
- Call `Loader.roadmap` with the second argument set to `true`.
```java
RoadMap map = Loader.roadmap("/vagrant/db.properties", true).construct();
```
