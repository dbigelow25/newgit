package com.scaha.beans;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;

import com.gbli.common.SendMailSSL;
import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.gbli.context.PenaltyPusher;
import com.scaha.objects.LiveGame;
import com.scaha.objects.LiveGameRosterSpot;
import com.scaha.objects.LiveGameRosterSpotList;
import com.scaha.objects.MailableObject;
import com.scaha.objects.Penalty;
import com.scaha.objects.PenaltyList;
import com.scaha.objects.ScahaTeam;
import com.scaha.objects.Scoring;
import com.scaha.objects.ScoringList;
import com.scaha.objects.Sog;
import com.scaha.objects.SogList;

@ManagedBean
@ViewScoped
public class GamesheetBean implements Serializable,  MailableObject {
	
	private static String mail_reg_body = Utils.getMailTemplateFromFile("/mail/gamechange.html");
	

	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;
	
	@ManagedProperty(value="#{rosterBean}")
	private rosterBean rb;
	
	private LiveGame livegame = null;
	private LiveGameRosterSpot selectedhomerosterspot;	
	private LiveGameRosterSpot selectedawayrosterspot;	
	
	private Scoring selectedhomescore = null;
	private Scoring selectedawayscore = null;
	private Scoring currentscore =null;
	private Penalty currentpenalty = null;
	private Sog currentsog = null;
	private Penalty selectedhomepenalty = null;
	private Penalty selectedawaypenalty = null;
	private Sog selectedhomesog = null;
	private Sog selectedawaysog = null;
	
	private LiveGameRosterSpotList awayteam = null;
	private LiveGameRosterSpotList awayteamforpl = null;
	private LiveGameRosterSpotList hometeam = null;
	private LiveGameRosterSpotList hometeamforpl = null;
	
	private ScoringList awayscoring = null;
	private ScoringList homescoring = null;
	private PenaltyList homepenalties = null;
	private PenaltyList awaypenalties = null;
	private SogList awaysogs = null;
	private SogList homesogs = null;
			
	
	private List<LiveGameRosterSpot> scoringpicklist = null;
	private ScahaTeam scoringteam = null;
	private LiveGameRosterSpotList scoringroster = null;
	private int selectedgoalroseterid = 0;
	private int selecteda1roseterid = 0;
	private int selecteda2roseterid = 0;
	private int selectedpenrosterid = 0;
	private int selectedsogrosterid = 0;
	
	private List<LiveGameRosterSpot> penpicklist = null;
	private ScahaTeam penteam = null;
	private LiveGameRosterSpotList penroster = null;
	
	private List<LiveGameRosterSpot> sogpicklist = null;
	private ScahaTeam sogteam = null;
	private LiveGameRosterSpotList sogroster = null;
	
	private int goalperiod = 0;
	private String goaltype = null;
	private String goalmin = null;
	private String goalsec = null;
	
	private int penperiod = 0;
	private String pentype = null;
	private String penminutes = null;
	private String penmin = null;
	private String pensec = null;
	
	private String sogplaytime = null;
	private int sogshots1 = 0;
	private int sogshots2 = 0;
	private int sogshots3 = 0;
	private int sogshots4 = 0;
	private int sogshots5 = 0;
	private int sogshots6 = 0;
	private int sogshots7 = 0;
	private int sogshots8 = 0;
	private int sogshots9 = 0;

	//
	// Class Level Variables
	//
	
	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private List<String> penalties = new ArrayList<String>();
	private Map<String, String> venues = new HashMap<String, String>();
	private Map<String, String> htpick = new HashMap<String, String>();
	private Map<String, String> atpick = new HashMap<String, String>();
	private Map<String, String> typepick = new HashMap<String, String>();
	private Map<String, String> statepick = new HashMap<String, String>();
	
	private String lgsheet = null;
	private String lgvenue = null;
	private String lgtype = null;
	private String lgstate = null;
	private String lgdate = null;
	private String lgtime = null;
	private String lgateam = null;
	private String lghteam = null;
	
	private String lgateamval = null;
	private String lghteamval = null;
	private String lgvenueval = null;
	private String lgtypeval = null;
	private String lgstateval = null;
 
	
	private boolean editgame = false;
	
	
	//
	// lets go get it!
	//
	public GamesheetBean() {
	}

	 @PostConstruct
	 public void init() {
		 
		 LOGGER.info(" *************** START :POST INIT FOR GAMESHEET  BEAN *****************");
		 this.setLivegame(pb.getSelectedlivegame());
		 LOGGER.info("/// here is selected live game.." + this.getLivegame());
		 
		 if (this.getLivegame() != null) {
			 this.setHometeam(this.refreshHomeRoster());
			 this.setAwayteam(this.refreshAwayRoster());
			 this.setHometeamforpl(this.refreshHomeRosterforpl());
			 this.setAwayteamforpl(this.refreshAwayRosterforpl());
			 this.setAwayscoring(this.refreshAwayScoring());
			 this.setHomescoring(this.refreshHomeScoring());
			 this.setHomepenalties(this.refreshHomePenalty());
			 this.setAwaypenalties(this.refreshAwayPenalty());
			 this.setHomesogs(this.refreshHomeSog());
			 this.setAwaysogs(this.refreshAwaySog());
		 } else {
			 LOGGER.info(" ##### NO LIVE GAME PASSED... IT WAS NULL #####");
			 
		 }
		 
		 setPenaltiesPickList();
		 setVenuesPickList();

		 
		 this.htpick.put(this.livegame.getHometeam().getTeamname(), this.livegame.getHometeam().ID+"");
		 this.htpick.put(this.livegame.getAwayteam().getTeamname(), this.livegame.getAwayteam().ID+"");

		 this.atpick.put(this.livegame.getAwayteam().getTeamname(),this.livegame.getAwayteam().ID+"");
		 this.atpick.put(this.livegame.getHometeam().getTeamname(),this.livegame.getHometeam().ID+"");
		 
		 this.typepick.put("Pre Season","Pre");
		 this.typepick.put("Exhibition","Exh");
		 this.typepick.put("Game","Game");
		 
		 this.statepick.put("Scheduled","Scheduled");
		 this.statepick.put("Cancelled","Cancelled");
		 this.statepick.put("Forfiet","Forfiet");
		 this.statepick.put("In Progress","InProgress");
		 this.statepick.put("Complete","Complete");
		 this.statepick.put("Stats Review","StatsReview");
		 this.statepick.put("Final","Final");

		 this.setDisplayValues();

		 LOGGER.info(" *************** FINISH :POST INIT FOR GAMESHEET BEAN *****************");
		 
	 }
	
	
	 /**
	  * for the given live game.. lets set up the display values
	  */
	private void setDisplayValues() {
		
		LOGGER.info("id Live Game is:" + this.livegame.ID);

		this.lgdate = this.livegame.getStartdate();
		this.lgtime = this.livegame.getStarttime();
		this.lgsheet = this.livegame.getSheetname();
		this.lgvenue = getStringKeyFromValue(this.venues,this.livegame.getVenuetag());
		this.lgtype =  getStringKeyFromValue(this.typepick, this.livegame.getTypetag());
		this.lgstate = getStringKeyFromValue(this.statepick, this.livegame.getStatetag());
		this.lgstateval =this.livegame.getStatetag();
		this.lgtypeval = this.livegame.getTypetag();
		this.lgvenueval =this.livegame.getVenuetag();
		this.lghteam = this.livegame.getHometeamname();
		this.lgateam = this.livegame.getAwayteamname();
		this.lghteamval = this.livegame.getHometeam().ID+"";
		this.lgateamval = this.livegame.getAwayteam().ID+"";
		
	}

