package com.scaha.beans;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;

import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.FamilyMemberDataModel;
import com.scaha.objects.MailableObject;
import com.scaha.objects.NewsItem;
import com.scaha.objects.NewsItemList;
import com.scaha.objects.Profile;

@ManagedBean
@ViewScoped
public class NewsBean implements Serializable,  MailableObject  {


	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	
	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private NewsItem currentnewsitem = null;
	private String title = null;
	private String newssubject = null;
	private String newsbody = null;
	
	//
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	@Override
	public String getSubject() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getTextBody() {
		// TODO Auto-generated method stub
		return null;
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

	 /**
	 * @return the currentnewsitem
	 */
	public NewsItem getCurrentNewsItem() {
		return currentnewsitem;
	}
	
	public String testMerge() {
		
		List<String> myTokens = new ArrayList<String>();
		myTokens.add("FIRSTNAME:David Bigelow");
		return Utils.mailMerge("/mail/test.mail",myTokens);
		
	}
	/**
	 * @param currentnewsitem the currentnewsitem to set
	 */
	public void setCurrentNewsItem(NewsItem currentnewsitem) {
		this.currentnewsitem = currentnewsitem;
	}
	public NewsItemList getNewsItemList() {
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		NewsItemList nil = null;
		try {
			nil = NewsItemList.NewsItemListFactory(scaha.getDefaultProfile(), db, "12/12/2013");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		db.free();
		return nil;
		
	 }
	
	public void updateNewsItem(NewsItem current) {
		FacesContext context = FacesContext.getCurrentInstance();
		Application app = context.getApplication();
		ValueExpression expression = app.getExpressionFactory().createValueExpression(context.getELContext(), "#{profileBean}", Object.class );
		ProfileBean pb = (ProfileBean) expression.getValue( context.getELContext() );
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try {
			current.setAuthor(pb.getProfile().getPerson().getsFirstName() + " " + pb.getProfile().getPerson().getsLastName());
			current.update(db);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		db.free();
		//LOGGER.info(current.toString());

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
	@Override
	public InternetAddress[] getToMailIAddress() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public InternetAddress[] getPreApprovedICC() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getTitle(){
		return title;
	}
	
	public void setTitle(String value){
		title=value;
	}
	
	public String getNewssubject(){
		return newssubject;
	}
	
	public void setNewssubject(String value){
		newssubject=value;
	}
	
	public String getNewsbody(){
		return newsbody;
	}
	
	public void setNewsbody(String value){
		newsbody=value;
	}
	
	public void addNewsItem() {
		FacesContext context = FacesContext.getCurrentInstance();
		Application app = context.getApplication();
		ValueExpression expression = app.getExpressionFactory().createValueExpression(context.getELContext(), "#{profileBean}", Object.class );
		ProfileBean pb = (ProfileBean) expression.getValue( context.getELContext() );
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try {
			CallableStatement cs = db.prepareCall("CALL scaha.updateNewsItem(?,?,?,?,?,?,?,?)");
    		cs.setInt("inout_idNewsItem", 0);
			cs.setString("in_subject", this.getNewssubject());
    		cs.setString("in_title", this.getTitle());
    		cs.setString("in_author", pb.getProfile().getPerson().getsFirstName() + " " + pb.getProfile().getPerson().getsLastName());
    		cs.setString("in_body", this.getNewsbody());
    		cs.setInt("in_isactive", 1);
    		cs.setString("in_updated", null);
    		cs.setString("in_state", "publish");
			cs.executeQuery();
			
			cs.close();
			db.cleanup();
			db.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		db.free();
		//LOGGER.info("added " + this.getNewssubject() + " news item");

		getNewsItemList();
	}
	
	public void hideNewsItem(NewsItem current) {
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try {
			CallableStatement cs = db.prepareCall("CALL scaha.hideNewsItem(?)");
    		cs.setInt("inout_idNewsItem", Integer.parseInt(current.getIDStr()));
			cs.execute();
			cs.close();
			db.commit();
			db.cleanup();
			db.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		db.free();
		//LOGGER.info("added " + this.getNewssubject() + " news item");

		getNewsItemList();
	}
}
