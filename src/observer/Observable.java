package observer;

public interface Observable {
	 public void addObserver(Observer obs);
	  public void removeObserver();
	  public void notifyObserver();
	  
	  public void addMainObserver(Observer obs);
	  public void removeMainObserver();
	  public void notifyMainObserver();
}