	/**
	 * @return the selectedhomerosterspot
	 */
	public LiveGameRosterSpot getSelectedhomerosterspot() {
		return selectedhomerosterspot;
	}

	/**
	 * @param selectedhomerosterspot the selectedhomerosterspot to set
	 */
	public void setSelectedhomerosterspot(LiveGameRosterSpot selectedhomerosterspot) {
		this.selectedhomerosterspot = selectedhomerosterspot;
	}

	/**
	 * @return the selectedawayrosterspot
	 */
	public LiveGameRosterSpot getSelectedawayrosterspot() {
		return selectedawayrosterspot;
	}

	/**
	 * @param selectedawayrosterspot the selectedawayrosterspot to set
	 */
	public void setSelectedawayrosterspot(LiveGameRosterSpot selectedawayrosterspot) {
		this.selectedawayrosterspot = selectedawayrosterspot;
	}

	/**
	 * @return the awayteam
	 */
	public LiveGameRosterSpotList getAwayteam() {
		return awayteam;
	}

	/**
	 * @param awayteam the awayteam to set
	 */
	public void setAwayteamforpl(LiveGameRosterSpotList awayteam) {
		this.awayteamforpl = awayteam;
	}

	public LiveGameRosterSpotList getAwayteamforpl() {
		return awayteamforpl;
	}

	/**
	 * @param awayteam the awayteam to set
	 */
	public void setAwayteam(LiveGameRosterSpotList awayteam) {
		this.awayteam = awayteam;
	}

	/**
	 * @return the hometeam
	 */
	public LiveGameRosterSpotList getHometeam() {
		return hometeam;
	}

	/**
	 * @param hometeam the hometeam to set
	 */
	public void setHometeamforpl(LiveGameRosterSpotList hometeam) {
		this.hometeamforpl = hometeam;
	}

	/**
	 * @return the hometeam
	 */
	public LiveGameRosterSpotList getHometeamforpl() {
		return hometeamforpl;
	}

	/**
	 * @param hometeam the hometeam to set
	 */
	public void setHometeam(LiveGameRosterSpotList hometeam) {
		this.hometeam = hometeam;
	}

	/**
	 * @return the scaha
	 */
	public ScahaBean getScaha() {
		return scaha;
	}

	/**
	 * @param scaha the scaha to set
	 */
	public void setScaha(ScahaBean scaha) {
		this.scaha = scaha;
	}

	/**
	 * @return the pb
	 */
	public ProfileBean getPb() {
		return pb;
	}

	/**
	 * @param pb the pb to set
	 */
	public void setPb(ProfileBean pb) {
		this.pb = pb;
	}

	/**
	 * @return the pb
	 */
	public rosterBean getRb() {
		return rb;
	}

	/**
	 * @param pb the pb to set
	 */
	public void setRb(rosterBean rb) {
		this.rb = rb;
	}
	
	/**
	 * @return the livegame
	 */
	public LiveGame getLivegame() {
		return livegame;
	}

	/**
	 * @param livegame the livegame to set
	 */
	public void setLivegame(LiveGame livegame) {
		this.livegame = livegame;
	}

	public LiveGameRosterSpotList refreshHomeRoster() {
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		LiveGameRosterSpotList list = null;
		try {
			list = LiveGameRosterSpotList.NewListFactory(pb.getProfile(), db, this.getLivegame(), scaha.getScahaTeamList(), "H");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			db.free();
		}
		
		return list;
	}

	public LiveGameRosterSpotList refreshHomeRosterforpl() {
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		LiveGameRosterSpotList list = null;
		try {
			list = LiveGameRosterSpotList.NewListFactoryByJerseyNumber(pb.getProfile(), db, this.getLivegame(), scaha.getScahaTeamList(), "H");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			db.free();
		}
		
		return list;
	}
	
	public LiveGameRosterSpotList refreshAwayRoster() {

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		LiveGameRosterSpotList list = null;
		try {
			list = LiveGameRosterSpotList.NewListFactory(pb.getProfile(), db, this.getLivegame(), scaha.getScahaTeamList(), "A");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			db.free();
		}
		
		
		return list;
	}
	
	public LiveGameRosterSpotList refreshAwayRosterforpl() {

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		LiveGameRosterSpotList list = null;
		try {
			list = LiveGameRosterSpotList.NewListFactoryByJerseyNumber(pb.getProfile(), db, this.getLivegame(), scaha.getScahaTeamList(), "A");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			db.free();
		}
		
		
		return list;
	}
	
public SogList refreshHomeSog() {
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		SogList list = null;
		try {
			list = SogList.NewListFactory(pb.getProfile(), db, this.getLivegame(), this.getLivegame().getHometeam(), this.getHometeam());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			db.free();
		}
		
	
		return list;
	}


	public SogList refreshAwaySog() {

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		SogList list = null;
		try {
			list = SogList.NewListFactory(pb.getProfile(), db, this.getLivegame(), this.getLivegame().getAwayteam(), this.getAwayteam());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			db.free();
		}
		return list;
	}



	private ScoringList refreshHomeScoring() {
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		ScoringList list = null;
		try {
			list = ScoringList.NewListFactory(pb.getProfile(), db, this.getLivegame(), this.getLivegame().getHometeam(), this.getHometeam());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			db.free();
		}
		
		
		return list;
	}

	private ScoringList refreshAwayScoring() {
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		ScoringList list = null;
		try {
			list = ScoringList.NewListFactory(pb.getProfile(), db, this.getLivegame(), this.getLivegame().getAwayteam(), this.getAwayteam());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		
			db.free();
		}
		
		return list;
	}
	
	private PenaltyList refreshHomePenalty() {
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		PenaltyList list = null;
		try {
			list = PenaltyList.NewListFactory(pb.getProfile(), db, this.getLivegame(), this.getLivegame().getHometeam(), this.getHometeam());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			db.free();
		}
		
		return list;
	}
	
