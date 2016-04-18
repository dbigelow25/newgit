package com.scaha.beans;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.SelectEvent;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Game;
import com.scaha.objects.Schedule;
import com.scaha.objects.Season;

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class gamecentralBean implements Serializable{

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	transient private ResultSet rs = null;
	//lists for generated datamodels
	private List<Game> listofgames = null;
	private List<Season> seasons = null;
	private List<Schedule> schedules = null;
	
	//bean level properties used by multiple methods
	private Date selecteddate = null;
	private String selectedseason = null;
	private Integer selectedschedule = null;
	private Integer selectedgame = null;
	private String mondaylink = null;
	private String tuesdaylink = null;
	private String wednesdaylink = null;
	private String thursdaylink = null;
	private String fridaylink = null;
	private String saturdaylink = null;
	private String sundaylink = null;
	
	private String mondaydate = null;
	private String tuesdaydate = null;
	private String wednesdaydate = null;
	private String thursdaydate = null;
	private String fridaydate = null;
	private String saturdaydate = null;
	private String sundaydate = null;
	private String eligibledates = null;
	
	//for calendar and days of the week
	private Date todaysdate = null;
	
	
	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	
	@ManagedProperty(value="#{clubBean}")
    private ClubBean clubbean;
	
	@ManagedProperty(value="#{scoreboardBean}")
    private ScoreboardBean scoreboard;
	
	@PostConstruct
    public void init() {
		HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		String initial = "";
		if(hsr.getParameter("initial") != null)
        {
    		initial = hsr.getParameter("initial");
        } else {
        	this.setSelectedschedule(0);
        }
		
		if(hsr.getParameter("schedule") != null)
        {
    		this.selectedschedule = Integer.parseInt(hsr.getParameter("schedule"));
        } else {
        	this.setSelectedschedule(0);
        }
    	
    	if(hsr.getParameter("season") != null)
        {
    		this.selectedseason = hsr.getParameter("season");
    		if (initial.equals("yes")){
    			this.onSeasonChange();
    		}
        } else {
        	this.selectedseason = scoreboard.getSelectedseason().getTag();
        }
    	
    	if(hsr.getParameter("selecteddate") != null)
        {
			Integer provideddate = Integer.parseInt(hsr.getParameter("selecteddate"));
			getDatefromGame(provideddate);
		}else{
			Date date = new Date();
			if (initial.equals("")){
				this.setSelecteddate(date);
    		}
			this.setTodaysdate(date);
            
        }
		
		
        //need to load the schedule list
    	loadScheduleList();
        
        
        //need to load the seasons in the menu list.
        loadSeasons();
        
        //ok now we need to set the values for the weekday breadcrumbs
        setWeekdaybreadcrumbs();
    	
		//Load season list
        loadGamesfordate();
        
        //setting todays date for the calendar
        this.setTodaysdate(Calendar.getInstance().getTime());
        
        //lets load the dates that have games
        setGameDays();
        
    }
	
    public gamecentralBean() {  
        
    }  
    
    public void setSelectedgame(Integer value){
    	selectedgame=value;
    }
    
    public Integer getSelectedgame(){
    	return selectedgame;
    }
    
    public List<Schedule> getSchedules(){
    	return schedules;
    }
    
    public void setSchedules(List<Schedule> list){
    	schedules=list;
    }
    
    public List<Season> getSeasons(){
    	return seasons;
    }
    
    public void setSeasons(List<Season> list){
    	seasons=list;
    }
    
    public void setEligibledates(String value){
    	this.eligibledates=value;
    }
    
    public String getEligibledates(){
    	return this.eligibledates;
    }
    
    public Integer getSelectedschedule(){
    	return selectedschedule;
    }
    
    public void setSelectedschedule(Integer value){
    	selectedschedule = value;
    }
    
    public List<Game> getListofgames(){
    	return listofgames;
    }
    
    public void setListofgames(List<Game> list){
    	listofgames = list;
    }
    
    
    public Date getSelecteddate(){
    	return selecteddate;
    }
    
    public void setSelecteddate(Date value){
    	selecteddate=value;
    }
    
    public String getSelectedseason(){
    	return selectedseason;
    }
    
    public void setSelectedseason(String value){
    	selectedseason=value;
    }
    
    public ScahaBean getScaha() {
		return scaha;
	}

	/**
	 * @param scaha the scaha to set
	 */
	public void setClubbean(ClubBean club) {
		this.clubbean = club;
	}
    
	public ClubBean getClubbean() {
		return this.clubbean;
	}

	/**
	 * @param scaha the scaha to set
	 */
	public void setScoreboard(ScoreboardBean club) {
		this.scoreboard = club;
	}
    
	public ScoreboardBean getScoreboard() {
		return this.scoreboard;
	}
	
	/**
	 * @param scaha the scaha to set
	 */
	public void setScaha(ScahaBean scaha) {
		this.scaha = scaha;
	}
	
	public void setTodaysdate(Date newdate){
		this.todaysdate=newdate;
	}
	
	public Date getTodaysdate(){
		return this.todaysdate;
	}
	
	public String getMondaylink(){
    	return mondaylink;
    }
    
    public void setMondaylink(String value){
    	mondaylink=value;
    }
    
    public String getTuesdaylink(){
    	return tuesdaylink;
    }
    
    public void setTuesdaylink(String value){
    	tuesdaylink=value;
    }
    
    public String getWednesdaylink(){
    	return wednesdaylink;
    }
    
    public void setWednesdaylink(String value){
    	wednesdaylink=value;
    }
    
    public String getThursdaylink(){
    	return thursdaylink;
    }
    
    public void setThursdaylink(String value){
    	thursdaylink=value;
    }
    
    public String getFridaylink(){
    	return fridaylink;
    }
    
    public void setFridaylink(String value){
    	fridaylink=value;
    }
	
    public String getSaturdaylink(){
    	return saturdaylink;
    }
    
    public void setSaturdaylink(String value){
    	saturdaylink=value;
    }
    
    public String getSundaylink(){
    	return sundaylink;
    }
    
    public void setSundaylink(String value){
    	sundaylink=value;
    }
    
    public String getMondaydate(){
    	return mondaydate;
    }
    
    public void setMondaydate(String value){
    	mondaydate=value;
    }
    
    public String getTuesdaydate(){
    	return tuesdaydate;
    }
    
    public void setTuesdaydate(String value){
    	tuesdaydate=value;
    }
    
    public String getWednesdaydate(){
    	return wednesdaydate;
    }
    
    public void setWednesdaydate(String value){
    	wednesdaydate=value;
    }
    
    public String getThursdaydate(){
    	return thursdaydate;
    }
    
    public void setThursdaydate(String value){
    	thursdaydate=value;
    }
    
    public String getFridaydate(){
    	return fridaydate;
    }
    
    public void setFridaydate(String value){
    	fridaydate=value;
    }
	
    public String getSaturdaydate(){
    	return saturdaydate;
    }
    
    public void setSaturdaydate(String value){
    	saturdaydate=value;
    }
    
    public String getSundaydate(){
    	return sundaydate;
    }
    
    public void setSundaydate(String value){
    	sundaydate=value;
    }
    
    
    
    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
    }
    
    
    
    
    public String getDisplayDate(){
    	DateFormat df=new SimpleDateFormat("MM/dd/yyyy");
    	String tempdate = df.format(this.selecteddate);
    	
    	return tempdate;
    }
    
    public void onDateSelect(SelectEvent event) {
    	
    	
        loadGamesfordate();
    }
    
	public void loadGamesfordate(){
		List<Game> templist = new ArrayList<Game>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//lets get the list of games for the date specified.
    		CallableStatement cs = db.prepareCall("CALL scaha.getScoreboardSchedule(?,?,?)");
    		
    		DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        	String tempdate = df.format(this.selecteddate);
        	
    		cs.setString("selecteddate", tempdate);
    		cs.setInt("selectedschedule", this.selectedschedule);
    		cs.setString("in_seasontag", this.selectedseason);
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					Integer idlivegame = rs.getInt("idlivegame");
					String hometeam = rs.getString("hometeam");
					String awayteam = rs.getString("awayteam");
					String typetag = rs.getString("typetag");
					String location = rs.getString("location");
					String gametime = rs.getString("time");
					String hometeamscore = rs.getString("homescore");
					String awayteamscore = rs.getString("awayscore");
					String status = rs.getString("status");
					String displaydivision = rs.getString("displaydivision");
					Integer homeclubid = rs.getInt("homeclubid");
					Integer awayclubid = rs.getInt("awayclubid");
					Boolean boxscore = rs.getBoolean("boxscore");
					
					
					Game game = new Game();
					game.setIdlivegame(idlivegame);
					game.setHometeam(hometeam);
					game.setAwayteam(awayteam);
					game.setTypetag(typetag);
					game.setLocation(location);
					game.setGametime(gametime);
					game.setHomescore(hometeamscore);
					game.setAwayscore(awayteamscore);
					game.setStatus(status);
					game.setDisplaydivision(displaydivision);
					game.setHomeclubid(homeclubid);
					game.setAwayclubid(awayclubid);
					game.setRenderboxscore(boxscore);
					templist.add(game);
				}
				LOGGER.info("We have game list results for the daet:" + this.selecteddate);
			}
			rs.close();
			
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading games for the date:" + this.selecteddate);
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	this.setListofgames(templist);
    	
	}
	
	/*public StreamedContent getClubLogoByParmId(Game game,String homeaway) {
		String get="";
		if (homeaway.equals("Away")){
			get = game.getAwayclubid().toString();
		}else{
			get = game.getHomeclubid().toString();
		}
		
	    if (get == null) {
    		return new DefaultStreamedContent();
	    } else if (get.length() == 0) {
    		return new DefaultStreamedContent();
	    }
    	int id = Integer.parseInt(get);
	    Club myclub  = scaha.findClubByID(id);
	    if (myclub == null) {
			LOGGER.info("*** Could not find club... for id LOGO ID IS (" + get + ") ");
    		return new DefaultStreamedContent();
	    }
	    
	    LOGGER.info("*** club is...("+ myclub + ") for id LOGO ID IS (" + get + ") ");
		return clubbean.getClubLogo(myclub);
	}*/
	
	public void setWeekdaybreadcrumbs(){
		this.setMondaylink("Monday");
		this.setTuesdaylink("Tuesday");
		this.setWednesdaylink("Wednesday");
		this.setThursdaylink("Thursday");
		this.setFridaylink("Friday");
		this.setSaturdaylink("Saturday");
		this.setSundaylink("Sunday");
		
		DateFormat format2=new SimpleDateFormat("EEEE"); 
		String currentdayofweek = format2.format(this.selecteddate);
		
		switch(currentdayofweek){
			case "Monday": this.setMondaylink("Today");
				break;
			case "Tuesday": this.setTuesdaylink("Today");
				break;
			case "Wednesday": this.setWednesdaylink("Today");
				break;
			case "Thursday": this.setThursdaylink("Today");
				break;
			case "Friday": this.setFridaylink("Today");
				break;
			case "Saturday": this.setSaturdaylink("Today");
				break;
			case "Sunday": this.setSundaylink("Today");
				break;
		}
	}
	
	public void loadDate(String selecteddayofweek){
		getDayOfWeekDate(selecteddayofweek);
		
		loadGamesfordate();
	}
	
	public void getDayOfWeekDate(String dayofweek) {
	    //need to determine how many days to subtract/add to the current date
		Integer newweekday = 0;
		switch(dayofweek){
		case "Monday": newweekday=2;
			break;
		case "Tuesday": newweekday=3;
			break;
		case "Wednesday": newweekday=4;
			break;
		case "Thursday": newweekday=5;
			break;
		case "Friday": newweekday=6;
			break;
		case "Saturday": newweekday=7;
			break;
		case "Sunday": newweekday=8;
			break;	
		}
		
		Calendar cal = Calendar.getInstance();
	    cal.setTime(this.getTodaysdate());
	    
	    Integer currentdayofweek = cal.get(Calendar.DAY_OF_WEEK);
	    Integer daystoadd = newweekday-currentdayofweek;
	    
	    cal.add(Calendar.DATE, daystoadd);
	    this.setSelecteddate(cal.getTime());
	    
	}
	
	public void onSeasonChange(){
		List<Schedule> data = new ArrayList<Schedule>();
		
    	this.selectedschedule=0;
    	
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//lets get the list of games for the date specified.
    		CallableStatement cs = db.prepareCall("CALL scaha.getSeasonDate(?)");
    		
    		cs.setString("in_seasontag", this.selectedseason);
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					this.selecteddate=rs.getDate(1);
				}
				LOGGER.info("We have selected date for schedule:" + selectedschedule);
			}
			rs.close();
			cs.close();
			
			cs = db.prepareCall("CALL scaha.getScoreboardSchedulesBySeasonTag(?)");
    		cs.setString("in_SeasonTag", this.getSelectedseason());
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					Schedule sch = new Schedule(rs.getInt("idschedule"));
					sch.setDescription(rs.getString("description"));
					
					data.add(sch);
				}
				LOGGER.info("We have results for divisions");
			}
			rs.close();
			cs.close();
			db.cleanup();
    		
			
		
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN retrieving selected date for schedule" + selectedschedule);
    		e.printStackTrace();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	
    	setSchedules(data);
    	this.loadGamesfordate();
    	setGameDays();
    		
    	
	}
	
	public void onScheduleChange(){
		
		//if schedule is not from this season need to get last day of the schedule otherwise
		//use today's date the following stored procedure determines which date to use.  this is for populating the calendar.
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//lets get the list of games for the date specified.
    		CallableStatement cs = db.prepareCall("CALL scaha.getScheduleDate(?)");
    		
    		cs.setInt("in_scheduleid", this.selectedschedule);
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					this.selecteddate=rs.getDate(1);
				}
				LOGGER.info("We have selected date for schedule:" + selectedschedule);
			}
			rs.close();
			cs.close();
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN retrieving selected date for schedule" + selectedschedule);
    		e.printStackTrace();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	
    	this.loadGamesfordate();
    	setGameDays();
		
	}
	
	public void gotoBoxscore(Game game){
		String gameid = game.getIdlivegame().toString();
		
		FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("boxscore.xhtml?id=" + gameid);
    	} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void setGameDays(){
		List<String> tempdates = new ArrayList<String>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//lets get the list of game dates for the month of the date specified
    		CallableStatement cs = db.prepareCall("CALL scaha.getGamedatesformonth(?,?,?)");
    		
    		DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        	String tempdate = df.format(this.selecteddate);
        	
    		cs.setString("selecteddate", tempdate);
    		cs.setInt("selectedschedule", this.selectedschedule);
    		cs.setString("in_seasontag", this.selectedseason);
			rs = cs.executeQuery();
			
			Boolean what = true;
			if (rs != null){
				while (rs.next()) {
					tempdates.add("\"" + rs.getString("actdate") + "\"");
					what = false;
				}
				
			}
			
			if (what){
				tempdates.add("1900-01-01");
			}
			
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN retrieving selected date for schedule" + selectedschedule);
    		e.printStackTrace();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setEligibledates(tempdates.toString());
    	
    	
	}
	
	public void loadSeasons(){
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
	
	public void loadScheduleList(){
		List<Schedule> data = new ArrayList<Schedule>();
		
    	
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		CallableStatement cs = db.prepareCall("CALL scaha.getScoreboardSchedulesBySeasonTag(?)");
    		cs.setString("in_SeasonTag", this.getSelectedseason());
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					Schedule sch = new Schedule(rs.getInt("idschedule"));
					sch.setDescription(rs.getString("description"));
					
					data.add(sch);
				}
				LOGGER.info("We have results for divisions");
			}
			rs.close();
			cs.close();
			db.cleanup();
    		
			
		
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN retrieving selected date for schedule" + selectedschedule);
    		e.printStackTrace();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	
    	setSchedules(data);
	}

	public void getDatefromGame(Integer gameid){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		CallableStatement cs = db.prepareCall("CALL scaha.getDatefromgame(?,?)");
    		cs.setInt("in_gameid", gameid);
    		cs.setString("in_selectedseason", this.selectedseason);
    		
			rs = cs.executeQuery();
			
			Date gamedate = null;
			Date date = new Date();
			this.setTodaysdate(date);
			if (rs != null){
				
				while (rs.next()) {
								
					gamedate = rs.getDate("actdate");
				}
				LOGGER.info("We have results for divisions");
			}
			rs.close();
			cs.close();
			db.cleanup();
    		
			if (gamedate==null){
				this.setSelecteddate(date);
	        }else{
	        	this.setSelecteddate(gamedate);
			}
		
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN retrieving selected date for schedule" + selectedschedule);
    		e.printStackTrace();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		

	}
	
	public void loadScrollingGamesfordate(){
		List<Game> templist = new ArrayList<Game>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//lets get the list of games for the date specified.
    		CallableStatement cs = db.prepareCall("CALL scaha.getScoreboardSchedule(?,?,?)");
    		
    		DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        	String tempdate = df.format(this.selecteddate);
        	tempdate = "2015-09-12";
    		cs.setString("selecteddate", tempdate);
    		cs.setInt("selectedschedule", this.selectedschedule);
    		cs.setString("in_seasontag", this.selectedseason);
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					Integer idlivegame = rs.getInt("idlivegame");
					String hometeam = rs.getString("hometeam");
					String awayteam = rs.getString("awayteam");
					String typetag = rs.getString("typetag");
					String location = rs.getString("location");
					String gametime = rs.getString("time");
					String hometeamscore = rs.getString("homescore");
					String awayteamscore = rs.getString("awayscore");
					String status = rs.getString("status");
					String displaydivision = rs.getString("displaydivision");
					Integer homeclubid = rs.getInt("homeclubid");
					Integer awayclubid = rs.getInt("awayclubid");
					Boolean boxscore = rs.getBoolean("boxscore");
					
					
					Game game = new Game();
					game.setIdlivegame(idlivegame);
					game.setHometeam(hometeam);
					game.setAwayteam(awayteam);
					game.setTypetag(typetag);
					game.setLocation(location);
					game.setGametime(gametime);
					game.setHomescore(hometeamscore);
					game.setAwayscore(awayteamscore);
					game.setStatus(status);
					game.setDisplaydivision(displaydivision);
					game.setHomeclubid(homeclubid);
					game.setAwayclubid(awayclubid);
					game.setRenderboxscore(boxscore);
					templist.add(game);
				}
				LOGGER.info("We have game list results for the daet:" + this.selecteddate);
			}
			rs.close();
			
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading games for the date:" + this.selecteddate);
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	this.setListofgames(templist);
    	
	}
}

