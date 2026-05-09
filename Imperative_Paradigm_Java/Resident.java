// Project CSI2120/CSI2520
// Winter 2026
// Robert Laganiere, uottawa.ca

// Student Name: Vineeth Ravi
// Student Number: 300380109

// this is the (incomplete) Resident class
public class Resident {
	
	private int residentID;
	private String firstname;
	private String lastname;
	private String[] rol;
	private Program matchedProgram;
	private int matchedRank;
	private int rank;
	private int rolIndex;
	
	// constructs a Resident
    public Resident(int id, String fname, String lname) {
		residentID= id;
		firstname= fname;
		lastname= lname;
		matchedProgram = null;
		rolIndex = 0;
	}

    // the rol in order of preference
	public void setROL(String[] rol) {
		this.rol= rol;
	}

	public String[] getROL(){
		return this.rol;
	}

	public int getID(){
		return this.residentID;
	}

	public void setMatchedProgram(Program program){
		matchedProgram = program;
	}

	public void setMatchedRank(int i){
		matchedRank = i;
	}

	public int getRank(){
		return this.rank;
	}

	public Program getMatchedProgram(){
		if (matchedProgram == null){
			return null;
		}
		return matchedProgram;
	}
	public String getName(){
		return (lastname + ", " + firstname);
	}

	// string representation
	public String toString() {
      
       return "["+residentID+"]: "+firstname+" "+ lastname+" ("+rol.length+")";	  
	}
}