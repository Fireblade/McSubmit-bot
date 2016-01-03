package mclama.com;

import static to.us.harha.twitchai.util.Globals.*;
import static to.us.harha.twitchai.util.LogUtils.logErr;

public class LevelCode {
	
	private String level = "none";
	private String author = "none";
	
	private String difficulty = "unknown";
	private String name = "unknown";
	
	public LevelCode(String author, String level, String response){
		this.level = level;
		this.author = author;
		
		if(!level.equals("none"))
		{
			try {
				
				if (response.contains("Super Expert")) {
					difficulty = "Super Expert";
				} else if (response.contains("Expert")) {
					difficulty = "Expert";
				} else if (response.contains("Normal")) {
					difficulty = "Normal";
				} else if (response.contains("Easy")) {
					difficulty = "Easy";
				} else difficulty = "0 clears";
				
				String[] split = response.split("<div class=" + '"' + "course-title" + '"' + ">");
				String[] xsplit = split[1].split("</div></div>");
				this.name = xsplit[0];
			} catch (Exception e) {
				logErr("Failed to get level name and difficulty.");
			}
		}
	}

	public String getLevel() {
		return level;
	}

	public String getAuthor() {
		return author;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public String getName() {
		return name;
	}

}
