package cz.zcu.kiv.memory.antipatterns;

import org.json.JSONObject;

/**
 * Represents a constraint in a method.
 * @author jens dietrich
 */
public  class AntipatternInstance {

	static final String MISSING_INFO = "-"; // useful for serializing

	private ProgramVersion programVersion = null;
	private String cuName = MISSING_INFO; // src file
	private int lineNo = -1;
	private String methodDeclaration = MISSING_INFO;
	private boolean methodAbstract = false;
	private String condition = MISSING_INFO;
	private String additionalInfo = MISSING_INFO;  // additional info, e.g. from exception message parameters

	public int getLineNo() {
		return lineNo;
	}
	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}
	public ProgramVersion getProgramVersion() {
		return programVersion;
	}
	public void setProgramVersion(ProgramVersion programVersion) {
		this.programVersion = programVersion;
	}
	public String getCuName() {
		return cuName;
	}
	public void setCuName(String cuName) {
		this.cuName = cuName;
	}

	public boolean isMethodAbstract() {
		return methodAbstract;
	}

	public void setMethodAbstract(boolean methodAbstract) {
		this.methodAbstract = methodAbstract;
	}

	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition==null?MISSING_INFO:condition;
	}
	public String getMethodDeclaration() {
		return methodDeclaration;
	}
	public void setMethodDeclaration(String methodDeclaration) {
		this.methodDeclaration = methodDeclaration==null?MISSING_INFO:methodDeclaration;
	}
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo==null?MISSING_INFO:additionalInfo;
	}

	public static final char CSV_SEPARATOR = '\t';

	/**
	 * Export this object to a JSON Object.
	 * @return
	 */
	public JSONObject toJSON () {

		return new JSONObject()
			.put("name", getProgramVersion().getName())
			.put("version", getProgramVersion().getVersion())
			.put("cu", getCuName())
			.put("line", getLineNo())
			.put("method", getMethodDeclaration())
			.put("condition", getCondition())
			.put("additional_info", getAdditionalInfo())
				.put("abstract_method", isMethodAbstract())
            ;
	}

	/**
	 * Export this object to a JSON Object.
	 * @return
	 */
	public static AntipatternInstance fromJSON (JSONObject json) {
		AntipatternInstance mc = new AntipatternInstance();
		mc.setProgramVersion(ProgramVersion.getOrCreate(json.getString("name"),json.getString("version")));
		mc.setCuName(json.getString("cu"));
		mc.setLineNo(json.getInt("line"));
		mc.setMethodDeclaration(json.getString("method"));
		mc.setCondition(json.getString("condition"));
		mc.setAdditionalInfo(json.getString("additional_info"));
		mc.setMethodAbstract(json.getBoolean("abstract_method"));
		return mc;
	}


}
