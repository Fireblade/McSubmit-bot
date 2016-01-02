package to.us.harha.twitchai.util;

import java.io.File;
import java.security.CodeSource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import mclama.com.GUIWindow;
import to.us.harha.twitchai.bot.TwitchAI;
import to.us.harha.twitchai.bot.TwitchUser;

public class Globals
{
    public static String           g_version        = "1.2.3"; //Version of submission bot
    public static int              g_build          = 3;       //general numeric guide
	
	
	//Super Mario Maker submit handler
	public static boolean g_smm_submits_open = true;
	public static boolean g_smm_message_all = true;
	public static boolean g_smm_remove_after_nextrandom = true;
	public static boolean g_smm_use_gui = true;
	
	public static boolean g_smm_allow_previously_done_levels = true;
	public static boolean g_smm_record_levels_played = true;
	public static boolean g_smm_record_previous_levels_played = true;
	public static boolean g_smm_follow_required = false;
	public static boolean g_smm_play_beep_sound_on_first_level = false;
	public static boolean g_smm_require_good_looking_code = true;
	public static int g_smm_submit_delay = 5;
	public static int g_smm_submit_max_in_list = 2;
	public static int g_smm_max_queue = 100;
	//1.2 additions
	public static boolean g_smm_allow_duplicate_submissions = false;
	public static boolean g_smm_allow_command_delete = true;
	public static String g_smm_url_list = "Not yet set";
	public static String g_smm_url_list_streamer = "Not yet set";
	public static String g_smm_url_list_completed = "Not yet set";
	
	//SMM String responses;
	public static String ml_submit_with_no_level = "To add your level to the list, use !submit code.";
	public static String ml_submit = "Thanks {sender}, your level will be in position {place} on the queue!";
	public static String ml_current = "Current level code: {current} by {currentauthor}.";
	public static String ml_previous = "Last level code: {previous} by {previousauthor}.";
	public static String ml_submit_open = "Sorry, Level submission is currently closed.";
	public static String ml_submit_delay = "There is a {delay} minute delay between submissions. Please wait a little longer {sender}.";
	public static String ml_on_next = "Next level is {current} by {currentauthor}. {queue_size} remaining in queue.";
	public static String ml_on_random = "Random level is {current} by {currentauthor}. {queue_size} remaining in queue.";
	public static String ml_queue = "The current queue has {queue_size} levels! Your next level is in position {place}.";
	public static String ml_no_queue = "No maps in the queue! Maybe someone would like to !submit code?";
	public static String ml_submit_max_in_list = "Sorry {sender}, You already have the maximum allowed submissions.({max_user_submit})";
	public static String ml_submit_max_queue = "Sorry {sender}, The queue is already at the maximum size. ({max_queue_size})";
	public static String ml_require_follow = "Sorry {sender}, You must be following the channel to do that.";
	public static String ml_require_good_looking_code = "Sorry {sender}, That code doesn't look like it is valid.";
	//v1.2 
	public static String ml_commands_list = "!submit, !current, !previous, !queue, !title, !delete, !queuelist, !list, !completed, !mylist.";
	public static String ml_delete_with_no_level = "Wrong syntax! You can !delete all, to simply remove all your levels. or !delete code for a specific level.";
	public static String ml_delete = "We removed your level {sender}.";
	public static String ml_delete_invalid = "I could not find your level in our queue, Check your code and try again.";
	public static String ml_list = "View the submission sheet here: {submission_list}";
	public static String ml_queuelist = "Next 3 levels are: {next_levels}";
	public static String ml_queuelist_syntax= "{next_level1} by {next_level_user1}, {next_level2} by {next_level_user2}, {next_level3} by {next_level_user3}. ";
	public static String ml_list_completed = "View the levels i completed with their time and deaths here: {completed_list}";
	public static String ml_duplicate_submission = "That code is already in the queue in position {place}.";
	public static String ml_streamer_submission_list = "I haven't set this list yet, I will add my levels here or at a url like: {streamer_list_url}";
	public static String ml_current_title = "Current level title is: {title}, And is labled as {difficulty}.";
	public static String ml_ = "";
	
	
	// FORMS
	public static boolean g_forms_use=false;
	public static String g_forms_url;
	public static String g_forms_submit_entry;
	public static String g_forms_level_entry;
	public static boolean g_forms_use_completed=false;
	public static String g_forms_completed_url;
	public static String g_forms_completed_submit_entry;
	public static String g_forms_completed_level_entry;
	public static String g_forms_completed_time_entry;
	public static String g_forms_completed_deaths_entry;

	//GUI information
	public static GUIWindow g_gui;
	public static boolean g_gui_timing=false;
//	public static int g_gui_pause_time=0;
//	public static int g_gui_start_pause;
	public static Long g_gui_start_time;
	public static String g_gui_ended_time;
	
	//SMM Commands
	public static final String g_com_submit = "submit";
	public static final String g_com_queue = "queue";
	public static final String g_com_current = "current";
	public static final String g_com_previous = "previous";
	public static final String g_com_next = "next";
	public static final String g_com_random = "random";
	//v1.2.0
	public static final String g_com_open = "open";
	public static final String g_com_close = "close";
	public static final String g_com_delete = "delete";
	public static final String g_com_queuelist = "queuelist";
	public static final String g_com_list = "list";
	public static final String g_com_completedlist = "completed";
	public static final String g_com_commands = "commands";
	public static final String g_com_streamerlist = "mylist";
	public static final String g_com_title = "title";
	
	//modified BOT vars
	public static String g_dir;
	public static TwitchAI g_BOT;
	public static TwitchAI g_PBOT; //private message

    // Config
    public static boolean          g_debug;
    public static boolean          g_bot_reqMembership;
    public static boolean          g_bot_reqCommands;
    public static boolean          g_bot_reqTags;
    public static String           g_bot_name;
    public static String           g_bot_oauth;
    public static String           g_bot_chan;
    public static String           g_bot_version    = "TwitchAI 0.0.3";
    public static String           g_lib_version    = "PircBot 1.5.0";

    // Time & Date
    public static DateFormat       g_datetimeformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public static DateFormat       g_dateformat     = new SimpleDateFormat("dd.MM.yyyy");
    public static DateFormat       g_timeformat     = new SimpleDateFormat("HH:mm:ss");
    public static Date             g_date           = new Date();

    // Global variables
    public static final String     g_commands_user  = "!help !performance !date !time";
    public static final String     g_commands_op    = "!permit";
    public static final String     g_commands_mod   = "!joinchan !partchan !addchan !delchan";
    public static final String     g_commands_admin = "!addmod !delmod ";
    public static final String     g_commands_bot   = "!help !register !unregister";
    public static final String[]   g_emotes_faces   = { "4Head", "BibleThump", "BloodTrail", "VaultBoy", "deIlluminati", "DOOMGuy", "FailFish", "Kappa", "Keepo" };

    // Server messages
    public static final String     g_server_memreq  = "CAP REQ :twitch.tv/membership";
    public static final String     g_server_memans  = ":tmi.twitch.tv CAP * ACK :twitch.tv/membership";
    public static final String     g_server_cmdreq  = "CAP REQ :twitch.tv/commands";
    public static final String     g_server_cmdans  = ":tmi.twitch.tv CAP * ACK :twitch.tv/commands";
    public static final String     g_server_tagreq  = "CAP REQ :twitch.tv/tags";
    public static final String     g_server_tagans  = ":tmi.twitch.tv CAP * ACK :twitch.tv/tags";

    // Java objects
    public static final TwitchUser g_nulluser       = new TwitchUser("null", "");

}
