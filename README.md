# McSubmit-bot
McSubmit bot is a twitch bot that only does one thing. Handle Submissions for streamers that do viewer levels for the Wii U game, Super mario maker.  
McSubmit bot uses TwitchAI by harha as a base for the twitch bot foundation and can be found at https://github.com/Harha/TwitchAI

# Features
* !submit allows viewers to submit their own levels to be played by the streamer.
* Highly customizable configuration for settings, and custom responses.
* Configuration like requiring submitter to be a follower of the channel. (defaulted off)
* [Small GUI](http://puu.sh/mgspI/bc38f8b481.png) included that shows small information, a timer, and death counter. Show's if the user is still in chat.
* Levels are validated through the nintendo bookmark website to verify real from troll codes.
* Open and closable submission.


# Install
To get the basic bot running, this video will help you. https://youtu.be/-68RLekviJ4  
For a quick attempt at teaching you how to get both the bot and spreadsheets working: https://youtu.be/9a2EMDl4zEs (v1.1.0)

## Commands

**Public commands**, available to anyone to use  
* !submit --used to submit a level.. its... why you are reading this  
* !current -- shows current level id and info, used by configurable response  
* !previous -- shows previous level id and info, used by configurable response  
* !queue -- short response info by configurable reponse  
* !title -- shows the title of the current level  
* !delete -- user may delete their levels from the queue. Can be turned on/off  
* !queuelist -- configurable response for the next 3 levels  
* !list -- URL to your sheet if you are using it. configurable response  
* !completed -- URL to your completed sheet if you are using it. configurable response  
* !mylist -- the streamers list of levels, Configured URL - {streamer_list_url}  
**Mod only commands**  
* !next --next level in line
* !random --random level in the queue
* !open --opens the queue to new submissions
* !close --closes the queue to new submissions
