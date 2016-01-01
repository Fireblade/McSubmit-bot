package mclama.com;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.table.DefaultTableModel;

import static to.us.harha.twitchai.util.Globals.*;
import static to.us.harha.twitchai.util.FileUtils.*;

public class SMMHandler {
	
	private static LevelCode current_level  = new LevelCode("none","none", null);
	private static LevelCode previous_level = new LevelCode("none","none", null);
	
	private static ArrayList<LevelCode> levels = new ArrayList<LevelCode>();
	
	public static int getLevelCount(){
		return levels.size();
	}
	
	public static String getUserNextLevelPlace(String sender){
		for(int i=0; i<levels.size(); i++)
		{
			LevelCode cur_lvl = levels.get(i);
			if(cur_lvl.getAuthor().equals(sender))
			{
				return i+1+"";
			}
		}
		return "no code found!!";
	}
	
	public static int getUserLevelCount(String sender){
		int count=0;
		for(int i=0; i<levels.size(); i++)
		{
			LevelCode cur_lvl = levels.get(i);
			if(cur_lvl.getAuthor().equals(sender))
			{
				count++;
			}
		}
		return count;
	}

	public static void addLevel(String sender, String code, String response){
		levels.add(new LevelCode(sender, code, response));
		
		if(current_level.getLevel().equals("none")){
			current_level = levels.get(0);
			if(g_smm_play_beep_sound_on_first_level){
				try { Toolkit.getDefaultToolkit().beep();
				} catch (Exception e) {e.printStackTrace();}
			}
			if(g_gui.getChckbxRemoveLevelOn())
			{
				levels.remove(0);
			} else
				cycleLevels();
		}
		else
		{
			DefaultTableModel model = (DefaultTableModel) g_gui.getGui_queue_list().getTable().getModel();
			model.addRow(new Object[]{code, sender});
		}
		
		g_gui.updateNewLevel(levels.size(),current_level, g_BOT.isUserInChat(current_level.getAuthor()));
	}
	
	public static boolean getNext(){
		if(levels.size()>0)
		{
			if(g_gui.getChckbxExportToComplete())
			{
				sendPost(g_forms_completed_url, true, current_level.getLevel(), current_level.getAuthor());
			}
			
			previous_level = current_level;
			current_level = levels.get(0);
			
			if(g_gui.getChckbxRemoveLevelOn())
			{
				levels.remove(0);
			} else
				cycleLevels();
			
			g_gui.updateNewLevel(levels.size(),current_level, g_BOT.isUserInChat(current_level.getAuthor()));
			updateCurrentFile();
			g_gui.getLblLevelcount().setText("Levels: " + levels.size());
			return true;
		}
		g_gui.getLblLevelcount().setText("Levels: " + levels.size());
		return false;
	}

	public static void sendPost(String url, boolean useCompleted, String level, String submit){
		try {
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			//add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", "Mozilla/7.0");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			String urlParameters = "";
			if(useCompleted)
			{
				if(g_gui_timing) g_gui.endTimer();
				urlParameters += (g_forms_completed_submit_entry + "=" + submit + "&");
				urlParameters += (g_forms_completed_level_entry + "=" + level + "&");
				urlParameters += (g_forms_completed_time_entry + "=" + (g_gui_ended_time.replace("null", "00:00:00")) + "&");
				urlParameters += (g_forms_completed_deaths_entry + "=" + g_gui.getTxtDeaths().getText());
			}
			else
			{
				urlParameters += (g_forms_submit_entry + "=" + submit + "&");
				urlParameters += (g_forms_level_entry + "=" + level);
			}
			
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
//			System.out.println("\nSending 'POST' request to URL : " + url);
//			System.out.println("Post parameters : " + urlParameters);
//			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			//print result
			//System.out.println(response.toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void cycleLevels() {
		ArrayList<LevelCode> levelClone = (ArrayList<LevelCode>) levels.clone();
		
		levels.clear();
		for(int i=1;i<levelClone.size();i++){
			levels.add(levelClone.get(i));
		}
		levels.add(levelClone.get(0));
	}

	private static void updateCurrentFile() {
		if(g_smm_record_levels_played)
		{
			writeTextFile("data/files/", "current_level.txt", current_level.getLevel());
			writeTextFile("data/files/", "current_level_author.txt", current_level.getAuthor());
		}
		
		if(g_smm_record_previous_levels_played)
		{
			writeTextFile("data/files/", "previous_level.txt", previous_level.getLevel());
			writeTextFile("data/files/", "previous_level_author.txt", previous_level.getAuthor());
		}
	}

	public static boolean getRandom(){
		if(levels.size()>0)
		{
			previous_level = current_level;
			Random gen = new Random();
			int i = gen.nextInt(levels.size());
			current_level = levels.get(i);
			
			if(g_smm_remove_after_nextrandom)
			{
				levels.remove(i);
			}
			
			g_gui.updateNewLevel(levels.size(),current_level, g_BOT.isUserInChat(current_level.getAuthor()));
			updateCurrentFile();
			g_gui.getLblLevelcount().setText("Levels: " + levels.size());
			return true;
		}
		g_gui.getLblLevelcount().setText("Levels: " + levels.size());
		return false;
	}

	public static boolean removeLevelFromQueue(String code){
		for(int i=0; i<levels.size(); i++)
		{
			LevelCode cur_lvl = levels.get(i);
			if(cur_lvl.getLevel().equals(code))
			{
				levels.remove(i);
				g_gui.updateViewLevels();
				return true;
			}
		}
		return false;
	}
	
	public static LevelCode getCurrent_level() {
		return current_level;
	}

	public static LevelCode getPrevious_level() {
		return previous_level;
	}

	public static ArrayList<LevelCode> getLevels() {
		return levels;
	}

	public static boolean isCodeDuplicate(String code) {
		if(g_smm_allow_duplicate_submissions) return false;
		for(int i=0; i<levels.size(); i++)
		{
			LevelCode cur_lvl = levels.get(i);
			if(cur_lvl.getLevel().equals(code))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean removeAllMyLevelsFromQueue(String sender) {
		boolean foundLevel = false;
		for(int i=0; i<levels.size(); i++)
		{
			LevelCode cur_lvl = levels.get(i);
			if(cur_lvl.getAuthor().equals(sender))
			{
				levels.remove(i);
				foundLevel = true;
				g_gui.updateViewLevels();
			}
		}
		return foundLevel;
	}

	public static String getNextFewLevels() {
		String str = ml_queuelist_syntax;
		if(levels.size()>0)
		{
			str = str.replace("{next_level1}", levels.get(0).getLevel());
			str = str.replace("{next_level_user1}", levels.get(0).getAuthor());
		}
		else
		{
			str = str.replace("{next_level1}", "Empty!");
			str = str.replace("{next_level_user1}", "none");
		}
		if(levels.size()>1)
		{
			str = str.replace("{next_level2}", levels.get(1).getLevel());
			str = str.replace("{next_level_user2}", levels.get(1).getAuthor());
		}
		else
		{
			str = str.replace("{next_level2}", "none");
			str = str.replace("{next_level_user2}", "none");
		}
		if(levels.size()>2)
		{
			str = str.replace("{next_level3}", levels.get(2).getLevel());
			str = str.replace("{next_level_user3}", levels.get(2).getAuthor());
		}
		else
		{
			str = str.replace("{next_level3}", "none");
			str = str.replace("{next_level_user3}", "none");
		}
		return str;
	}
	
}
