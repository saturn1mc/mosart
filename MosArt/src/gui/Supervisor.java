package gui;

public class Supervisor {
	private static Supervisor singleton = null;
	
	private Supervisor(){
		
	}
	
	public static Supervisor getInstance(){
		if(singleton == null){
			singleton = new Supervisor();
		}
		
		return singleton;
	}
	
	public void reportTask(String message){
		System.out.println(message);
	}
	
	public void reportProgress(String message, float progress){
		//TODO
		System.out.println((progress*100f) + "% - " + message);
	}
}
