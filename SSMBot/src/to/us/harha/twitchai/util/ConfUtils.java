package to.us.harha.twitchai.util;

import static to.us.harha.twitchai.util.Globals.*;
import static to.us.harha.twitchai.util.LogUtils.logMsg;
import static to.us.harha.twitchai.util.LogUtils.logErr;
import static to.us.harha.twitchai.util.GenUtils.exit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import to.us.harha.twitchai.Main;

public class ConfUtils
{

    public static void init()
    {
        File f = new File("data/config.cfg");
        if (f.exists() && !f.isDirectory())
        {
            load();
        }
//        else
//        {
//            create();
//            init();
//        }
    }


    public static void create()
    {
    	
//    	URL inputUrl = Main.class.getResource("res/config.cfg");
//    	File dest = new File("data/config.cfg");
//    	try {
//			org.apache.commons.io.FileUtils.copyURLToFile(inputUrl, dest);
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//    	
        Properties p = new Properties();
        OutputStream o = null;
        try
        {
            o = new FileOutputStream("data/config.cfg");

            // Set each variable
            p.setProperty("g_debug", "false");
            p.setProperty("g_bot_reqMembership", "true");
            p.setProperty("g_bot_reqCommands", "true");
            p.setProperty("g_bot_reqTags", "false");
            p.setProperty("g_bot_name", "TwitchAI");
            p.setProperty("g_bot_oauth", "youroauth");
            p.setProperty("g_bot_chan", "#IllusionAI");
            //Super Mario Maker config
            p.setProperty("g_smm_submits_open", "true");
            p.setProperty("g_smm_message_all", "true");
            p.setProperty("g_smm_remove_after_nextrandom", "true");
            p.setProperty("g_smm_allow_previously_done_levels", "true");
            p.setProperty("g_smm_record_levels_played", "true");
            p.setProperty("g_smm_record_previous_levels_played", "true");
            p.setProperty("g_smm_follow_required", "true");
            p.setProperty("g_smm_submit_delay", "5");
            p.setProperty("g_smm_submit_max_in_list", "2");
            p.setProperty("g_smm_max_queue", "100");
            
            //p.setProperty("g_smm_", "");

            // Store the variables
            p.store(o, null);

            // Close the outputstream object
            o.close();

            logMsg("config.cfg" + " Created succesfully!");
        } catch (IOException e)
        {
            logErr("Couldn't create the main configuration file, closing program...");
            exit(1);
        }
    }

    public static void load()
    {
        Properties p = new Properties();
        InputStream i = null;
        try
        {
            i = new FileInputStream("data/config.cfg");

            // Load the file
            p.load(i);
            
            String line;
            boolean bool;
            // Get the properties and set the config variables
            g_debug = Boolean.valueOf(p.getProperty("g_debug"));
            g_bot_reqMembership = Boolean.valueOf(p.getProperty("g_bot_reqMembership"));
            g_bot_reqCommands = Boolean.valueOf(p.getProperty("g_bot_reqCommands"));
            g_bot_reqTags = Boolean.valueOf(p.getProperty("g_bot_reqTags"));
            g_bot_name = String.valueOf(p.getProperty("g_bot_name"));
            g_bot_oauth = String.valueOf(p.getProperty("g_bot_oauth"));
            g_bot_chan = String.valueOf(p.getProperty("g_bot_chan"));
            
            if(!g_bot_chan.contains("#")){g_bot_chan = "#" + g_bot_chan;}
            //SMM
            g_smm_submit_max_in_list = Integer.valueOf(p.getProperty("g_smm_submit_max_in_list"));
    		g_smm_submits_open = Boolean.valueOf(p.getProperty("g_smm_submits_open"));
    		g_smm_remove_after_nextrandom = Boolean.valueOf(p.getProperty("g_smm_remove_after_nextrandom"));
    		g_smm_submit_delay = Integer.valueOf(p.getProperty("g_smm_submit_delay"));
    		g_smm_message_all = Boolean.valueOf(p.getProperty("g_smm_message_all"));
    		g_smm_max_queue = Integer.valueOf(p.getProperty("g_smm_max_queue"));
    		g_smm_record_previous_levels_played = Boolean.valueOf(p.getProperty("g_smm_record_previous_levels_played"));
    		g_smm_allow_previously_done_levels = Boolean.valueOf(p.getProperty("g_smm_allow_previously_done_levels"));
    		g_smm_record_levels_played = Boolean.valueOf(p.getProperty("g_smm_record_levels_played"));
    		g_smm_follow_required = Boolean.valueOf(p.getProperty("g_smm_follow_required"));
    		g_smm_play_beep_sound_on_first_level = Boolean.valueOf(p.getProperty("g_smm_play_beep_sound_on_first_level")); 
    		//v1.2.0 additions of SMM
    		
    		if(((bool = Boolean.valueOf(p.getProperty("g_smm_allow_duplicate_submissions")) == null))) g_smm_allow_duplicate_submissions = bool; else 
            	FileUtils.writeToTextFile("data/", "config.cfg", "g_smm_allow_duplicate_submissions=" + g_smm_allow_duplicate_submissions);
    		if(((bool = Boolean.valueOf(p.getProperty("g_smm_allow_command_delete")) == null))) g_smm_allow_command_delete = bool; else 
            	FileUtils.writeToTextFile("data/", "config.cfg", "g_smm_allow_command_delete=" + g_smm_allow_command_delete);
    		
    		if(!((line = String.valueOf(p.getProperty("g_smm_url_list"))).equals("null"))) g_smm_url_list = line; else 
            	FileUtils.writeToTextFile("data/", "config.cfg", "g_smm_url_list=" + g_smm_url_list);
    		if(!((line = String.valueOf(p.getProperty("g_smm_url_list_streamer"))).equals("null"))) g_smm_url_list_streamer = line; else 
            	FileUtils.writeToTextFile("data/", "config.cfg", "g_smm_url_list_streamer=" + g_smm_url_list_streamer);
    		if(!((line = String.valueOf(p.getProperty("g_smm_url_list_completed"))).equals("null"))) g_smm_url_list_completed = line; else 
            	FileUtils.writeToTextFile("data/", "config.cfg", "g_smm_url_list_completed=" + g_smm_url_list_completed);
    		
            //FORMS
    		g_forms_use = Boolean.valueOf(p.getProperty("g_forms_use"));
			g_forms_url = String.valueOf(p.getProperty("g_forms_url"));
			g_forms_submit_entry = String.valueOf(p.getProperty("g_forms_submit_entry"));
			g_forms_level_entry = String.valueOf(p.getProperty("g_forms_level_entry"));
			g_forms_use_completed = Boolean.valueOf(p.getProperty("g_forms_use_completed"));
			g_forms_completed_url = String.valueOf(p.getProperty("g_forms_completed_url"));
			g_forms_completed_submit_entry = String.valueOf(p.getProperty("g_forms_completed_submit_entry"));
			g_forms_completed_level_entry = String.valueOf(p.getProperty("g_forms_completed_level_entry"));
			g_forms_completed_time_entry = String.valueOf(p.getProperty("g_forms_completed_time_entry"));
			g_forms_completed_deaths_entry = String.valueOf(p.getProperty("g_forms_completed_deaths_entry"));
			
            // Close the inputstream object
            i.close();

            logMsg("config.cfg" + " loaded succesfully!");
        } catch (IOException e)
        {
            logErr("Couldn't load the main configuration file, closing program...");
            exit(1);
        }
    }

}