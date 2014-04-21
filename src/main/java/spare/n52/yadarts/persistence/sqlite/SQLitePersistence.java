/**
 * Copyright 2014 the staff of 52°North Initiative for Geospatial Open
 * Source Software GmbH in their free time
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spare.n52.yadarts.persistence.sqlite;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spare.n52.yadarts.persistence.PersistencyException;

public class SQLitePersistence extends AbstractJDBCPersistence {
    
    private static Logger LOG = LoggerFactory.getLogger(SQLitePersistence.class);
    
    public SQLitePersistence() throws PersistencyException {
        super();
    }
    
    @Override
    protected Connection createConnection() throws SQLException, IOException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Could not find the SQLite driver.");
        }
        
        File f = resolveFile();
        return DriverManager.getConnection("jdbc:sqlite:".concat(f
            .getAbsolutePath()));
    }
    
    protected File resolveFile() throws IOException {
        String legacyHighscoreFileName = getLegacyHighscorePath();
        
        // search in user home and current path / data
        String userHome = System.getProperty("user.home");
        // if file is not found in user home
        File userHomeHighscore = new File(
            getUserHomeHighscorePath(userHome, legacyHighscoreFileName));
        
        if (!userHomeHighscore.exists()) {
            File legacyDataHighscore = new File(legacyHighscoreFileName);
            LOG.info("Highscore file not found in '{}'. Searching in '{}'.",
                userHomeHighscore.getAbsolutePath(),
                legacyDataHighscore.getAbsolutePath());
            // search in data
            if (legacyDataHighscore.exists() && legacyDataHighscore.canRead()) {
                LOG.info("Found legacy highscore in '{}'.",
                    legacyDataHighscore.getAbsolutePath());
                // if found => move to user home
                FileUtils.copyFile(legacyDataHighscore, userHomeHighscore);
                LOG.info(
                    String.format("Moved legacy highscore db from '%s' to '%s'.",
                    legacyDataHighscore.getAbsolutePath(),
                    userHomeHighscore.getAbsolutePath()));
            }
            else if (!userHomeHighscore.createNewFile()) {
                throw new IOException(String.format("Could not create the DB file: ",
                        legacyDataHighscore.getAbsoluteFile()));
            }
        }
        if (!userHomeHighscore.exists() || !userHomeHighscore.canWrite()) {
            throw new IOException(
                String.format("Could access to DB file '%s'.",
                userHomeHighscore.getAbsolutePath()));
        }
        return userHomeHighscore;
    }

    protected static String getLegacyHighscorePath() {
        return new StringBuilder(".")
            .append(File.separator)
            .append(SQLiteConstants.DATA_BASE_DIRECTORY)
            .append(File.separator)
            .append(SQLiteConstants.HIGHSCORE_FILE)
            .toString();
    }

    protected static String getUserHomeHighscorePath(String userHome, String legacyHighscoreFileName) {
        return userHome
            .concat(File.separator)
            .concat(".yadarts")
            .concat(File.separator)
            .concat(legacyHighscoreFileName);
    }
    
}