	private PenaltyList refreshAwayPenalty() {
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		PenaltyList list = null;
		try {
			list = PenaltyList.NewListFactory(pb.getProfile(), db, this.getLivegame(), this.getLivegame().getAwayteam(), this.getAwayteam());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.free();
		}
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public void newGoal(String _ha) {
		
		if (_ha.equals("H")) { 
			this.scoringteam = this.livegame.getHometeam();
			this.scoringroster = this.getHometeam();
			this.scoringpicklist = (List<LiveGameRosterSpot>) this.getHometeamforpl().getWrappedData();
			this.currentscore = new Scoring(0,pb.getProfile(),this.livegame,this.scoringteam);
		} else {
			this.scoringteam = this.livegame.getAwayteam();
			this.scoringroster = this.getAwayteam();
			this.scoringpicklist = (List<LiveGameRosterSpot>) this.getAwayteamforpl().getWrappedData();
			this.currentscore = new Scoring(0,pb.getProfile(),this.livegame,this.scoringteam);
		}
		
		//
		// reinitialize the info
		//
		this.goalperiod = 0;
		this.goalmin = null;
		this.goalsec = null;
		this.goaltype = null;
		this.selectedgoalroseterid = 0;
		this.selecteda1roseterid = 0;
		this.selecteda2roseterid = 0;

		
	}
	
	@SuppressWarnings("unchecked")
	public void newPenalty(String _ha) {
		
		if (_ha.equals("H")) { 
			this.penteam = this.livegame.getHometeam();
			this.penroster = this.getHometeam();
			this.penpicklist = (List<LiveGameRosterSpot>) this.getHometeamforpl().getWrappedData();
			this.currentpenalty = new Penalty(0,pb.getProfile(),this.livegame,this.penteam);
		} else {
			this.penteam = this.livegame.getAwayteam();
			this.penroster = this.getAwayteam();
			this.penpicklist = (List<LiveGameRosterSpot>) this.getAwayteamforpl().getWrappedData();
			this.currentpenalty = new Penalty(0,pb.getProfile(),this.livegame,this.penteam);
		}
		
		//
		// reinitialize the info
		//
		this.penperiod = 0;
		this.penminutes = null;
		this.penmin = null;
		this.pensec = null;
		this.pentype = null;
		this.selectedpenrosterid = 0;
		
	}
	
	@SuppressWarnings("unchecked")
	public void editGoal(String _ha) {
		
		if (_ha.equals("H")) { 
			this.scoringteam = this.livegame.getHometeam();
			this.scoringroster = this.getHometeam();
			this.scoringpicklist = (List<LiveGameRosterSpot>) this.getHometeamforpl().getWrappedData();
			this.currentscore = this.homescoring.getByKey(this.selectedhomescore.ID);
		} else {
			this.scoringteam = this.livegame.getAwayteam();
			this.scoringroster = this.getAwayteam();
			this.scoringpicklist = (List<LiveGameRosterSpot>) this.getAwayteamforpl().getWrappedData();
			this.currentscore = this.awayscoring.getByKey(this.selectedawayscore.ID);
		}

		
		//
		// reinitialize the info to the current selected score
		//
		LOGGER.info("currentscore is: " + currentscore);
		
		this.goalperiod = currentscore.getPeriod();
		String[] ms = currentscore.getTimescored().split(":");
		this.goalmin = ms[0];
		this.goalsec = ms[1];
		this.goaltype = currentscore.getGoaltype();
		this.selectedgoalroseterid = currentscore.getIdrostergoal();
		this.selecteda1roseterid = currentscore.getIdrostera1();
		this.selecteda2roseterid = currentscore.getIdrostera2();
		
	}
	
	
	@SuppressWarnings("unchecked")
	public void editPenalty(String _ha) {
		
		if (_ha.equals("H")) { 
			this.penteam = this.livegame.getHometeam();
			this.penroster = this.getHometeam();
			this.penpicklist = (List<LiveGameRosterSpot>) this.getHometeamforpl().getWrappedData();
			this.currentpenalty = this.homepenalties.getByKey(this.selectedhomepenalty.ID);
		} else {
			this.penteam = this.livegame.getAwayteam();
			this.penroster = this.getAwayteam();
			this.penpicklist = (List<LiveGameRosterSpot>) this.getAwayteamforpl().getWrappedData();
			this.currentpenalty = this.awaypenalties.getByKey(this.selectedawaypenalty.ID);
		}
		
		//
		// reinitialize the info
		//
		this.penperiod = currentpenalty.getPeriod();
		this.penminutes = currentpenalty.getMinutes();
		String[] ms = currentpenalty.getTimeofpenalty().split(":");
		this.penmin = ms[0];
		this.pensec = ms[1];
		this.pentype = currentpenalty.getPenaltytype();
		this.selectedpenrosterid = currentpenalty.getIdroster();
		
	}
	
	@SuppressWarnings("unchecked")
	public void editSog(String _ha) {
		
		if (_ha.equals("H")) { 
			this.sogteam = this.livegame.getHometeam();
			this.sogroster = this.getHometeam();
			this.sogpicklist = (List<LiveGameRosterSpot>) this.getHometeamforpl().getWrappedData();
			this.currentsog = this.homesogs.getByKey(this.selectedhomesog.ID);
		} else {
			this.sogteam = this.livegame.getAwayteam();
			this.sogroster = this.getAwayteam();
			this.sogpicklist = (List<LiveGameRosterSpot>) this.getAwayteamforpl().getWrappedData();
			this.currentsog = this.awaysogs.getByKey(this.selectedawaysog.ID);
		}
		
		//
		// reinitialize the info
		//
		this.sogplaytime = currentsog.getPlaytime();
		this.sogshots1 = currentsog.getShots1();
		this.sogshots2 = currentsog.getShots2();
		this.sogshots3 = currentsog.getShots3();
		this.sogshots4 = currentsog.getShots4();
		this.sogshots5 = currentsog.getShots5();
		this.sogshots6 = currentsog.getShots6();
		this.sogshots7 = currentsog.getShots7();
		this.sogshots8 = currentsog.getShots8();
		this.sogshots9 = currentsog.getShots9();
		this.selectedsogrosterid = currentsog.getIdroster();
		
	}
	
	@SuppressWarnings("unchecked")
	public void newSog(String _ha) {
		
		if (_ha.equals("H")) { 
			this.sogteam = this.livegame.getHometeam();
			this.sogroster = this.getHometeam();
			this.sogpicklist = (List<LiveGameRosterSpot>) this.getHometeamforpl().getWrappedData();
			this.currentsog = new Sog(0,pb.getProfile(),this.livegame,this.sogteam);
		} else {
			this.sogteam = this.livegame.getAwayteam();
			this.sogroster = this.getAwayteam();
			this.sogpicklist = (List<LiveGameRosterSpot>) this.getAwayteamforpl().getWrappedData();
			this.currentsog = new Sog(0,pb.getProfile(),this.livegame,this.sogteam);
		}
		
		//
		// reinitialize the info
		//
		this.sogplaytime = "";
		this.sogshots1 = 0;
		this.sogshots2 = 0;
		this.sogshots3 = 0;
		this.sogshots4 = 0;
		this.sogshots5 = 0;
		this.sogshots6 = 0;
		this.sogshots7 = 0;
		this.sogshots8 = 0;
		this.sogshots9 = 0;
		this.selectedsogrosterid = 0;
		
	}
	
	
	/**
	 * For the selected Player.. we need to toggle his MIA...
	 * Then refresh the list
	 */
	public void toggleMIA(String _ha) {
		
		LiveGameRosterSpot spot = null;
		
		if (_ha.equals("H")) {
			spot = this.getHometeam().getByKey(this.selectedhomerosterspot.ID);
		} else {
			spot = this.getAwayteam().getByKey(this.selectedawayrosterspot.ID);
		}
		
		LOGGER.info("toggling MIA for " + spot);
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try {
			CallableStatement pc = db.prepareCall("call scaha.toggleMIA(?,?,?)");
			pc.setInt(1,this.livegame.ID);
			pc.setInt(2, spot.ID);
			pc.setInt(3, (spot.isMia() ? 1 : 0));
			pc.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 	
		db.free();
		
		spot.setMia(!spot.isMia());
//		if (_ha.equals("H")) {
//			refreshHomeRoster();
//		} else {
//			refreshAwayRoster();
//		}
		
	}
	
	public String getHomeClubId() {
		return this.getLivegame().getHometeam().getTeamClub().ID+"";
	}

	public String getAwayClubId() {
		
		return this.getLivegame().getAwayteam().getTeamClub().ID+"";
	}

	@Override
	public String getSubject() {
		return "SCAHA Game Change Notification for " + this.getLivegame();
	}

	@Override
	public String getTextBody() {
		List<String> myTokens = new ArrayList<String>();
		myTokens.add("SCHEDULE|" + this.livegame.getSched().getDescription());
		myTokens.add("HOMETEAM|" + this.livegame.getHometeamname());
		myTokens.add("AWAYTEAM|" + this.livegame.getAwayteamname());
		
		myTokens.add("GAMENUMBER|" + this.livegame.ID+"");
		myTokens.add("OLDTYPE|" + this.livegame.getTypetag());
		myTokens.add("OLDSTATE|" + this.livegame.getStatetag());
		myTokens.add("OLDHOMETEAM|" + this.livegame.getHometeamname());
		myTokens.add("OLDAWAYTEAM|" + this.livegame.getAwayteamname());
		myTokens.add("OLDVENUE|" + getStringKeyFromValue(this.venues,this.livegame.getVenuetag()));
		myTokens.add("OLDSHEET|" + this.livegame.getSheetname());
		myTokens.add("OLDDATE|" + this.livegame.getStartdate());
		myTokens.add("OLDTIME|" + this.livegame.getStarttime());
		myTokens.add("NEWTYPE|" + this.lgtypeval);
		myTokens.add("NEWSTATE|" + this.lgstate);
		myTokens.add("NEWHOMETEAM|" + this.lghteam);
		myTokens.add("NEWAWAYTEAM|" + this.lgateam);
		myTokens.add("NEWVENUE|" + this.lgvenue);
		myTokens.add("NEWSHEET|" + this.lgsheet);
		myTokens.add("NEWDATE|" + this.lgdate);
		myTokens.add("NEWTIME|" + this.lgtime);
		return Utils.mergeTokens(GamesheetBean.mail_reg_body,myTokens,"\\|");
	}

	@Override
	public String getPreApprovedCC() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToMailAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InternetAddress[] getToMailIAddress() {
		//
		// Here is where we get all the e-mails we need to get
		//
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		List<InternetAddress> data = new ArrayList<InternetAddress>();
		
		LOGGER.info(this.livegame.toString());
		try {
			PreparedStatement ps = db.prepareCall("call scaha.getLiveGameEmails(?)");
			ps.setInt(1, this.livegame.ID);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				data.add(new InternetAddress(rs.getString(2),rs.getString(1)));
			}
			rs.close();
			ps.close();
			
			for (InternetAddress ia : data) {
				LOGGER.info("e-mail:" + ia);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
		
		return data.toArray(new InternetAddress[data.size()]);
	}

	@Override
	public InternetAddress[] getPreApprovedICC() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the awayscoring
	 */
	public ScoringList getAwayscoring() {
		return awayscoring;
	}

	/**
	 * @param awayscoring the awayscoring to set
	 */
	public void setAwayscoring(ScoringList awayscoring) {
		this.awayscoring = awayscoring;
	}

	/**
	 * @return the homescoring
	 */
	public ScoringList getHomescoring() {
		return homescoring;
	}
	
	public int getDerivedHomeScore() {
		return this.homescoring.getRowCount();
	}

	public int getDerivedAwayScore() {
		return this.awayscoring.getRowCount();
	}

	
	/**
	 * @param homescoring the homescoring to set
	 */
	public void setHomescoring(ScoringList homescoring) {
		this.homescoring = homescoring;
	}

	/**
	 * @return the selectedhomescore
	 */
	public Scoring getSelectedhomescore() {
		return selectedhomescore;
	}

	/**
	 * @param selectedhomescore the selectedhomescore to set
	 */
	public void setSelectedhomescore(Scoring selectedhomescore) {
		this.selectedhomescore = selectedhomescore;
	}

	/**
	 * @return the selectedawayscore
	 */
	public Scoring getSelectedawayscore() {
		return selectedawayscore;
	}

	/**
	 * @param selectedawayscore the selectedawayscore to set
	 */
	public void setSelectedawayscore(Scoring selectedawayscore) {
		this.selectedawayscore = selectedawayscore;
	}

	
	public void deleteGoal(String _ha) {
		
		Scoring sc = null;
		if (_ha.equals("H")) {
			sc = this.getHomescoring().getByKey(selectedhomescore.ID);
		} else {
			sc = this.getAwayscoring().getByKey(selectedawayscore.ID);
		}
		LOGGER.info("we need to delete: " + sc);
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try {
			sc.delete(db);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
		this.setAwayscoring(this.refreshAwayScoring());
		this.setHomescoring(this.refreshHomeScoring());
		
	}
	
	public void deletePenalty(String _ha) {
		
		Penalty pen = null;
		if (_ha.equals("H")) {
			pen = this.getHomepenalties().getByKey(this.selectedhomepenalty.ID);
		} else {
			pen = this.getAwaypenalties().getByKey(this.selectedawaypenalty.ID);
		}
		LOGGER.info("we need to delete: " + pen);
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try {
			pen.delete(db);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
		this.setAwaypenalties(this.refreshAwayPenalty());
		this.setHomepenalties(this.refreshHomePenalty());
		
	}
	
	public void deleteSog(String _ha) {
		
		Sog sog = null;
		if (_ha.equals("H")) {
			sog = this.getHomesogs().getByKey(this.selectedhomesog.ID);
		} else {
			sog = this.getAwaysogs().getByKey(this.selectedawaysog.ID);
		}
		LOGGER.info("we need to delete: " + sog);
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try {
			sog.delete(db);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
		this.setHomesogs(this.refreshHomeSog());
		this.setAwaysogs(this.refreshAwaySog());
		
	}
	
	public void saveSog() {
		
		LOGGER.info("HERE IS WHERE WE save a SOG for " + this.sogteam.getTeamname());
				
		Sog sog = this.currentsog;
		
		
		sog.setShots1(this.getSogshots1());
		sog.setShots2(this.getSogshots2());
		sog.setShots3(this.getSogshots3());
		sog.setShots4(this.getSogshots4());
		sog.setShots5(this.getSogshots5());
		sog.setShots6(this.getSogshots6());
		sog.setShots7(this.getSogshots7());
		sog.setShots8(this.getSogshots8());
		sog.setShots9(this.getSogshots9());
		sog.setPlaytime(this.getSogplaytime());
		sog.setRosterspot(this.sogroster.getByKey(this.selectedsogrosterid));
			
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try {
			sog.update(db);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
		this.setHomesogs(this.refreshHomeSog());
		this.setAwaysogs(this.refreshAwaySog());
		
	}
	public void saveGoal() {
		
		LOGGER.info("HERE IS WHERE WE save a GOAL for " + this.scoringteam.getTeamname());
		
		Scoring score = this.currentscore;
		
		score.setPeriod(this.getGoalperiod());
		score.setGoaltype(this.getGoaltype());
		score.setTimescored("00:" + this.getGoalmin() + ":" + this.getGoalsec());
		score.setLgrosterspotgoal(this.scoringroster.getByKey(this.selectedgoalroseterid));
		score.setLgrosterspota1(this.scoringroster.getByKey(this.selecteda1roseterid));
		score.setLgrosterspota2(this.scoringroster.getByKey(this.selecteda2roseterid));
		
		LOGGER.info("updating score for " + score);
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try {
			score.update(db);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
		this.setAwayscoring(this.refreshAwayScoring());
		this.setHomescoring(this.refreshHomeScoring());
		
	}

	public void savePenalty() {
		
		LOGGER.info("HERE IS WHERE WE save a Penalty for " + this.penteam.getTeamname());
		
		Penalty pen = this.currentpenalty;
		
		pen.setPeriod(this.getPenperiod());
		pen.setPenaltytype(this.getPentype());
		pen.setTimeofpenalty(("00:" + this.getPenmin() + ":" + this.getPensec()));
		pen.setMinutes(this.getPenminutes());
		pen.setRosterspot(this.penroster.getByKey(this.selectedpenrosterid));
		
		LOGGER.info("updating score for " + pen);
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try {
			pen.update(db);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
		this.setAwaypenalties(this.refreshAwayPenalty());
		this.setHomepenalties(this.refreshHomePenalty());
		
	}

	/**
	 * @return the goalperiod
	 */
	public int getGoalperiod() {
		return goalperiod;
	}

	/**
	 * @param goalperiod the goalperiod to set
	 */
	public void setGoalperiod(int goalperiod) {
		this.goalperiod = goalperiod;
	}

	/**
	 * @return the goaltype
	 */
	public String getGoaltype() {
		return goaltype;
	}

	/**
	 * @param goaltype the goaltype to set
	 */
	public void setGoaltype(String goaltype) {
		this.goaltype = goaltype;
	}

	/**
	 * @return the goalmin
	 */
	public String getGoalmin() {
		return goalmin;
	}

	/**
	 * @param goalmin the goalmin to set
	 */
	public void setGoalmin(String goalmin) {
		this.goalmin = goalmin;
	}

	/**
	 * @return the goalsec
	 */
	public String getGoalsec() {
		return goalsec;
	}

	/**
	 * @param goalsec the goalsec to set
	 */
	public void setGoalsec(String goalsec) {
		this.goalsec = goalsec;
	}

	/**
	 * @return the selectedgoalroseterid
	 */
	public int getSelectedgoalroseterid() {
		return selectedgoalroseterid;
	}

	/**
	 * @param selectedgoalroseterid the selectedgoalroseterid to set
	 */
	public void setSelectedgoalroseterid(int selectedgoalroseterid) {
		this.selectedgoalroseterid = selectedgoalroseterid;
	}

	/**
	 * @return the selecteda1roseterid
	 */
	public int getSelecteda1roseterid() {
		return selecteda1roseterid;
	}

	/**
	 * @param selecteda1roseterid the selecteda1roseterid to set
	 */
	public void setSelecteda1roseterid(int selecteda1roseterid) {
		this.selecteda1roseterid = selecteda1roseterid;
	}

	/**
	 * @return the selecteda2roseterid
	 */
	public int getSelecteda2roseterid() {
		return selecteda2roseterid;
	}

	/**
	 * @param selecteda2roseterid the selecteda2roseterid to set
	 */
	public void setSelecteda2roseterid(int selecteda2roseterid) {
		this.selecteda2roseterid = selecteda2roseterid;
	}

	/**
	 * @return the scoringpicklist
	 */
	public List<LiveGameRosterSpot> getScoringpicklist() {
		return scoringpicklist;
	}

	/**
	 * @param scoringpicklist the scoringpicklist to set
	 */
	public void setScoringpicklist(List<LiveGameRosterSpot> scoringpicklist) {
		this.scoringpicklist = scoringpicklist;
	}

	/**
	 * @return the scoringteam
	 */
	public ScahaTeam getScoringteam() {
		return scoringteam;
	}

	/**
	 * @param scoringteam the scoringteam to set
	 */
	public void setScoringteam(ScahaTeam scoringteam) {
		this.scoringteam = scoringteam;
	}

	/**
	 * @return the scoringroster
	 */
	public LiveGameRosterSpotList getScoringroster() {
		return scoringroster;
	}

	/**
	 * @param scoringroster the scoringroster to set
	 */
	public void setScoringroster(LiveGameRosterSpotList scoringroster) {
		this.scoringroster = scoringroster;
	}

	/**
	 * @return the currentscore
	 */
	public Scoring getCurrentscore() {
		return currentscore;
	}

	/**
	 * @param currentscore the currentscore to set
	 */
	public void setCurrentscore(Scoring currentscore) {
		this.currentscore = currentscore;
	}



	/**
	 * @return the awaypenalties
	 */
	public PenaltyList getAwaypenalties() {
		return awaypenalties;
	}

	/**
	 * @param awaypenalties the awaypenalties to set
	 */
	public void setAwaypenalties(PenaltyList awaypenalties) {
		this.awaypenalties = awaypenalties;
	}

	/**
	 * @return the homepenalties
	 */
	public PenaltyList getHomepenalties() {
		return homepenalties;
	}

	/**
	 * @param homepenalties the homepenalties to set
	 */
	public void setHomepenalties(PenaltyList homepenalties) {
		this.homepenalties = homepenalties;
	}

	/**
	 * @return the penperiod
	 */
	public int getPenperiod() {
		return penperiod;
	}

	/**
	 * @param penperiod the penperiod to set
	 */
	public void setPenperiod(int penperiod) {
		this.penperiod = penperiod;
	}

	/**
	 * @return the pentype
	 */
	public String getPentype() {
		return pentype;
	}

	/**
	 * @param pentype the pentype to set
	 */
	public void setPentype(String pentype) {
		this.pentype = pentype;
	}

	/**
	 * @return the penminutes
	 */
	public String getPenminutes() {
		return penminutes;
	}

	/**
	 * @param penminutes the penminutes to set
	 */
	public void setPenminutes(String penminutes) {
		this.penminutes = penminutes;
	}

	
	/**
	 * @return the selectedpenrosterid
	 */
	public int getSelectedpenrosterid() {
		return selectedpenrosterid;
	}

	/**
	 * @param selectedpenrosterid the selectedpenrosterid to set
	 */
	public void setSelectedpenrosterid(int selectedpenrosterid) {
		this.selectedpenrosterid = selectedpenrosterid;
	}

	/**
	 * @return the penroster
	 */
	public LiveGameRosterSpotList getPenroster() {
		return penroster;
	}

	/**
	 * @param penroster the penroster to set
	 */
	public void setPenroster(LiveGameRosterSpotList penroster) {
		this.penroster = penroster;
	}

	/**
	 * @return the penteam
	 */
	public ScahaTeam getPenteam() {
		return penteam;
	}

	/**
	 * @param penteam the penteam to set
	 */
	public void setPenteam(ScahaTeam penteam) {
		this.penteam = penteam;
	}

	/**
	 * @return the penpicklist
	 */
	public List<LiveGameRosterSpot> getPenpicklist() {
		return penpicklist;
	}

	/**
	 * @param penpicklist the penpicklist to set
	 */
	public void setPenpicklist(List<LiveGameRosterSpot> penpicklist) {
		this.penpicklist = penpicklist;
	}

	/**
	 * @return the penmin
	 */
	public String getPenmin() {
		return penmin;
	}

	/**
	 * @param penmin the penmin to set
	 */
	public void setPenmin(String penmin) {
		this.penmin = penmin;
	}

	/**
	 * @return the pensec
	 */
	public String getPensec() {
		return pensec;
	}

	/**
	 * @param pensec the pensec to set
	 */
	public void setPensec(String pensec) {
		this.pensec = pensec;
	}

	/**
	 * @return the currentpenalty
	 */
	public Penalty getCurrentpenalty() {
		return currentpenalty;
	}

	/**
	 * @param currentpenalty the currentpenalty to set
	 */
	public void setCurrentpenalty(Penalty currentpenalty) {
		this.currentpenalty = currentpenalty;
	}

	/**
	 * @return the selectedhomepenalty
	 */
	public Penalty getSelectedhomepenalty() {
		return selectedhomepenalty;
	}

	/**
	 * @param selectedhomepenalty the selectedhomepenalty to set
	 */
	public void setSelectedhomepenalty(Penalty selectedhomepenalty) {
		this.selectedhomepenalty = selectedhomepenalty;
	}

	/**
	 * @return the selectedawaypenalty
	 */
	public Penalty getSelectedawaypenalty() {
		return selectedawaypenalty;
	}

	/**
	 * @param selectedawaypenalty the selectedawaypenalty to set
	 */
	public void setSelectedawaypenalty(Penalty selectedawaypenalty) {
		this.selectedawaypenalty = selectedawaypenalty;
	}

	/**
	 * @return the penalties
	 */
	public List<String> getPenalties() {
		return penalties;
	}

	/**
	 * @param penalties the penalties to set
	 */
	public void setPenalties(List<String> penalties) {
		this.penalties = penalties;
	}

	/**
	 * @return the selectedhomesog
	 */
	public Sog getSelectedhomesog() {
		return selectedhomesog;
	}

	/**
	 * @param selectedhomesog the selectedhomesog to set
	 */
	public void setSelectedhomesog(Sog selectedhomesog) {
		this.selectedhomesog = selectedhomesog;
	}

	/**
	 * @return the selectedawaysog
	 */
	public Sog getSelectedawaysog() {
		return selectedawaysog;
	}

	/**
	 * @param selectedawaysog the selectedawaysog to set
	 */
	public void setSelectedawaysog(Sog selectedawaysog) {
		this.selectedawaysog = selectedawaysog;
	}

	/**
	 * @return the selectedsogrosterid
	 */
	public int getSelectedsogrosterid() {
		return selectedsogrosterid;
	}

	/**
	 * @param selectedsogrosterid the selectedsogrosterid to set
	 */
	public void setSelectedsogrosterid(int selectedsogrosterid) {
		this.selectedsogrosterid = selectedsogrosterid;
	}

	/**
	 * @return the sogpicklist
	 */
	public List<LiveGameRosterSpot> getSogpicklist() {
		return sogpicklist;
	}

	/**
	 * @param sogpicklist the sogpicklist to set
	 */
	public void setSogpicklist(List<LiveGameRosterSpot> sogpicklist) {
		this.sogpicklist = sogpicklist;
	}

	/**
	 * @return the sogteam
	 */
	public ScahaTeam getSogteam() {
		return sogteam;
	}

	/**
	 * @param sogteam the sogteam to set
	 */
	public void setSogteam(ScahaTeam sogteam) {
		this.sogteam = sogteam;
	}

	/**
	 * @return the sogroster
	 */
	public LiveGameRosterSpotList getSogroster() {
		return sogroster;
	}

	/**
	 * @param sogroster the sogroster to set
	 */
	public void setSogroster(LiveGameRosterSpotList sogroster) {
		this.sogroster = sogroster;
	}



	/**
	 * @return the awaysogs
	 */
	public SogList getAwaysogs() {
		return awaysogs;
	}

	/**
	 * @param awaysogs the awaysogs to set
	 */
	public void setAwaysogs(SogList awaysogs) {
		this.awaysogs = awaysogs;
	}

	/**
	 * @return the homesogs
	 */
	public SogList getHomesogs() {
		return homesogs;
	}

	/**
	 * @param homesogs the homesogs to set
	 */
	public void setHomesogs(SogList homesogs) {
		this.homesogs = homesogs;
	}

	/**
	 * @return the currentsog
	 */
	public Sog getCurrentsog() {
		return currentsog;
	}

	/**
	 * @param currentsog the currentsog to set
	 */
	public void setCurrentsog(Sog curentsog) {
		this.currentsog = curentsog;
	}

	/**
	 * @return the sogshots1
	 */
	public int getSogshots1() {
		return sogshots1;
	}

	/**
	 * @param sogshots1 the sogshots1 to set
	 */
	public void setSogshots1(int sogshots1) {
		this.sogshots1 = sogshots1;
	}

	/**
	 * @return the sogshots2
	 */
	public int getSogshots2() {
		return sogshots2;
	}

	/**
	 * @param sogshots2 the sogshots2 to set
	 */
	public void setSogshots2(int sogshots2) {
		this.sogshots2 = sogshots2;
	}

	/**
	 * @return the sogshots3
	 */
	public int getSogshots3() {
		return sogshots3;
	}

	/**
	 * @param sogshots3 the sogshots3 to set
	 */
	public void setSogshots3(int sogshots3) {
		this.sogshots3 = sogshots3;
	}

	/**
	 * @return the sogshots4
	 */
	public int getSogshots4() {
		return sogshots4;
	}

	/**
	 * @param sogshots4 the sogshots4 to set
	 */
	public void setSogshots4(int sogshots4) {
		this.sogshots4 = sogshots4;
	}

	/**
	 * @return the sogshots5
	 */
	public int getSogshots5() {
		return sogshots5;
	}

	/**
	 * @param sogshots5 the sogshots5 to set
	 */
	public void setSogshots5(int sogshots5) {
		this.sogshots5 = sogshots5;
	}

	/**
	 * @return the sogshots6
	 */
	public int getSogshots6() {
		return sogshots6;
	}

	/**
	 * @param sogshots6 the sogshots6 to set
	 */
	public void setSogshots6(int sogshots6) {
		this.sogshots6 = sogshots6;
	}

	/**
	 * @return the sogshots7
	 */
	public int getSogshots7() {
		return sogshots7;
	}

	/**
	 * @param sogshots7 the sogshots7 to set
	 */
	public void setSogshots7(int sogshots7) {
		this.sogshots7 = sogshots7;
	}

	/**
	 * @return the sogshots8
	 */
	public int getSogshots8() {
		return sogshots8;
	}

	/**
	 * @param sogshots8 the sogshots8 to set
	 */
	public void setSogshots8(int sogshots8) {
		this.sogshots8 = sogshots8;
	}

	/**
	 * @return the sogshots9
	 */
	public int getSogshots9() {
		return sogshots9;
	}

	/**
	 * @param sogshots9 the sogshots9 to set
	 */
	public void setSogshots9(int sogshots9) {
		this.sogshots9 = sogshots9;
	}

	/**
	 * @return the venues
	 */
	public Map<String, String> getVenues() {
		return venues;
	}

	/**
	 * @param venues the venues to set
	 */
	public void setVenues(Map<String, String> venues) {
		this.venues = venues;
	}

	/**
	 * @return the htpick
	 */
	public Map<String, String> getHtpick() {
		return htpick;
	}

	/**
	 * @param htpick the htpick to set
	 */
	public void setHtpick(Map<String, String> htpick) {
		this.htpick = htpick;
	}

	/**
	 * @return the atpick
	 */
	public Map<String, String> getAtpick() {
		return atpick;
	}

	/**
	 * @param atpick the atpick to set
	 */
	public void setAtpick(Map<String, String> atpick) {
		this.atpick = atpick;
	}

	/**
	 * @return the typepick
	 */
	public Map<String, String> getTypepick() {
		return typepick;
	}

	/**
	 * @param typepick the typepick to set
	 */
	public void setTypepick(Map<String, String> typepick) {
		this.typepick = typepick;
	}

	/**
	 * @return the statepick
	 */
	public Map<String, String> getStatepick() {
		return statepick;
	}

	/**
	 * @param statepick the statepick to set
	 */
	public void setStatepick(Map<String, String> statepick) {
		this.statepick = statepick;
	}

	/**
	 * @return the editgame
	 */
	public boolean isEditgame() {
		return editgame;
	}

	/**
	 * @param editgame the editgame to set
	 */
	public void setEditgame(boolean editgame) {
		this.editgame = editgame;
	}

	/**
	 * @return the lgsheet
	 */
	public String getLgsheet() {
		return lgsheet;
	}

	/**
	 * @param lgsheet the lgsheet to set
	 */
	public void setLgsheet(String lgsheet) {
		this.lgsheet = lgsheet;
	}

	/**
	 * @return the lgvenue
	 */
	public String getLgvenue() {
		return lgvenue;
	}

	/**
	 * @param lgvenue the lgvenue to set
	 */
	public void setLgvenue(String lgvenue) {
		this.lgvenue = lgvenue;
	}

	/**
	 * @return the lgtype
	 */
	public String getLgtype() {
		return lgtype;
	}

	/**
	 * @param lgtype the lgtype to set
	 */
	public void setLgtype(String lgtype) {
		this.lgtype = lgtype;
	}

	/**
	 * @return the lgdate
	 */
	public String getLgdate() {
		return lgdate;
	}

	/**
	 * @param lgdate the lgdate to set
	 */
	public void setLgdate(String lgdate) {
		this.lgdate = lgdate;
	}

	/**
	 * @return the lgtime
	 */
	public String getLgtime() {
		return lgtime;
	}

	/**
	 * @param lgtime the lgtime to set
	 */
	public void setLgtime(String lgtime) {
		this.lgtime = lgtime;
	}

	/**
	 * @return the lgstate
	 */
	public String getLgstate() {
		return lgstate;
	}

	/**
	 * @param lgstate the lgstate to set
	 */
	public void setLgstate(String lgstate) {
		this.lgstate = lgstate;
	}

	/**
	 * @return the lgvenueval
	 */
	public String getLgvenueval() {
		return lgvenueval;
	}

	/**
	 * @param lgvenueval the lgvenueval to set
	 */
	public void setLgvenueval(String lgvenueval) {
		this.lgvenueval = lgvenueval;
	}

	/**
	 * @return the lgtypeval
	 */
	public String getLgtypeval() {
		return lgtypeval;
	}

	/**
	 * @param lgtypeval the lgtypeval to set
	 */
	public void setLgtypeval(String lgtypeval) {
		this.lgtypeval = lgtypeval;
	}

	/**
	 * @return the lghteam
	 */
	public String getLghteam() {
		return lghteam;
	}

	/**
	 * @param lghteam the lghteam to set
	 */
	public void setLghteam(String lghteam) {
		this.lghteam = lghteam;
	}

	/**
	 * @return the lgateam
	 */
	public String getLgateam() {
		return lgateam;
	}

	/**
	 * @param lgateam the lgateam to set
	 */
	public void setLgateam(String lgateam) {
		this.lgateam = lgateam;
	}

	
	/**
	 * @return the lgstateval
	 */
	public String getLgstateval() {
		return lgstateval;
	}

	/**
	 * @param lgstateval the lgstateval to set
	 */
	public void setLgstateval(String lgstateval) {
		this.lgstateval = lgstateval;
	}

	public void setGameStarted() {

		//
		// we do not save score here..
		//
		this.livegame.setAwayscore(getDerivedAwayScore());
		this.livegame.setHomescore(getDerivedHomeScore());
		this.livegame.setStatetag("InProgress");
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try {
			
			this.livegame.update(db, false);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
		
	}

	public void setGameComplete() {

		//
		// we do not save score here..
		//
		this.livegame.setAwayscore(getDerivedAwayScore());
		this.livegame.setHomescore(getDerivedHomeScore());
		this.livegame.setStatetag("Complete");
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try {
			
			this.livegame.update(db, false);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
		
	}
	
	public void setStatsComplete() {

		//
		// we do not save score here..
		//
		this.livegame.setAwayscore(getDerivedAwayScore());
		this.livegame.setHomescore(getDerivedHomeScore());
		this.livegame.setStatetag("StatsReview");
		
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try {
			
			this.livegame.update(db, false);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
		
		
		
	}
	
	public void setGameFinal() {

		//
		// we do not save score here..
		//
		this.livegame.setAwayscore(getDerivedAwayScore());
		this.livegame.setHomescore(getDerivedHomeScore());
		this.livegame.setStatetag("Final");
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try {
			
			this.livegame.update(db, false);
			
			//need to update stats table as game is being finalized.
			//pass in team id and livegame id
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		db.free();
		
		
		
		//
		// ok.. now we want to check all penalties from both sides..and report any game misconducts
		// or matches..
		//
		PenaltyPusher pp = new PenaltyPusher();
		String temppenaltyrows = "";
		for (Penalty p : this.getAwaypenalties()) {
			
			if (p.getPenaltytype().equals("Game Misconduct") || 
				p.getPenaltytype().equals("Match Penalty")) {
				
				//need to add to the penalty row string so we send all suspensions in one email, not multiple emails
				temppenaltyrows = temppenaltyrows + "<tr><td>&nbsp;" + p.getRosterspot().getFname() + " " + p.getRosterspot().getLname() +"&nbsp;</td><td>&nbsp;" + p.getRosterspot().getJerseynumber() + "&nbsp;</td><td>&nbsp;" + p.getPenaltytype() + "&nbsp;</td></tr>";
				
				pp.setPenalty(p);
				pp.setLivegame(this.livegame);
				
			}
			
			
		}
		
		pp.setPenaltyrows(temppenaltyrows);
		pp.pushPenalty();
		
		//need to reset for away teams
		temppenaltyrows = "";
		
		for (Penalty p : this.getHomepenalties()) {
			
			if (p.getPenaltytype().equals("Game Misconduct") || 
				p.getPenaltytype().equals("Match Penalty")) {
				
				//need to add to the penalty row string so we send all suspensions in one email, not multiple emails
				temppenaltyrows = temppenaltyrows + "<tr><td>&nbsp;" + p.getRosterspot().getFname() + " " + p.getRosterspot().getLname() +"&nbsp;</td><td>&nbsp;" + p.getRosterspot().getJerseynumber() + "&nbsp;</td><td>&nbsp;" + p.getPenaltytype() + "&nbsp;</td></tr>";
				
				pp.setPenalty(p);
				pp.setLivegame(this.livegame);
				
			}
		}
		
		pp.setPenaltyrows(temppenaltyrows);
		pp.pushPenalty();
		
		//need to reset for away teams
		temppenaltyrows = "";
		
	}
	public void saveScheduleInfo() {
		
		//
		// Lets set up some changed display values..  temp solution until we save and refresh from object
		//
		//
		this.lgvenue = getStringKeyFromValue(this.venues,this.lgvenueval);
		this.lgtype =  getStringKeyFromValue(this.typepick, this.lgtypeval);
		this.lgstate = getStringKeyFromValue(this.statepick, this.lgstateval);
		this.lghteam = getStringKeyFromValue(this.htpick, this.lghteamval);
		this.lgateam = getStringKeyFromValue(this.atpick, this.lgateamval);
		
		LOGGER.info("Start Date: " + this.lgdate + ", orig value is " + this.livegame.getStartdate());
		LOGGER.info("Start Time: " + this.lgtime + ", orig value is " + this.livegame.getStarttime());
		LOGGER.info("Type: " + this.lgtypeval + ":" + this.lgtype + ", orig value is " + this.livegame.getTypetag());
		LOGGER.info("State: " + this.lgstateval + ":" + this.lgstate + ", orig value is " + this.livegame.getStatetag());
		LOGGER.info("Venue: " + this.lgvenueval + ":" + this.lgvenue + ", orig value is " + this.livegame.getVenuetag());
		LOGGER.info("Sheet: " + this.lgsheet +  ", orig value is " + this.livegame.getSheetname());
		LOGGER.info("Away Team + " + this.lgateamval + ":" + this.lgateam + ", orig value is " + this.livegame.getAwayteam().ID);
		LOGGER.info("Home Team + " + this.lghteamval + ":" + this.lghteam + ", orig value is " + this.livegame.getHometeam().ID);
		
		//
		// ok.. lets generate the e-mail.. so we can put prior information.. and current information..
		//
		LOGGER.info("HERE IS WHERE WE SAVE EVERYTHING COLLECTED FROM GameChange And Send Mail..");
		LOGGER.info("Sending Game Change mail here...");
		SendMailSSL mail = new SendMailSSL(this);
		mail.sendMail();
		
		//
		// we do not save score here..
		//
		this.livegame.setAwayscore(getDerivedAwayScore());
		this.livegame.setHomescore(getDerivedHomeScore());
		
		//
		// Lets check to see if home and away teams have been swapped
		//
		if (this.livegame.getHometeam().ID != Integer.parseInt(lghteamval)) {
			ScahaTeam tmp = this.livegame.getHometeam();
			this.livegame.setHometeam(this.livegame.getAwayteam());
			this.livegame.setAwayteam(tmp);
			this.livegame.setHometeamname(this.lghteam);
			this.livegame.setAwayteamname(this.lgateam);
		}
		
		this.livegame.setStartdate(this.lgdate);
		this.livegame.setStarttime(this.lgtime);
		this.livegame.setVenuetag(this.lgvenueval);
		this.livegame.setVenueshortname(this.lgvenue);
		this.livegame.setSheetname(this.lgsheet);
		this.livegame.setStatetag(this.lgstateval);
		this.livegame.setTypetag(this.lgtypeval);

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
				
		try {
			this.livegame.update(db, true);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		db.free();
		
		//
		// ok.. we should be all good here..
		//
		this.setDisplayValues();
		this.setEditgame(false);
		
	}
	
	public void cancelScheduleInfoChanges() {
		
		LOGGER.info(this.lgvenueval + ":" + this.lgvenue);
		//
		// reset the display values..
		//
		this.setDisplayValues();
		this.setEditgame(false);
		
	}

	/**
	 * @return the lgateamval
	 */
	public String getLgateamval() {
		return lgateamval;
	}

	/**
	 * @param lgateamval the lgateamval to set
	 */
	public void setLgateamval(String lgateamval) {
		this.lgateamval = lgateamval;
	}

	/**
	 * @return the lghteamval
	 */
	public String getLghteamval() {
		return lghteamval;
	}

	/**
	 * @param lghteamval the lghteamval to set
	 */
	public void setLghteamval(String lghteamval) {
		this.lghteamval = lghteamval;
	}
	
	 public  String getStringKeyFromValue(Map<String, String> hm, String value) {
	    for (String o : hm.keySet()) {
	      if (hm.get(o).equals(value)) {
	        return o;
	      }
	    }
	    return null;
	  }

	/**
	 * @return the sogplaytime
	 */
	public String getSogplaytime() {
		return sogplaytime;
	}

	/**
	 * @param sogplaytime the sogplaytime to set
	 */
	public void setSogplaytime(String sogplaytime) {
		this.sogplaytime = sogplaytime;
	}
	
    public void gamesheetClose(){

    	FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect(pb.getLivegameeditreturn());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void saveScore(){
    	
    	//lets update the livegame with the new scores, all other details should remain the same
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
		try {
			this.livegame.setStatetag("Final");
			this.livegame.update(db, true);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		db.free();
		
    
    	//ok now that we saved the score lets redirect back to the manager portal
    	FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect(pb.getLivegameeditreturn());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void setPenaltiesPickList(){
    	
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
    	try {
			PreparedStatement ps = db.prepareCall("call scaha.getPenaltyList()");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String penalty = rs.getString("penaltyname");
				
				this.penalties.add(penalty);
			}
			rs.close();
			ps.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
		
		
	
    	//need to add these to database
		 /*this.penalties.put("15 Team Penalties","15 Team Penalties");
		 this.penalties.put("2 Majors","2 Majors");
		 this.penalties.put("3rd Man in","3rd Man in");
		 this.penalties.put("5 Penalties","5 Penalties");
		 this.penalties.put("Abuse of Officials", "Abuse of Officials");
		 this.penalties.put("Attempt to injure","Attempt to injure");
		 this.penalties.put("Bench","Bench" );
		 this.penalties.put("Boarding","Boarding");
		 this.penalties.put("Body Checking","Body Checking");
		 this.penalties.put("Butt-Ending", "Butt-Ending");
		 this.penalties.put("Charging","Charging");
		 this.penalties.put("Hooking", "Hooking");
		 this.penalties.put("Checking from Behind", "Checking from Behind");
		 this.penalties.put("Cross-Checking", "Cross-Checking");
		 this.penalties.put("Delay of Game", "Delay of Game");
		 this.penalties.put("Elbowing", "Elbowing");
		 this.penalties.put("Fisticuffs/Fighting", "Fisticuffs/Fighting");
		 this.penalties.put("Game Misconduct", "Game Misconduct");
		 this.penalties.put("Head Contact", "Head Contact");
		 this.penalties.put("High-Sticking", "High-Sticking");
		 this.penalties.put("Holding","Holding");
		 this.penalties.put("Holding the Facemask","Holding the Facemask");
		 this.penalties.put("Hooking", "Hooking");
		 this.penalties.put("Illegal Equipment", "Illegal Equipment");
		 this.penalties.put("Interference", "Interference");
		 this.penalties.put("Kicking", "Kicking");
		 this.penalties.put("Kneeing", "Kneeing");
		 this.penalties.put("Major", "Major");
		 this.penalties.put("Match Penalty","Match Penalty");
		 this.penalties.put("Misconduct","Misconduct");
		 this.penalties.put("Mouthpiece","Mouthpiece");
		 this.penalties.put("Roughing","Roughing");
		 this.penalties.put("Slashing","Slashing");
		 this.penalties.put("Spearing","Spearing");
		 this.penalties.put("Too Many Men","Too Many Men");
		 this.penalties.put("Tripping","Tripping");
		 this.penalties.put("Unsportsmanlike","Unsportsmanlike");*/
    }
    
    public void setVenuesPickList(){
    	
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
    	try {
			PreparedStatement ps = db.prepareCall("call scaha.getVenueList()");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				this.venues.put(rs.getString("description"),rs.getString("tag"));
			}
			rs.close();
			ps.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
   
		//make sure all of these are in the db
		/*this.venues.put("The Rinks - Yorba Linda ICE","YLICE");
		 this.venues.put("The Rinks - Anaheim ICE","AICE");
		 this.venues.put("The Rinks - Westminster ICE","WICE");
		 this.venues.put("The Rinks - Lakewood ICE","LAKEWOOD");
		 this.venues.put("Bakersfield Ice Sports Center","BAKERICE");
		 this.venues.put("Skating Edge Ice Center","BHSEIC");
		 this.venues.put("Iceoplex Simi Valley","SIMI");
		 this.venues.put("Valencia Ice Station","ICESTATION");
		 this.venues.put("Pickwick Ice Arena","PICKWICK");
		 this.venues.put("LA Kings Valley Ice Center","VALLEYICE");
		 this.venues.put("East West Ice Palace","EWICEP");
		 this.venues.put("Ontario Center Ice Arena","OCIA");
		 this.venues.put("Channel Islands Ice Center","CIIC");
		 this.venues.put("LA Kings Icetown Riverside","RIVICE");
		 this.venues.put("Desert Ice Castle","DICE");
		 this.venues.put("KHS Ice Arena","KHS");
		 this.venues.put("Toyota Sports Center","TSC");
		 this.venues.put("Lake Forest Ice Palace","LFIP");
		 this.venues.put("Ontario Ice Skating Center","ONTICE");
		 this.venues.put("Pasadena Skating Center","PISC");
		 this.venues.put("Iceoplex Escondido","ESICOPLEX");
		 this.venues.put("Kroc Center Ice Arena","KROC");
		 this.venues.put("San Diego Ice Arena","SDIA");
		 this.venues.put("Carlsbad  Ice Arena","CARLSBAD");*/
		 
    }
    
    public void loadRosterForEdit(String homeaway){
    	if (homeaway.equals("H")){
    		rb.setSelectedteam(this.livegame.getHometeam().ID);
        }else {
        	rb.setSelectedteam(this.livegame.getAwayteam().ID);
    	}
    	rb.onTeamChange();
    }
    
    public void reloadTeamRoster(String homeaway){
    	if (homeaway.equals("H")){
    		this.setHometeam(this.refreshHomeRoster());
    	}else {
    		this.setHometeam(this.refreshAwayRoster());
    	}
    	
    }
}