package libconfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class manages the user defined configurations.
 * The configuration file is expected to be in ~/.magma-playout.conf
 * 
 * @author rombus
 */
public class ConfigurationManager {
    private static ConfigurationManager instance;

    private static final String CONFIG_PATH = System.getProperty("user.home")+File.separator+".magma-playout.conf";

    private static final String REDIS_HOST_KEY = "redis_server_hostname";
    private static final String REDIS_PORT_KEY = "redis_server_port";
    private static final String REDIS_PCCP_CHANNEL_KEY = "redis_pccp_channel";
    private static final String REDIS_FSCP_CHANNEL_KEY = "redis_fscp_channel";
    private static final String REDIS_PCR_CHANNEL_KEY = "redis_pcr_channel";
    private static final String REDIS_MSTA_CHANNEL_KEY = "redis_msta_channel";
    private static final String REDIS_RECONNECTION_TIMEOUT_KEY = "redis_reconnection_timeout";

    private static final String MELTED_HOST_KEY = "melted_server_hostname";
    private static final String MELTED_PORT_KEY = "melted_server_port";
    private static final String MELTED_RECONNECTION_TIMEOUT_KEY = "melted_reconnection_timeout";
    private static final String MELTED_RECONNECTION_TRIES_KEY = "melted_reconnection_tries";
    /**
     * This key defines the duration of melted playlist. This is used for
     * avoiding overloading melted's playlist.
     * The APND's over melted are controlled by the MeltedProxy class.
     *
     * In Minutes
     */
    private static final String MELTED_PLAYLIST_MAX_DURATION = "melted_playlist_max_duration";
    /**
     * This key defines the polling interval for the melted appender module.
     *
     * In Minutes
     */
    private static final String MELTED_APPENDER_WORKER_FREQ = "melted_appender_worker_freq";
    private static final String MELT_PATH_KEY = "melt_path";

    /**
     * The path of the default media that will be played when there's nothing else loaded.
     * Must be an "MLT XML" .mlt file.
     * Must be an absolute path without shell modifiers like ~/
     */
    private static final String DEFAULT_MEDIA_PATH = "default_media_path";

    /**
     * The path where the spacers mlt files are going to be generated.
     * Needs to be an absolute path (without shell modifiers like ~/)
     */
    private static final String MLT_SPACERS_PATH = "mlt_spacers_path";

    private static final String FILTER_SERVER_URL_KEY = "filter_server_hostname";

    private static final String BASH_TIMEOUT_KEY = "bash_timeout_ms";

    /**
     * URL of mp-playout-api. Must be a valid URL.
     */
    private static final String PLAYOUT_API_URL = "playout_api_url";

    /**
     * The FPS of all medias loaded in the system.
     * Note that if you change this you'll have to reload all your medias and pieces with the new configuration.
     */    
    
    private static final String MEDIAS_FPS = "medias_fps";
    
    /**
     * MP-Devourer
     */
    private static final String DEVOURER_INPUT_DIR = "devourer_input_dir";
    private static final String DEVOURER_OUTPUT_DIR = "devourer_output_dir";
    private static final String DEVOURER_MEDIA_DIR = "devourer_media_dir";
    private static final String DEVOURER_THUMB_DIR = "devourer_thumb_dir";
    private static final String DEVOURER_FFMPEG_PATH = "devourer_ffmpeg_path";
    private static final String DEVOURER_FFPROBE_PATH = "devourer_ffprobe_path";
    private static final String DEVOURER_MELT_PATH = "devourer_melt_path";

    private Properties properties;

    private ConfigurationManager(){
    }

    public static ConfigurationManager getInstance(){
        if(instance==null){
            instance = new ConfigurationManager();
        }
        return instance;
    }

    /**
     * Reads the configuration file into a Properties object.
     * If the file doesn't exists it creates one with default values.
     * If there are IO errors, a warning is logged and the application
     * continues by using the default values.
     *
     * @param logger The application logger
     */
    public void init(Logger logger){
        properties = setDefaultValues(new Properties());
        boolean ioError = false;

        try (FileInputStream configFile = new FileInputStream(CONFIG_PATH)) {
            properties.load(configFile);
        }
        catch (FileNotFoundException e){
            try (FileOutputStream out = new FileOutputStream(CONFIG_PATH)) {
                logger.log(Level.WARNING, "Configuration file not found at {0}. Creating it with default values.", CONFIG_PATH);
                properties.store(out, "Magma Playout Configuration File");
            }
            catch (IOException ex) {
                ioError = true;
            }
        }
        catch (IOException ex) {
            ioError = true;
        }

        if(ioError){
            logger.log(Level.WARNING, "Failed reading/writing the configuration file. Continuing with default values.");
        }
    }

