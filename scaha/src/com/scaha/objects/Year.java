package com.scaha.objects;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import com.gbli.context.ContextManager;

public class Year extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private String yearname = null;
	private List<Minute> minutes = null;
	
	public String getYearname() {
		return this.yearname;
	}

	public void setYearname(String fam) {
		this.yearname = fam;
	}
	
	public List<Minute> getMinutes() {
		return this.minutes;
	}

	public void setMinutes(List<Minute> fam) {
		this.minutes = fam;
	}
}
