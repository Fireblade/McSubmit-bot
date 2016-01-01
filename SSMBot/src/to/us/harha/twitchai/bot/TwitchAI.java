package to.us.harha.twitchai.bot;

import static to.us.harha.twitchai.util.Globals.*;
import static to.us.harha.twitchai.util.LogUtils.logMsg;
import static to.us.harha.twitchai.util.LogUtils.logErr;
import static to.us.harha.twitchai.util.GenUtils.exit;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;


import mclama.com.SMMHandler;
import to.us.harha.twitchai.util.FileUtils;

import java.time.*;

public class TwitchAI extends PircBot
{

    private float                    m_cycleTime;
    private float                    m_cmdTime;
    private boolean                  m_hasMembership;
    private boolean                  m_hasCommands;
    private boolean                  m_hasTags;
    private ArrayList<TwitchUser>    m_moderators;
    private ArrayList<TwitchChannel> m_channels;

    public TwitchAI()
    {
        m_cycleTime = 0.0f;
        m_cmdTime = 0.0f;
        m_hasMembership = false;
        m_hasCommands = false;
        m_hasTags = true;
        m_moderators = new ArrayList<TwitchUser>();
        m_channels = new ArrayList<TwitchChannel>();

        setName(g_bot_name);
        setVersion(g_lib_version);
        setVerbose(false);
        setMessageDelay(300);
    }

    public void init_twitch(String address)
    {
        logMsg("Loading all registered TwitchAI moderators...");
        ArrayList<String> loadedModerators = FileUtils.readTextFile("data/moderators.txt");
        for (String m : loadedModerators)
        {
            String[] m_split = m.split(" ");
            TwitchUser newmod = new TwitchUser(m_split[0], m_split[1]);
            logMsg("Added a TwitchAI moderator (" + newmod + ") to m_moderators");
            m_moderators.add(newmod);
        }

        logMsg("Attempting to connect to irc.twitch.tv...");
        try
        {
            connect(address, 6667, g_bot_oauth);
        } catch (IOException | IrcException e)
        {
            logErr(e.getStackTrace().toString());
            exit(1);
        }

        if (g_bot_reqMembership)
        {
            logMsg("Requesting twitch membership capability for NAMES/JOIN/PART/MODE messages...");
            sendRawLine(g_server_memreq);
        }
        else
        {
            logMsg("Membership request is disabled!");
            m_hasMembership = true;
        }

        if (g_bot_reqCommands)
        {
            logMsg("Requesting twitch commands capability for NOTICE/HOSTTARGET/CLEARCHAT/USERSTATE messages... ");
            sendRawLine(g_server_cmdreq);
        }
        else
        {
            logMsg("Commands request is disabled!");
            m_hasCommands = true;
        }

        if (g_bot_reqTags)
        {
            logMsg("Requesting twitch tags capability for PRIVMSG/USERSTATE/GLOBALUSERSTATE messages... ");
            sendRawLine(g_server_tagreq);
        }
        else
        {
            logMsg("Tags request is disabled!");
            m_hasTags = true;
        }
    }

    public void init_channels()
    {
        logMsg("Attempting to join all registered channels...");
        ArrayList<String> loadedChannels = FileUtils.readTextFile("data/channels.txt");
        for (String c : loadedChannels)
        {
            if (!c.startsWith("#"))
            {
                c = "#" + c;
            }
            joinToChannel(c);
        }
        
        sendMessage("#_mclamabot_1449386648724","/w fireblade212 Bot whisper test.");
    }

    public void joinToChannel(String channel)
    {
        logMsg("Attempting to join channel " + channel);
        joinChannel(channel);
        m_channels.add(new TwitchChannel(channel));
    }

    public void partFromChannel(String channel)
    {
        logMsg("Attempting to part from channel " + channel);
        partChannel(channel);
        m_channels.remove(getTwitchChannel(channel));
    }

    public void addChannel(String channel, String sender, String addChan)
    {
        ArrayList<String> addchan_channels = FileUtils.readTextFile("data/channels.txt");
        if (addchan_channels.size() <= 0 || !addchan_channels.contains(addChan))
        {
            logMsg("Registering a new channel: " + addChan);
            sendTwitchMessage(channel, "Registering a new channel: " + addChan);
            FileUtils.writeToTextFile("data/", "channels.txt", addChan);
            joinToChannel(addChan);
        }
        else
        {
            logErr("Failed to register a new channel: " + addChan);
            sendTwitchMessage(channel, "That channel is already registered!");
        }
        return;
    }

