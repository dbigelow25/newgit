package com.scaha.beans;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Division;
import com.scaha.objects.RosterEdit;
import com.scaha.objects.Season;
import com.scaha.objects.Team;

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class rosterBean implements Serializable{

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	transient private ResultSet rs = null;
	//lists for generated datamodels
	private List<RosterEdit> players = null;
	private List<RosterEdit> coaches = null;
	private List<Season> seasons = null;
	private List<Division> divisions = null;
	private List<Team> teams = null;
	
	//bean level properties used by multiple methods
	private String selectedseason = null;
	private String selecteddivision = null;
	private Integer selectedteam = null;
	private String teamname = null;
	private RosterEdit selectedplayer = null;
    
	
	@PostConstruct
    public void init() {
	    
    	//Load season list
        loadSeason();
        
        
    }
	
    public rosterBean() {  
        
    }  
    
    public RosterEdit getSelectedplayer(){
    	return selectedplayer;
    }
    
    public void setSelectedplayer(RosterEdit name){
    	selectedplayer=name;
    }
    
    
    public String getTeamname(){
    	return teamname;
    }
    
    public void setTeamname(String name){
    	teamname=name;
    }
    
    public String getSelectedseason(){
    	return selectedseason;
    }
    
    public void setSelectedseason(String value){
    	selectedseason = value;
    }
    
    public String getSelecteddivision(){
    	return selecteddivision;
    }
    
    public void setSelecteddivision(String value){
    	selecteddivision = value;
    }
    
    public Integer getSelectedteam(){
    	return selectedteam;
    }
    
    public void setSelectedteam(Integer value){
    	selectedteam = value;
    }
    
    
    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
    }
    
    public List<Season> getSeasons(){
    	return seasons;
    }
    
    public void setSeasons(List<Season> list){
    	seasons=list;
    }
    
    public List<Division> getDivisions(){
    	return divisions;
    }
    
    public void setDivisions(List<Division> list){
    	divisions=list;
    }
    
    public List<Team> getTeams(){
    	return teams;
    }
    
    public void setTeams(List<Team> list){
    	teams=list;
    }
    
    public void onSeasonChange(){
    	//need to load divisions available for the selected season
    	List<Division> templist = new ArrayList<Division>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getDivisionBySeason(?)");
    		cs.setString("seasontag", this.getSelectedseason());
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String divisionid = rs.getString("scheduletags");
					String divisionname = rs.getString("divisionname");
					
					Division division = new Division();
					division.setTag(divisionid);
					division.setDivisionname(divisionname);
					templist.add(division);
				}
				LOGGER.info("We have results for divisions");
			}
			rs.close();
			
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading divisions");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setDivisions(templist);
    	this.setCoaches(null);
    	this.setPlayers(null);
    	this.selecteddivision=null;
    	this.selectedteam=null;
    	
	}
    
    public void onDivisionChange(){
    	//need to load divisions available for the selected season
    	List<Team> templist = new ArrayList<Team>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getTeamByDivisionSeason(?,?)");
    		cs.setString("seasontag", this.getSelectedseason());
    		cs.setString("iddivision", this.getSelecteddivision());
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String teamid = rs.getString("idteams");
					String teamname = rs.getString("teamname");
					
					Team team = new Team(teamname,teamid);
					templist.add(team);
				}
				LOGGER.info("We have results for divisions");
			}
			rs.close();
			
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading divisions");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setTeams(templist);
    	this.setCoaches(null);
    	this.setPlayers(null);
    	this.selectedteam=null;
	}
    
	public void onTeamChange(){
		List<RosterEdit> templist = new ArrayList<RosterEdit>();
		List<RosterEdit> templist2 = new ArrayList<RosterEdit>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getTeamByTeamId(?)");
			cs.setInt("teamid", this.selectedteam);
		    rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					this.teamname = rs.getString("teamname");
				}
				LOGGER.info("We have results for team name");
			}
			rs.close();
			db.cleanup();
    		
    		cs = db.prepareCall("CALL scaha.getRosterPlayersForManagerByTeamID(?)");
			cs.setInt("teamid", this.selectedteam);
		    rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String playerid = rs.getString("idroster");
					String fname = rs.getString("fname");
					String lname = rs.getString("lname");
					String jerseynumber = rs.getString("jerseynumber");
					
					
					RosterEdit player = new RosterEdit();
					player.setIdplayer(playerid);
					player.setFirstname(fname);
					player.setLastname(lname);
					player.setOldjerseynumber(jerseynumber);
					player.setJerseynumber(jerseynumber);
					
					templist.add(player);
				}
				LOGGER.info("We have results for team roster");
			}
			rs.close();
			
			cs = db.prepareCall("CALL scaha.getRosterCoachesForManagerByTeamID(?)");
			cs.setInt("teamid", this.selectedteam);
		    rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String playerid = rs.getString("idroster");
					String fname = rs.getString("fname");
					String lname = rs.getString("lname");
					String jerseynumber = rs.getString("teamrole");
					
					
					RosterEdit player = new RosterEdit();
					player.setIdplayer(playerid);
					player.setFirstname(fname);
					player.setLastname(lname);
					player.setOldjerseynumber(jerseynumber);
					player.setJerseynumber(jerseynumber);
					
					templist2.add(player);
				}
				LOGGER.info("We have results for team roster");
			}
			rs.close();
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading teams");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setPlayers(templist);
    	setCoaches(templist2);
    	
	}
    
    public List<RosterEdit> getPlayers(){
		return players;
	}
	
	public void setPlayers(List<RosterEdit> list){
		players = list;
	}
	
	public List<RosterEdit> getCoaches(){
		return coaches;
	}
	
	public void setCoaches(List<RosterEdit> list){
		coaches = list;
	}
	
	public void loadSeason(){
		List<Season> templist = new ArrayList<Season>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getAllSeasonsByType('SCAHA')");
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String seasonid = rs.getString("tag");
					String seasonname = rs.getString("Description");
					
					Season season = new Season();
					season.setSeasonid(seasonid);
					season.setSeasonname(seasonname);
					
					templist.add(season);
				}
				LOGGER.info("We have results for seasons");
			}
			rs.close();
			
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading teams");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setSeasons(templist);
    	
	}
}

