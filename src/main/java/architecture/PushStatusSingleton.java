package architecture;

import java.util.HashMap;

public final class PushStatusSingleton {

	private static PushStatusSingleton INSTANCE;
	private HashMap<String, Boolean> isUpdate = new HashMap<>();

	private PushStatusSingleton() {
	}

	public static PushStatusSingleton getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PushStatusSingleton();
		}

		return INSTANCE;
	}
	// getters and setters

	public HashMap<String, Boolean> getIsUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(HashMap<String, Boolean> isUpdate) {
		this.isUpdate = isUpdate;
	}

}