    public void delChannel(String channel, String sender, String delChan)
    {
        if (!Arrays.asList(getChannels()).contains(delChan))
        {
            logErr("Can't delete channel " + delChan + " from the global channels list because it isn't in the joined channels list!");
            return;
        }
        logMsg(sender + " Requested a deletion of channel: " + delChan);
        sendTwitchMessage(channel, sender + " Requested a deletion of channel: " + delChan);
        partFromChannel(delChan);
        FileUtils.removeFromTextFile("data", "/channels.txt", delChan);
    }

    public void sendTwitchMessage(String channel, String message)
    {
        TwitchChannel twitch_channel = getTwitchChannel(channel);
        TwitchUser twitch_user = twitch_channel.getUser(g_bot_name);

        if (twitch_user == null)
        {
            twitch_user = g_nulluser;
        }

        if (twitch_user.isOperator())
        {
            if (twitch_channel.getCmdSent() <= 48)
            {
                twitch_channel.setCmdSent(twitch_channel.getCmdSent() + 1);
                sendMessage(channel, message);
            }
            else
            {
                logErr("Cannot send a message to channel (" + twitch_channel + ")! 100 Messages per 30s limit nearly exceeded! (" + twitch_channel.getCmdSent() + ")");
            }
        }
        else
        {
            if (twitch_channel.getCmdSent() <= 16)
            {
                twitch_channel.setCmdSent(twitch_channel.getCmdSent() + 1);
                sendMessage(channel, message);
            }
            else
            {
                logErr("Cannot send a message to channel (" + twitch_channel + ")! 20 Messages per 30s limit nearly exceeded! (" + twitch_channel.getCmdSent() + ")");
            }
        }
    }
    
    public void sendWhisper(String sendTo, String message){
    	//if(g_dir.contains("desktop") || sendTo.equals("fireblade212"))
    	//{ //only send message to author if running from development
    		g_PBOT.sendMessage("#_mclamabot_1449386648724","/w " + sendTo + " " + message);
    	//}
    }

    @Override
    public void handleLine(String line)
    {
        //logMsg("handleLine | " + line);

        super.handleLine(line);

        if (!isInitialized())
        {
            if (line.equals(g_server_memans))
            {
                m_hasMembership = true;
            }

            if (line.equals(g_server_cmdans))
            {
                m_hasCommands = true;
            }

            if (line.equals(g_server_tagans))
            {
                m_hasTags = true;
            }
        }
        if (line.contains("WHISPER"))
        {
        	String[] line_array = line.split(g_bot_name.toLowerCase() + " :");
        	String sender = line_array[0].split("!")[0].replace(":","");
        	String message = line_array[1];
        	onPrivateMessage(sender,"","",message);
        }
        if (line.contains(":jtv "))
        {
            line = line.replace(":jtv ", "");
            String[] line_array = line.split(" ");

            if (line_array[0].equals("MODE") && line_array.length >= 4)
            {
                onMode(line_array[1], line_array[3], line_array[3], "", line_array[2]);
            }
        }
    }

    @Override
    public void onUserList(String channel, User[] users)
    {
        super.onUserList(channel, users);

        TwitchChannel twitch_channel = getTwitchChannel(channel);

        if (twitch_channel == null)
        {
            logErr("Error on USERLIST, channel (" + channel + ") doesn't exist!");
            return;
        }

        for (User u : users)
        {
            if (twitch_channel.getUser(u.getNick()) == null)
            {
                TwitchUser twitch_mod = getOfflineModerator(u.getNick());
                String prefix = "";
                if (twitch_mod != null)
                {
                    prefix = twitch_mod.getPrefix();
                }
                TwitchUser user = new TwitchUser(u.getNick(), prefix);
                twitch_channel.addUser(user);
                logMsg("Adding new user (" + user + ") to channel (" + twitch_channel.toString() + ")");
            }
        }
    }

