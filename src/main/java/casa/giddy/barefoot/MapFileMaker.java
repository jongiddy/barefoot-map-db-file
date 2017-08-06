package casa.giddy.barefoot;

import com.bmwcarit.barefoot.road.BaseRoad;
import com.bmwcarit.barefoot.road.BfmapWriter;
import com.bmwcarit.barefoot.road.PostGISReader;
import com.bmwcarit.barefoot.roadmap.Loader;
import com.bmwcarit.barefoot.util.Tuple;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Make Barefoot map file
 *
 */
public class MapFileMaker
{
    public static void main( String[] args )
    {
        if (args.length != 2) {
            System.err.println("Usage: MapFileMaker <db-properties> <map-file>");
            System.exit(1);
        }

        String dbProperties = args[0];
        String mapFile = args[1];

        new MapFileMaker().MakeMapFile(dbProperties, mapFile);
    }

    private void MakeMapFile(String dbProperties, String mapFile) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(dbProperties));
        } catch (IOException e) {
            System.err.println(String.format("Cannot read properties file %s", dbProperties));
            System.err.println(e);
            System.exit(1);
        }

        String host = properties.getProperty("database.host");
        if (host == null) {
            System.err.println("No database host specified in database properties");
            System.exit(1);
        }
        int port = Integer.parseInt(properties.getProperty("database.port", "0"));
        String database = properties.getProperty("database.name");
        if (database == null) {
            System.err.println("No database name specified in database properties");
            System.exit(1);
        }
        String table = properties.getProperty("database.table");
        if (table == null) {
            System.err.println("No database table specified in database properties");
            System.exit(1);
        }
        String user = properties.getProperty("database.user");
        if (user == null) {
            System.err.println("No database user specified in database properties");
            System.exit(1);
        }
        String password = properties.getProperty("database.password");
        if (password == null) {
            System.err.println("No database password specified in database properties");
            System.exit(1);
        }
        String path = properties.getProperty("database.road-types");
        if (path == null) {
            System.err.println("No road types file specified in database properties");
            System.exit(1);
        }

        Map<Short, Tuple<Double, Integer>> config = null;
        try {
            config = Loader.read(path);
        } catch (Exception e) {
            System.err.println(String.format("Cannot read road map file %s", path));
            System.err.println(e);
            System.exit(1);
        }

        PostGISReader reader = new PostGISReader(host, port, database, table, user, password, config);
        BfmapWriter writer = new BfmapWriter(mapFile);

        ExtractMap(reader, writer);
    }

    private void ExtractMap(PostGISReader reader, BfmapWriter writer) {
        reader.open();
        try {
            writer.open();
            try {
                BaseRoad road = reader.next();
                while (road != null) {
                    writer.write(road);
                    road = reader.next();
                }
            } finally {
                writer.close();
            }
        } finally {
            reader.close();
        }
    }
}
