package mclama.com;

import static to.us.harha.twitchai.util.FileUtils.writeTextFile;
import static to.us.harha.twitchai.util.Globals.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import java.awt.Font;

import to.us.harha.twitchai.util.FileUtils;

import java.awt.Component;
import java.awt.EventQueue;

public class GUIWindow {

	private JFrame frmMcsubmitBot;
	private JTextField txtLevelCode;
	private JTextField txtLevelSubmitter;
	private JTextField txtTime;
	private JTextField txtDeaths;
	private JLabel lblSubmitterinchat;
	private JLabel lblLevelcount;
	private JCheckBox chckbxExportToComplete;
	private JCheckBox chckbxRemoveLevelOn;
	private JLabel lblLevelName;
	private JLabel lblLevelDifficulty;
	
	private GUIQueueList gui_queue_list = null;

//	/**
//	 * Launch the application.
//	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					GUIWindow window = new GUIWindow();
//					window.getFrame().setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the application.
	 */
	public GUIWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setFrame(new JFrame());
		getFrame().setBounds(100, 100, 285, 280);
		getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMcsubmitBot.getContentPane().setLayout(null);
		
		JLabel lblLevelCode = new JLabel("Level code:");
		lblLevelCode.setBounds(8, 42, 64, 16);
		frmMcsubmitBot.getContentPane().add(lblLevelCode);
		
		txtLevelCode = new JTextField();
		txtLevelCode.setEditable(false);
		txtLevelCode.setHorizontalAlignment(SwingConstants.CENTER);
		txtLevelCode.setText("xxxx-xxxx-xxxx-xxxx");
		txtLevelCode.setBounds(8, 56, 137, 20);
		frmMcsubmitBot.getContentPane().add(txtLevelCode);
		txtLevelCode.setColumns(10);
		
		txtLevelSubmitter = new JTextField();
		txtLevelSubmitter.setHorizontalAlignment(SwingConstants.CENTER);
		txtLevelSubmitter.setEditable(false);
		txtLevelSubmitter.setText("Username");
		txtLevelSubmitter.setBounds(145, 56, 128, 20);
		frmMcsubmitBot.getContentPane().add(txtLevelSubmitter);
		txtLevelSubmitter.setColumns(10);
		
		JLabel lblSubmitter = new JLabel("Submitter:");
		lblSubmitter.setBounds(145, 42, 59, 16);
		frmMcsubmitBot.getContentPane().add(lblSubmitter);
		
		JLabel lblTime = new JLabel("Time:");
		lblTime.setBounds(8, 183, 46, 14);
		frmMcsubmitBot.getContentPane().add(lblTime);
		
		txtTime = new JTextField();
		txtTime.setEditable(false);
		txtTime.setText("00:00:00");
		txtTime.setHorizontalAlignment(SwingConstants.CENTER);
		txtTime.setBounds(8, 196, 116, 20);
		frmMcsubmitBot.getContentPane().add(txtTime);
		txtTime.setColumns(10);
		