    @Override
    public void onJoin(String channel, String sender, String login, String hostname)
    {
        super.onJoin(channel, sender, login, hostname);

        TwitchChannel twitch_channel = getTwitchChannel(channel);
        TwitchUser twitch_user = twitch_channel.getUser(sender);
        TwitchUser twitch_mod = getOfflineModerator(sender);

        if (twitch_channel != null && twitch_user == null)
        {
            String prefix = "";
            if (twitch_mod != null)
            {
                prefix = twitch_mod.getPrefix();
            }
            TwitchUser user = new TwitchUser(sender, prefix);
            twitch_channel.addUser(user);
            logMsg("Adding new user (" + user + ") to channel (" + twitch_channel.toString() + ")");
        }
    }

    @Override
    public void onPart(String channel, String sender, String login, String hostname)
    {
        super.onPart(channel, sender, login, hostname);

        TwitchChannel twitch_channel = getTwitchChannel(channel);
        TwitchUser twitch_user = twitch_channel.getUser(sender);

        if (twitch_channel != null && twitch_user != null)
        {
            twitch_channel.delUser(twitch_user);
            logMsg("Removing user (" + twitch_user + ") from channel (" + twitch_channel.toString() + ")");
        }
    }

    @Override
     public void onMode(String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode)
    {
        super.onMode(channel, sourceNick, sourceLogin, sourceHostname, mode);

        TwitchChannel twitch_channel = getTwitchChannel(channel);
        TwitchUser twitch_user = twitch_channel.getUser(sourceNick);

        if (twitch_user == null)
        {
            logErr("Error on MODE, cannot find (" + twitch_user + ") from channel (" + twitch_channel.toString() + ")");
            return;
        }

        if (mode.equals("+o"))
        {
            logMsg("Adding +o MODE for user (" + twitch_user + ") in channel (" + twitch_channel.toString() + ")");
            twitch_user.addPrefixChar("@");
        }
        else if (mode.equals("-o"))
        {
            logMsg("Adding -o MODE for user (" + twitch_user + ") in channel (" + twitch_channel.toString() + ")");
            twitch_user.delPrefixChar("@");
        }
    }

