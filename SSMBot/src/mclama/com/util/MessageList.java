package mclama.com.util;

import static to.us.harha.twitchai.util.GenUtils.exit;
import static to.us.harha.twitchai.util.Globals.g_bot_chan;
import static to.us.harha.twitchai.util.Globals.g_bot_name;
import static to.us.harha.twitchai.util.Globals.g_bot_oauth;
import static to.us.harha.twitchai.util.Globals.g_bot_reqCommands;
import static to.us.harha.twitchai.util.Globals.g_bot_reqMembership;
import static to.us.harha.twitchai.util.Globals.g_bot_reqTags;
import static to.us.harha.twitchai.util.Globals.g_debug;
import static to.us.harha.twitchai.util.LogUtils.logErr;
import static to.us.harha.twitchai.util.LogUtils.logMsg;

import static to.us.harha.twitchai.util.Globals.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import to.us.harha.twitchai.util.FileUtils;

public class MessageList {

    public static void init()
    {
        File f = new File("data/responses.cfg");
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
        Properties p = new Properties();
        OutputStream o = null;
        try
        {
            o = new FileOutputStream("data/responses.cfg");

            // Set each variable
            p.setProperty("ml_submit_with_no_level", "To add your level to the list, use !submit code.");
            p.setProperty("ml_submit", "Thanks {sender}, your level will be in position {place} on the queue!");
            p.setProperty("ml_current", "Current level code: {current} by {currentauthor}.");
            p.setProperty("ml_previous", "Last level code: {previous} by {previousauthor}.");
            p.setProperty("ml_submit_open", "Sorry, Level submission is currently closed.");
            p.setProperty("ml_submit_delay", "There is a {delay} minute delay between submissions. Please wait a little longer {sender}.");
            p.setProperty("ml_on_next", "Next level is {current} by {currentauthor}. {queue_size} remaining in queue.");
            p.setProperty("ml_on_random", "Random level is {current} by {currentauthor}. {queue_size} remaining in queue.");
            p.setProperty("ml_queue", "The current queue has {queue_size} levels! Your next level is in position {place}.");
            p.setProperty("ml_no_queue", "No maps in the queue! Maybe someone would like to !submit code?");
            // p.setProperty("ml_l", "");
        	

            // Store the variables
            p.store(o, null);

            // Close the outputstream object
            o.close();

            logMsg("responses.cfg" + " Created succesfully!");
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
        	String line;
            i = new FileInputStream("data/responses.cfg");

            // Load the file
            p.load(i);

            // Get the properties and set the config variables
            ml_submit_with_no_level = String.valueOf(p.getProperty("ml_submit_with_no_level"));
            ml_submit = String.valueOf(p.getProperty("ml_submit"));
            ml_current = String.valueOf(p.getProperty("ml_current"));
            ml_previous = String.valueOf(p.getProperty("ml_previous"));
            ml_submit_open = String.valueOf(p.getProperty("ml_submit_open"));
            ml_submit_delay = String.valueOf(p.getProperty("ml_submit_delay"));
            ml_on_next = String.valueOf(p.getProperty("ml_on_next"));
            ml_on_random = String.valueOf(p.getProperty("ml_on_random"));
            ml_queue = String.valueOf(p.getProperty("ml_queue"));
            ml_no_queue = String.valueOf(p.getProperty("ml_no_queue"));
            //v1.2.0
            
            //line = String.valueOf(p.getProperty("ml_commands_list"));
            if(!((line = String.valueOf(p.getProperty("ml_commands_list"))).equals("null"))) ml_commands_list = line; else 
            	FileUtils.writeToTextFile("data/", "responses.cfg", "ml_commands_list=" + ml_commands_list);
            if(!((line = String.valueOf(p.getProperty("ml_delete_with_no_level"))).equals("null"))) ml_delete_with_no_level = line; else 
            	FileUtils.writeToTextFile("data/", "responses.cfg", "ml_delete_with_no_level=" + ml_delete_with_no_level);
            if(!((line = String.valueOf(p.getProperty("ml_delete"))).equals("null"))) ml_delete = line; else 
            	FileUtils.writeToTextFile("data/", "responses.cfg", "ml_delete=" + ml_delete);
            if(!((line = String.valueOf(p.getProperty("ml_delete_invalid"))).equals("null"))) ml_delete_invalid = line; else 
            	FileUtils.writeToTextFile("data/", "responses.cfg", "ml_delete_invalid=" + ml_delete_invalid);
            if(!((line = String.valueOf(p.getProperty("ml_list"))).equals("null"))) ml_list = line; else 
            	FileUtils.writeToTextFile("data/", "responses.cfg", "ml_list=" + ml_list);
            if(!((line = String.valueOf(p.getProperty("ml_queuelist"))).equals("null"))) ml_queuelist = line; else 
            	FileUtils.writeToTextFile("data/", "responses.cfg", "ml_queuelist=" + ml_queuelist);
            if(!((line = String.valueOf(p.getProperty("ml_queuelist_syntax"))).equals("null"))) ml_queuelist_syntax = line; else 
            	FileUtils.writeToTextFile("data/", "responses.cfg", "ml_queuelist_syntax=" + ml_queuelist_syntax);
            if(!((line = String.valueOf(p.getProperty("ml_list_completed"))).equals("null"))) ml_list_completed = line; else 
            	FileUtils.writeToTextFile("data/", "responses.cfg", "ml_list_completed=" + ml_list_completed);
            if(!((line = String.valueOf(p.getProperty("ml_duplicate_submission"))).equals("null"))) ml_duplicate_submission = line; else 
            	FileUtils.writeToTextFile("data/", "responses.cfg", "ml_duplicate_submission=" + ml_duplicate_submission);
            if(!((line = String.valueOf(p.getProperty("ml_streamer_submission_list"))).equals("null"))) ml_streamer_submission_list = line; else 
            	FileUtils.writeToTextFile("data/", "responses.cfg", "ml_streamer_submission_list=" + ml_streamer_submission_list);
        	
        	// Close the inputstream object
            i.close();
        	
            if(ml_commands_list.equals("null")){
            	System.out.println("nulllll");
            }

            logMsg("responses.cfg" + " loaded succesfully!");
        } catch (IOException e)
        {
            logErr("Couldn't load the main responses file, closing program...");
            exit(1);
        }
    }
}
