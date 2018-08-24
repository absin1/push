package architecture;

import java.util.HashMap;

public class Model {

	private static Model INSTANCE;
	private HashMap<Integer, ComplexObject> complexMap = new HashMap<>();

	private Model() {
	}

	public static Model getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Model();
		}

		return INSTANCE;
	}

	public HashMap<Integer, ComplexObject> getComplexMap() {
		return complexMap;
	}

	public void setComplexMap(HashMap<Integer, ComplexObject> complexMap) {
		this.complexMap = complexMap;
	}

}