    @Override
    public void onMessage(String channel, String sender, String login, String hostname, String message)
    {
        logMsg("data/channels/" + channel, "/onMessage", "User: " + sender + /*" Hostname: " + hostname + */" Message: " + message);
    	
        TwitchChannel twitch_channel = getTwitchChannel(channel);
        TwitchUser twitch_user = twitch_channel.getUser(sender);
        TwitchUser twitch_mod = getOfflineModerator(sender);

        if (twitch_channel != null && twitch_user == null)
        {
            String prefix = "";
            if (twitch_mod != null)
            {
                prefix = twitch_mod.getPrefix();
            }
            TwitchUser user = new TwitchUser(sender, prefix);
            twitch_channel.addUser(user);
            logMsg("Adding new user (" + user + ") to channel (" + twitch_channel.toString() + ")");
        }
        
    	//TwitchChannel twitch_channel = getTwitchChannel(g_bot_chan);
    	
    	if (message.startsWith("!"))
        {
        	//System.out.println(twitch_channel.getUsers().toString());
            twitch_user = twitch_channel.getUser(sender);
            
            if (twitch_user == null)
            {
                logErr("Error on ONMESSAGE, user (" + sender + ") doesn't exist! Creating a temp null user object for user!");
                twitch_user = g_nulluser;
            }
            
            if (message.length() > 3)
            {
                if (twitch_user.getCmdTimer() > 0)
                {
                    if (twitch_user.getCmdTimer() > 10 && twitch_channel.getCmdSent() < 32)
                    {
                        sendWhisper(sender, " Please wait " + twitch_user.getCmdTimer() + " seconds before sending a new command.");
                    }
                    twitch_user.setCmdTimer(twitch_user.getCmdTimer() + 5);
                    return;
                }
                else
                {
                    if (!twitch_user.getName().equals("null"))
                    {
                        twitch_user.setCmdTimer(5);
                    }
                }
            }
            
            message = message.replace("!", "");
            String[] msg_array = message.split(" ");
            String msg_command = msg_array[0];
            String user_sender = sender;
            String user_target;
            String chan_sender = channel;
            String chan_target;
            float time;
            long timeStart, timeEnd;

            timeStart = System.nanoTime();
            if (channel.equals(g_bot_chan))
            {
	            switch (msg_command)
	            {
	            /*
	             * Normal commands
	             */
	            case g_com_submit:
	            	if(!g_smm_submits_open)
	            	{
	            		sendResponse(sender, ml_submit_open);
	            		break;
	            	}
	            	if (msg_array.length < 2)
                    {
	            		sendResponse(sender, ml_submit_with_no_level);
	            		break;
                    }
	            	if(SMMHandler.getUserLevelCount(sender)>=g_smm_max_queue){
	            		sendResponse(sender, ml_submit_max_queue);
	            		break;
	            	}
	            	if(SMMHandler.getUserLevelCount(sender)>=g_smm_submit_max_in_list){
	            		sendResponse(sender, ml_submit_max_in_list);
	            		break;
	            	}
	            	if(twitch_user != null || g_smm_submit_delay>0)
	            	{
		            	if((new Date().getTime() <= twitch_user.getLastSubmit() + (g_smm_submit_delay * 100000)) && twitch_user.getLastSubmit() != 0)
		            	{
		            		sendResponse(sender, ml_submit_delay);
		            		break;
		            	}
	            	}
	            	if(g_smm_follow_required){
	            		if(!isUserFollowing(sender, g_bot_chan)){
	            			sendResponse(sender, ml_require_follow);
		            		break;
	            		}
	            	}
	            	if(msg_array[1].charAt(msg_array[1].length()-1) == '.')
	            		msg_array[1] = msg_array[1].substring(0, msg_array[1].length()-1);
	            	
	            	
	            	String response = null;
	            	if(g_smm_require_good_looking_code){
	            		int minLength=16 + (msg_array[1].length() - msg_array[1].replace("-", "").length());
	            		if(msg_array[1].length() < minLength)
	            		{
	            			sendResponse(sender, ml_require_good_looking_code);
		            		break;
	            		}
	            		else
	            		{
	            			if(!msg_array[1].contains("-"))
	            			{
	            				msg_array[1] = insertPeriodically(msg_array[1], "-", 4);
	            			}
	            		}
	            		response = sendGetRequest("https://supermariomakerbookmark.nintendo.net/courses/" + msg_array[1]);
	            		//System.out.println("response: " + response);
	            		
	            		if(response.contains("404"))
	            		{
	            			//System.out.println("invalid level");
	            			sendResponse(sender, ml_require_good_looking_code);
		            		break;
	            		}
	            	}
	            	if(SMMHandler.isCodeDuplicate(msg_array[1])){
	            		sendResponse(sender, ml_duplicate_submission);
	            		break;
	            	}
	            	
	            	twitch_user.setLastSubmit(new Date().getTime());
	            	SMMHandler.addLevel(sender, msg_array[1], response);
	            	if(g_forms_use)
	    			{
	    				SMMHandler.sendPost(g_forms_url, false, msg_array[1], sender);
	    			}
	            	sendResponse(sender, ml_submit);
	            	break;
	            	
	            case g_com_queue:
	            	sendResponse(sender, ml_queue);
	            	break;
	            	
	            case g_com_current:
	            	sendResponse(sender, ml_current);
	            	break;
	            	
	            case g_com_previous:
	            	sendResponse(sender, ml_previous);
	            	break;
	            	
	            case g_com_next:
	            	if (!twitch_user.isModerator())
                    {
                        break;
                    }
	            	
	            	if(SMMHandler.getNext())
	            	{
	            		sendResponse(sender, ml_on_next);
	            		break;
	            	}
	            	sendResponse(sender, ml_no_queue);
	            	break;
	            	
	            case g_com_random:
	            	if (!twitch_user.isModerator())
                    {
                        break;
                    }
	            	
	            	if(SMMHandler.getRandom())
	            	{
	            		sendResponse(sender, ml_on_random);
	            		break;
	            	}
	            	sendResponse(sender, ml_no_queue);
	            	break;
	            case g_com_commands:
		            {
		            	sendResponse(sender, ml_commands_list);
		            	break;
		            }
		            
	            case g_com_queuelist:
		            {
		            	sendResponse(sender, ml_queuelist);
		            	break;
		            }
	            case g_com_close:
		            {
		            	g_smm_submits_open = false;
		            	sendResponse(sender, "Closed submissions!");
		            	break;
		            }
	            case g_com_open:
		            {
		            	g_smm_submits_open = true;
		            	sendResponse(sender, "Opened submissions!");
		            	break;
		            }
	            case g_com_delete:
		            {
		            	if (msg_array.length < 2)
	                    {
		            		sendResponse(sender, ml_delete_with_no_level);
		            		break;
	                    }
		            	if(msg_array[1].equals("all"))
		            	{
		            		if(SMMHandler.removeAllMyLevelsFromQueue(sender))
			            	{
			            		sendResponse(sender, ml_delete);
			            	}
		            		break;
		            	}
		            	if(g_smm_require_good_looking_code){
		            		int minLength=16 + (msg_array[1].length() - msg_array[1].replace("-", "").length());
		            		if(msg_array[1].length() < minLength)
		            		{
		            			sendResponse(sender, ml_require_good_looking_code);
			            		break;
		            		}
		            		else
		            		{
		            			if(!msg_array[1].contains("-"))
		            			{
		            				msg_array[1] = insertPeriodically(msg_array[1], "-", 4);
		            			}
		            		}
		            		response = sendGetRequest("https://supermariomakerbookmark.nintendo.net/courses/" + msg_array[1]);
		            		//System.out.println("response: " + response);
		            		
		            		if(response.contains("404"))
		            		{
		            			//System.out.println("invalid level");
		            			sendResponse(sender, ml_require_good_looking_code);
			            		break;
		            		}
		            	}
		            	if(SMMHandler.removeLevelFromQueue(msg_array[1]))
		            	{
		            		sendResponse(sender, ml_delete);
		            		break;
		            	}
		            	else{
		            		sendResponse(sender, ml_delete_invalid);
		            		break;
		            	}
		            }
	            case g_com_list:
		            {
		            	sendResponse(sender, ml_list);
	            		break;
		            }
	            case g_com_completedlist:
		            {
		            	sendResponse(sender, ml_list_completed);
	            		break;
		            }
	            case g_com_streamerlist:
		            {
		            	sendResponse(sender, ml_streamer_submission_list);
	            		break;
		            }
	            case g_com_title:
		            {
		            	sendResponse(sender, ml_current_title);
	            		break;
		            }
	            }
	            
            }

            timeEnd = System.nanoTime();
            time = (float) (timeEnd - timeStart) / 1000000.0f;

            setCmdTime(getCmdTime() * 0.1f + time * 0.9f);
        }
    	
    }

