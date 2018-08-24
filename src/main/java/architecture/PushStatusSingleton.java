package architecture;

import java.util.HashMap;

public final class PushStatusSingleton {

	private static PushStatusSingleton INSTANCE;
	private HashMap<Integer, Boolean> isUpdate = new HashMap<>();

	private PushStatusSingleton() {
	}

	public static PushStatusSingleton getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PushStatusSingleton();
		}

		return INSTANCE;
	}
	// getters and setters

	public HashMap<Integer, Boolean> getIsUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(HashMap<Integer, Boolean> isUpdate) {
		this.isUpdate = isUpdate;
	}

}