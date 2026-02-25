// Project CSI2120/CSI2520
// Winter 2026
// Robert Laganiere, uottawa.ca

// Student Name: Vineeth Ravi
// Student Number: 300380109

// this is the (incomplete) Program class

import java.util.ArrayList;

public class Program {
	
	private String programID;
	private String name;
	private int quota;
	private int[] rol;
	private ArrayList<Resident> matchedResidents;
	
	// constructs a Program
    public Program(String id, String n, int q) {
	
		programID= id;
		name= n;
		quota= q;
		matchedResidents = new ArrayList<>();
	}

    // the rol in order of preference
	public void setROL(int[] rol) {
		
		this.rol= rol;
	}
	
	public boolean member(int residentID){
		for(int element : rol){
			if (element == residentID){
				return true;
			}
		}
		return false;
	}

	public int rank(int residentID){
		for(int i = 0; i < rol.length; i++){
			if (rol[i] == residentID){
				return i;
			}
		}
		return -1;
	}
	public int getQuota(){
		return quota;
	}

	public String getName(){
		return name;
	}

	public boolean full(){
		return matchedResidents.size() == quota;
	}
	public boolean empty(){
		return matchedResidents.size() == 0;
	}
	public ArrayList<Resident> getMatchedResidents(){
		return matchedResidents;
	}

	public Resident leastPreferred(){
		Resident leastPreferedResident = matchedResidents.get(0);
		for (Resident resident : matchedResidents){
			if (rank(resident.getID()) > rank(leastPreferedResident.getID()))  {
				leastPreferedResident = resident;
			}
		}
		return leastPreferedResident;
	}

	public void addResident(Resident resident){
		if (matchedResidents.size() < quota){
			matchedResidents.add(resident);
		}
		else if(rank(resident.getID()) > this.leastPreferred().getID()){
			matchedResidents.remove(leastPreferred());
			matchedResidents.add(resident);
		}
	}
	
	// string representation
	public String toString() {
      
       return "["+programID+"]: "+name+" {"+ quota+ "}" +" ("+rol.length+")";	  
	}
}