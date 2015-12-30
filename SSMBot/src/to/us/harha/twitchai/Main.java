package to.us.harha.twitchai;

import static to.us.harha.twitchai.util.Globals.*;
import static to.us.harha.twitchai.util.LogUtils.logMsg;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;

import mclama.com.GUIWindow;
import mclama.com.util.MessageList;

import static to.us.harha.twitchai.util.LogUtils.logErr;
import static to.us.harha.twitchai.util.GenUtils.exit;
import to.us.harha.twitchai.bot.TwitchChannel;
import to.us.harha.twitchai.bot.TwitchUser;
import to.us.harha.twitchai.bot.TwitchAI;
import to.us.harha.twitchai.util.ConfUtils;
import to.us.harha.twitchai.util.FileUtils;

public class Main
{

    public static void main(String[] args)
    {
    	try {
			CodeSource codeSource = Main.class.getProtectionDomain().getCodeSource();
			File jarFile = new File(codeSource.getLocation().toURI().getPath());
			String jarDir = jarFile.getParentFile().getPath();
			System.out.println(jarDir);
			g_dir = jarDir;
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
    	
        FileUtils.directoryExists("data");
        FileUtils.directoryExists("data/channels");
        FileUtils.directoryExists("data/logs");
        FileUtils.directoryExists("data/errors");
        FileUtils.directoryExists("data/files");
        
        FileUtils.createFiles("current_level_author.txt");
        FileUtils.createFiles("current_level_deaths.txt");
        FileUtils.createFiles("current_level_time.txt");
        FileUtils.createFiles("current_level_title.txt");
        FileUtils.createFiles("current_level_difficulty.txt");
        FileUtils.createFiles("current_level.txt");
        FileUtils.createFiles("previous_level_author.txt");
        FileUtils.createFiles("previous_level.txt");
        
        initCreate();
        
        ConfUtils.init();
        MessageList.init();
        
        EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					g_gui = new GUIWindow();
					if(g_smm_use_gui){
			        	g_gui.getFrame().setVisible(true);
			        } else
			        	g_gui.getFrame().setVisible(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
        
        
        TwitchAI twitchai = new TwitchAI();
        g_BOT = twitchai;
        twitchai.init_twitch("irc.twitch.tv");
        
        TwitchAI twitchgroup = new TwitchAI();
        g_PBOT = twitchgroup;
        twitchgroup.init_twitch("199.9.253.120");
        

        int init_time = 5;
        while (!twitchai.isInitialized())
        {
            init_time--;
            try
            {
                logMsg("Waiting for twitch member/cmd/tag responses... " + init_time);
                Thread.sleep(1000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        if (!twitchai.isInitialized())
        {
            logErr("Failed to receive twitch member/cmd/tag permissions!");
            exit(1);
        }
        
        twitchai.init_channels();
        twitchgroup.init_channels();
        
        float time;
        long timeStart, timeEnd;

        logMsg("McSubmit is now running.");
        g_gui.getLblSubmitterinchat().setText("McSubmit is now running! Waiting on submissions.");
        
        while (twitchai.isConnected())
        {
            timeStart = System.nanoTime();
            g_date.setTime(System.currentTimeMillis());

            for (TwitchChannel c : twitchai.getTwitchChannels())
            {
                if (c.getCmdSent() > 0)
                {
                    c.setCmdSent(c.getCmdSent() - 1);
                }
            }

            for (TwitchUser u : twitchai.getAllUsers())
            {
                if (u.getCmdTimer() > 0)
                {
                    u.setCmdTimer(u.getCmdTimer() - 1);
                }
            }
            
            for (TwitchChannel c : twitchgroup.getTwitchChannels())
            {
                if (c.getCmdSent() > 0)
                {
                    c.setCmdSent(c.getCmdSent() - 1);
                }
            }

            for (TwitchUser u : twitchgroup.getAllUsers())
            {
                if (u.getCmdTimer() > 0)
                {
                    u.setCmdTimer(u.getCmdTimer() - 1);
                }
            }
            
            timeEnd = System.nanoTime();
            time = (float) (timeEnd - timeStart) / 6000000.0f;

            twitchai.setCycleTime(time);
            twitchgroup.setCycleTime(time);

            if(g_gui_timing){
            	int nanoSeconds = (int) ((System.nanoTime() - g_gui_start_time)/1000/1000/1000);
            	
				String seconds = (nanoSeconds%60)+"";
				String minutes = (nanoSeconds/60%60)+"";
				String hours = (nanoSeconds/60/60%60)+"";
				
				if(seconds.length()==1)
					seconds = "0" + seconds;
				if(minutes.length()==1)
					minutes = "0" + minutes;
				if(hours.length()==1)
					hours = "0" + hours;
				
				g_gui_ended_time = (hours + ":" + minutes + ":" + seconds);
				g_gui.getTxtTime().setText(g_gui_ended_time);
				FileUtils.writeTextFile("data/files/", "current_level_time.txt", g_gui_ended_time);
            }
            
            /*
             * Main loop ticks only once per second.
             */
            try
            {
                if (time < 17.0f)
                {
                    Thread.sleep((long) (17.0f - time));
                }
                else
                {
                    logErr("Warning! Main thread cycle time is longer than a second! Skipping sleep! Cycle-time: " + time);
                }
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

	private static void initCreate() {
		File dest = new File("data/config.cfg");
        if (!dest.exists() && !dest.isDirectory())
        {
			URL inputUrl = Main.class.getResource("/config.cfg");
	    	try {
				org.apache.commons.io.FileUtils.copyURLToFile(inputUrl, dest);
				logMsg("config.cfg created.");
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        dest = new File("data/responses.cfg");
        if (!dest.exists() && !dest.isDirectory())
        {
			URL inputUrl = Main.class.getResource("/responses.cfg");
	    	try {
				org.apache.commons.io.FileUtils.copyURLToFile(inputUrl, dest);
				logMsg("responses.cfg created.");
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        dest = new File("data/channels.txt");
        if (!dest.exists() && !dest.isDirectory())
        {
			URL inputUrl = Main.class.getResource("/channels.txt");
	    	try {
				org.apache.commons.io.FileUtils.copyURLToFile(inputUrl, dest);
				logMsg("channels.txt created.");
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        dest = new File("data/moderators.txt");
        if (!dest.exists() && !dest.isDirectory())
        {
			URL inputUrl = Main.class.getResource("/moderators.txt");
	    	try {
				org.apache.commons.io.FileUtils.copyURLToFile(inputUrl, dest);
				logMsg("moderators.txt created.");
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
}