    /**
     * Default configuration values are added here.
     *
     * @param p Properties object where to load the default values
     * @return Returns the Properties object received as an argument for convenience
     */
    private Properties setDefaultValues(Properties p){
        p.setProperty(REDIS_HOST_KEY, "localhost");
        p.setProperty(REDIS_PORT_KEY, "6379");
        p.setProperty(REDIS_PCCP_CHANNEL_KEY, "PCCP");
        p.setProperty(REDIS_FSCP_CHANNEL_KEY, "FSCP");
        p.setProperty(REDIS_PCR_CHANNEL_KEY, "PCR");
        p.setProperty(REDIS_MSTA_CHANNEL_KEY, "MSTA");
        p.setProperty(REDIS_RECONNECTION_TIMEOUT_KEY, "1000");

        p.setProperty(MELTED_HOST_KEY, "localhost");
        p.setProperty(MELTED_PORT_KEY, "5250");
        p.setProperty(MELTED_RECONNECTION_TIMEOUT_KEY, "1000");
        p.setProperty(MELTED_RECONNECTION_TRIES_KEY, "0");
        p.setProperty(MELTED_PLAYLIST_MAX_DURATION, "120"); // 2 hs
        p.setProperty(MELTED_APPENDER_WORKER_FREQ, "5");    // 5 mins

        p.setProperty(DEFAULT_MEDIA_PATH, "/usr/local/magma-playout/default.mlt");
        p.setProperty(MLT_SPACERS_PATH, "/usr/local/magma-playout/spacers/");

        p.setProperty(MELT_PATH_KEY, "/usr/bin/melt/melt");
        p.setProperty(FILTER_SERVER_URL_KEY, "http://localhost:3001/filter-banner.html");
        p.setProperty(BASH_TIMEOUT_KEY, "5000");

        p.setProperty(PLAYOUT_API_URL, "http://localhost:8001/api/");
        p.setProperty(MEDIAS_FPS, "60");
        
        p.setProperty(DEVOURER_INPUT_DIR, "EDIT ME!--> ~/Videos/input");
        p.setProperty(DEVOURER_OUTPUT_DIR, "EDIT ME!--> ~/Videos/output");
        p.setProperty(DEVOURER_MEDIA_DIR, "EDIT ME!--> ~/Videos/");
        p.setProperty(DEVOURER_THUMB_DIR, "EDIT ME!--> /XXX/mp-installer/magma-playout/gui/mp-ui-playout/src/assets/img");
        p.setProperty(DEVOURER_FFMPEG_PATH, "EDIT ME!--> /XXX/mp-installer//MagmaPlayout/core/melted/XXXXXXX/bin/ffmpeg");
        p.setProperty(DEVOURER_FFPROBE_PATH, "EDIT ME!--> /XXX/mp-installer//MagmaPlayout/core/melted/XXXXXXX/bin/ffprobe");
        p.setProperty(DEVOURER_MELT_PATH, "EDIT ME!--> /XXX/mp-installer//MagmaPlayout/core/melted/XXXXXXX/bin/melt");
        

        return p;
    }

    
    public String getRedisHost(){
        return properties.getProperty(REDIS_HOST_KEY);
    }
    
    public int getRedisPort(){
        return Integer.parseInt(properties.getProperty(REDIS_PORT_KEY));
    }

    public String getRedisPccpChannel(){
        return properties.getProperty(REDIS_PCCP_CHANNEL_KEY);
    }

    public String getRedisFscpChannel(){
        return properties.getProperty(REDIS_FSCP_CHANNEL_KEY);
    }

    public String getRedisPcrChannel(){
        return properties.getProperty(REDIS_PCR_CHANNEL_KEY);
    }

    public String getRedisMstaChannel(){
        return properties.getProperty(REDIS_MSTA_CHANNEL_KEY);
    }
    
    public int getRedisReconnectionTimeout(){
        return Integer.parseInt(properties.getProperty(REDIS_RECONNECTION_TIMEOUT_KEY));
    }

    public String getMeltedHost(){
        return properties.getProperty(MELTED_HOST_KEY);
    }

    public int getMeltedPort(){
        return Integer.parseInt(properties.getProperty(MELTED_PORT_KEY));
    }

    public int getMeltedReconnectionTimeout(){
        return Integer.parseInt(properties.getProperty(MELTED_RECONNECTION_TIMEOUT_KEY));
    }

    public int getMeltedReconnectionTries(){
        return Integer.parseInt(properties.getProperty(MELTED_RECONNECTION_TRIES_KEY));
    }

    public int getMeltedPlaylistMaxDuration(){
        return Integer.parseInt(properties.getProperty(MELTED_PLAYLIST_MAX_DURATION));
    }

    public int getMeltedAppenderWorkerFreq(){
        return Integer.parseInt(properties.getProperty(MELTED_APPENDER_WORKER_FREQ));
    }

    public String getMeltPath(){
        return properties.getProperty(MELT_PATH_KEY);
    }

    public String getDefaultMediaPath(){
        return properties.getProperty(DEFAULT_MEDIA_PATH);
    }

    public String getMltSpacersPath(){
        return properties.getProperty(MLT_SPACERS_PATH);
    }

    public String getFilterServerHost(){
        return properties.getProperty(FILTER_SERVER_URL_KEY);
    }

    public int getMeltXmlTimeout(){
        return Integer.parseInt(properties.getProperty(BASH_TIMEOUT_KEY));
    }