    public void sendResponse(String sender, String message) {
    	message = message.replace("{sender}", sender)
    	.replace("{place}", SMMHandler.getUserNextLevelPlace(sender))
    	.replace("{current}", SMMHandler.getCurrent_level().getLevel())
    	.replace("{currentauthor}", SMMHandler.getCurrent_level().getAuthor())
    	.replace("{previous}", SMMHandler.getPrevious_level().getLevel())
    	.replace("{previousauthor}", SMMHandler.getPrevious_level().getAuthor())
    	.replace("{delay}", g_smm_submit_delay+"")
    	.replace("{queue_size}", SMMHandler.getLevelCount()+"")
    	.replace("{max_queue_size}", g_smm_max_queue+"")
    	.replace("{max_user_submit}", g_smm_submit_max_in_list+"")
    	.replace("{next_levels}", SMMHandler.getNextFewLevels())
    	.replace("{submission_list}", g_smm_url_list)
    	.replace("{completed_list}", g_smm_url_list_completed)
    	.replace("{streamer_list_url}", g_smm_url_list_streamer)
    	.replace("{title}", SMMHandler.getCurrent_level().getName())
    	.replace("{difficulty}", SMMHandler.getCurrent_level().getDifficulty())
    	;
    	
		if(g_smm_message_all)
		{
			sendMessage(g_bot_chan, message);
		}
		else
		{
			sendWhisper(sender,message);
		}
	}

	@Override
    public void onPrivateMessage(String sender, String login, String hostname, String message)
    {
    	//System.out.println("pm received");
        logMsg("data/whisper", "/WHISPER", "User: " + sender + " Hostname: " + hostname + " Message: " + message);
        
    }

