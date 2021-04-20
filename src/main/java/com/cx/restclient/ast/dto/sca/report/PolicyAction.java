package com.cx.restclient.ast.dto.sca.report;

import java.io.Serializable;


public class PolicyAction implements Serializable {
	private boolean breakBuild;

	public final boolean isBreakBuild() {
		return breakBuild;
	}

	public final void setBreakBuild(boolean breakBuild) {
		this.breakBuild = breakBuild;
	}
	
	
}