    public String getRestBaseUrl(){
        return properties.getProperty(PLAYOUT_API_URL);
    }

    public int getMediasFPS(){
        return Integer.parseInt(properties.getProperty(MEDIAS_FPS));
    }

    public String getDevourerInputDir() {
        return properties.getProperty(DEVOURER_INPUT_DIR);
    }

    public String getDevourerOutputDir() {
        return properties.getProperty(DEVOURER_OUTPUT_DIR);
    }

    public String getDevourerMediaDir() {
        return properties.getProperty(DEVOURER_MEDIA_DIR);
    }

    public String getDevourerThumbDir() {
        return properties.getProperty(DEVOURER_THUMB_DIR);
    }

    public String getDevourerFfmpegPath() {
        return properties.getProperty(DEVOURER_FFMPEG_PATH);
    }

    public String getDevourerFfprobePath() {
        return properties.getProperty(DEVOURER_FFPROBE_PATH);
    }

    public String getDevourerMeltPath() {
        return properties.getProperty(DEVOURER_MELT_PATH);
    }
    
    
    
    /**
     * Logs all the parameters loaded.
     * For debug use.
     * 
     * @param logger
     */
    public void printConfig(Logger logger){
        logger.log(Level.INFO,
            "Loaded configuration:\n"
            +"\n\tCONFIG_PATH: " + ConfigurationManager.CONFIG_PATH
            +"\n\tREDIS_HOST_KEY: " + properties.getProperty(REDIS_HOST_KEY)
            +"\n\tREDIS_PORT_KEY: " + properties.getProperty(REDIS_PORT_KEY)
            +"\n\tREDIS_PCCP_CHANNEL_KEY: " + properties.getProperty(REDIS_PCCP_CHANNEL_KEY)
            +"\n\tREDIS_FSCP_CHANNEL_KEY: " + properties.getProperty(REDIS_FSCP_CHANNEL_KEY)
            +"\n\tREDIS_PCR_CHANNEL_KEY: " + properties.getProperty(REDIS_PCR_CHANNEL_KEY)
            +"\n\tREDIS_MSTA_CHANNEL_KEY: " + properties.getProperty(REDIS_MSTA_CHANNEL_KEY)
            +"\n\tREDIS_RECONNECTION_TIMEOUT_KEY: " + properties.getProperty(REDIS_RECONNECTION_TIMEOUT_KEY)
            +"\n\tMELTED_HOST_KEY: " + properties.getProperty(MELTED_HOST_KEY)
            +"\n\tMELTED_PORT_KEY: " + properties.getProperty(MELTED_PORT_KEY)
            +"\n\tMELTED_RECONNECTION_TIMEOUT_KEY: " + properties.getProperty(MELTED_RECONNECTION_TIMEOUT_KEY)
            +"\n\tMELTED_RECONNECTION_TRIES_KEY: " + properties.getProperty(MELTED_RECONNECTION_TRIES_KEY)
            +"\n\tMELTED_PLAYLIST_MAX_DURATION: " + properties.getProperty(MELTED_PLAYLIST_MAX_DURATION)
            +"\n\tMELTED_APPENDER_WORKER_FREQ: " + properties.getProperty(MELTED_APPENDER_WORKER_FREQ)
            +"\n\tMELT_PATH_KEY: " + properties.getProperty(MELT_PATH_KEY)
            +"\n\tDEFAULT_MEDIA_PATH: " + properties.getProperty(DEFAULT_MEDIA_PATH)
            +"\n\tMLT_SPACERS_PATH: " + properties.getProperty(MLT_SPACERS_PATH)
            +"\n\tFILTER_SERVER_URL_KEY: " + properties.getProperty(FILTER_SERVER_URL_KEY)
            +"\n\tBASH_TIMEOUT_KEY: " + properties.getProperty(BASH_TIMEOUT_KEY)
            +"\n\tREST_BASE_URL: " + properties.getProperty(PLAYOUT_API_URL)
            +"\n\tMEDIAS_FPS: " + properties.getProperty(MEDIAS_FPS)
            +"\n\tDEVOURER_INPUT_DIR: " + properties.getProperty(DEVOURER_INPUT_DIR)
            +"\n\tDEVOURER_OUTPUT_DIR: " + properties.getProperty(DEVOURER_OUTPUT_DIR)
            +"\n\tDEVOURER_MEDIA_DIR: " + properties.getProperty(DEVOURER_MEDIA_DIR)
            +"\n\tDEVOURER_THUMB_DIR: " + properties.getProperty(DEVOURER_THUMB_DIR)
            +"\n\tDEVOURER_FFMPEG_PATH: " + properties.getProperty(DEVOURER_FFMPEG_PATH)
            +"\n\tDEVOURER_FFPROBE_PATH: " + properties.getProperty(DEVOURER_FFPROBE_PATH)        
            +"\n\tDEVOURER_MELT_PATH: " + properties.getProperty(DEVOURER_MELT_PATH)        
            +"\n"
        );
    }
}