    public ArrayList<TwitchChannel> getTwitchChannels()
    {
        return m_channels;
    }

    public TwitchChannel getTwitchChannel(String name)
    {
        TwitchChannel result = null;

        for (TwitchChannel tc : m_channels)
        {
            if (tc.getName().equals(name))
            {
                result = tc;
                break;
            }
        }

        return result;
    }

    public ArrayList<TwitchUser> getAllUsers()
    {
        ArrayList<TwitchUser> result = new ArrayList<TwitchUser>();

        for (TwitchChannel tc : m_channels)
        {
            result.addAll(tc.getUsers());
        }

        return result;
    }

    public ArrayList<TwitchUser> getAllOperators()
    {
        ArrayList<TwitchUser> result = new ArrayList<TwitchUser>();

        for (TwitchChannel tc : m_channels)
        {
            result.addAll(tc.getOperators());
        }

        return result;
    }

    public ArrayList<TwitchUser> getOnlineModerators()
    {
        ArrayList<TwitchUser> result = new ArrayList<TwitchUser>();

        for (TwitchChannel tc : m_channels)
        {
            result.addAll(tc.getModerators());
        }

        return result;
    }

    public ArrayList<TwitchUser> getOfflineModerators()
    {
        return m_moderators;
    }

    public TwitchUser getOfflineModerator(String nick)
    {
        TwitchUser result = null;

        for (TwitchUser tu : m_moderators)
        {
            if (tu.getName().equals(nick))
            {
                result = tu;
            }
        }

        return result;
    }

    public float getCycleTime()
    {
        return m_cycleTime;
    }

    public void setCycleTime(float cycleTime)
    {
        m_cycleTime = cycleTime;
    }

    public float getCmdTime()
    {
        return m_cmdTime;
    }

    public void setCmdTime(float cmdTime)
    {
        m_cmdTime = cmdTime;
    }

    public boolean isInitialized()
    {
        return m_hasMembership & m_hasCommands & m_hasTags;
    }

	public boolean isUserInChat(String author) {
		TwitchChannel bot_channel = null;
		bot_channel = getTwitchChannel(g_bot_chan);
		try {
			if(bot_channel.getUser(author) != null)
				return true;
		} catch (Exception e) {
			logErr("Failed to find user " + author + " in user list");
		}
		
		for (TwitchUser u : getAllUsers())
        {
            if (u.getName().replace("&","").equals("author"))
            {
                return true;
            }
        }
		
		return false;
	}
	
	public boolean isUserFollowing(String sender, String channel){
		String httpLink = "https://api.twitch.tv/kraken/users/" + sender + "/follows/channels/" + (channel.replace("#", ""));
		String response = sendGetRequest(httpLink);
		if(response.contains(httpLink)){
			return true;
		}
		return false;
		
    }
	
	public static String sendGetRequest(String httpLink){
		try {
    		URL obj = new URL(httpLink);
    		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

    		// optional default is GET
    		con.setRequestMethod("GET");

    		//add request header
    		con.setRequestProperty("User-Agent", "java");

    		int responseCode = con.getResponseCode();
    		if(responseCode == 404){
    			return "404";
    		}

    		BufferedReader in = new BufferedReader(
    		        new InputStreamReader(con.getInputStream()));
    		String inputLine;
    		StringBuffer response = new StringBuffer();

    		while ((inputLine = in.readLine()) != null) {
    			response.append(inputLine);
    		}
    		in.close();
    		
    		
    		//System.out.println(response.toString());
			return response.toString();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "404";
	}
	
	public static String insertPeriodically(
		    String text, String insert, int period)
		{
		    StringBuilder builder = new StringBuilder(
		         text.length() + insert.length() * (text.length()/period)+1);

		    int index = 0;
		    String prefix = "";
		    while (index < text.length())
		    {
		        // Don't put the insert in the very first iteration.
		        // This is easier than appending it *after* each substring
		        builder.append(prefix);
		        prefix = insert;
		        builder.append(text.substring(index, 
		            Math.min(index + period, text.length())));
		        index += period;
		    }
		    return builder.toString();
		}

}