		JButton btnStart = new JButton("Start");
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				g_gui_timing = true;
				g_gui_start_time = System.nanoTime();
			}
		});
		btnStart.setBounds(8, 220, 62, 23);
		frmMcsubmitBot.getContentPane().add(btnStart);
		
		JButton btnEnd = new JButton("End");
		btnEnd.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				endTimer();
			}
		});
		btnEnd.setBounds(69, 220, 55, 23);
		frmMcsubmitBot.getContentPane().add(btnEnd);
		
		JButton btnNext = new JButton("Next");
		btnNext.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SMMHandler.getNext())
            	{
					
            		g_BOT.sendResponse(g_bot_name, ml_on_next);
            	}
				else
					g_BOT.sendResponse(g_bot_name, ml_no_queue);
				
				txtTime.setText("00:00:00");
				g_gui_timing = false;
				txtDeaths.setText("0");
				g_gui_ended_time = "00:00:00";
				writeTextFile("data/files/", "current_level_deaths.txt", "0");
				writeTextFile("data/files/", "current_level_time.txt", "00:00:00");
				updateViewLevels();
			}
		});
		btnNext.setBounds(43, 78, 89, 24);
		frmMcsubmitBot.getContentPane().add(btnNext);
		
		JButton btnRandom = new JButton("Random");
		btnRandom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(SMMHandler.getRandom())
            	{
            		g_BOT.sendResponse(g_bot_name, ml_on_next);
            	}
				else
					g_BOT.sendResponse(g_bot_name, ml_no_queue);
				txtTime.setText("00:00:00");
				g_gui_timing = false;
				txtDeaths.setText("0");
				g_gui_ended_time = "00:00:00";
				writeTextFile("data/files/", "current_level_deaths.txt", "0");
				writeTextFile("data/files/", "current_level_time.txt", "00:00:00");
				updateViewLevels();
			}
		});
		btnRandom.setBounds(155, 78, 89, 24);
		frmMcsubmitBot.getContentPane().add(btnRandom);
		
		JLabel lblDeaths = new JLabel("Deaths:");
		lblDeaths.setBounds(145, 183, 46, 14);
		frmMcsubmitBot.getContentPane().add(lblDeaths);
		
		txtDeaths = new JTextField();
		txtDeaths.setEditable(false);
		txtDeaths.setText("0");
		txtDeaths.setHorizontalAlignment(SwingConstants.CENTER);
		txtDeaths.setBounds(145, 196, 86, 20);
		frmMcsubmitBot.getContentPane().add(txtDeaths);
		txtDeaths.setColumns(10);
		
		JButton btnAdd = new JButton("^");
		btnAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				txtDeaths.setText(Integer.parseInt(txtDeaths.getText())+1+"");
				writeTextFile("data/files/", "current_level_deaths.txt", txtDeaths.getText());
				//TODO check for level. set in levelCodes as a variable. 
			}
		});
		btnAdd.setBounds(190, 220, 41, 23);
		frmMcsubmitBot.getContentPane().add(btnAdd);
		
		lblSubmitterinchat = new JLabel("Oops! Not set up! take a look at Install Instructions");
		lblSubmitterinchat.setFont(new Font("Dialog", Font.BOLD, 10));
		lblSubmitterinchat.setBounds(8, 103, 279, 14);
		frmMcsubmitBot.getContentPane().add(lblSubmitterinchat);
		
		chckbxRemoveLevelOn = new JCheckBox("Remove level from list on next/random?");
		chckbxRemoveLevelOn.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxRemoveLevelOn.setSelected(g_smm_remove_after_nextrandom);
		chckbxRemoveLevelOn.setBounds(8, 125, 223, 24);
		frmMcsubmitBot.getContentPane().add(chckbxRemoveLevelOn);
		
		lblLevelcount = new JLabel("Levels in Queue: ");
		lblLevelcount.setFont(new Font("Dialog", Font.BOLD, 10));
		lblLevelcount.setBounds(8, 0, 137, 16);
		frmMcsubmitBot.getContentPane().add(lblLevelcount);
		
		chckbxExportToComplete = new JCheckBox("Export to complete form on next/random");
		chckbxExportToComplete.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxExportToComplete.setBounds(8, 153, 221, 22);
		frmMcsubmitBot.getContentPane().add(chckbxExportToComplete);
		
		JLabel lblPublicRelease = new JLabel("v" + g_version);
		lblPublicRelease.setFont(new Font("Dialog", Font.BOLD, 8));
		lblPublicRelease.setBounds(0, 241, 89, 10);
		frmMcsubmitBot.getContentPane().add(lblPublicRelease);
		
		lblLevelDifficulty = new JLabel("Level Difficulty: ");
		lblLevelDifficulty.setFont(new Font("Dialog", Font.BOLD, 10));
		lblLevelDifficulty.setBounds(8, 14, 189, 16);
		frmMcsubmitBot.getContentPane().add(lblLevelDifficulty);
		
		lblLevelName = new JLabel("Level Title:");
		lblLevelName.setFont(new Font("Dialog", Font.BOLD, 10));
		lblLevelName.setBounds(8, 28, 265, 16);
		frmMcsubmitBot.getContentPane().add(lblLevelName);
		
		JButton btnSubtract = new JButton("v");
		btnSubtract.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				txtDeaths.setText(Integer.parseInt(txtDeaths.getText())-1+"");
				writeTextFile("data/files/", "current_level_deaths.txt", txtDeaths.getText());
			}
		});
		btnSubtract.setFont(new Font("Dialog", Font.BOLD, 12));
		btnSubtract.setBounds(145, 220, 41, 23);
		frmMcsubmitBot.getContentPane().add(btnSubtract);
		
		JButton btnQueuelist = new JButton("View Levels");
		btnQueuelist.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(gui_queue_list!= null){
					gui_queue_list.frmMcsubmitLevelViewer.dispose();
					gui_queue_list = null;
				}
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							gui_queue_list = new GUIQueueList();
							gui_queue_list.frmMcsubmitLevelViewer.setVisible(true);
							
							updateViewLevels();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		btnQueuelist.setFont(new Font("Dialog", Font.PLAIN, 8));
		btnQueuelist.setBounds(198, 5, 75, 20);
		frmMcsubmitBot.getContentPane().add(btnQueuelist);
		
		setChckbxRemoveLevelOn(g_smm_remove_after_nextrandom);
		setChckbxExportToComplete(g_forms_use_completed);
	}
	
	public void updateViewLevels(){
		ArrayList<LevelCode> levels = SMMHandler.getLevels();
		DefaultTableModel model = (DefaultTableModel) gui_queue_list.getTable().getModel();
		
		model.setRowCount(0);
		
		for(int i=0; i<levels.size(); i++)
		{
			LevelCode cur_lvl = levels.get(i);
			model.addRow(new Object[]{cur_lvl.getLevel(), cur_lvl.getAuthor()});
		}
		gui_queue_list.getTable().setModel(model);
	}

	public void endTimer() {
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
		txtTime.setText(g_gui_ended_time);
		g_gui_timing = false;
	}

	public JFrame getFrame() {
		return frmMcsubmitBot;
	}

	public void setFrame(JFrame frame) {
		this.frmMcsubmitBot = frame;
		frmMcsubmitBot.setTitle("McSubmit Bot");
		frame.setResizable(false);
	}

	public JTextField getTxtLevelCode() {
		return txtLevelCode;
	}

	public void setTxtLevelCode(JTextField txtLevelCode) {
		this.txtLevelCode = txtLevelCode;
	}

	public JTextField getTxtLevelSubmitter() {
		return txtLevelSubmitter;
	}

	public void setTxtLevelSubmitter(JTextField txtLevelSubmitter) {
		this.txtLevelSubmitter = txtLevelSubmitter;
	}

	public JTextField getTxtTime() {
		return txtTime;
	}

	public void setTxtTime(JTextField txtTime) {
		this.txtTime = txtTime;
	}

	public JTextField getTxtDeaths() {
		return txtDeaths;
	}

	public void setTxtDeaths(JTextField txtDeaths) {
		this.txtDeaths = txtDeaths;
	}

	public JLabel getLblSubmitterinchat() {
		return lblSubmitterinchat;
	}

	public void setLblSubmitterinchat(JLabel lblSubmitterinchat) {
		this.lblSubmitterinchat = lblSubmitterinchat;
	}

	public JLabel getLblLevelcount() {
		return lblLevelcount;
	}

	public void setLblLevelcount(JLabel lblLevelcount) {
		this.lblLevelcount = lblLevelcount;
	}

	public void updateNewLevel(int size, LevelCode current_level, boolean inChat) {
		lblLevelcount.setText("Levels in queue: " + size);
		
		txtLevelCode.setText(current_level.getLevel());
		txtLevelSubmitter.setText(current_level.getAuthor());
		
		if(inChat)
			lblSubmitterinchat.setText(current_level.getAuthor() + " is currently in the chat!");
		else
			lblSubmitterinchat.setText(current_level.getAuthor() + " is NOT in the chat.");
		
		lblLevelName.setText("Title: " + current_level.getName());
		lblLevelDifficulty.setText("Level Difficulty: " + current_level.getDifficulty());
		
		FileUtils.writeTextFile("data/files/", "current_level_title.txt", current_level.getName());
		FileUtils.writeTextFile("data/files/", "current_level_difficulty.txt", current_level.getDifficulty());
		
		g_gui_ended_time = "00:00:00";
		updateViewLevels();
	}

	public boolean getChckbxExportToComplete() {
		return chckbxExportToComplete.isSelected();
	}

	public void setChckbxExportToComplete(boolean bool) {
		this.chckbxExportToComplete.setSelected(bool);
	}

	public boolean getChckbxRemoveLevelOn() {
		return chckbxRemoveLevelOn.isSelected();
	}

	public void setChckbxRemoveLevelOn(boolean bool) {
		this.chckbxRemoveLevelOn.setSelected(bool);
	}

	public GUIQueueList getGui_queue_list() {
		return gui_queue_list;
	}

	public void setGui_queue_list(GUIQueueList gui_queue_list) {
		this.gui_queue_list = gui_queue_list;
	}
}
