package com.scaha.beans;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Link;
import com.scaha.objects.Team;

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class modifytournamentBean implements Serializable{

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	//private static String mail_exhibitiongame_body = Utils.getMailTemplateFromFile("/mail/exhibitiongame.html");
	transient private ResultSet rs = null;
	
	//lists for generated datamodels
	//private List<ExhibitionGame> exhibitiongames = null;
	
	//properties for creating the heart of the tournament records
	private String tournamentname=null;
	private String startdate=null;
	private String enddate=null;
	private String sanction=null;
	private String website=null;
	private String director=null;
	private String phone = null;	
	private String email = null;

	//lookup list variables
	private List<Link> venues = null;
	
	//datamodels for all of the lists on the page
	//private ExhibitionGameDataModel ExhibitionGameDataModel = null;
    
    //properties for storing the selected row of each of the datatables
	private String[] selectedvenues = null;
	
	
		
	
	@PostConstruct
    public void init() {
		
		//not sure what is needed yet.
        
    }
	
    public modifytournamentBean() {  
    
    }  
    
    public String[] getSelectedvenues() {
        return selectedvenues;
    }
 
    public void setSelectedVenues(String[] selectedVenues) {
        this.selectedvenues = selectedVenues;
    }
    
    public List<Link> getVenues(){
    	return venues;
    }
    
    public void setVenues(List<Link> value){
    	venues=value;
    }
    
    public String getTournamentname(){
    	return tournamentname;
    }
    
    public void setTournamentname(String gdate){
    	tournamentname=gdate;
    }
    
    
    public String getStartdate(){
    	return startdate;
    }
    
    public void setStartdate(String gdate){
    	startdate=gdate;
    }
    
    public String getEnddate(){
    	return enddate;
    }
    
    public void setEnddate(String gdate){
    	enddate=gdate;
    }
    
    public String getSanction(){
    	return sanction;
    }
    
    public void setSanction(String gdate){
    	sanction=gdate;
    }
    
    
    public String getWebsite(){
    	return website;
    }
    
    public void setWebsite(String gdate){
    	website=gdate;
    }
    
    public String getDirector(){
    	return director;
    }
    
    public void setDirector(String value){
    	director=value;
    }
    
    public void setPhone(String name){
    	phone=name;
    }
    
    public String getPhone(){
    	return phone;
    }
    
    public void setEmail(String name){
    	email=name;
    }
    
    public String getEmail(){
    	return email;
    }
    
    
    
    
	
	public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
    }
    
    	
		
	
		
	public void addTournament(){
		
		//need to add logic to add base tournament record meta data
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				LOGGER.info("add tournament to scaha list");
 				CallableStatement cs = db.prepareCall("CALL scaha.addScahaTournament(?,?,?,?,?,?,?,?)");
    		    cs.setString("tourneyname", this.tournamentname);
    		    cs.setString("startdate", this.startdate);
    		    cs.setString("enddate", this.enddate);
    		    cs.setString("sanction", this.sanction);
    		    cs.setString("website", this.website);
    		    cs.setString("director", this.director);
    		    cs.setString("phone", this.phone);
    		    cs.setString("email", this.email);
    		    cs.executeQuery();
    		    
    		    //ok now we need to add venues
    		    
    		    //and now we finish up with 
    		    
    		    db.commit();
    			
    		    FacesContext context = FacesContext.getCurrentInstance();  
                context.addMessage(null, new FacesMessage("Successful", "You have add the scaha tournament"));
                
                
    			db.cleanup();
    			
    			
			} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Adding the Tournament");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		
		
	}

	public void loadVenues(){
		List<Link> templist = new ArrayList<Link>();

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

    		if (db.setAutoCommit(false)) {
    		
    			//Vector<Integer> v = new Vector<Integer>();
    			//v.add(1);
    			//db.getData("CALL scaha.getTeamsByClub(?)", v);
    		    CallableStatement cs = db.prepareCall("CALL scaha.getVenues()");
    		    rs = cs.executeQuery();
    			
    			if (rs != null){
    				
    				while (rs.next()) {
    					Integer idvenue = rs.getInt("idvenues");
        				String description = rs.getString("description");
        				
        				Link link = new Link();
        				link.setLinkid(idvenue);
        				link.setLinklabel(description);
        				templist.add(link);
    				}
    				LOGGER.info("We have results for get venue list");
    			}
    			rs.close();
    			cs.close();
    			db.cleanup();
    		} else {
    		
    		}
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading venues");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setVenues(templist);
	}
}

