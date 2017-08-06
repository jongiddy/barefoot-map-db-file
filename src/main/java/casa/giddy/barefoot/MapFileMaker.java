package casa.giddy.barefoot;

import com.bmwcarit.barefoot.road.BaseRoad;
import com.bmwcarit.barefoot.road.BfmapWriter;
import com.bmwcarit.barefoot.road.PostGISReader;
import com.bmwcarit.barefoot.roadmap.Loader;
import com.bmwcarit.barefoot.util.SourceException;
import com.bmwcarit.barefoot.util.Tuple;
import org.json.JSONException;

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

        try {
            new MapFileMaker().MakeMapFile(dbProperties, mapFile);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void MakeMapFile(String dbProperties, String mapFile) throws IOException, JSONException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(dbProperties));

        String host = properties.getProperty("database.host");
        if (host == null) {
            throw new SourceException("No database host specified in database properties");
        }
        int port = Integer.parseInt(properties.getProperty("database.port", "0"));
        String database = properties.getProperty("database.name");
        if (database == null) {
            throw new SourceException("No database name specified in database properties");
        }
        String table = properties.getProperty("database.table");
        if (table == null) {
            throw new SourceException("No database table specified in database properties");
        }
        String user = properties.getProperty("database.user");
        if (user == null) {
            throw new SourceException("No database user specified in database properties");
        }
        String password = properties.getProperty("database.password");
        if (password == null) {
            throw new SourceException("No database password specified in database properties");
        }
        String path = properties.getProperty("database.road-types");
        if (path == null) {
            throw new SourceException("No road types file specified in database properties");
        }

        Map<Short, Tuple<Double, Integer>> config = Loader.read(path);

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
