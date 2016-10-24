package com.scaha.objects;

public class Game extends ScahaObject {

	//game level properties used by multiple methods
	private Integer idlivegame = null;
	private String hometeam = null;
	private String awayteam = null;
	private String typetag = null;
	private String location = null;
	private String gametime = null;
	private String homescore = null;
	private String awayscore = null;
	private String status = null;
	private String displaydivision = null;
	private Integer homeclubid = null;
	private Integer awayclubid = null;
	private Boolean renderboxscore = null;
	private String scoresheeturl = null;	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void setScoresheeturl(String value){
		scoresheeturl=value;
	}
	
	public String getScoresheeturl(){
		return scoresheeturl;
	}
	
	public void setRenderboxscore(Boolean value){
		renderboxscore=value;
	}
	
	public Boolean getRenderboxscore(){
		return renderboxscore;
	}
	
	public void setIdlivegame(Integer value){
		idlivegame = value;
	}
	
	public Integer getIdlivegame(){
		return idlivegame;
	}
	
	public void setHometeam(String value){
		hometeam = value;
	}
	
	public String getHometeam(){
		return hometeam;
	}
	
	public void setAwayteam(String value){
		awayteam = value;
	}
	
	public String getAwayteam(){
		return awayteam;
	}
	
	public void setTypetag(String value){
		typetag = value;
	}
	
	public String getTypetag(){
		return typetag;
	}
	
	public void setLocation(String value){
		location = value;
	}
	
	public String getLocation(){
		return location;
	}
	
	public void setGametime(String value){
		gametime = value;
	}
	
	public String getGametime(){
		return gametime;
	}
	
	public void setHomescore(String value){
		homescore = value;
	}
	
	public String getHomescore(){
		return homescore;
	}
	
	public void setAwayscore(String value){
		awayscore = value;
	}
	
	public String getAwayscore(){
		return awayscore;
	}
	
	public void setStatus(String value){
		status = value;
	}
	
	public String getStatus(){
		return status;
	}
	
	public void setDisplaydivision(String value){
		displaydivision = value;
	}
	
	public String getDisplaydivision(){
		return displaydivision;
	}
	
	public void setHomeclubid(Integer value){
		homeclubid = value;
	}
	
	public Integer getHomeclubid(){
		return homeclubid;
	}
	
	public void setAwayclubid(Integer value){
		awayclubid = value;
	}
	
	public Integer getAwayclubid(){
		return awayclubid;
	}
}
